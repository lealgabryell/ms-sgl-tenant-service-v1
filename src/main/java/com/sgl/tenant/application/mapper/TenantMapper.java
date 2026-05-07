package com.sgl.tenant.application.mapper;

import com.sgl.tenant.application.dto.response.TenantResponse;
import com.sgl.tenant.domain.entity.Tenant;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TenantMapper {

    public TenantResponse toResponse(Tenant tenant) {
        return new TenantResponse(
                tenant.getId(),
                tenant.getNome(),
                tenant.getCnpj(),
                tenant.getSchemaName(),
                tenant.getStatus(),
                tenant.getCreatedAt()
        );
    }

    public List<TenantResponse> toResponseList(List<Tenant> tenants) {
        return tenants.stream()
                .map(this::toResponse)
                .toList();
    }
}

