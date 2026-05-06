package com.sgl.tenant.application.dto.response;

import com.sgl.tenant.domain.enums.TenantStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record TenantResponse(
        UUID id,
        String nome,
        String cnpj,
        String schemaName,
        TenantStatus status,
        LocalDateTime createdAt
) {
}

