// =============================================================================
// SALON SERVICE - SalonController CORREGIDO
// src/main/java/com/zosh/controller/SalonController.java
// =============================================================================
package com.zosh.controller;

import com.zosh.exception.UserException;
import com.zosh.mapper.SalonMapper;
import com.zosh.modal.Salon;
import com.zosh.payload.dto.SalonDTO;
import com.zosh.payload.dto.UserDTO;
import com.zosh.payload.request.CreateUserFromCognitoRequest;
import com.zosh.service.SalonService;
import com.zosh.service.clients.UserFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/salons")
public class SalonController {

    private final SalonService salonService;
    private final UserFeignClient userFeignClient;

    // =========================================================================
    // CREAR SALÓN (MÉTODO PRINCIPAL)
    // =========================================================================
    @PostMapping
    public ResponseEntity<SalonDTO> createSalon(
            @RequestBody SalonDTO salonDTO,
            HttpServletRequest request,
            @RequestHeader("Authorization") String jwt,
            @RequestHeader(value = "X-Cognito-Sub", required = false) String cognitoSub,
            @RequestHeader(value = "X-User-Email", required = false) String userEmail,
            @RequestHeader(value = "X-User-Username", required = false) String username,
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @RequestHeader(value = "X-Auth-Source", required = false) String authSource) throws Exception {

        System.out.println("🏛️ =========================");
        System.out.println("🏛️ SALON SERVICE - CREATE SALON");
        System.out.println("🏛️ =========================");

        // 🚀 DEBUG COMPLETO DEL DTO RECIBIDO
        System.out.println("📋 SALON DTO RECIBIDO:");
        System.out.println("   name: '" + salonDTO.getName() + "'");
        System.out.println("   address: '" + salonDTO.getAddress() + "'");
        System.out.println("   city: '" + salonDTO.getCity() + "'");
        System.out.println("   phoneNumber: '" + salonDTO.getPhoneNumber() + "'");
        System.out.println("   email: '" + salonDTO.getEmail() + "'");
        System.out.println("   openTime: " + salonDTO.getOpenTime());
        System.out.println("   closeTime: " + salonDTO.getCloseTime());
        System.out.println(
                "   images: " + (salonDTO.getImages() != null ? salonDTO.getImages().size() + " items" : "NULL"));
        System.out.println("   homeService: " + salonDTO.isHomeService());
        System.out.println("   active: " + salonDTO.isActive());
        System.out.println("   ownerId: " + salonDTO.getOwnerId());

        // 🚀 DEBUG HEADERS
        System.out.println("🎯 HEADERS:");
        System.out.println("   X-Cognito-Sub: " + cognitoSub);
        System.out.println("   X-User-Email: " + userEmail);
        System.out.println("   X-Auth-Source: " + authSource);

        UserDTO user = null;

        if ("Cognito".equals(authSource) && cognitoSub != null && !cognitoSub.isEmpty()) {
            System.out.println("✅ PROCESANDO USUARIO DE COGNITO");
            user = handleCognitoUserFromGateway(cognitoSub, userEmail, username, userRole, jwt);
        } else {
            System.out.println("✅ PROCESANDO USUARIO TRADICIONAL");
            try {
                user = userFeignClient.getUserFromJwtToken(jwt).getBody();
            } catch (Exception e) {
                System.err.println("❌ Error obteniendo usuario tradicional: " + e.getMessage());
                throw new UserException("Error obteniendo usuario: " + e.getMessage());
            }
        }

        if (user == null) {
            throw new UserException("Usuario no encontrado");
        }

        System.out.println("🏛️ Usuario encontrado: " + user.getEmail() + " (ID: " + user.getId() + ")");

        // 🚀 VALIDAR DATOS ANTES DE CREAR
        System.out.println("🔍 VALIDANDO DATOS ANTES DE CREAR SALÓN:");

        if (salonDTO.getName() == null || salonDTO.getName().trim().isEmpty()) {
            throw new Exception("Nombre del salón es requerido");
        }
        if (salonDTO.getAddress() == null || salonDTO.getAddress().trim().isEmpty()) {
            throw new Exception("Dirección del salón es requerida");
        }
        if (salonDTO.getCity() == null || salonDTO.getCity().trim().isEmpty()) {
            throw new Exception("Ciudad del salón es requerida");
        }
        if (salonDTO.getEmail() == null || salonDTO.getEmail().trim().isEmpty()) {
            throw new Exception("Email del salón es requerido");
        }
        if (user.getId() == null) {
            throw new Exception("ID del usuario es requerido");
        }

        // 🚀 SANITIZAR DATOS
        System.out.println("🧹 SANITIZANDO DATOS:");
        salonDTO.setName(salonDTO.getName().trim());
        salonDTO.setAddress(salonDTO.getAddress().trim());
        salonDTO.setCity(salonDTO.getCity().trim());
        salonDTO.setEmail(salonDTO.getEmail().trim());

        // Manejar phoneNumber null
        if (salonDTO.getPhoneNumber() == null) {
            salonDTO.setPhoneNumber("");
            System.out.println("⚠️ phoneNumber era NULL, establecido a cadena vacía");
        }

        // Manejar images null
        if (salonDTO.getImages() == null || salonDTO.getImages().isEmpty()) {
            salonDTO.setImages(List.of(
                    "https://images.pexels.com/photos/3998415/pexels-photo-3998415.jpeg?auto=compress&cs=tinysrgb&w=600"));
            System.out.println("⚠️ images era NULL, establecido imagen por defecto");
        }

        // Manejar tiempos null
        if (salonDTO.getOpenTime() == null) {
            salonDTO.setOpenTime(LocalTime.of(9, 0));
            System.out.println("⚠️ openTime era NULL, establecido a 09:00");
        }
        if (salonDTO.getCloseTime() == null) {
            salonDTO.setCloseTime(LocalTime.of(18, 0));
            System.out.println("⚠️ closeTime era NULL, establecido a 18:00");
        }

        System.out.println("✅ DATOS SANITIZADOS - CREANDO SALÓN");

        try {
            // 🚀 CREAR EL SALÓN
            Salon salon = salonService.createSalon(salonDTO, user);
            System.out.println("✅ Salón creado exitosamente con ID: " + salon.getId());

            // 🚀 ACTUALIZAR ROL DEL USUARIO via Feign Client
            UserDTO updatedUser = null;
            try {
                if (cognitoSub != null && !cognitoSub.isEmpty()) {
                    updatedUser = userFeignClient.upgradeToSalonOwnerByCognitoId(cognitoSub, jwt).getBody();
                    System.out.println("✅ Usuario actualizado a SALON_OWNER por cognitoId: " + cognitoSub);
                } else {
                    updatedUser = userFeignClient.upgradeToSalonOwner(user.getId(), jwt).getBody();
                    System.out.println("✅ Usuario actualizado a SALON_OWNER por userId: " + user.getId());
                }
            } catch (Exception e) {
                System.err.println("⚠️ Error actualizando rol de usuario: " + e.getMessage());
                updatedUser = user; // Usar el usuario original si falla
            }

            // 🚀 CREAR RESPONSE DTO
            SalonDTO response = SalonMapper.mapToDTO(salon, updatedUser);

            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            System.err.println("❌ ERROR CREANDO SALÓN:");
            System.err.println("   Mensaje: " + e.getMessage());
            System.err.println("   Tipo: " + e.getClass().getSimpleName());
            e.printStackTrace();
            throw e;
        }
    }

    // =========================================================================
    // ACTUALIZAR SALÓN
    // =========================================================================
    @PutMapping("/{salonId}")
    public ResponseEntity<SalonDTO> updateSalon(
            @PathVariable Long salonId,
            @RequestBody Salon salon,
            @RequestHeader("Authorization") String jwt) throws Exception {

        System.out.println("🔧 Actualizando salón ID: " + salonId);

        Salon existingSalon = salonService.getSalonById(salonId);
        if (existingSalon == null) {
            throw new Exception("Salón no encontrado con ID: " + salonId);
        }

        Salon updatedSalon = salonService.updateSalon(salonId, salon);

        try {
            UserDTO owner = userFeignClient.getUserById(updatedSalon.getOwnerId()).getBody();
            SalonDTO salonDTO = SalonMapper.mapToDTO(updatedSalon, owner);
            return new ResponseEntity<>(salonDTO, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("⚠️ Error obteniendo propietario: " + e.getMessage());
            UserDTO basicOwner = createBasicOwnerDTO(updatedSalon.getOwnerId());
            SalonDTO salonDTO = SalonMapper.mapToDTO(updatedSalon, basicOwner);
            return new ResponseEntity<>(salonDTO, HttpStatus.OK);
        }
    }

    // =========================================================================
    // OBTENER TODOS LOS SALONES
    // =========================================================================
    @GetMapping
    public ResponseEntity<List<SalonDTO>> getAllSalons(
            @RequestHeader("Authorization") String jwt) throws Exception {

        System.out.println("📋 Obteniendo todos los salones");

        List<Salon> salons = salonService.getAllSalons();
        List<SalonDTO> salonDTOs = new ArrayList<>();

        for (Salon salon : salons) {
            try {
                UserDTO owner = userFeignClient.getUserById(salon.getOwnerId()).getBody();
                SalonDTO salonDTO = SalonMapper.mapToDTO(salon, owner);
                salonDTOs.add(salonDTO);
            } catch (Exception e) {
                System.err.println("⚠️ Error obteniendo owner para salón " + salon.getId() + ": " + e.getMessage());
                UserDTO basicOwner = createBasicOwnerDTO(salon.getOwnerId());
                SalonDTO salonDTO = SalonMapper.mapToDTO(salon, basicOwner);
                salonDTOs.add(salonDTO);
            }
        }

        return ResponseEntity.ok(salonDTOs);
    }

    // =========================================================================
    // OBTENER SALÓN POR ID
    // =========================================================================
    @GetMapping("/{salonId}")
    public ResponseEntity<SalonDTO> getSalonById(
            @PathVariable Long salonId,
            @RequestHeader("Authorization") String jwt) throws Exception {

        System.out.println("🔍 Obteniendo salón por ID: " + salonId);

        Salon salon = salonService.getSalonById(salonId);
        if (salon == null) {
            throw new Exception("Salón no encontrado con ID: " + salonId);
        }

        try {
            UserDTO owner = userFeignClient.getUserById(salon.getOwnerId()).getBody();
            SalonDTO salonDTO = SalonMapper.mapToDTO(salon, owner);
            return ResponseEntity.ok(salonDTO);
        } catch (Exception e) {
            System.err.println("⚠️ Error obteniendo propietario: " + e.getMessage());
            UserDTO basicOwner = createBasicOwnerDTO(salon.getOwnerId());
            SalonDTO salonDTO = SalonMapper.mapToDTO(salon, basicOwner);
            return ResponseEntity.ok(salonDTO);
        }
    }

    // =========================================================================
    // OBTENER SALÓN POR PROPIETARIO
    // =========================================================================
    @GetMapping("/owner")
    public ResponseEntity<SalonDTO> getSalonByOwner(
            @RequestHeader("Authorization") String jwt,
            @RequestHeader(value = "X-Cognito-Sub", required = false) String cognitoSub,
            @RequestHeader(value = "X-User-Email", required = false) String userEmail,
            @RequestHeader(value = "X-Auth-Source", required = false) String authSource) throws Exception {

        System.out.println("🔍 Buscando salón por propietario");
        System.out.println("🔍 Cognito Sub: " + cognitoSub);
        System.out.println("🔍 Auth Source: " + authSource);

        UserDTO user = null;

        if ("Cognito".equals(authSource) && cognitoSub != null && !cognitoSub.isEmpty()) {
            // Usuario desde Cognito
            try {
                user = userFeignClient.getUserByCognitoId(cognitoSub).getBody();
            } catch (Exception e) {
                System.out.println("❌ Usuario no encontrado con cognitoSub: " + cognitoSub);
                return ResponseEntity.notFound().build();
            }
        } else {
            // Usuario tradicional
            try {
                user = userFeignClient.getUserFromJwtToken(jwt).getBody();
            } catch (Exception e) {
                System.out.println("❌ Error obteniendo usuario tradicional: " + e.getMessage());
                return ResponseEntity.notFound().build();
            }
        }

        if (user == null) {
            System.out.println("❌ Usuario no encontrado");
            return ResponseEntity.notFound().build();
        }

        System.out.println("✅ Usuario encontrado: " + user.getEmail() + " (ID: " + user.getId() + ")");

        Salon salon = salonService.getSalonByOwnerId(user.getId());
        if (salon == null) {
            System.out.println("❌ Salón no encontrado para el usuario: " + user.getId());
            return ResponseEntity.notFound().build();
        }

        System.out.println("✅ Salón encontrado: " + salon.getName() + " (ID: " + salon.getId() + ")");

        SalonDTO salonDTO = SalonMapper.mapToDTO(salon, user);
        return ResponseEntity.ok(salonDTO);
    }

    // =========================================================================
    // BUSCAR SALONES POR CIUDAD
    // =========================================================================
    @GetMapping("/search")
    public ResponseEntity<List<SalonDTO>> searchSalons(
            @RequestParam String city,
            @RequestHeader("Authorization") String jwt) throws Exception {

        System.out.println("🔍 Buscando salones en ciudad: " + city);

        List<Salon> salons = salonService.searchSalonByCity(city);
        List<SalonDTO> salonDTOs = salons.stream()
                .map(salon -> {
                    try {
                        UserDTO owner = userFeignClient.getUserById(salon.getOwnerId()).getBody();
                        return SalonMapper.mapToDTO(salon, owner);
                    } catch (Exception e) {
                        System.err.println("⚠️ Error obteniendo owner para salón " + salon.getId());
                        UserDTO basicOwner = createBasicOwnerDTO(salon.getOwnerId());
                        return SalonMapper.mapToDTO(salon, basicOwner);
                    }
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(salonDTOs);
    }

    // =========================================================================
    // MÉTODOS AUXILIARES PRIVADOS
    // =========================================================================
    private UserDTO handleCognitoUserFromGateway(String cognitoSub, String email, String username, String role,
            String jwt) {
        try {
            System.out.println("🔍 Procesando usuario de Cognito:");
            System.out.println("   Sub: " + cognitoSub);
            System.out.println("   Email: " + email);
            System.out.println("   Username: " + username);
            System.out.println("   Role: " + role);

            // 1. Intentar obtener usuario existente
            try {
                UserDTO existingUser = userFeignClient.getUserByCognitoId(cognitoSub).getBody();
                if (existingUser != null) {
                    System.out.println("✅ Usuario existente encontrado con cognitoSub");
                    return existingUser;
                }
            } catch (Exception e) {
                System.out.println("🔍 Usuario no existe, creando nuevo...");
            }

            // 2. Si no existe, crear nuevo usuario
            System.out.println("🚀 Creando nuevo usuario desde Cognito");

            CreateUserFromCognitoRequest request = new CreateUserFromCognitoRequest();
            request.setCognitoUserId(cognitoSub);
            request.setEmail(email != null && !email.isEmpty() ? email : generateEmailFromCognito(cognitoSub));
            request.setFullName(
                    username != null && !username.isEmpty() ? username : generateUsernameFromCognito(cognitoSub));
            request.setRole(role != null ? role : "CUSTOMER");

            UserDTO newUser = userFeignClient.createUserFromCognito(request).getBody();
            System.out.println("✅ Usuario creado exitosamente con ID: " + newUser.getId());

            return newUser;

        } catch (Exception e) {
            System.err.println("❌ Error procesando usuario de Cognito: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private UserDTO createBasicOwnerDTO(Long ownerId) {
        UserDTO basicOwner = new UserDTO();
        basicOwner.setId(ownerId);
        basicOwner.setEmail("owner@unknown.com");
        basicOwner.setFullName("Owner " + ownerId);
        return basicOwner;
    }

    private String generateEmailFromCognito(String cognitoSub) {
        return cognitoSub.substring(0, Math.min(8, cognitoSub.length())) + "@cognito.generated";
    }

    private String generateUsernameFromCognito(String cognitoSub) {
        return "User " + cognitoSub.substring(0, Math.min(8, cognitoSub.length()));
    }
}