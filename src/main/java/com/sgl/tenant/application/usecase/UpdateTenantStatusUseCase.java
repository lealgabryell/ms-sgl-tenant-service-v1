package com.sgl.tenant.application.usecase;

import com.sgl.tenant.domain.entity.Tenant;
import com.sgl.tenant.domain.enums.TenantStatus;
import com.sgl.tenant.domain.repository.TenantRepository;
import com.sgl.tenant.infrastructure.exception.TenantNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateTenantStatusUseCase {

    private final TenantRepository tenantRepository;

    @Transactional
    public Tenant execute(UUID id, TenantStatus newStatus) {
        log.info("Updating tenant status. id: {}, newStatus: {}", id, newStatus);

        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new TenantNotFoundException("Tenant não encontrado com id: " + id));

        tenant.setStatus(newStatus);
        Tenant updated = tenantRepository.save(tenant);

        log.info("Tenant status updated successfully. id: {}, status: {}", updated.getId(), updated.getStatus());
        return updated;
    }
}

