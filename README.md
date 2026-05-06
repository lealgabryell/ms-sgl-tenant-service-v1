# ms-sgl-tenant-service-v1

Microserviço responsável pelo gerenciamento de **Tenants** (Lava-Jatos) no SaaS **SGL (Sistema de Gestão de Lava-Jatos)**.

---

## 🏗️ Arquitetura

O projeto segue os princípios de **Clean Architecture**, com separação clara de responsabilidades:

```
src/main/java/com/sgl/tenant/
├── domain/                         # Núcleo do negócio — sem dependências externas
│   ├── entity/Tenant.java          # Entidade de domínio pura
│   ├── enums/TenantStatus.java     # ACTIVE | INACTIVE
│   └── repository/TenantRepository.java  # Porta (interface) do repositório
│
├── application/                    # Casos de uso e orquestração
│   ├── dto/
│   │   ├── request/                # CreateTenantRequest, UpdateTenantStatusRequest
│   │   └── response/               # TenantResponse
│   ├── mapper/TenantMapper.java
│   └── usecase/                    # CreateTenantUseCase, FindTenantUseCase, UpdateTenantStatusUseCase
│
├── infrastructure/                 # Adaptadores e configurações técnicas
│   ├── config/                     # WebMvcConfig, OpenApiConfig
│   ├── context/TenantContext.java  # ThreadLocal para Multi-tenancy
│   ├── interceptor/TenantInterceptor.java  # Lê header X-Tenant-ID
│   ├── persistence/
│   │   ├── entity/TenantJpaEntity.java
│   │   ├── repository/TenantJpaRepository.java
│   │   └── adapter/TenantRepositoryAdapter.java
│   └── exception/                  # GlobalExceptionHandler + Exceções customizadas
│
└── web/
    └── controller/TenantController.java  # Endpoints REST /api/v1/tenants
```

---

## 🚀 Stack Tecnológica

| Tecnologia        | Versão  |
|-------------------|---------|
| Java              | 21      |
| Spring Boot       | 3.4.0   |
| Spring Cloud AWS  | 3.2.1   |
| PostgreSQL        | 17.2    |
| Flyway            | managed |
| Springdoc OpenAPI | 2.7.0   |
| Lombok            | 1.18.36 |

---

## ⚙️ Pré-requisitos (Desenvolvimento Local)

- Java 21+
- Maven 3.9+
- Docker & Docker Compose

---

## 🐳 Rodando Localmente com Docker Compose

```bash
# Sobe o PostgreSQL 17.2 + a aplicação
docker-compose up -d

# Apenas o banco (para rodar a app pela IDE)
docker-compose up -d postgres
```

- Aplicação: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

---

## 🔧 Rodando pela IDE / Maven

```bash
# Perfil default (aponta para localhost:5432)
./mvnw spring-boot:run

# Perfil AWS
./mvnw spring-boot:run -Dspring-boot.run.profiles=aws
```

---

## 🌐 Endpoints da API

| Método  | Endpoint                      | Descrição                   |
|---------|-------------------------------|-----------------------------|
| POST    | `/api/v1/tenants`             | Criar novo tenant           |
| GET     | `/api/v1/tenants`             | Listar todos os tenants     |
| GET     | `/api/v1/tenants/{id}`        | Buscar tenant por ID (UUID) |
| PATCH   | `/api/v1/tenants/{id}/status` | Atualizar status do tenant  |

### Exemplo — Criar Tenant

```http
POST /api/v1/tenants
Content-Type: application/json
X-Tenant-ID: master

{
  "nome": "Lava-Jato do João",
  "cnpj": "12345678000199",
  "schemaName": "lavajato_joao"
}
```

---

## 🏢 Multi-tenancy

O header **`X-Tenant-ID`** é capturado em cada requisição pelo `TenantInterceptor` e armazenado via `TenantContext` (ThreadLocal). O contexto é limpo automaticamente no `afterCompletion`.

---

## ☁️ Perfil AWS

Quando `SPRING_PROFILES_ACTIVE=aws`, o serviço:
1. Ativa o **AWS Secrets Manager** para buscar credenciais do RDS em `/sgl/tenant-service/rds-credentials`
2. Ativa o **AWS Parameter Store** para demais configs em `/sgl/tenant-service/`
3. Usa **Instance Profile** para autenticação na AWS (zero credenciais hardcoded)

**Estrutura esperada no Secrets Manager:**
```json
{
  "rds.host": "seu-rds-endpoint.amazonaws.com",
  "rds.port": "5432",
  "rds.dbname": "sgl_tenant_db",
  "rds.username": "sgl_user",
  "rds.password": "sua-senha-segura"
}
```

---

## 🧪 Testes

```bash
./mvnw test
```

---

## 🐋 Build da Imagem Docker

```bash
docker build -t sgl/tenant-service:1.0.0 .
```

---

## 📁 Migrations Flyway

Localização: `src/main/resources/db/migration/`

| Versão | Arquivo                       | Descrição                |
|--------|-------------------------------|--------------------------|
| V1     | V1__create_tenant_table.sql   | Criação da tabela tenants|
