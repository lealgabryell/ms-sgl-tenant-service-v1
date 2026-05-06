package com.sgl.tenant.application.usecase;

import com.sgl.tenant.domain.entity.Tenant;
import com.sgl.tenant.domain.repository.TenantRepository;
import com.sgl.tenant.infrastructure.exception.TenantNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindTenantUseCase {

    private final TenantRepository tenantRepository;

    @Transactional(readOnly = true)
    public Tenant findById(UUID id) {
        log.info("Finding tenant by id: {}", id);
        return tenantRepository.findById(id)
                .orElseThrow(() -> new TenantNotFoundException("Tenant não encontrado com id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Tenant> findAll() {
        log.info("Listing all tenants");
        return tenantRepository.findAll();
    }
}

