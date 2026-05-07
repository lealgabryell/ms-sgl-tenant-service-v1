-- V1__create_tenant_table.sql
-- Criação da tabela de tenants no schema público

CREATE TABLE IF NOT EXISTS public.tenants
(
    id          UUID         NOT NULL,
    nome        VARCHAR(100) NOT NULL,
    cnpj        VARCHAR(14)  NOT NULL,
    schema_name VARCHAR(50)  NOT NULL,
    status      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_tenants PRIMARY KEY (id),
    CONSTRAINT uq_tenants_cnpj UNIQUE (cnpj),
    CONSTRAINT uq_tenants_schema_name UNIQUE (schema_name),
    CONSTRAINT chk_tenants_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

CREATE INDEX IF NOT EXISTS idx_tenants_status ON public.tenants (status);
CREATE INDEX IF NOT EXISTS idx_tenants_cnpj ON public.tenants (cnpj);

COMMENT ON TABLE public.tenants IS 'Tabela mestre de tenants (lava-jatos) do SaaS SGL';
COMMENT ON COLUMN public.tenants.id IS 'Identificador único do tenant (UUID)';
COMMENT ON COLUMN public.tenants.nome IS 'Nome fantasia do lava-jato';
COMMENT ON COLUMN public.tenants.cnpj IS 'CNPJ do lava-jato (somente dígitos)';
COMMENT ON COLUMN public.tenants.schema_name IS 'Nome do schema PostgreSQL isolado deste tenant';
COMMENT ON COLUMN public.tenants.status IS 'Status do tenant: ACTIVE ou INACTIVE';
COMMENT ON COLUMN public.tenants.created_at IS 'Data e hora de criação do tenant';

