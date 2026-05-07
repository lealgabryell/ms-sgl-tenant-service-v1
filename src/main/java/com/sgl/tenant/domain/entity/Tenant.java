package com.sgl.tenant.domain.entity;

import com.sgl.tenant.domain.enums.TenantStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tenant {

    private UUID id;
    private String nome;
    private String cnpj;
    private String schemaName;
    private TenantStatus status;
    private LocalDateTime createdAt;
}

