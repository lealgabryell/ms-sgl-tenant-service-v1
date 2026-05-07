package com.sgl.tenant.application.dto.request;

import com.sgl.tenant.domain.enums.TenantStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateTenantStatusRequest(

        @NotNull(message = "O status é obrigatório")
        TenantStatus status
) {
}

