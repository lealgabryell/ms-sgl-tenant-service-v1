package com.sgl.tenant.infrastructure.persistence.repository;

import com.sgl.tenant.domain.enums.TenantStatus;
import com.sgl.tenant.infrastructure.persistence.entity.TenantJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantJpaRepository extends JpaRepository<TenantJpaEntity, UUID> {

    Optional<TenantJpaEntity> findByCnpj(String cnpj);

    Optional<TenantJpaEntity> findBySchemaName(String schemaName);

    List<TenantJpaEntity> findAllByStatus(TenantStatus status);

    boolean existsByCnpj(String cnpj);

    boolean existsBySchemaName(String schemaName);
}

