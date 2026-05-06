package com.sgl.tenant.infrastructure.interceptor;

import com.sgl.tenant.infrastructure.context.TenantContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class TenantInterceptor implements HandlerInterceptor {

    public static final String TENANT_HEADER = "X-Tenant-ID";

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler) {

        String tenantId = request.getHeader(TENANT_HEADER);

        if (StringUtils.hasText(tenantId)) {
            log.debug("Tenant identificado via header '{}': {}", TENANT_HEADER, tenantId);
            TenantContext.setCurrentTenant(tenantId);
        } else {
            log.debug("Header '{}' não presente na requisição. URI: {}", TENANT_HEADER, request.getRequestURI());
        }

        return true;
    }

    @Override
    public void afterCompletion(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler,
            Exception ex) {

        TenantContext.clear();
        log.debug("TenantContext limpo após conclusão da requisição.");
    }
}

