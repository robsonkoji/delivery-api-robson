package com.deliverytech.delivery.dto.response;

public class UserResponse {

    private Long id;
    private String nome;
    private String email;
    private String role;

    private UserResponse() {
        // Construtor privado para frameworks (ex: Jackson)
    }

    private UserResponse(Long id, String nome, String email, String role) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.role = role;
    }

    // Método estático de fábrica para criar a instância
    public static UserResponse of(Long id, String nome, String email, String role) {
        return new UserResponse(id, nome, email, role);
    }

    // Getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
