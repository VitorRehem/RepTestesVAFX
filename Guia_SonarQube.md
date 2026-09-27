# 🔧 Guia de Configuração do SonarQube — PokéSal (Fase 02)

> **Objetivo:** Rodar o SonarQube localmente para gerar o relatório com métricas de Code Smells, Bugs, Vulnerabilities e Coverage %.

---

## Pré-requisitos

- **Java 17+** instalado (`java --version`)
- **Maven 3.8+** instalado (`mvn --version`)
- **Docker Desktop** instalado e rodando (`docker --version`)

---

## Passo 1: Subir o SonarQube via Docker

Abra o terminal (PowerShell ou CMD) e execute:

```bash
docker run -d --name sonarqube -p 9000:9000 sonarqube:lts-community
```

> Aguarde cerca de 1-2 minutos para o SonarQube inicializar.  
> Acesse http://localhost:9000 no navegador.  
> Login padrão: **admin / admin** (ele pedirá para trocar a senha no primeiro acesso).

---

## Passo 2: Criar o Token de Autenticação

1. Acesse http://localhost:9000 e faça login.
2. Vá em **My Account → Security → Generate Tokens**.
3. Dê um nome ao token (ex: `pokesal-token`) e clique em **Generate**.
4. **Copie o token gerado** (ex: `squ_abc123...`). Você vai precisar dele no próximo passo.

---

## Passo 3: Rodar os Testes com Cobertura (JaCoCo)

No diretório raiz do projeto (`RepTestesVAFX/`), execute:

```bash
mvn clean test
```

> Isso compilará o código, rodará os testes JUnit e gerará o relatório JaCoCo em `target/site/jacoco/`.

Para verificar que o relatório foi gerado:

```bash
# Deve existir o arquivo:
# target/site/jacoco/jacoco.xml
```

---

## Passo 4: Enviar Análise para o SonarQube

Execute o comando abaixo substituindo `SEU_TOKEN_AQUI` pelo token copiado no Passo 2:

```bash
mvn sonar:sonar -Dsonar.token=SEU_TOKEN_AQUI
```

### Comando completo (caso precise especificar tudo manualmente):

```bash
mvn sonar:sonar ^
  -Dsonar.projectKey=pokesal ^
  -Dsonar.projectName=PokeSal ^
  -Dsonar.host.url=http://localhost:9000 ^
  -Dsonar.token=SEU_TOKEN_AQUI ^
  -Dsonar.java.coveragePlugin=jacoco ^
  -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
```

> 💡 **Dica:** No PowerShell, use `` ` `` (backtick) ao invés de `^` para quebrar linhas.

---

## Passo 5: Visualizar o Relatório

1. Acesse http://localhost:9000/dashboard?id=pokesal
2. Você verá as métricas:
   - **Bugs** — Erros potenciais no código
   - **Vulnerabilities** — Possíveis falhas de segurança
   - **Code Smells** — Problemas de manutenibilidade
   - **Coverage %** — Percentual de cobertura pelos testes JUnit

---

## Comando Único (Tudo de Uma Vez)

Se quiser rodar tudo em um único comando:

```bash
mvn clean test sonar:sonar -Dsonar.token=SEU_TOKEN_AQUI
```

---

## Passo 6: Parar e Remover o SonarQube (quando terminar)

```bash
# Parar o container
docker stop sonarqube

# Remover o container
docker rm sonarqube
```

---

## Possíveis Problemas

| Problema | Solução |
|----------|---------|
| `Connection refused` ao rodar `mvn sonar:sonar` | Verifique se o container Docker está rodando: `docker ps` |
| `401 Unauthorized` | Token inválido ou expirado. Gere um novo em My Account → Security |
| `java.lang.OutOfMemoryError` no SonarQube | Aumente a memória do Docker: `docker run -d --name sonarqube -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true -p 9000:9000 sonarqube:lts-community` |
| Testes falham antes do Sonar | Corrija os testes primeiro com `mvn test` separadamente |
| Coverage 0% no SonarQube | Verifique se o `jacoco.xml` foi gerado em `target/site/jacoco/` |

---

## Estrutura Esperada Após Configuração

```
RepTestesVAFX/
├── pom.xml                          ← Configuração Maven (já criado)
├── src/
│   └── pokesal/                     ← Código-fonte
├── test/
│   └── pokesal/
│       └── PokeSalServiceTest.java  ← Testes JUnit (já criado)
└── target/
    └── site/
        └── jacoco/
            └── jacoco.xml           ← Relatório de cobertura (gerado pelo Maven)
```
