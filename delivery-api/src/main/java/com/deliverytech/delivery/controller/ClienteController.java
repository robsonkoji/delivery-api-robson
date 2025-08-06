package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.dto.request.ClienteRequest;
import com.deliverytech.delivery.dto.response.ClienteResponse;
import com.deliverytech.delivery.dto.response.common.ApiWrapperResponse;
import com.deliverytech.delivery.dto.response.common.UtilsResponse;
import com.deliverytech.delivery.entity.Cliente;
import com.deliverytech.delivery.mapper.ClienteMapper;
import com.deliverytech.delivery.service.ClienteService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
@Tag(name = "Clientes", description = "Operações relacionadas a clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final ClienteMapper mapper;

    public ClienteController(ClienteService clienteService, ClienteMapper mapper) {
        this.clienteService = clienteService;
        this.mapper = mapper;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Cadastrar um novo cliente")
    @ApiResponse(responseCode = "201", description = "Cliente cadastrado com sucesso",
        content = @Content(schema = @Schema(implementation = ClienteResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    public ResponseEntity<ApiWrapperResponse<ClienteResponse>> cadastrarCliente(
            @Valid @RequestBody ClienteRequest request) {
        Cliente cliente = clienteService.cadastrarCliente(request);
        ClienteResponse response = mapper.toResponse(cliente);
        return ResponseEntity.created(URI.create("/api/clientes/" + cliente.getId()))
                .body(UtilsResponse.created(response));
    }

    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente por ID")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado",
        content = @Content(schema = @Schema(implementation = ClienteResponse.class)))
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content)
    public ResponseEntity<ApiWrapperResponse<ClienteResponse>> buscarPorId(
        @Parameter(description = "ID do cliente", example = "1") @PathVariable Long id) {
    Cliente cliente = clienteService.buscarClientePorId(id);
    ClienteResponse response = mapper.toResponse(cliente);
    return ResponseEntity.ok(UtilsResponse.success(response));
}

    @PreAuthorize("hasRole('ADMIN') or #email == principal.email")
    @GetMapping("/email")
    @Operation(summary = "Buscar cliente por e-mail")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado",
        content = @Content(schema = @Schema(implementation = ClienteResponse.class)))
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content)
    public ResponseEntity<ApiWrapperResponse<ClienteResponse>> buscarPorEmail(
            @Parameter(description = "E-mail do cliente", example = "joao@email.com") @RequestParam String email) {
        Cliente cliente = clienteService.buscarClientePorEmail(email);
        return ResponseEntity.ok(UtilsResponse.success(mapper.toResponse(cliente)));
    }

    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cliente")
    @ApiResponse(responseCode = "200", description = "Cliente atualizado",
        content = @Content(schema = @Schema(implementation = ClienteResponse.class)))
    public ResponseEntity<ApiWrapperResponse<ClienteResponse>> atualizarCliente(
            @Parameter(description = "ID do cliente", example = "1") @PathVariable Long id,
            @RequestBody ClienteRequest request) {
        Cliente clienteAtualizado = clienteService.atualizarCliente(id, request);
        return ResponseEntity.ok(UtilsResponse.success(mapper.toResponse(clienteAtualizado)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    @Operation(summary = "Ativar ou desativar cliente")
    @ApiResponse(responseCode = "200", description = "Status alterado com sucesso",
        content = @Content(schema = @Schema(implementation = ClienteResponse.class)))
    public ResponseEntity<ApiWrapperResponse<ClienteResponse>> alterarStatus(
            @Parameter(description = "ID do cliente", example = "1") @PathVariable Long id) {
        Cliente cliente = clienteService.ativarDesativarCliente(id);
        return ResponseEntity.ok(UtilsResponse.success(mapper.toResponse(cliente)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/ativos")
    @Operation(summary = "Listar todos os clientes ativos")
    @ApiResponse(responseCode = "200", description = "Clientes ativos listados",
        content = @Content(schema = @Schema(implementation = ClienteResponse.class)))
    public ResponseEntity<ApiWrapperResponse<List<ClienteResponse>>> listarAtivos() {
        List<Cliente> ativos = clienteService.listarClientesAtivos();
        List<ClienteResponse> responses = ativos.stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(UtilsResponse.success(responses));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Operation(summary = "Listar todos os clientes")
    @ApiResponse(responseCode = "200", description = "Lista de clientes",
        content = @Content(schema = @Schema(implementation = ClienteResponse.class)))
    public ResponseEntity<ApiWrapperResponse<List<ClienteResponse>>> listarTodos() {
        List<Cliente> clientes = clienteService.listarTodosClientes();
        List<ClienteResponse> responses = clientes.stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(UtilsResponse.success(responses));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/nome")
    @Operation(summary = "Buscar clientes por nome")
    @ApiResponse(responseCode = "200", description = "Clientes encontrados",
        content = @Content(schema = @Schema(implementation = ClienteResponse.class)))
    public ResponseEntity<ApiWrapperResponse<List<ClienteResponse>>> buscarPorNome(
            @Parameter(description = "Nome do cliente", example = "João da Silva") @RequestParam String nome) {
        List<Cliente> clientes = clienteService.buscarClientesPorNome(nome);
        List<ClienteResponse> responses = clientes.stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(UtilsResponse.success(responses));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/buscar-por-telefone")
    @Operation(summary = "Buscar clientes por telefone")
    @ApiResponse(responseCode = "200", description = "Clientes encontrados",
        content = @Content(schema = @Schema(implementation = ClienteResponse.class)))
    public ResponseEntity<ApiWrapperResponse<List<ClienteResponse>>> buscarPorTelefone(
            @Parameter(description = "Telefone", example = "11999998888") @RequestParam String telefone) {
        List<Cliente> clientes = clienteService.buscarClientesPorTelefone(telefone);
        List<ClienteResponse> responses = clientes.stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(UtilsResponse.success(responses));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/buscar-por-endereco")
    @Operation(summary = "Buscar clientes por endereço")
    @ApiResponse(responseCode = "200", description = "Clientes encontrados",
        content = @Content(schema = @Schema(implementation = ClienteResponse.class)))
    public ResponseEntity<ApiWrapperResponse<List<ClienteResponse>>> buscarPorEndereco(
            @Parameter(description = "Endereço", example = "Rua das Flores, 123") @RequestParam String endereco) {
        List<Cliente> clientes = clienteService.buscarClientesPorEndereco(endereco);
        List<ClienteResponse> responses = clientes.stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(UtilsResponse.success(responses));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/inativar")
    @Operation(summary = "Inativar cliente")
    @ApiResponse(responseCode = "200", description = "Cliente inativado",
        content = @Content(schema = @Schema(implementation = ClienteResponse.class)))
    public ResponseEntity<ApiWrapperResponse<ClienteResponse>> inativarCliente(
            @Parameter(description = "ID do cliente", example = "1") @PathVariable Long id) {
        Cliente cliente = clienteService.inativarCliente(id);
        return ResponseEntity.ok(UtilsResponse.success(mapper.toResponse(cliente)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/reativar")
    @Operation(summary = "Reativar cliente")
    @ApiResponse(responseCode = "200", description = "Cliente reativado",
        content = @Content(schema = @Schema(implementation = ClienteResponse.class)))
    public ResponseEntity<ApiWrapperResponse<ClienteResponse>> reativarCliente(
            @Parameter(description = "ID do cliente", example = "1") @PathVariable Long id) {
        Cliente cliente = clienteService.ativarCliente(id);
        return ResponseEntity.ok(UtilsResponse.success(mapper.toResponse(cliente)));
    }
}
