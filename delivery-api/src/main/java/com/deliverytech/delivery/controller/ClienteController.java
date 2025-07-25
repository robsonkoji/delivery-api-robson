package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.dto.request.ClienteRequest;
import com.deliverytech.delivery.entity.Cliente;
import com.deliverytech.delivery.service.ClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequestMapping("/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    

    private final ClienteService clienteService;
    
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    /**
     * Método para cadastrar um novo cliente.
     * @param cliente Cliente a ser cadastrado.
     * @return ResponseEntity com o cliente cadastrado e status CREATED.
     */
    @PostMapping
    public ResponseEntity<Cliente> cadastrarCliente(@RequestBody ClienteRequest request) {
        Cliente cliente = clienteService.cadastrarCliente(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
    }

    /**
     * Método para buscar um cliente por ID.
     * @param id ID do cliente a ser buscado.
     * @return ResponseEntity com o cliente encontrado ou erro 404 se não encontrado.
     */
   @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable Long id) {
        Cliente cliente = clienteService.buscarClientePorId(id);
        return ResponseEntity.ok(cliente);
    }

    /**
     * Método para buscar um cliente por email.
     * @param email Email do cliente a ser buscado.
     * @return ResponseEntity com o cliente encontrado ou erro 404 se não encontrado.
     */
    @GetMapping("/email")
    public ResponseEntity<Cliente> buscarPorEmail(@RequestParam String email) {
        Cliente cliente = clienteService.buscarClientePorEmail(email);
        return ResponseEntity.ok(cliente);
    }

    /**
     * Método para atualizar um cliente existente.
     * @param id ID do cliente a ser atualizado.
     * @param dto Dados do cliente a serem atualizados.
     * @return ResponseEntity com o cliente atualizado ou erro 404 se não encontrado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> atualizarCliente(@PathVariable Long id, @RequestBody ClienteRequest request) {
        Cliente clienteAtualizado = clienteService.atualizarCliente(id, request);
        return ResponseEntity.ok(clienteAtualizado);
    }

    /**
     * Método para ativar ou desativar um cliente.
     * @param id ID do cliente a ser ativado/desativado.
     * @return ResponseEntity com o cliente atualizado ou erro 404 se não encontrado.
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Cliente> alterarStatus(@PathVariable Long id) {
        Cliente cliente = clienteService.ativarDesativarCliente(id);
        return ResponseEntity.ok(cliente);
    }

    /**
     * Método para listar todos os clientes ativos.
     * @return ResponseEntity com a lista de clientes ativos.
     */
    @GetMapping("/ativos")
    public ResponseEntity<List<Cliente>> listarAtivos() {
        List<Cliente> ativos = clienteService.listarClientesAtivos();
        return ResponseEntity.ok(ativos);
    }

}