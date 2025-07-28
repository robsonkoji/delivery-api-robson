package com.deliverytech.delivery.dto.response;

import lombok.Data;

@Data
public class ClienteResponse {

    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String endereco;
    private boolean ativo;
    private java.time.LocalDateTime dataCriacao;
}
