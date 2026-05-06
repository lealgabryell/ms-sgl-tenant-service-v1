package com.sgl.tenant.infrastructure.interceptor;

import com.sgl.tenant.infrastructure.context.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TenantInterceptor")
class TenantInterceptorTest {

    private final TenantInterceptor interceptor = new TenantInterceptor();
    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private final MockHttpServletResponse response = new MockHttpServletResponse();
    private final Object handler = new Object();

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("deve definir o TenantContext quando o header X-Tenant-ID estiver presente")
    void shouldSetTenantContextWhenHeaderIsPresent() {
        request.addHeader("X-Tenant-ID", "tenant_lava_jato");

        boolean result = interceptor.preHandle(request, response, handler);

        assertThat(result).isTrue();
        assertThat(TenantContext.getCurrentTenant()).isEqualTo("tenant_lava_jato");
    }

    @Test
    @DisplayName("deve permitir a requisição sem definir tenant quando o header estiver ausente")
    void shouldAllowRequestWithoutSettingTenantWhenHeaderAbsent() {
        boolean result = interceptor.preHandle(request, response, handler);

        assertThat(result).isTrue();
        assertThat(TenantContext.getCurrentTenant()).isNull();
    }

    @Test
    @DisplayName("deve limpar o TenantContext após a conclusão da requisição")
    void shouldClearTenantContextAfterCompletion() {
        TenantContext.setCurrentTenant("tenant_para_limpar");

        interceptor.afterCompletion(request, response, handler, null);

        assertThat(TenantContext.getCurrentTenant()).isNull();
    }

    @Test
    @DisplayName("deve limpar o TenantContext mesmo quando ocorre uma exceção")
    void shouldClearTenantContextEvenOnException() {
        TenantContext.setCurrentTenant("tenant_com_erro");

        interceptor.afterCompletion(request, response, handler, new RuntimeException("erro simulado"));

        assertThat(TenantContext.getCurrentTenant()).isNull();
    }
}

