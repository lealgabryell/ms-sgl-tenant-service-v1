package com.sgl.tenant.application.usecase;

import com.sgl.tenant.domain.entity.Tenant;
import com.sgl.tenant.domain.enums.TenantStatus;
import com.sgl.tenant.domain.repository.TenantRepository;
import com.sgl.tenant.infrastructure.exception.TenantNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("FindTenantUseCase")
class FindTenantUseCaseTest {

    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private FindTenantUseCase findTenantUseCase;

    private Tenant buildTenant(UUID id) {
        return Tenant.builder()
                .id(id)
                .nome("Lava-Jato Teste")
                .cnpj("12345678000199")
                .schemaName("lavajato_teste")
                .status(TenantStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("deve retornar tenant quando o ID existe")
    void shouldReturnTenantById() {
        UUID id = UUID.randomUUID();
        Tenant tenant = buildTenant(id);
        when(tenantRepository.findById(id)).thenReturn(Optional.of(tenant));

        Tenant result = findTenantUseCase.findById(id);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("deve lançar TenantNotFoundException quando ID não existe")
    void shouldThrowExceptionWhenIdNotFound() {
        UUID id = UUID.randomUUID();
        when(tenantRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> findTenantUseCase.findById(id))
                .isInstanceOf(TenantNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    @DisplayName("deve retornar lista de todos os tenants")
    void shouldReturnAllTenants() {
        List<Tenant> tenants = List.of(buildTenant(UUID.randomUUID()), buildTenant(UUID.randomUUID()));
        when(tenantRepository.findAll()).thenReturn(tenants);

        List<Tenant> result = findTenantUseCase.findAll();

        assertThat(result).hasSize(2);
    }
}

