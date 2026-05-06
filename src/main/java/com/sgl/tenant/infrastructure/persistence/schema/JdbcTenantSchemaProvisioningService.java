package com.sgl.tenant.infrastructure.persistence.schema;

import com.sgl.tenant.domain.service.TenantSchemaProvisioningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Implementação do provisionamento de schema usando JdbcTemplate.
 *
 * <p>No PostgreSQL, comandos DDL (CREATE SCHEMA) são transacionais,
 * portanto a criação do schema participa da mesma transação do caso de uso.
 * Se qualquer etapa falhar, tudo é revertido atomicamente.</p>
 *
 * <p>O schemaName é validado via regex no DTO antes de chegar aqui
 * (^[a-z][a-z0-9_]{2,49}$), garantindo segurança contra SQL injection.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JdbcTenantSchemaProvisioningService implements TenantSchemaProvisioningService {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void createSchema(String schemaName) {
        validateSchemaName(schemaName);

        log.info("Provisionando schema '{}' no banco de dados...", schemaName);

        // CREATE SCHEMA é idempotente com IF NOT EXISTS
        String sql = "CREATE SCHEMA IF NOT EXISTS \"" + schemaName + "\"";
        jdbcTemplate.execute(sql);

        log.info("Schema '{}' provisionado com sucesso.", schemaName);
    }

    /**
     * Dupla validação defensiva do schemaName antes de qualquer operação DDL.
     * A primeira validação ocorre no DTO via @Pattern, esta é uma camada extra de segurança.
     */
    private void validateSchemaName(String schemaName) {
        if (schemaName == null || !schemaName.matches("^[a-z][a-z0-9_]{2,49}$")) {
            throw new IllegalArgumentException(
                    "schemaName inválido para provisionamento: '" + schemaName + "'. " +
                    "Deve conter apenas letras minúsculas, números e underscore (3-50 caracteres)."
            );
        }
    }
}

