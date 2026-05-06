package com.sgl.tenant.application.usecase;

import com.sgl.tenant.application.dto.request.CreateTenantRequest;
import com.sgl.tenant.domain.entity.Tenant;
import com.sgl.tenant.domain.enums.TenantStatus;
import com.sgl.tenant.domain.repository.TenantRepository;
import com.sgl.tenant.domain.service.TenantSchemaProvisioningService;
import com.sgl.tenant.infrastructure.exception.TenantAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateTenantUseCase")
class CreateTenantUseCaseTest {

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private TenantSchemaProvisioningService schemaProvisioningService;

    @InjectMocks
    private CreateTenantUseCase createTenantUseCase;

    private CreateTenantRequest request;

    @BeforeEach
    void setUp() {
        request = new CreateTenantRequest("Lava-Jato Exemplo", "12345678000199", "lavajato_exemplo");
    }

    @Test
    @DisplayName("deve criar um tenant e provisionar o schema com sucesso")
    void shouldCreateTenantAndProvisionSchemaSuccessfully() {
        when(tenantRepository.existsByCnpj(anyString())).thenReturn(false);
        when(tenantRepository.existsBySchemaName(anyString())).thenReturn(false);
        when(tenantRepository.save(any(Tenant.class))).thenAnswer(inv -> inv.getArgument(0));

        Tenant result = createTenantUseCase.execute(request);

        assertThat(result).isNotNull();
        assertThat(result.getNome()).isEqualTo("Lava-Jato Exemplo");
        assertThat(result.getCnpj()).isEqualTo("12345678000199");
        assertThat(result.getSchemaName()).isEqualTo("lavajato_exemplo");
        assertThat(result.getStatus()).isEqualTo(TenantStatus.ACTIVE);
        assertThat(result.getId()).isNotNull();
        assertThat(result.getCreatedAt()).isNotNull();

        verify(tenantRepository).save(any(Tenant.class));
        // garante que o schema foi provisionado com o schemaName correto
        verify(schemaProvisioningService).createSchema("lavajato_exemplo");
    }

    @Test
    @DisplayName("deve lançar TenantAlreadyExistsException quando CNPJ já existe")
    void shouldThrowExceptionWhenCnpjAlreadyExists() {
        when(tenantRepository.existsByCnpj(request.cnpj())).thenReturn(true);

        assertThatThrownBy(() -> createTenantUseCase.execute(request))
                .isInstanceOf(TenantAlreadyExistsException.class)
                .hasMessageContaining(request.cnpj());

        verify(tenantRepository, never()).save(any());
        // schema NÃO deve ser criado se a validação falhar
        verify(schemaProvisioningService, never()).createSchema(anyString());
    }

    @Test
    @DisplayName("deve lançar TenantAlreadyExistsException quando schemaName já existe")
    void shouldThrowExceptionWhenSchemaNameAlreadyExists() {
        when(tenantRepository.existsByCnpj(anyString())).thenReturn(false);
        when(tenantRepository.existsBySchemaName(request.schemaName())).thenReturn(true);

        assertThatThrownBy(() -> createTenantUseCase.execute(request))
                .isInstanceOf(TenantAlreadyExistsException.class)
                .hasMessageContaining(request.schemaName());

        verify(tenantRepository, never()).save(any());
        verify(schemaProvisioningService, never()).createSchema(anyString());
    }
}
