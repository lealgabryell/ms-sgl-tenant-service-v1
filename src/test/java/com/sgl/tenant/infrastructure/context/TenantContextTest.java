package com.sgl.tenant.infrastructure.context;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TenantContext")
class TenantContextTest {

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("deve armazenar e recuperar o tenant atual via ThreadLocal")
    void shouldSetAndGetCurrentTenant() {
        TenantContext.setCurrentTenant("tenant_abc");

        assertThat(TenantContext.getCurrentTenant()).isEqualTo("tenant_abc");
    }

    @Test
    @DisplayName("deve retornar null após limpar o contexto")
    void shouldReturnNullAfterClear() {
        TenantContext.setCurrentTenant("tenant_xyz");
        TenantContext.clear();

        assertThat(TenantContext.getCurrentTenant()).isNull();
    }

    @Test
    @DisplayName("deve retornar null quando nenhum tenant foi definido")
    void shouldReturnNullWhenNotSet() {
        assertThat(TenantContext.getCurrentTenant()).isNull();
    }
}

