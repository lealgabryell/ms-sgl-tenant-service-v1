package com.sgl.tenant.application.usecase;

import com.sgl.tenant.application.dto.request.CreateTenantRequest;
import com.sgl.tenant.domain.entity.Tenant;
import com.sgl.tenant.domain.enums.TenantStatus;
import com.sgl.tenant.domain.repository.TenantRepository;
import com.sgl.tenant.domain.service.TenantSchemaProvisioningService;
import com.sgl.tenant.infrastructure.exception.TenantAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateTenantUseCase {

    private final TenantRepository tenantRepository;
    private final TenantSchemaProvisioningService schemaProvisioningService;

    @Transactional
    public Tenant execute(CreateTenantRequest request) {
        log.info("Creating tenant with CNPJ: {}", request.cnpj());

        if (tenantRepository.existsByCnpj(request.cnpj())) {
            throw new TenantAlreadyExistsException("Já existe um tenant com o CNPJ: " + request.cnpj());
        }

        if (tenantRepository.existsBySchemaName(request.schemaName())) {
            throw new TenantAlreadyExistsException("Já existe um tenant com o schemaName: " + request.schemaName());
        }

        Tenant tenant = Tenant.builder()
                .id(UUID.randomUUID())
                .nome(request.nome())
                .cnpj(request.cnpj())
                .schemaName(request.schemaName())
                .status(TenantStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        Tenant saved = tenantRepository.save(tenant);
        log.info("Tenant created successfully with id: {}", saved.getId());

        schemaProvisioningService.createSchema(saved.getSchemaName());

        return saved;
    }
}

