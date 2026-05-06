# ---- STAGE 1: Build ----
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copia apenas o pom.xml primeiro para aproveitar o cache de camadas do Docker
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .

# Baixa as dependências (cacheia esta camada separadamente)
RUN ./mvnw dependency:go-offline -B || true

# Copia o restante do código-fonte
COPY src/ src/

# Compila e empacota, pulando os testes (testes devem rodar no CI)
RUN ./mvnw clean package -DskipTests -B

# ---- STAGE 2: Runtime ----
FROM eclipse-temurin:21-jre-alpine AS runtime

# Cria usuário não-root por segurança
RUN addgroup -S sglgroup && adduser -S sgluser -G sglgroup

WORKDIR /app

# Copia apenas o JAR gerado no estágio de build
COPY --from=builder /app/target/*.jar app.jar

# Define o dono dos arquivos
RUN chown sgluser:sglgroup app.jar

USER sgluser

EXPOSE 8080

# Configura JVM para containers: respeita limites de CPU/memória do container
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

