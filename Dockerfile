# Usamos apenas o estágio de Runtime para ser ultra-rápido
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Cria o usuário de segurança
RUN addgroup -S sglgroup && adduser -S sgluser -G sglgroup

# Copia o JAR que o GitHub Actions baixou para a pasta target
COPY /*.jar app.jar

# Garante que o usuário sgluser é dono do arquivo
RUN chown sgluser:sglgroup app.jar
USER sgluser

# Configurações de Memória para o Fargate
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"
ENV SPRING_PROFILES_ACTIVE=aws

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]