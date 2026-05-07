package com.sgl.tenant.infrastructure.persistence.schema;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("JdbcTenantSchemaProvisioningService")
class JdbcTenantSchemaProvisioningServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private JdbcTenantSchemaProvisioningService service;

    @Test
    @DisplayName("deve executar CREATE SCHEMA com o schemaName correto")
    void shouldExecuteCreateSchemaWithCorrectName() {
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);

        service.createSchema("lavajato_teste");

        verify(jdbcTemplate).execute(sqlCaptor.capture());
        String executedSql = sqlCaptor.getValue();
        assertThat(executedSql).contains("CREATE SCHEMA IF NOT EXISTS");
        assertThat(executedSql).contains("\"lavajato_teste\"");
    }

    @Test
    @DisplayName("deve executar CREATE SCHEMA para schemaName mínimo válido (3 caracteres)")
    void shouldExecuteCreateSchemaForMinimalValidName() {
        service.createSchema("abc");

        verify(jdbcTemplate).execute(anyString());
    }

    @Test
    @DisplayName("deve executar CREATE SCHEMA para schemaName com números e underscore")
    void shouldExecuteCreateSchemaWithAlphanumericAndUnderscore() {
        service.createSchema("lava_jato_01");

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).execute(sqlCaptor.capture());
        assertThat(sqlCaptor.getValue()).contains("\"lava_jato_01\"");
    }

    @ParameterizedTest
    @DisplayName("deve lançar IllegalArgumentException para schemaName inválido")
    @ValueSource(strings = {
            "",               // vazio
            "AB",             // letras maiúsculas e curto demais
            "1abc",           // começa com número
            "_abc",           // começa com underscore
            "ab",             // muito curto (2 chars)
            "schema com espaco", // espaço
            "schema-hifen",   // hífen não permitido
    })
    void shouldThrowIllegalArgumentExceptionForInvalidSchemaName(String invalidName) {
        assertThatThrownBy(() -> service.createSchema(invalidName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("schemaName inválido");

        verify(jdbcTemplate, never()).execute(anyString());
    }

    @Test
    @DisplayName("deve lançar IllegalArgumentException para schemaName nulo")
    void shouldThrowExceptionForNullSchemaName() {
        assertThatThrownBy(() -> service.createSchema(null))
                .isInstanceOf(IllegalArgumentException.class);

        verify(jdbcTemplate, never()).execute(anyString());
    }
}

