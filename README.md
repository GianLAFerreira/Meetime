## Meetime HubSpot Integration API

Este documento orienta como configurar, executar e testar sua aplicação de integração com a API do HubSpot. Siga cada passo para garantir que tudo funcione sem problemas.

---

## 📋 Sumário

1. [Pré-requisitos](#pré-requisitos)
2. [Configuração](#configuração)
3. [Segurança (Basic Auth)](#segurança-basic-auth)
4. [Execução da aplicação](#execução-da-aplicação)
5. [Execução da aplicação docker compose](#execução-da-aplicação-docker-compose)
6. [Exemplos de CURL](#exemplos-de-curl)
7. [Swagger / OpenAPI](#swagger--openapi)
8. [Armazenamento de Token (Redis)](#armazenamento-de-token-redis)
9. [Considerações Finais](#considerações-finais)

---

## Pré-requisitos

- Java 17+
- Maven 3.6+
- Docker & Docker Compose (para Redis)
- Conta de desenvolvedor no HubSpot (Client ID / Client Secret)

---

## Configuração

1. **Clone o repositório**
   ```bash
   git clone https://github.com/GianLAFerreira/meetime-hubspot.git
   cd meetime-hubspot

2. **Variáveis de Ambiente**
    - Crie um arquivo src/main/resources/application.yml com:
   ```yaml
    server:
    port: 8081
    
    hubspot:
    client-id:     SEU_CLIENT_ID
    client-secret: SEU_CLIENT_SECRET
    redirect-uri:  http://localhost:8080/oauth/callback
    scopes:        crm.objects.contacts.read crm.objects.contacts.write oauth
    
    api:
    base-url:      https://api.hubapi.com
    contacts-path: /crm/v3/objects/contacts
    
    spring:
    redis:
    host: localhost
    port: 6379
    timeout: 2s
   
3. **Instalar o ngrok**

Para testar o seu endpoint de webhook localmente, você pode usar o ngrok para expor sua porta 8080 na internet e informar essa URL ao HubSpot. Veja como:

Se ainda não tiver, faça o download e instalação conforme seu sistema:
   - Windows/macOS/Linux: baixe em https://ngrok.com/download
   - Descompacte e coloque o executável no seu PATH, ou simplesmente execute onde ele estiver.

## Segurança (Basic Auth)
Todas as rotas internas — exceto o /oauth/authorize e /oauth/callback — estão protegidas por Basic Auth.
Use as credenciais definidas em application.yml:

   - Usuário: admin

   - Senha: admin123

Você receberá 401 Unauthorized se não fornecer ou fornecer credenciais incorretas.

## Execução da aplicação
1. **Endpoints REST**

| Método | URI                  | Entrada / Saída                             | Descrição                                                 |
| ------ | -------------------- | ------------------------------------------- | --------------------------------------------------------- |
| GET    | `/oauth/authorize`   | → `{"url": "..."}`                          | Gera URL de autorização OAuth2 com HubSpot                |
| GET    | `/oauth/callback`    | → `{"access_token": "...", ...}`            | Troca “code” por `access_token` e armazena em Redis       |
| POST   | `/hubspot/contacts`  | Req: `CreateContactRequest`<br>Resp: `CreateContactResponse` | Cria contato no CRM do HubSpot (rate-limit interno aplicado) |
| GET    | `/hubspot/contacts`  | → `ListContactsResponse`                    | Lista contatos via API HubSpot                            |
| POST   | `/webhook/contact`   | → `204 No Content`                          | Recebe eventos de webhook `contact.creation`              |

2. **Execução da aplicação**
    - Crie docker-compose.yml na raiz:
   ```yaml
    version: '3'
    services:
    redis:
    image: redis:7
    ports:
    - "6379:6379"
- Em Seguida:
  ```bash
  docker-compose up -d
- Dentro da pasta do projeto:
  ```bash
  mvn clean package
  java -jar target/Meetime-0.0.1-SNAPSHOT.jar
- Rodar o ngrok
    - No seu terminal, inicie um túnel HTTP para a sua porta 8081 (mesma porta da aplicação):
  ```bash
  ngrok http 8080

- Isto vai criar duas URLs públicas, algo como:

  Forwarding    https://a1b2c3d4e5f6.ngrok.io  →  http://localhost:8081  
  Forwarding    http://a1b2c3d4e5f6.ngrok.io   →  http://localhost:8081

- Copie a URL HTTPS (por exemplo, https://a1b2c3d4e5f6.ngrok.io)
- Configurar o Webhook no HubSpot
    1. No painel da sua App no HubSpot Developer:
        - Navegue em Webhooks → + Add subscription.
        - Object type: Contact
        - Event: contact.creation
        - Target URL: cole https://a1b2c3d4e5f6.ngrok.io/webhook/contact
        - Salve e reenvie o evento de teste (há botão “Test”).


2. **Validar Localmente se o webhook foi chamado**
   - Com sua aplicação rodando (java -jar …) e o túnel ngrok ativo,
   - Crie um contato no HubSpot (via UI ou API),
   - Observe no seu log:
   ```bash
   INFO … WebhookController : Novo contato criado: { subscriptionType=contact.creation, … }
   
Se o evento aparecer, você concluiu a integração com sucesso!

## Execução da aplicação docker compose
Para facilitar o setup e execução, coloquei um `docker-compose.yml` que sobe sua aplicação Spring Boot e o Redis juntos.

- **Dockerfile** (na raiz do projeto):
  ```dockerfile
  FROM eclipse-temurin:17-jdk-alpine AS build
  WORKDIR /app
  COPY mvnw pom.xml ./
  COPY .mvn .mvn
  RUN chmod +x mvnw && ./mvnw dependency:go-offline -B
  COPY src src
  RUN ./mvnw clean package -DskipTests -B

  FROM eclipse-temurin:17-jre-alpine
  WORKDIR /app
  COPY --from=build /app/target/Meetime-0.0.1-SNAPSHOT.jar app.jar
  EXPOSE 8080
  ENTRYPOINT ["java","-jar","app.jar"]
  
- docker-compose.yml (na raiz do projeto):
    ```yaml
    services:
    app:
    build:
    context: .
    dockerfile: Dockerfile
    image: meetime-hubspot:latest
    container_name: meetime-app
    ports:
    - "8080:8080"
    depends_on:
      - redis
      environment:
      HUBSPOT_CLIENT_ID: ${HUBSPOT_CLIENT_ID}
      HUBSPOT_CLIENT_SECRET: ${HUBSPOT_CLIENT_SECRET}
      HUBSPOT_REDIRECT_URI: http://localhost:8080/oauth/callback
      SPRING_REDIS_HOST: redis
      SPRING_REDIS_PORT: 6379
    
    redis:
    image: redis:7-alpine
    container_name: meetime-redis
    ports:
    - "6379:6379"
    volumes:
      - redis-data:/data
    
    volumes:
    redis-data:

Passo a passo
1. Defina suas variáveis de ambiente (ou crie um arquivo .env na raiz):
   ```bash
   export HUBSPOT_CLIENT_ID=<Seu client id>
   export HUBSPOT_CLIENT_SECRET=<Seu client secret>
2. Suba os serviços na pasta raiz do projeto:
    ```bash
   docker compose up --build -d
3. Verifique que ambos estão no ar:
    ```bash
   docker compose ps
4. Veja os logs (se quiser acompanhar output em tempo real):
    ```bash
   docker compose logs -f app
5. Faça requisições no Postman / cURL usando http://localhost:8080 como base:
   - Ex.: GET http://localhost:8080/hubspot/contacts com Basic Auth admin:admin123.

6. Para derrubar tudo:
    ```bash
   docker compose down

## Exemplos de CURL

> **Basic Auth**: `admin:admin123` → base64 `YWRtaW46YWRtaW4xMjM=`

1. **Gerar URL de autorização**
   ```bash
   curl --location --request GET 'http://localhost:8081/oauth/authorize' \
     --header 'Authorization: Basic YWRtaW46YWRtaW4xMjM='
   
2. **Trocar code por access_token**
   ```bash
   curl --location --request GET 'http://localhost:8081/oauth/callback?code=SEU_CODE_AQUI' \
   --header 'Authorization: Basic YWRtaW46YWRtaW4xMjM='

3. **Criar um contato**
   ```bash
   curl --location --request POST 'http://localhost:8081/hubspot/contacts' \
   --header 'Content-Type: application/json' \
   --header 'Authorization: Basic YWRtaW46YWRtaW4xMjM=' \
   --data '{
   "email": "example+uniq@meetime.com",
   "firstname": "Teste",
   "lastname": "User",
   "phone": "(555) 555-5555",
   "company": "HubSpot"
   }'

4. **Listar Contatos**
   ```bash
   curl --location --request GET 'http://localhost:8081/hubspot/contacts' \
   --header 'Authorization: Basic YWRtaW46YWRtaW4xMjM='
   
5. **Dica** para gerenciar e-mails únicos no CLI, você pode usar um timestamp:
   ```bash
   EMAIL="test+$(date +%s)@meetime.com"
   curl ... --data "{\"email\":\"${EMAIL}\", ...}"
  
## Swagger / OpenAPI

1. A dependência **springdoc-openapi-starter-webflux-ui** já está no pom.xml.
2. Após subir a aplicação, acesse no navegador:
[http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html
)
3. Explore a UI, veja esquemas de DTOs e teste endpoints diretamente.

## Armazenamento de Token (Redis)
   - O token OAuth é salvo em Redis com TTL de 1800 segundos.
   - TokenStorageService encapsula ReactiveRedisTemplate<String,String>

## Considerações Finais

Este projeto foi desenvolvido como parte do processo seletivo técnico da Meetime, com foco em:

- **Requisitos do Desafio**
    - Fluxo OAuth2 (Authorization Code) com HubSpot
    - Endpoints REST para criar/listar contatos e receber webhooks
    - Proteção Basic Auth em rotas internas
    - Rate-limiting interno e tratamento de erros HTTP 429 e 401
    - Armazenamento de token em Redis com TTL automático

- **Boas Práticas Aplicadas**
    - **Separação de camadas**: controllers → services → integration clients
    - **DTOs claros e validados** com `jakarta.validation`
    - **Testes unitários** abrangendo serviços com Mockito e JUnit 5
    - **Documentação automatizada** via Swagger/OpenAPI
    - **Configuração externalizada** em `application.yml` e variáveis de ambiente
    - **Contêineres Docker** para ambiente isolado e reproduzível

- **Possíveis Melhorias**
    - Adicionar testes de integração (JUnit + WebTestClient)
    - Implementar circuit breaker (Resilience4j) para chamadas externas
    - Monitoramento de métricas (Micrometer + Prometheus)
    - Pipelines CI/CD para build, testes e deploy automatizados
    - URL pública fixa para webhook
