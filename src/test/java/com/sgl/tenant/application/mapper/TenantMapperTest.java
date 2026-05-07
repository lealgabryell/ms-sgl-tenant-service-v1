package com.sgl.tenant.application.mapper;

import com.sgl.tenant.application.dto.response.TenantResponse;
import com.sgl.tenant.domain.entity.Tenant;
import com.sgl.tenant.domain.enums.TenantStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TenantMapper")
class TenantMapperTest {

    private final TenantMapper mapper = new TenantMapper();

    private Tenant buildTenant() {
        return Tenant.builder()
                .id(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .nome("Lava-Jato Modelo")
                .cnpj("12345678000199")
                .schemaName("lavajato_modelo")
                .status(TenantStatus.ACTIVE)
                .createdAt(LocalDateTime.of(2024, 1, 15, 10, 0, 0))
                .build();
    }

    @Test
    @DisplayName("deve mapear Tenant para TenantResponse corretamente")
    void shouldMapTenantToResponse() {
        Tenant tenant = buildTenant();

        TenantResponse response = mapper.toResponse(tenant);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(tenant.getId());
        assertThat(response.nome()).isEqualTo(tenant.getNome());
        assertThat(response.cnpj()).isEqualTo(tenant.getCnpj());
        assertThat(response.schemaName()).isEqualTo(tenant.getSchemaName());
        assertThat(response.status()).isEqualTo(TenantStatus.ACTIVE);
        assertThat(response.createdAt()).isEqualTo(tenant.getCreatedAt());
    }

    @Test
    @DisplayName("deve mapear lista de Tenants para lista de TenantResponse")
    void shouldMapTenantListToResponseList() {
        List<Tenant> tenants = List.of(buildTenant(), buildTenant());

        List<TenantResponse> responses = mapper.toResponseList(tenants);

        assertThat(responses).hasSize(2);
        assertThat(responses).allSatisfy(r -> {
            assertThat(r.nome()).isEqualTo("Lava-Jato Modelo");
            assertThat(r.status()).isEqualTo(TenantStatus.ACTIVE);
        });
    }

    @Test
    @DisplayName("deve mapear lista vazia para lista vazia")
    void shouldMapEmptyListToEmptyList() {
        List<TenantResponse> responses = mapper.toResponseList(List.of());

        assertThat(responses).isEmpty();
    }
}

