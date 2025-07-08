// =============================================================================
// SALON SERVICE - Feign Client para comunicarse con USER service
// src/main/java/com/zosh/service/clients/UserFeignClient.java
// =============================================================================
package com.zosh.service.clients;

import com.zosh.payload.dto.UserDTO;
import com.zosh.payload.request.CreateUserFromCognitoRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient("USER")
public interface UserFeignClient {

        @GetMapping("/api/users/profile")
        ResponseEntity<UserDTO> getUserFromJwtToken(
                        @RequestHeader("Authorization") String jwt) throws Exception;

        @GetMapping("/api/users/{userId}")
        ResponseEntity<UserDTO> getUserById(
                        @PathVariable Long userId) throws Exception;

        @GetMapping("/api/users/by-cognito-id/{cognitoUserId}")
        ResponseEntity<UserDTO> getUserByCognitoId(
                        @PathVariable String cognitoUserId) throws Exception;

        @PutMapping("/api/users/{userId}/upgrade-to-salon-owner")
        ResponseEntity<UserDTO> upgradeToSalonOwner(
                        @PathVariable Long userId,
                        @RequestHeader("Authorization") String jwt) throws Exception;

        @PutMapping("/api/users/cognito/{cognitoUserId}/upgrade-to-salon-owner")
        ResponseEntity<UserDTO> upgradeToSalonOwnerByCognitoId(
                        @PathVariable String cognitoUserId,
                        @RequestHeader("Authorization") String jwt) throws Exception;

        @GetMapping("/api/users/{userId}/has-salon")
        ResponseEntity<Boolean> userHasSalon(
                        @PathVariable Long userId,
                        @RequestHeader("Authorization") String jwt) throws Exception;

        @PostMapping("/api/users/create-from-cognito")
        ResponseEntity<UserDTO> createUserFromCognito(
                        @RequestBody CreateUserFromCognitoRequest request) throws Exception;
}