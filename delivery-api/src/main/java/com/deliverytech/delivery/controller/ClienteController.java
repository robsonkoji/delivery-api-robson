package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.dto.request.ClienteRequest;
import com.deliverytech.delivery.entity.Cliente;
import com.deliverytech.delivery.service.ClienteService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequestMapping("/api/clientes")
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
    public ResponseEntity<Cliente> cadastrarCliente(@RequestBody @Valid ClienteRequest request) {
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

    /**
     * Método para buscar todos os clientes.
     * @return ResponseEntity com a lista de todos os clientes.
     */
    @GetMapping
    public ResponseEntity<List<Cliente>> listarTodos() {
        List<Cliente> clientes = clienteService.listarClientesAtivos();
        return ResponseEntity.ok(clientes);
    }

    /**
     * Método para buscar clientes por nome.
     * @param nome Nome do cliente a ser buscado.
     * @return ResponseEntity com a lista de clientes encontrados.
     */
    @GetMapping("/nome")
    public ResponseEntity<List<Cliente>> buscarPorNome(@RequestParam String nome) {
        List<Cliente> clientes = clienteService.buscarClientesPorNome(nome);
        return ResponseEntity.ok(clientes); 
    }

    /**
     * Método para buscar clientes por telefone.
     * @param telefone Telefone do cliente a ser buscado.
     * @return ResponseEntity com a lista de clientes encontrados.
     */
    @GetMapping("/buscar-por-telefone")
    public ResponseEntity<List<Cliente>> buscarPorTelefone(@RequestParam String telefone) {
        List<Cliente> clientes = clienteService.buscarClientesPorTelefone(telefone);
        return ResponseEntity.ok(clientes); 
    }
    /**
     * Método para buscar clientes por endereço.
     * @param endereco Endereço do cliente a ser buscado.
     * @return ResponseEntity com a lista de clientes encontrados.
     */
    @GetMapping("/buscar-por-endereco")
    public ResponseEntity<List<Cliente>> buscarPorEndereco(@RequestParam String endereco) {
        List<Cliente> clientes = clienteService.buscarClientesPorEndereco(endereco);
        return ResponseEntity.ok(clientes); 
    }

    /**
     * Intivar um cliente.
     * @param id ID do cliente a ser inativado.
     * @return ResponseEntity com o cliente inativado ou erro 404 se não encontrado.
     */
    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Cliente> inativarCliente(@PathVariable Long id) {
        Cliente cliente = clienteService.inativarCliente(id);
        return ResponseEntity.ok(cliente);
    }   
    /**
     * reativar um cliente.
     * @param id ID do cliente a ser reativado.
     * @return ResponseEntity com o cliente reativado ou erro 404 se não encontrado.
     */
    @PatchMapping("/{id}/reativar")
    public ResponseEntity<Cliente> reativarCliente(@PathVariable Long id) {
        Cliente cliente = clienteService.ativarCliente(id);
        return ResponseEntity.ok(cliente);
    }
}
  