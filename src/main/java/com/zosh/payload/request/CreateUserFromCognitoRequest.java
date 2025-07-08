// =============================================================================
// SALON SERVICE - DTO simple para comunicación con USER service
// src/main/java/com/zosh/payload/request/CreateUserFromCognitoRequest.java
// =============================================================================
package com.zosh.payload.request;

public class CreateUserFromCognitoRequest {
    private String cognitoUserId;
    private String email;
    private String fullName;
    private String role;

    // Constructores
    public CreateUserFromCognitoRequest() {}

    public CreateUserFromCognitoRequest(String cognitoUserId, String email, String fullName, String role) {
        this.cognitoUserId = cognitoUserId;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }

    // Getters y setters
    public String getCognitoUserId() {
        return cognitoUserId;
    }

    public void setCognitoUserId(String cognitoUserId) {
        this.cognitoUserId = cognitoUserId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "CreateUserFromCognitoRequest{" +
                "cognitoUserId='" + cognitoUserId + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}