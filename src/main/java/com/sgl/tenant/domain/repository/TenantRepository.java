package com.sgl.tenant.domain.repository;

import com.sgl.tenant.domain.entity.Tenant;
import com.sgl.tenant.domain.enums.TenantStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TenantRepository {

    Tenant save(Tenant tenant);

    Optional<Tenant> findById(UUID id);

    Optional<Tenant> findByCnpj(String cnpj);

    Optional<Tenant> findBySchemaName(String schemaName);

    List<Tenant> findAll();

    List<Tenant> findAllByStatus(TenantStatus status);

    boolean existsByCnpj(String cnpj);

    boolean existsBySchemaName(String schemaName);
}

