# 🎓 Guia de Arguição — PokéSal (Fase 02)

> **Objetivo:** Preparar você para a avaliação individual eliminatória do professor.  
> **Formato:** Resumo da arquitetura + Lógica dos testes + Perguntas prováveis com dicas de resposta.

---

## Parte 1: Resumo da Arquitetura do Sistema

### 1.1 Visão Geral

O PokéSal é um sistema de batalha PvP em turnos por console, onde dois jogadores locais selecionam seus Pokésal e se enfrentam. A arquitetura segue uma estrutura orientada a objetos com separação clara entre **domínio** (entidades), **lógica de negócio** (serviços) e **interface** (console).

### 1.2 Diagrama de Dependências

```
Main (ponto de entrada)
 ├── SelecaoInicial (factory de Pokésal via teclado)
 │    └── PokesalInicialOpcao (enum com atributos)
 │         └── Subclasses: BulbaSal, CharSal, SquirSal, ChikoSal, CyndaSal, TotoSal
 │              └── Pokesal (classe abstrata base)
 │                   ├── TipoElemental (enum: FOGO, AGUA, PLANTA)
 │                   └── StatusEfeito (enum: NENHUM, QUEIMADO, ENVENENADO, PARALISADO)
 ├── Treinador (gerencia equipe + mochila de itens)
 │    └── ItemBatalha (interface)
 │         ├── Potion (cura 20 HP)
 │         ├── SuperPotion (cura 50 HP)
 │         └── Antidoto (remove ENVENENADO)
 ├── Batalha (engine de combate — turnos, ataques, terreno)
 │    ├── Batalha.Acao (inner class — Factory Method)
 │    ├── Terreno (enum: NORMAL, ASFALTO_QUENTE, POCA_DE_CHUVA, CANTEIRO_CENTRAL)
 │    └── Esquiva (utilitário — 10% chance de desviar)
 └── BatalhaPrototipo (protótipo de testes da Fase 01)
```

### 1.3 Padrões de Projeto Utilizados

| Padrão | Onde | Explicação |
|--------|------|------------|
| **Template Method** | `Pokesal` (abstract) | Define esqueleto de atributos e comportamentos comuns; subclasses estendem. |
| **Factory Method** | `PokesalInicialOpcao.criarPokesal()` | Cada constante do enum sabe criar sua instância de Pokésal. |
| **Static Factory Method** | `Batalha.Acao.atacar()`, `.usarItem()`, `.fugir()` | Métodos estáticos que criam instâncias com configuração específica. |
| **Strategy** (implícito) | `ItemBatalha` (interface) | Cada item implementa `usar()` com sua própria estratégia de efeito. |

### 1.4 Fluxo de uma Batalha (Turno a Turno)

1. **Seleção Inicial** — Cada jogador escolhe 1 de 6 Pokésal.
2. **Sorteio de Terreno** — Um dos 4 terrenos é sorteado aleatoriamente.
3. **Loop de Turnos** — Repete enquanto ambos estiverem vivos e ninguém fugir:
   - Cada jogador escolhe: Atacar (1), Usar Item (2) ou Fugir (3).
   - **Resolução de Fuga** — Prioridade máxima. Se SPD ≥ oponente, fuga garantida; senão, 30%.
   - **Uso de Itens** — Resolvido antes dos ataques. Limite de 2 por batalha.
   - **Ataques** — Ordenados por SPD efetivo. Fórmula: `ATK - (DEF × 0.5) × multiplicadorTipo`.
   - **Efeitos de Fim de Turno** — Dano de queimadura/veneno, cura do Canteiro Central.
4. **Resultado** — Pokésal com HP=0 perde; fuga encerra sem vencedor.

### 1.5 Fórmula de Dano (detalhada)

```
danoBase = ATK_atacante - (DEF_defensor × 0.5)
se danoBase < 0: danoBase = 0

dano = danoBase × multiplicadorTipo(atacante, defensor)

se terreno == ASFALTO_QUENTE e tipo == FOGO:
    dano = dano × 1.15

se terreno == POCA_DE_CHUVA e tipo == AGUA (50% chance):
    dano = dano + (dano × 0.10)

se acerto_critico (10% chance):
    dano = dano × 2.0

danoFinal = Math.round(dano)
```

---

## Parte 2: Lógica por Trás de Cada Teste

### Teste 1 — Vantagem Elemental
**Por que testamos:** É a mecânica central do jogo. Se os multiplicadores estiverem errados, todo o balanço do jogo quebra.

**Lógica:** Chamamos `TipoElemental.calcularVantagem()` para todas as 9 combinações (3×3) e verificamos:
- Vantagem (ex: Fogo→Planta) = **2.0**
- Desvantagem (ex: Fogo→Água) = **0.5**
- Neutro (ex: Fogo→Fogo) = **1.0**

**Teste integrado:** Calculamos manualmente o dano de CharSal contra BulbaSal e verificamos que o HP diminui exatamente pela quantia esperada.

---

### Teste 2 — Terreno (Estacionamento UCSal)
**Por que testamos:** Os terrenos são um diferencial criativo do projeto. Cada um deve afetar o tipo correto.

**Lógica:**
- **Canteiro Central:** Tipo Planta cura `5% × hpMaximo` por turno → verificamos HP antes e depois.
- **Asfalto Quente:** Tipo Fogo dano `× 1.15` → calculamos e comparamos com esperado (40).
- **Poça de Chuva:** Tipo Água dano `+ 10%` → calculamos e comparamos com esperado (40).
- **Normal:** Sem bônus → dano = fórmula base (35).

---

### Teste 3 — Ordem de Ataque por SPD
**Por que testamos:** Se a prioridade estiver errada, o mais lento pode atacar primeiro e matar o mais rápido injustamente.

**Lógica:**
- Comparamos `getSpd()` entre pares de Pokésal.
- Aplicamos `StatusEfeito.PARALISADO` e verificamos que `getSpdEfetivo()` retorna metade.
- Verificamos que um Pokésal paralisado perde prioridade para outro mais lento normalmente.

---

### Teste 4 — Limite de Itens
**Por que testamos:** Sem limite, um jogador poderia curar infinitamente, tornando a batalha eterna.

**Lógica:**
- Adicionamos 3 itens à mochila e usamos 2 com sucesso.
- No 3º uso, `podeUsarItem()` retorna `false` e `usarItem()` retorna `false`.
- Testamos também item fora da mochila → `false`.

---

### Teste 5 — Boundary Values
**Por que testamos:** Valores extremos (0, máximo, negativo) são onde os bugs mais aparecem.

**Lógica:**
- **DEF > ATK:** danoBase negativo deve virar 0.
- **Dano > HP:** HP deve clampar em 0 (nunca negativo).
- **Cura > espaço restante:** HP não pode exceder hpMaximo.
- **HP = 1:** Pokésal deve estar vivo (boundary mínimo).
- **ATK = DEF:** danoBase = 12.5 (positivo, confirma fórmula).

---

### Teste Autoral 1 — Efeitos de Status
**Por que testamos:** Status são uma mecânica autoral do grupo que diferencia nosso jogo.

**Lógica:**
- Queimadura: `6.25% HP` de dano + `-1 ATK` por turno.
- Veneno: `8% HP` de dano por turno.
- Antídoto cura veneno (e não tem efeito em quem não está envenenado).

---

### Teste Autoral 2 — Seleção Inicial
**Por que testamos:** É a primeira regra de negócio do jogo. Se a fábrica criar o Pokésal errado, todo o jogo desmorona.

**Lógica:**
- `escolher(1)` → BulbaSal, `escolher(2)` → CharSal, etc.
- Índices 0, 7, -1 → `IllegalArgumentException`.
- Todos os 6 iniciais têm HP=100 (balanceamento).
- Atributos conferem com o enum `PokesalInicialOpcao`.

---

## Parte 3: Perguntas Prováveis do Professor

### Sobre Arquitetura e Design

**P1: "Por que Pokesal é uma classe abstrata e não uma classe concreta?"**
> **Dica:** Explique que a decisão de design foi usar `abstract` para impedir instanciação direta e forçar a criação via subclasses tipadas (BulbaSal, CharSal, etc.), garantindo que todo Pokésal tenha um tipo definido. Na prática, as subclasses não adicionam comportamento, então uma alternativa seria tornar `Pokesal` concreta e usar o Factory Pattern puro — reconheça isso como uma melhoria possível.

**P2: "Qual padrão de projeto você usou na criação dos Pokésal?"**
> **Dica:** Factory Method via `PokesalInicialOpcao.criarPokesal()`. Cada constante do enum encapsula os atributos e sabe como instanciar a subclasse correta. Cite também os Static Factory Methods em `Batalha.Acao`.

**P3: "Como vocês tratam o acoplamento entre Batalha e Treinador?"**
> **Dica:** A `Batalha` recebe os `Treinador`s via construtor (injeção por construtor). No entanto, ela acessa diretamente `treinador.usarItem()` e `treinador.getAtivo()`, criando um acoplamento razoável. Uma melhoria seria usar interfaces para desacoplar.

---

### Sobre os Testes

**P4: "Como você lida com o Math.random() nos testes?"**
> **Dica:** Reconheça que o `Math.random()` torna certos métodos não-determinísticos (acerto crítico, esquiva, fuga). Nos testes unitários, **isolamos a lógica testável** (fórmula de dano, multiplicadores, SPD, limites) sem depender do aleatório. Para testar o fluxo completo, seria necessário **injetar um Random** ou usar um **Mock** — mencione isso como melhoria futura.

**P5: "Qual técnica de teste vocês aplicaram no testCalculoDanoBoundaryValues?"**
> **Dica:** Análise de Valor Limite (Boundary Value Analysis / BVA). Testamos os extremos: DEF > ATK (dano = 0), HP = 1 (mínimo vivo), dano > HP (clamp em 0), cura > espaço (clamp no máximo). Isso detecta off-by-one errors e edge cases.

**P6: "O que a Matriz de Rastreabilidade garante?"**
> **Dica:** Garante que **todo requisito do sistema tem pelo menos um teste** que o valida. Se um requisito não estiver mapeado, significa que não foi testado — um risco de qualidade. A matriz permite ao time e ao professor verificar a cobertura lógica (não só a cobertura de linhas).

**P7: "Vocês usaram TDD (Test-Driven Development)?"**
> **Dica:** Seja honesto. Se os testes foram escritos DEPOIS do código (como é o caso), diga que usaram a abordagem **Test-After** (ou Test-Last). Explique que TDD (escrever o teste antes do código) seria ideal, mas como o código já existia da Fase 01, optaram por criar testes para validar as regras já implementadas. Mencione que para a próxima funcionalidade, TDD poderia ser adotado.

---

### Sobre o SonarQube

**P8: "O que são Code Smells e como vocês trataram?"**
> **Dica:** Code Smells são indicadores de problemas de manutenibilidade, não bugs. Exemplos no PokéSal: parâmetro `defensor` não usado em `Esquiva.verificarEsquiva()`, duplicação nas subclasses de Pokésal, `Math.random()` acoplado. Explique que identificaram via SonarQube e documentaram no Checklist de Testes Estáticos.

**P9: "Qual a diferença entre Bug, Vulnerability e Code Smell no SonarQube?"**
> **Dica:**
> - **Bug:** Erro que vai causar comportamento incorreto em runtime (ex: NullPointer, divisão por zero).
> - **Vulnerability:** Falha de segurança explorável (ex: injeção SQL, senhas hardcoded). Pouco provável em app console.
> - **Code Smell:** Código que funciona, mas é difícil de manter/entender (ex: métodos longos, duplicação, nomes ruins).

**P10: "Por que a cobertura de código não é 100%?"**
> **Dica:** Porque a `Main.java` depende de `Scanner` (interação humana), `BatalhaPrototipo` é um protótipo obsoleto, e os métodos privados de `Batalha` usam `Math.random()`. Explique que **100% de cobertura não é um objetivo realista** — o importante é cobrir as **regras de negócio críticas**, que atingimos mapeando na Matriz de Rastreabilidade.

---

### Sobre Bugs e Problemas Encontrados

**P11: "Qual o bug mais crítico que vocês encontraram na revisão estática?"**
> **Dica:** O mais crítico potencialmente é a falta de validação de `equipe != null` no construtor de `Treinador.java` (L27). Se alguém passasse `null` ao invés de uma lista, causaria `NullPointerException`. Nos testes, sempre passamos listas válidas, mas em um ambiente de produção, precisaríamos de validação defensiva.

**P12: "Por que Treinador.usarItem() não lança exceção quando o limite é excedido?"**
> **Dica:** A decisão de design foi retornar `false` ao invés de lançar exceção, tratando o limite como uma condição normal de negócio (fail-safe). A `Main.escolherAcao()` já verifica `podeUsarItem()` antes de criar a ação. Porém, sem exceção, um uso programático direto poderia silenciosamente ignorar o limite — mencionem isso como potencial melhoria.

**P13: "O BatalhaPrototipo deveria estar no projeto?"**
> **Dica:** Não. É um artefato da Fase 01 que deveria ter sido movido para a pasta de testes ou removido. Ele contém um `main()` duplicado e replica a fórmula de dano manualmente, o que pode causar confusão e divergência. Agora que temos JUnit, ele é substituído por `PokeSalServiceTest`.

---

### Perguntas Avançadas (para se destacar)

**P14: "O que vocês fariam diferente se começassem do zero?"**
> **Sugestão de resposta:**
> 1. Injetaríamos `Random` via construtor para tornar tudo testável.
> 2. Não criaríamos subclasses vazias — usaríamos uma classe concreta com Factory.
> 3. Aplicaríamos TDD desde o início.
> 4. Usaríamos interfaces para desacoplar `Batalha` de `Treinador`.
> 5. Separaríamos a lógica de I/O (System.out) da lógica de negócio.

**P15: "Como vocês garantiriam que novos Pokésal não quebram o balanceamento?"**
> **Sugestão de resposta:** Criaríamos testes parametrizados (JUnit `@ParameterizedTest`) que validam constraints de balanceamento: HP entre 80-120, ATK entre 20-35, soma total de atributos não excede um teto. Qualquer novo Pokésal adicionado ao enum automaticamente passaria por esses testes.

---

## Dicas Gerais para a Arguição

1. **Não decore — entenda.** O professor vai mudar o contexto da pergunta. Se você entende o *porquê*, consegue adaptar.
2. **Use os termos técnicos corretos:** BVA, Partição de Equivalência, Code Smell, Coverage, TDD, Factory Method.
3. **Admita limitações com segurança:** "Não atingimos 100% de cobertura porque..." é melhor que "cobrimos tudo" (mentira que o SonarQube desmascara).
4. **Relacione teoria com prática:** "Usamos BVA no teste X porque a fórmula de dano tem um clamp em 0..."
5. **Conheça seus números:** Quantos testes? (28). Quantos requisitos cobertos? (10). Qual Coverage %? (veja no SonarQube).
