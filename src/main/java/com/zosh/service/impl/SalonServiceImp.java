package com.zosh.service.impl;

import com.zosh.modal.Salon;
import com.zosh.payload.dto.SalonDTO;
import com.zosh.payload.dto.UserDTO;
import com.zosh.repository.SalonRepository;
import com.zosh.service.SalonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SalonServiceImp implements SalonService {

    private final SalonRepository salonRepository;

    @Override
    public Salon createSalon(SalonDTO req, UserDTO user) {
        System.out.println("🔧 =========================");
        System.out.println("🔧 SALON SERVICE IMPL - CREATE SALON");
        System.out.println("🔧 =========================");

        System.out.println("📋 DATOS RECIBIDOS EN SERVICE:");
        System.out.println("   SalonDTO.name: '" + req.getName() + "'");
        System.out.println("   SalonDTO.address: '" + req.getAddress() + "'");
        System.out.println("   SalonDTO.city: '" + req.getCity() + "'");
        System.out.println("   SalonDTO.phoneNumber: '" + req.getPhoneNumber() + "'");
        System.out.println("   SalonDTO.email: '" + req.getEmail() + "'");
        System.out.println("   SalonDTO.openTime: " + req.getOpenTime());
        System.out.println("   SalonDTO.closeTime: " + req.getCloseTime());
        System.out.println(
                "   SalonDTO.images: " + (req.getImages() != null ? req.getImages().size() + " items" : "NULL"));
        System.out.println("   UserDTO.id: " + user.getId());
        System.out.println("   UserDTO.email: " + user.getEmail());

        Salon salon = new Salon();

        // 🚀 SETEAR CADA CAMPO CON VALIDACIÓN
        System.out.println("🔧 SETEANDO CAMPOS DEL SALÓN:");

        salon.setName(req.getName());
        System.out.println("   ✅ name: '" + salon.getName() + "'");

        salon.setImages(req.getImages());
        System.out
                .println("   ✅ images: " + (salon.getImages() != null ? salon.getImages().size() + " items" : "NULL"));

        salon.setCity(req.getCity());
        System.out.println("   ✅ city: '" + salon.getCity() + "'");

        salon.setAddress(req.getAddress());
        System.out.println("   ✅ address: '" + salon.getAddress() + "'");

        salon.setEmail(req.getEmail());
        System.out.println("   ✅ email: '" + salon.getEmail() + "'");

        // 🚀 MANEJO ESPECIAL DE PHONE
        String phone = req.getPhoneNumber();
        if (phone == null || phone.trim().isEmpty()) {
            phone = "";
            System.out.println("   ⚠️ phoneNumber era NULL/vacío, usando cadena vacía");
        }
        salon.setPhoneNumber(phone);
        System.out.println("   ✅ phoneNumber: '" + salon.getPhoneNumber() + "'");

        salon.setOpenTime(req.getOpenTime());
        System.out.println("   ✅ openTime: " + salon.getOpenTime());

        salon.setCloseTime(req.getCloseTime());
        System.out.println("   ✅ closeTime: " + salon.getCloseTime());

        salon.setHomeService(true);
        System.out.println("   ✅ homeService: " + salon.isHomeService());

        salon.setOpen(true);
        System.out.println("   ✅ isOpen: " + salon.isOpen());

        salon.setOwnerId(user.getId());
        System.out.println("   ✅ ownerId: " + salon.getOwnerId());

        salon.setActive(true);
        System.out.println("   ✅ active: " + salon.isActive());

        // 🚀 VALIDACIÓN FINAL ANTES DE GUARDAR
        System.out.println("🔍 VALIDACIÓN FINAL ANTES DE GUARDAR:");
        System.out.println("   name != null: " + (salon.getName() != null));
        System.out.println("   address != null: " + (salon.getAddress() != null));
        System.out.println("   city != null: " + (salon.getCity() != null));
        System.out.println("   email != null: " + (salon.getEmail() != null));
        System.out.println("   phoneNumber != null: " + (salon.getPhoneNumber() != null));
        System.out.println("   ownerId != null: " + (salon.getOwnerId() != null));
        System.out.println("   openTime != null: " + (salon.getOpenTime() != null));
        System.out.println("   closeTime != null: " + (salon.getCloseTime() != null));

        try {
            System.out.println("💾 INTENTANDO GUARDAR EN REPOSITORY...");
            Salon savedSalon = salonRepository.save(salon);
            System.out.println("✅ SALÓN GUARDADO EXITOSAMENTE CON ID: " + savedSalon.getId());
            return savedSalon;
        } catch (Exception e) {
            System.err.println("❌ ERROR GUARDANDO SALÓN:");
            System.err.println("   Error type: " + e.getClass().getSimpleName());
            System.err.println("   Error message: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("   Cause: " + e.getCause().getMessage());
            }
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public Salon updateSalon(Long salonId, Salon salon) throws Exception {

        Salon existingSalon = getSalonById(salonId);
        if (existingSalon != null) {

            existingSalon.setName(salon.getName());
            existingSalon.setAddress(salon.getAddress());
            existingSalon.setPhoneNumber(salon.getPhoneNumber());
            existingSalon.setEmail(salon.getEmail());
            existingSalon.setCity(salon.getCity());
            existingSalon.setOpen(salon.isOpen());
            existingSalon.setHomeService(salon.isHomeService());
            existingSalon.setActive(salon.isActive());
            existingSalon.setOpenTime(salon.getOpenTime());
            existingSalon.setCloseTime(salon.getCloseTime());

            return salonRepository.save(existingSalon);
        }
        throw new Exception("salon not exist");
    }

    @Override
    public List<Salon> getAllSalons() {
        return salonRepository.findAll();
    }

    @Override
    public Salon getSalonById(Long salonId) {
        return salonRepository.findById(salonId).orElse(null);
    }

    @Override
    public Salon getSalonByOwnerId(Long ownerId) {
        return salonRepository.findByOwnerId(ownerId);
    }

    @Override
    public List<Salon> searchSalonByCity(String city) {
        return salonRepository.searchSalons(city);
    }
@Override
    public List<String> getAllCities() {
        System.out.println("🏙️ SERVICE - Obteniendo todas las ciudades");
        
        try {
            // Obtener todas las ciudades únicas de los salones activos
            List<Salon> salons = salonRepository.findAll();
            
            List<String> cities = salons.stream()
                .filter(salon -> salon.getCity() != null && !salon.getCity().trim().isEmpty())
                .map(salon -> salon.getCity().trim())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
                
            System.out.println("✅ Ciudades encontradas: " + cities);
            return cities;
            
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo ciudades: " + e.getMessage());
            // Retornar ciudades por defecto en caso de error
            return Arrays.asList("Santiago", "Valparaíso", "Concepción", "La Serena", "Temuco");
        }
    }

    @Override
    public List<Salon> searchWithFilters(String city, String salonName, boolean homeService) {
        System.out.println("🔍 SERVICE - Búsqueda con filtros");
        System.out.println("   City: " + city);
        System.out.println("   Salon Name: " + salonName);
        System.out.println("   Home Service: " + homeService);
        
        try {
            List<Salon> allSalons = salonRepository.findAll();
            
            List<Salon> filteredSalons = allSalons.stream()
                .filter(salon -> {
                    // Filtro por ciudad
                    if (city != null && !city.trim().isEmpty()) {
                        if (salon.getCity() == null || 
                            !salon.getCity().toLowerCase().contains(city.toLowerCase())) {
                            return false;
                        }
                    }
                    
                    // Filtro por nombre del salón
                    if (salonName != null && !salonName.trim().isEmpty()) {
                        if (salon.getName() == null || 
                            !salon.getName().toLowerCase().contains(salonName.toLowerCase())) {
                            return false;
                        }
                    }
                    
                    // Filtro por servicio a domicilio
                    if (homeService && !salon.isHomeService()) {
                        return false;
                    }
                    
                    // Solo salones activos
                    return salon.isActive();
                })
                .collect(Collectors.toList());
                
            System.out.println("✅ SERVICE - Encontrados " + filteredSalons.size() + " salones");
            return filteredSalons;
            
        } catch (Exception e) {
            System.err.println("❌ Error en búsqueda con filtros: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
