package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.model.Cliente;
import com.deliverytech.delivery.service.ClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    private static final String PREFIXO_ERRO = "Erro: ";
    private static final String ERRO_INTERNAL = "Error interno do servidor";

    private final ClienteService clienteService;
    
    ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }
    /**
     * Cadastrar novo cliente
     */
    @PostMapping
    public ResponseEntity<Object> cadastrar(@Valid @RequestBody Cliente cliente) {
        try {
            Cliente clienteSalvo = clienteService.cadastrar(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(clienteSalvo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(PREFIXO_ERRO + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ERRO_INTERNAL);
        }
    }
 

    /**
     * Listar todos os clientes ativos
     */
    @GetMapping
    public ResponseEntity<Object> listarAtivos() {
        try {
            return ResponseEntity.ok(clienteService.listarAtivos());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ERRO_INTERNAL);
        }
    }

    /*
     * Buscar cliente por email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<Object> buscarPorEmail(@PathVariable String email) {
        try {
            Optional<Cliente> cliente = clienteService.buscarPorEmail(email);
            if (cliente.isPresent()) {
                return ResponseEntity.ok(cliente.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(PREFIXO_ERRO + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ERRO_INTERNAL);
        }
    }

    /*
     * busca por nome parcial
     */
    @GetMapping("/nome/{nome}")
    public ResponseEntity<Object> buscarPorNome(@PathVariable String nome) {
        try {
            List<Cliente> clientes = clienteService.buscarPorNome(nome);
            if (clientes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(PREFIXO_ERRO + "Nenhum cliente encontrado com o nome: " + nome);
            }
            return ResponseEntity.ok(clientes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ERRO_INTERNAL);
        }
    }   

    /*
     * verifica se o email já está cadastrado
     */
    @GetMapping("/email/cadastrado/{email}")
    public ResponseEntity<Object> emailJaCadastrado(@PathVariable String email) {
        try {
            boolean cadastrado = clienteService.emailJaCadastrado(email);
            return ResponseEntity.ok(cadastrado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ERRO_INTERNAL);
        }
    }

    /**
     * Buscar cliente por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarPorId(@PathVariable Long id) {
        Optional<Cliente> cliente = clienteService.buscarPorId(id);

        if (cliente.isPresent()) {
            return ResponseEntity.ok(cliente.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    /**
     * Atualizar cliente
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> atualizar(@PathVariable Long id,
                                      @Valid @RequestBody Cliente cliente) {
        try {
            Cliente clienteAtualizado = clienteService.atualizar(id, cliente);
            return ResponseEntity.ok(clienteAtualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(PREFIXO_ERRO + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ERRO_INTERNAL);
        }
    }
    /**
     * Inativar cliente (soft delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> inativar(@PathVariable Long id) {
        try {
            clienteService.inativar(id);
            return ResponseEntity.ok().body("Cliente inativado com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(PREFIXO_ERRO + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ERRO_INTERNAL);
        }
    }
    /**
     * Reativar cliente
     */
    @PutMapping("/{id}/reativar")
    public ResponseEntity<Object> reativar(@PathVariable Long id) {
        try {
            clienteService.reativar(id);
            return ResponseEntity.ok("Cliente reativado com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(PREFIXO_ERRO + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ERRO_INTERNAL);
        }
        
    }

}