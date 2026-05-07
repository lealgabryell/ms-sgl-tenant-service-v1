package com.sgl.tenant.infrastructure.persistence.adapter;

import com.sgl.tenant.domain.entity.Tenant;
import com.sgl.tenant.domain.enums.TenantStatus;
import com.sgl.tenant.domain.repository.TenantRepository;
import com.sgl.tenant.infrastructure.persistence.entity.TenantJpaEntity;
import com.sgl.tenant.infrastructure.persistence.repository.TenantJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TenantRepositoryAdapter implements TenantRepository {

    private final TenantJpaRepository jpaRepository;

    @Override
    public Tenant save(Tenant tenant) {
        TenantJpaEntity entity = toJpaEntity(tenant);
        TenantJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Tenant> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Tenant> findByCnpj(String cnpj) {
        return jpaRepository.findByCnpj(cnpj).map(this::toDomain);
    }

    @Override
    public Optional<Tenant> findBySchemaName(String schemaName) {
        return jpaRepository.findBySchemaName(schemaName).map(this::toDomain);
    }

    @Override
    public List<Tenant> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Tenant> findAllByStatus(TenantStatus status) {
        return jpaRepository.findAllByStatus(status).stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsByCnpj(String cnpj) {
        return jpaRepository.existsByCnpj(cnpj);
    }

    @Override
    public boolean existsBySchemaName(String schemaName) {
        return jpaRepository.existsBySchemaName(schemaName);
    }

    private Tenant toDomain(TenantJpaEntity entity) {
        return Tenant.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .cnpj(entity.getCnpj())
                .schemaName(entity.getSchemaName())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private TenantJpaEntity toJpaEntity(Tenant tenant) {
        return TenantJpaEntity.builder()
                .id(tenant.getId())
                .nome(tenant.getNome())
                .cnpj(tenant.getCnpj())
                .schemaName(tenant.getSchemaName())
                .status(tenant.getStatus())
                .createdAt(tenant.getCreatedAt())
                .build();
    }
}

