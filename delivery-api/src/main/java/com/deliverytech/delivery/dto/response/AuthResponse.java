package com.deliverytech.delivery.dto.response;

public class AuthResponse {

    private String token;
    private String email;
    private String nome;
    private String role;

    public AuthResponse(String token, String email, String nome, String role) {
        this.token = token;
        this.email = email;
        this.nome = nome;
        this.role = role;
    }

    // Getters e setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
