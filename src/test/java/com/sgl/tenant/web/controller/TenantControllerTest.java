package com.sgl.tenant.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sgl.tenant.application.dto.request.CreateTenantRequest;
import com.sgl.tenant.application.dto.request.UpdateTenantStatusRequest;
import com.sgl.tenant.application.mapper.TenantMapper;
import com.sgl.tenant.application.usecase.CreateTenantUseCase;
import com.sgl.tenant.application.usecase.FindTenantUseCase;
import com.sgl.tenant.application.usecase.UpdateTenantStatusUseCase;
import com.sgl.tenant.domain.entity.Tenant;
import com.sgl.tenant.domain.enums.TenantStatus;
import com.sgl.tenant.infrastructure.exception.GlobalExceptionHandler;
import com.sgl.tenant.infrastructure.exception.TenantAlreadyExistsException;
import com.sgl.tenant.infrastructure.exception.TenantNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TenantController.class)
@Import({TenantMapper.class, GlobalExceptionHandler.class})
@DisplayName("TenantController")
class TenantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateTenantUseCase createTenantUseCase;

    @MockitoBean
    private FindTenantUseCase findTenantUseCase;

    @MockitoBean
    private UpdateTenantStatusUseCase updateTenantStatusUseCase;

    private UUID tenantId;
    private Tenant tenant;

    @BeforeEach
    void setUp() {
        tenantId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        tenant = Tenant.builder()
                .id(tenantId)
                .nome("Lava-Jato Exemplo")
                .cnpj("12345678000199")
                .schemaName("lavajato_exemplo")
                .status(TenantStatus.ACTIVE)
                .createdAt(LocalDateTime.of(2024, 1, 15, 10, 0, 0))
                .build();
    }

    // ── POST /api/v1/tenants ───────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/v1/tenants → 201 Created com dados válidos")
    void shouldCreateTenantAndReturn201() throws Exception {
        CreateTenantRequest request = new CreateTenantRequest("Lava-Jato Exemplo", "12345678000199", "lavajato_exemplo");
        when(createTenantUseCase.execute(any(CreateTenantRequest.class))).thenReturn(tenant);

        mockMvc.perform(post("/api/v1/tenants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(tenantId.toString())))
                .andExpect(jsonPath("$.nome", is("Lava-Jato Exemplo")))
                .andExpect(jsonPath("$.cnpj", is("12345678000199")))
                .andExpect(jsonPath("$.status", is("ACTIVE")));
    }

    @Test
    @DisplayName("POST /api/v1/tenants → 409 Conflict quando CNPJ já existe")
    void shouldReturn409WhenCnpjAlreadyExists() throws Exception {
        CreateTenantRequest request = new CreateTenantRequest("Lava-Jato Exemplo", "12345678000199", "lavajato_exemplo");
        when(createTenantUseCase.execute(any())).thenThrow(new TenantAlreadyExistsException("Já existe um tenant com o CNPJ: 12345678000199"));

        mockMvc.perform(post("/api/v1/tenants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title", is("Tenant Já Existe")));
    }

    @Test
    @DisplayName("POST /api/v1/tenants → 422 Unprocessable Entity com dados inválidos")
    void shouldReturn422WhenRequestBodyIsInvalid() throws Exception {
        // nome vazio força violação de @NotBlank
        String invalidJson = """
                {"nome": "", "cnpj": "12345678000199", "schemaName": "schema"}
                """;

        mockMvc.perform(post("/api/v1/tenants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isUnprocessableEntity());
    }

    // ── GET /api/v1/tenants ────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/v1/tenants → 200 OK com lista de tenants")
    void shouldReturnAllTenants() throws Exception {
        when(findTenantUseCase.findAll()).thenReturn(List.of(tenant, tenant));

        mockMvc.perform(get("/api/v1/tenants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nome", is("Lava-Jato Exemplo")));
    }

    @Test
    @DisplayName("GET /api/v1/tenants → 200 OK com lista vazia")
    void shouldReturnEmptyList() throws Exception {
        when(findTenantUseCase.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/tenants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ── GET /api/v1/tenants/{id} ───────────────────────────────────────────

    @Test
    @DisplayName("GET /api/v1/tenants/{id} → 200 OK quando tenant existe")
    void shouldReturnTenantById() throws Exception {
        when(findTenantUseCase.findById(tenantId)).thenReturn(tenant);

        mockMvc.perform(get("/api/v1/tenants/{id}", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(tenantId.toString())))
                .andExpect(jsonPath("$.schemaName", is("lavajato_exemplo")));
    }

    @Test
    @DisplayName("GET /api/v1/tenants/{id} → 404 Not Found quando tenant não existe")
    void shouldReturn404WhenTenantNotFound() throws Exception {
        when(findTenantUseCase.findById(tenantId))
                .thenThrow(new TenantNotFoundException("Tenant não encontrado com id: " + tenantId));

        mockMvc.perform(get("/api/v1/tenants/{id}", tenantId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is("Tenant Não Encontrado")));
    }

    // ── PATCH /api/v1/tenants/{id}/status ─────────────────────────────────

    @Test
    @DisplayName("PATCH /api/v1/tenants/{id}/status → 200 OK ao atualizar para INACTIVE")
    void shouldUpdateTenantStatusToInactive() throws Exception {
        UpdateTenantStatusRequest request = new UpdateTenantStatusRequest(TenantStatus.INACTIVE);
        Tenant inactiveTenant = Tenant.builder()
                .id(tenantId).nome("Lava-Jato Exemplo").cnpj("12345678000199")
                .schemaName("lavajato_exemplo").status(TenantStatus.INACTIVE)
                .createdAt(LocalDateTime.now()).build();

        when(updateTenantStatusUseCase.execute(eq(tenantId), eq(TenantStatus.INACTIVE))).thenReturn(inactiveTenant);

        mockMvc.perform(patch("/api/v1/tenants/{id}/status", tenantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("INACTIVE")));
    }

    @Test
    @DisplayName("PATCH /api/v1/tenants/{id}/status → 404 quando tenant não existe")
    void shouldReturn404OnStatusUpdateWhenTenantNotFound() throws Exception {
        UpdateTenantStatusRequest request = new UpdateTenantStatusRequest(TenantStatus.INACTIVE);
        when(updateTenantStatusUseCase.execute(any(), any()))
                .thenThrow(new TenantNotFoundException("Tenant não encontrado com id: " + tenantId));

        mockMvc.perform(patch("/api/v1/tenants/{id}/status", tenantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}

