package com.sgl.tenant.web.controller;

import com.sgl.tenant.application.dto.request.CreateTenantRequest;
import com.sgl.tenant.application.dto.request.UpdateTenantStatusRequest;
import com.sgl.tenant.application.dto.response.TenantResponse;
import com.sgl.tenant.application.mapper.TenantMapper;
import com.sgl.tenant.application.usecase.CreateTenantUseCase;
import com.sgl.tenant.application.usecase.FindTenantUseCase;
import com.sgl.tenant.application.usecase.UpdateTenantStatusUseCase;
import com.sgl.tenant.domain.entity.Tenant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
@Tag(name = "Tenants", description = "Gerenciamento de Tenants (Lava-Jatos)")
public class TenantController {

    private final CreateTenantUseCase createTenantUseCase;
    private final FindTenantUseCase findTenantUseCase;
    private final UpdateTenantStatusUseCase updateTenantStatusUseCase;
    private final TenantMapper tenantMapper;

    @PostMapping
    @Operation(summary = "Criar novo Tenant", description = "Provisiona um novo tenant (lava-jato) no sistema.")
    public ResponseEntity<TenantResponse> create(@Valid @RequestBody CreateTenantRequest request) {
        Tenant tenant = createTenantUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(tenantMapper.toResponse(tenant));
    }

    @GetMapping
    @Operation(summary = "Listar todos os Tenants", description = "Retorna a lista completa de tenants cadastrados.")
    public ResponseEntity<List<TenantResponse>> findAll() {
        List<Tenant> tenants = findTenantUseCase.findAll();
        return ResponseEntity.ok(tenantMapper.toResponseList(tenants));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar Tenant por ID", description = "Retorna os dados de um tenant pelo seu UUID.")
    public ResponseEntity<TenantResponse> findById(@PathVariable UUID id) {
        Tenant tenant = findTenantUseCase.findById(id);
        return ResponseEntity.ok(tenantMapper.toResponse(tenant));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status do Tenant", description = "Ativa ou inativa um tenant existente.")
    public ResponseEntity<TenantResponse> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTenantStatusRequest request) {
        Tenant updated = updateTenantStatusUseCase.execute(id, request.status());
        return ResponseEntity.ok(tenantMapper.toResponse(updated));
    }
}

