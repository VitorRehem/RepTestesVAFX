# ✅ Checklist de Testes Estáticos de Código — PokéSal

> **Projeto:** PokéSal — Sistema de Batalha em Turnos  
> **Data da Revisão:** 26/09/2026  
> **Revisor:** Revisão Estática Automatizada (Análise Manual do Código-Fonte)  
> **Escopo:** Todas as 21 classes do pacote `pokesal`

---

## 1. Todas as variáveis estão inicializadas antes do uso?

| Status | Detalhe |
|--------|---------|
| ✅ OK | Todas as variáveis de instância são inicializadas nos construtores. |
| ✅ OK | `Pokesal.java` (L25-L33): Todos os campos (`nome`, `hpMaximo`, `hp`, `atk`, `def`, `spd`, `tipo`, `status`) são atribuídos no construtor. |
| ✅ OK | `Treinador.java` (L24-L30): Os campos `nome`, `equipe`, `ativo`, `mochila` e `itensUsados` são inicializados corretamente. |
| ✅ OK | `Batalha.java` (L84-L88): `treinador1`, `treinador2` e `terreno` inicializados no construtor. `batalhaEncerrada` inicializada na declaração (L76). |
| ⚠️ ATENÇÃO | `Batalha.Acao` (L27-L31): Os campos `tipo`, `item` e `alvoDoItem` **não possuem inicialização padrão explícita**. Apesar de os Factory Methods (`atacar()`, `usarItem()`, `fugir()`) garantirem que `tipo` é sempre definido, os campos `item` e `alvoDoItem` ficam `null` para ações de Atacar e Fugir. Isso funciona porque não são acessados nesses contextos, mas viola o princípio defensivo. |

---

## 2. Há variáveis declaradas e nunca usadas?

| Status | Detalhe |
|--------|---------|
| ⚠️ ENCONTRADO | `Esquiva.java` (L16): O parâmetro `defensor` do método `verificarEsquiva(Pokesal defensor)` é recebido mas **nunca utilizado** no corpo do método. O cálculo usa apenas `Math.random()`. Isso é um **Code Smell** — o parâmetro sugere que a chance de esquiva deveria depender dos atributos do defensor (ex: SPD), mas na prática é ignorado. |
| ✅ OK | Nas demais classes, todas as variáveis declaradas são utilizadas. |

---

## 3. Existe código inacessível?

| Status | Detalhe |
|--------|---------|
| ⚠️ ENCONTRADO | `Batalha.java` (L247): A verificação `if (dano < 0) dano = 0;` no método `calcularDano()` é **redundante/inacessível** na prática, pois a mesma verificação já foi feita na L228 (`if (danoBase < 0) danoBase = 0;`). Como os multiplicadores subsequentes (`multiplicadorTipo`, `MULTIPLICADOR_FOGO_ASFALTO`, `bonusDanoTerreno`, `MULT_CRITICO`) são todos **≥ 0**, o dano nunca será negativo neste ponto. Não é um erro, mas é código defensivo redundante. |
| ✅ OK | Não há blocos `else` inacessíveis, `return` antes de código executável, ou branches impossíveis nas demais classes. |

---

## 4. Há código duplicado?

| Status | Detalhe |
|--------|---------|
| ⚠️ ENCONTRADO | **Subclasses de Pokésal** (`BulbaSal.java`, `CharSal.java`, `SquirSal.java`, `ChikoSal.java`, `CyndaSal.java`, `TotoSal.java`): As 6 subclasses são **idênticas em estrutura** — contêm apenas um construtor que chama `super(...)` sem adicionar nenhum comportamento ou atributo específico. Isso é uma duplicação clara. Poderiam ser substituídas por uma única classe concreta `PokesalConcreto` ou instanciadas diretamente se `Pokesal` não fosse abstrata. |
| ⚠️ ENCONTRADO | `BatalhaPrototipo.java` (L18-L21): Replica manualmente a fórmula de cálculo de dano que já existe em `Batalha.calcularDano()`. Fórmula duplicada que pode divergir com o tempo. |
| ⚠️ ENCONTRADO | `Potion.java` e `SuperPotion.java` (L13-L16): Os métodos `usar()` têm a mesma lógica, diferindo apenas na constante `CURA` e no texto exibido. Poderiam ser unificados em uma classe base `PotionBase`. |

---

## 5. Existem erros de sintaxe?

| Status | Detalhe |
|--------|---------|
| ✅ OK | Nenhum erro de sintaxe encontrado. Todas as 21 classes compilam sem erros. Os imports estão corretos, os modificadores de acesso são válidos, e todos os blocos estão devidamente fechados. |

---

## 6. Há erros de lógica que quebram regras de negócio?

| Status | Detalhe |
|--------|---------|
| ⚠️ POTENCIAL | `BatalhaPrototipo.java` (L25): `int danoEsperado = (int) ((charSal.getAtk() - (bulbaSal.getDef() * 0.5)) * 2.0);` — Usa **cast `(int)` (truncamento)** para calcular o dano esperado, enquanto a fórmula real em `Batalha.calcularDano()` (L249) usa **`Math.round()`**. Para os valores atuais (ATK=30, DEF=25, resultado=35.0), ambos dão o mesmo resultado, mas para **valores fracionários**, haveria divergência. Isso poderia causar um falso positivo/negativo nos testes de validação. |
| ⚠️ POTENCIAL | `Treinador.usarItem()` (L71-L83): Quando o limite de itens é atingido, o método apenas retorna `false` e imprime uma mensagem no console. **Não lança exceção.** No entanto, o fluxo da `Main.escolherAcao()` (L103-L105) verifica `podeUsarItem()` antes de criar a ação, então no fluxo normal isso é seguro. Porém, na `Batalha.executarTurno()` (L115-L120), o item é usado **sem verificar o limite** — se um jogador criasse uma ação `USAR_ITEM` manualmente contornando o menu, o limite poderia ser silenciosamente excedido (retornando `false` sem exceção). |
| ✅ OK | Vantagem elemental (Fogo→Planta=2.0, Água→Fogo=2.0, Planta→Água=2.0) está correta e cobre todos os 9 casos (3×3). |

---

## 7. Há erros de tipagem?

| Status | Detalhe |
|--------|---------|
| ✅ OK | Todas as atribuições, retornos e parâmetros são consistentes em tipo. Os casts `(int)` em `Batalha.calcularDano()` e `aplicarEfeitosFimDeTurno()` são usados de forma adequada com `Math.round()` para converter `double → int`. |
| ✅ OK | A interface `ItemBatalha` é implementada corretamente por `Potion`, `SuperPotion` e `Antidoto`. |

---

## 8. O fluxo de controle é válido?

| Status | Detalhe |
|--------|---------|
| ⚠️ ATENÇÃO | `Main.escolherAcao()` (L89): O `while (true)` é um loop potencialmente infinito, mas é **intencional e correto** — repete até o jogador digitar uma opção válida (1, 2 ou 3), prática padrão para menus de console. |
| ⚠️ ATENÇÃO | `SelecaoInicial.selecionarViaTeclado()` (L41): Mesmo padrão de `while (true)` para o menu de seleção inicial. Correto pelo mesmo motivo. |
| ✅ OK | O loop principal da batalha em `Main.main()` (L52) tem condições de saída claras: `!estaVivo()` ou `batalhaEncerrada`. |
| ✅ OK | Nenhuma condição impossível ou contradição lógica nas estruturas `if/switch`. |

---

## 9. Outros erros/problemas identificados

| # | Tipo | Classe / Linha | Descrição |
|---|------|----------------|-----------|
| 1 | **Testabilidade** | `Batalha.java` (L151, L170, L192, L202, L242) | Uso extensivo de `Math.random()` diretamente no código de produção torna os testes **não-determinísticos**. Recomendação: Injetar um `Random` ou usar um `Supplier<Double>` para permitir mocking nos testes. |
| 2 | **Testabilidade** | `Esquiva.java` (L17) | `Math.random()` embutido impede testes unitários determinísticos da esquiva. |
| 3 | **Encapsulamento** | `Batalha.Acao` (L28-L30) | Os campos `item` e `alvoDoItem` são `private` mas acessados diretamente em `Batalha.executarTurno()` (L116, L119) via acesso de pacote (mesma classe externa). Funciona, mas `acao1.item` parece quebrar encapsulamento ao não usar getters. |
| 4 | **Javadoc ausente** | `BatalhaPrototipo.java` | A classe inteira não tem Javadoc. |
| 5 | **Classe obsoleta** | `BatalhaPrototipo.java` | Parece ser um protótipo de validação da Fase 01 que deveria ter sido removido ou movido para uma pasta de testes. Contém um `main()` adicional que pode confundir. |
| 6 | **Princípio SOLID** | Subclasses de `Pokesal` | Violação de propósito: 6 subclasses sem comportamento específico não justificam herança. Considerar padrão Factory puro sem subclasses. |
| 7 | **Tratamento de nulos** | `Treinador.java` (L27) | `this.ativo = equipe.isEmpty() ? null : equipe.get(0);` — Se `equipe` for `null` (não uma lista vazia), haverá `NullPointerException`. Não há validação de `equipe != null`. |

---

## Resumo Geral

| Critério | Resultado |
|----------|-----------|
| Variáveis inicializadas | ✅ OK (1 observação defensiva em `Acao`) |
| Variáveis não usadas | ⚠️ 1 encontrada (`Esquiva.defensor`) |
| Código inacessível | ⚠️ 1 redundância (`Batalha.L247`) |
| Código duplicado | ⚠️ 3 ocorrências (subclasses, Potion/SuperPotion, BatalhaPrototipo) |
| Erros de sintaxe | ✅ Nenhum |
| Erros de lógica | ⚠️ 2 potenciais (truncamento vs round, limite sem exceção) |
| Erros de tipagem | ✅ Nenhum |
| Fluxo de controle | ✅ Válido (loops intencionais) |
| Outros problemas | ⚠️ 7 observações (testabilidade, encapsulamento, SOLID) |

> **Veredicto:** O código está funcional e sem erros de compilação. Os problemas encontrados são majoritariamente de **qualidade, testabilidade e design** — exatamente o que a Fase 02 (Testes e Qualidade) visa endereçar.
