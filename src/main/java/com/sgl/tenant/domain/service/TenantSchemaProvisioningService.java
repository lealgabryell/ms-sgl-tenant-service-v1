package com.sgl.tenant.domain.service;

/**
 * Porta de saída responsável pelo provisionamento de schemas no banco de dados.
 * Cada tenant possui seu próprio schema para garantir o isolamento de dados (multi-tenancy).
 */
public interface TenantSchemaProvisioningService {

    /**
     * Cria um schema isolado no banco de dados para o tenant informado.
     *
     * @param schemaName nome do schema a ser criado (deve seguir o padrão ^[a-z][a-z0-9_]{2,49}$)
     */
    void createSchema(String schemaName);
}

