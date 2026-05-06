package com.sgl.tenant.application.usecase;

import com.sgl.tenant.domain.entity.Tenant;
import com.sgl.tenant.domain.enums.TenantStatus;
import com.sgl.tenant.domain.repository.TenantRepository;
import com.sgl.tenant.infrastructure.exception.TenantNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateTenantStatusUseCase")
class UpdateTenantStatusUseCaseTest {

    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private UpdateTenantStatusUseCase updateTenantStatusUseCase;

    private UUID tenantId;
    private Tenant tenant;

    @BeforeEach
    void setUp() {
        tenantId = UUID.randomUUID();
        tenant = Tenant.builder()
                .id(tenantId)
                .nome("Lava-Jato Teste")
                .cnpj("12345678000199")
                .schemaName("lavajato_teste")
                .status(TenantStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("deve atualizar o status para INACTIVE com sucesso")
    void shouldUpdateStatusToInactiveSuccessfully() {
        when(tenantRepository.findById(tenantId)).thenReturn(Optional.of(tenant));
        when(tenantRepository.save(any(Tenant.class))).thenAnswer(inv -> inv.getArgument(0));

        Tenant result = updateTenantStatusUseCase.execute(tenantId, TenantStatus.INACTIVE);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(TenantStatus.INACTIVE);
        verify(tenantRepository).save(tenant);
    }

    @Test
    @DisplayName("deve atualizar o status para ACTIVE com sucesso")
    void shouldUpdateStatusToActiveSuccessfully() {
        tenant.setStatus(TenantStatus.INACTIVE);
        when(tenantRepository.findById(tenantId)).thenReturn(Optional.of(tenant));
        when(tenantRepository.save(any(Tenant.class))).thenAnswer(inv -> inv.getArgument(0));

        Tenant result = updateTenantStatusUseCase.execute(tenantId, TenantStatus.ACTIVE);

        assertThat(result.getStatus()).isEqualTo(TenantStatus.ACTIVE);
    }

    @Test
    @DisplayName("deve lançar TenantNotFoundException quando o ID não existe")
    void shouldThrowExceptionWhenTenantNotFound() {
        when(tenantRepository.findById(tenantId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> updateTenantStatusUseCase.execute(tenantId, TenantStatus.INACTIVE))
                .isInstanceOf(TenantNotFoundException.class)
                .hasMessageContaining(tenantId.toString());
    }
}

