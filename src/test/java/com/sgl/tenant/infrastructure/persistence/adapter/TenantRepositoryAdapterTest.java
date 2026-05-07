package com.sgl.tenant.infrastructure.persistence.adapter;

import com.sgl.tenant.domain.entity.Tenant;
import com.sgl.tenant.domain.enums.TenantStatus;
import com.sgl.tenant.infrastructure.persistence.entity.TenantJpaEntity;
import com.sgl.tenant.infrastructure.persistence.repository.TenantJpaRepository;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TenantRepositoryAdapter")
class TenantRepositoryAdapterTest {

    @Mock
    private TenantJpaRepository jpaRepository;

    @InjectMocks
    private TenantRepositoryAdapter adapter;

    private UUID id;
    private TenantJpaEntity jpaEntity;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        jpaEntity = TenantJpaEntity.builder()
                .id(id)
                .nome("Lava-Jato Teste")
                .cnpj("12345678000199")
                .schemaName("lavajato_teste")
                .status(TenantStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Tenant buildDomainTenant() {
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
    @DisplayName("deve salvar e retornar o tenant como domínio")
    void shouldSaveAndReturnDomainTenant() {
        when(jpaRepository.save(any(TenantJpaEntity.class))).thenReturn(jpaEntity);

        Tenant result = adapter.save(buildDomainTenant());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getNome()).isEqualTo("Lava-Jato Teste");
        assertThat(result.getStatus()).isEqualTo(TenantStatus.ACTIVE);
    }

    @Test
    @DisplayName("deve encontrar tenant por ID")
    void shouldFindTenantById() {
        when(jpaRepository.findById(id)).thenReturn(Optional.of(jpaEntity));

        Optional<Tenant> result = adapter.findById(id);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("deve retornar Optional vazio quando ID não existe")
    void shouldReturnEmptyWhenIdNotFound() {
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Tenant> result = adapter.findById(id);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("deve encontrar tenant por CNPJ")
    void shouldFindTenantByCnpj() {
        when(jpaRepository.findByCnpj("12345678000199")).thenReturn(Optional.of(jpaEntity));

        Optional<Tenant> result = adapter.findByCnpj("12345678000199");

        assertThat(result).isPresent();
        assertThat(result.get().getCnpj()).isEqualTo("12345678000199");
    }

    @Test
    @DisplayName("deve encontrar tenant por schemaName")
    void shouldFindTenantBySchemaName() {
        when(jpaRepository.findBySchemaName("lavajato_teste")).thenReturn(Optional.of(jpaEntity));

        Optional<Tenant> result = adapter.findBySchemaName("lavajato_teste");

        assertThat(result).isPresent();
        assertThat(result.get().getSchemaName()).isEqualTo("lavajato_teste");
    }

    @Test
    @DisplayName("deve retornar todos os tenants")
    void shouldFindAllTenants() {
        when(jpaRepository.findAll()).thenReturn(List.of(jpaEntity, jpaEntity));

        List<Tenant> result = adapter.findAll();

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("deve retornar tenants filtrados por status")
    void shouldFindAllByStatus() {
        when(jpaRepository.findAllByStatus(TenantStatus.ACTIVE)).thenReturn(List.of(jpaEntity));

        List<Tenant> result = adapter.findAllByStatus(TenantStatus.ACTIVE);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(TenantStatus.ACTIVE);
    }

    @Test
    @DisplayName("deve verificar existência por CNPJ")
    void shouldCheckExistsByCnpj() {
        when(jpaRepository.existsByCnpj("12345678000199")).thenReturn(true);

        assertThat(adapter.existsByCnpj("12345678000199")).isTrue();
        assertThat(adapter.existsByCnpj("00000000000000")).isFalse();
    }

    @Test
    @DisplayName("deve verificar existência por schemaName")
    void shouldCheckExistsBySchemaName() {
        when(jpaRepository.existsBySchemaName("lavajato_teste")).thenReturn(true);

        assertThat(adapter.existsBySchemaName("lavajato_teste")).isTrue();
        assertThat(adapter.existsBySchemaName("outro_schema")).isFalse();
    }
}

