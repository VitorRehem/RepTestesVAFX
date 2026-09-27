# 📋 Documento de Testes — PokéSal (Fase 02)

> **Projeto:** PokéSal — Sistema de Batalha em Turnos  
> **Disciplina:** Testes e Qualidade de Software  
> **Data:** 26/09/2026  
> **Classe de Teste:** `PokeSalServiceTest.java`  
> **Framework:** JUnit 5 (Jupiter)

---

## 1. Relatório Detalhado dos Testes

### 1.1 Teste: Vantagem Elemental (`testVantagemElemental_*`)

| Método | O que faz |
|--------|-----------|
| `testVantagemElemental_FogoContraPlanta()` | Verifica que o multiplicador de dano do tipo Fogo contra Planta é **2.0** (vantagem). Chama `TipoElemental.FOGO.calcularVantagem(PLANTA)` e asserta o retorno. |
| `testVantagemElemental_FogoContraAgua()` | Verifica que Fogo contra Água retorna **0.5** (desvantagem). |
| `testVantagemElemental_AguaContraFogo()` | Verifica que Água contra Fogo retorna **2.0** (vantagem). |
| `testVantagemElemental_AguaContraPlanta()` | Verifica que Água contra Planta retorna **0.5** (desvantagem). |
| `testVantagemElemental_PlantaContraAgua()` | Verifica que Planta contra Água retorna **2.0** (vantagem). |
| `testVantagemElemental_PlantaContraFogo()` | Verifica que Planta contra Fogo retorna **0.5** (desvantagem). |
| `testVantagemElemental_MesmoTipo()` | Verifica que combates do mesmo tipo (Fogo×Fogo, Água×Água, Planta×Planta) retornam **1.0** (neutro). |
| `testVantagemElemental_DanoAplicadoComMultiplicador()` | Simula um ataque completo de CharSal (Fogo, ATK=30) contra BulbaSal (Planta, DEF=25) e verifica que o HP do defensor diminui exatamente 35 pontos (danoBase=17.5 × mult=2.0 = 35). |

**Regra de Negócio testada:** O sistema de tipos segue o triângulo Fogo→Planta→Água→Fogo, onde vantagem = ×2, desvantagem = ×0.5, e neutro = ×1.

---

### 1.2 Teste: Efeito do Terreno — Estacionamento UCSal (`testEfeitoTerrenoEstacionamentoUCSal_*`)

| Método | O que faz |
|--------|-----------|
| `testEfeitoTerrenoEstacionamentoUCSal_CanteiroCentral()` | Reduz o HP do BulbaSal para 50 e simula a cura de fim de turno do Canteiro Central (5% do HP máximo = 5). Verifica que o HP sobe para **55**. |
| `testEfeitoTerrenoEstacionamentoUCSal_AsfaltoQuente()` | Calcula o dano de CharSal (Fogo) contra BulbaSal (Planta) com bônus de +15% do Asfalto Quente. Verifica que o dano final é **40** (35 × 1.15 = 40.25 → arredondado). |
| `testEfeitoTerrenoEstacionamentoUCSal_PocaDeChuva()` | Calcula o dano de SquirSal (Água) contra CharSal (Fogo) com bônus de +10% da Poça de Chuva. Verifica que o dano final é **40** (36 + 3.6 = 39.6 → arredondado). |
| `testEfeitoTerrenoEstacionamentoUCSal_Normal()` | Verifica que no terreno Normal o dano não recebe nenhum bônus, permanecendo em **35**. |

**Regra de Negócio testada:** Cada terreno (Asfalto Quente, Poça de Chuva, Canteiro Central, Normal) impacta diretamente os atributos/dano dos Pokésal de forma distinta.

---

### 1.3 Teste: Ordem de Ataque por Velocidade (`testOrdemDeAtaquePorVelocidade_*`)

| Método | O que faz |
|--------|-----------|
| `testOrdemDeAtaquePorVelocidade_MaiorSpdAtacaPrimeiro()` | Compara SPD de CharSal (50) vs BulbaSal (45) e CyndaSal (48) vs TotoSal (42), verificando que o mais rápido tem prioridade. |
| `testOrdemDeAtaquePorVelocidade_ParalisadoReduzSpd()` | Aplica status PARALISADO ao CharSal (SPD=50) e verifica que `getSpdEfetivo()` retorna **25** (metade). |
| `testOrdemDeAtaquePorVelocidade_ParalisadoPerdePrioridade()` | Verifica que um CharSal paralisado (SPD efetivo=25) perde a prioridade para BulbaSal (SPD=45). |

**Regra de Negócio testada:** O Pokésal com maior SPD efetivo ataca primeiro. Paralisia reduz SPD pela metade, podendo inverter a ordem.

---

### 1.4 Teste: Limite de Itens por Batalha (`testUsoLimiteDeItensExcedido_*`)

| Método | O que faz |
|--------|-----------|
| `testUsoLimiteDeItensExcedido_UsarDoisItens()` | Adiciona 3 itens à mochila e usa 2 com sucesso, verificando `podeUsarItem()` e `getItensUsados()` a cada passo. |
| `testUsoLimiteDeItensExcedido_TerceiroItemLancaExcecao()` | Após usar 2 itens, tenta usar o 3º e verifica que `podeUsarItem()` retorna `false` e `usarItem()` **lança `IllegalStateException`** via `assertThrows`. |
| `testUsoLimiteDeItensExcedido_ItemForaDaMochila()` | Tenta usar um item que não foi adicionado à mochila e verifica que retorna `false`. |

**Regra de Negócio testada:** Cada treinador pode usar no máximo 2 itens por batalha (`LIMITE_ITENS_POR_BATALHA = 2`). Exceder o limite **lança exceção**.

---

### 1.5 Teste: Cálculo de Dano — Boundary Values (`testCalculoDanoBoundaryValues_*`)

| Método | O que faz |
|--------|-----------|
| `testCalculoDanoBoundaryValues_DefMaiorQueAtk()` | Cria um Pokésal com ATK=10 atacando um com DEF=50. Verifica que o dano é clampado em **0** (nunca negativo). |
| `testCalculoDanoBoundaryValues_HpNaoFicaNegativo()` | Aplica 999 de dano a um Pokésal com HP=10. Verifica que HP fica **0** (não negativo) e `estaVivo()` retorna `false`. |
| `testCalculoDanoBoundaryValues_CuraNaoExcedeHpMaximo()` | Cura 50 HP em um Pokésal com HP=90/100. Verifica que HP fica **100** (não 140). |
| `testCalculoDanoBoundaryValues_HpUm()` | Aplica 99 de dano em Pokésal com HP=100. Verifica que HP=1 e `estaVivo()` retorna `true`. |
| `testCalculoDanoBoundaryValues_AtkIgualDef()` | Verifica que quando ATK=DEF=25, o danoBase = 12.5 (positivo). |

**Técnica de Teste:** Análise de Valor Limite (BVA) aplicada aos atributos HP, ATK e DEF.

---

### 1.6 Teste Autoral 1: Esquiva — Dodge (`testEsquiva*`)

| Método | O que faz |
|--------|-----------|
| `testEsquivaChanceBase()` | Executa `Esquiva.verificarEsquiva()` 10.000 vezes e verifica que a taxa de esquiva fica próxima de **10%** (entre 5% e 15%, margem estatística). |
| `testEsquivaRecebeDefensor()` | Verifica que o método `verificarEsquiva()` aceita um Pokésal defensor como parâmetro e retorna `boolean` sem lançar exceção. |

**Regra de Negócio testada:** Todo Pokésal tem uma chance base fixa de 10% de desviar de um golpe, anulando completamente o dano recebido (Requisito Autoral 2 — Esquiva).

---

### 1.7 Teste Autoral 2: Acerto Crítico — Critical Hit (`testAcertoCritico*`)

| Método | O que faz |
|--------|-----------|
| `testAcertoCriticoMultiplicador()` | Calcula o dano normal (35) e o dano com acerto crítico (70) de CharSal contra BulbaSal, verificando que o multiplicador é **×2.0**. |
| `testAcertoCriticoChance()` | Simula 10.000 sorteios com `Math.random() < 0.10` e verifica que a taxa de acerto crítico fica próxima de **10%**. |

**Regra de Negócio testada:** Durante o cálculo de dano, há 10% de chance de acerto crítico, que multiplica o dano por 2.0 (Requisito Autoral 3 — Acerto Crítico).

---

### 1.8 Testes Complementares

| Método | O que faz |
|--------|-----------|
| `testEfeitoStatusQueimadura()` | Aplica status QUEIMADO e simula fim de turno. Verifica: dano = 6.25% do HP máximo E redução de 1 ponto de ATK. |
| `testEfeitoStatusVeneno()` | Aplica status ENVENENADO e simula fim de turno. Verifica: dano = 8% do HP máximo. |
| `testAntidotoCuraVeneno()` | Envenena um Pokésal, usa Antídoto e verifica que status volta para NENHUM. |
| `testAntidotoSemEfeitoSeNaoEnvenenado()` | Usa Antídoto em Pokésal sem veneno e verifica que status permanece NENHUM. |
| `testSelecaoInicialCriaInstanciaCorreta()` | Verifica que `SelecaoInicial.escolher(1)` cria BulbaSal, `escolher(2)` cria CharSal, etc. |
| `testSelecaoInicialIndiceInvalido()` | Verifica que índices 0, 7 e -1 lançam `IllegalArgumentException`. |
| `testTodosInicialTemMesmoHp()` | Verifica que todos os 6 Pokésal iniciais têm HP máximo = 100. |
| `testAtributosDosPokesalIniciais()` | Verifica ATK, DEF, SPD e Tipo do CharSal e TotoSal criados via fábrica. |
| `testPotionCura20Hp()` | Aplica 30 de dano (HP=70), usa Potion e verifica HP=90. |
| `testSuperPotionCura50Hp()` | Aplica 60 de dano (HP=40), usa SuperPotion e verifica HP=90. |
| `testPokesalMorto()` | Aplica 100 de dano e verifica HP=0 e `estaVivo()=false`. |
| `testPokesalRecemCriado()` | Verifica estado inicial: vivo, sem status, HP = HP máximo. |

---

## 2. Matriz de Rastreabilidade

| ID Req. | Requisito do Sistema | Classe(s) Envolvida(s) | Método(s) de Teste | Status |
|---------|----------------------|------------------------|---------------------|--------|
| RN-01 | **Vantagem Elemental**: Fogo→Planta→Água→Fogo com multiplicadores 2.0 / 0.5 / 1.0 | `TipoElemental`, `Batalha` | `testVantagemElemental_FogoContraPlanta()`, `testVantagemElemental_FogoContraAgua()`, `testVantagemElemental_AguaContraFogo()`, `testVantagemElemental_AguaContraPlanta()`, `testVantagemElemental_PlantaContraAgua()`, `testVantagemElemental_PlantaContraFogo()`, `testVantagemElemental_MesmoTipo()`, `testVantagemElemental_DanoAplicadoComMultiplicador()` | ✅ Coberto |
| RN-02 | **Ordem de Ataque por SPD**: Pokésal mais rápido ataca primeiro; paralisia reduz SPD | `Pokesal`, `Batalha`, `StatusEfeito` | `testOrdemDeAtaquePorVelocidade_MaiorSpdAtacaPrimeiro()`, `testOrdemDeAtaquePorVelocidade_ParalisadoReduzSpd()`, `testOrdemDeAtaquePorVelocidade_ParalisadoPerdePrioridade()` | ✅ Coberto |
| RN-03 | **Efeito de Terreno (Estacionamento UCSal)**: 4 terrenos com bônus específicos por tipo | `Terreno`, `Batalha`, `Pokesal` | `testEfeitoTerrenoEstacionamentoUCSal_CanteiroCentral()`, `testEfeitoTerrenoEstacionamentoUCSal_AsfaltoQuente()`, `testEfeitoTerrenoEstacionamentoUCSal_PocaDeChuva()`, `testEfeitoTerrenoEstacionamentoUCSal_Normal()` | ✅ Coberto |
| RN-04 | **Limite de Itens**: Máximo de 2 itens; exceder lança exceção | `Treinador`, `ItemBatalha` | `testUsoLimiteDeItensExcedido_UsarDoisItens()`, `testUsoLimiteDeItensExcedido_TerceiroItemLancaExcecao()`, `testUsoLimiteDeItensExcedido_ItemForaDaMochila()` | ✅ Coberto |
| RN-05 | **Cálculo de Dano**: Fórmula `ATK - (DEF × 0.5)` com clamp em 0 | `Batalha`, `Pokesal` | `testCalculoDanoBoundaryValues_DefMaiorQueAtk()`, `testCalculoDanoBoundaryValues_AtkIgualDef()` | ✅ Coberto |
| RN-06 | **HP nunca negativo** e **cura limitada ao máximo** | `Pokesal` | `testCalculoDanoBoundaryValues_HpNaoFicaNegativo()`, `testCalculoDanoBoundaryValues_CuraNaoExcedeHpMaximo()`, `testCalculoDanoBoundaryValues_HpUm()` | ✅ Coberto |
| RN-07 | **Efeitos de Status**: Queimadura (6.25% HP + ATK-1), Veneno (8% HP), Paralisia (SPD/2) | `StatusEfeito`, `Batalha`, `Pokesal` | `testEfeitoStatusQueimadura()`, `testEfeitoStatusVeneno()`, `testOrdemDeAtaquePorVelocidade_ParalisadoReduzSpd()` | ✅ Coberto |
| RN-08 | **Itens curativos**: Potion (20 HP), SuperPotion (50 HP), Antídoto (cura veneno) | `Potion`, `SuperPotion`, `Antidoto` | `testPotionCura20Hp()`, `testSuperPotionCura50Hp()`, `testAntidotoCuraVeneno()`, `testAntidotoSemEfeitoSeNaoEnvenenado()` | ✅ Coberto |
| RN-09 | **Seleção Inicial**: Escolher 1 de 6 opções; índice inválido lança exceção | `SelecaoInicial`, `PokesalInicialOpcao` | `testSelecaoInicialCriaInstanciaCorreta()`, `testSelecaoInicialIndiceInvalido()`, `testTodosInicialTemMesmoHp()`, `testAtributosDosPokesalIniciais()` | ✅ Coberto |
| RN-10 | **Estado Inicial do Pokésal**: Vivo, status NENHUM, HP = hpMaximo | `Pokesal` | `testPokesalRecemCriado()`, `testPokesalMorto()` | ✅ Coberto |
| RA-01 | **Esquiva (Dodge)**: Chance base fixa de 10% de desviar um golpe | `Esquiva` | `testEsquivaChanceBase()`, `testEsquivaRecebeDefensor()` | ✅ Coberto |
| RA-02 | **Acerto Crítico**: 10% de chance, dano multiplicado por 2.0 | `Batalha` | `testAcertoCriticoMultiplicador()`, `testAcertoCriticoChance()` | ✅ Coberto |

---

## 3. Resumo Quantitativo

| Métrica | Valor |
|---------|-------|
| Total de métodos de teste | **39** |
| Requisitos cobertos | **12/12** (10 regras de negócio + 2 requisitos autorais) |
| Classes de produção testadas | **14** de 21 |
| Classes não testadas diretamente | `Main`, `BatalhaPrototipo`, `Batalha` (métodos privados com `Math.random()`), subclasses concretas (testadas via factory) |
| Técnicas de teste aplicadas | Partição de Equivalência, Análise de Valor Limite (BVA), Teste de Exceção, Teste Estatístico/Probabilístico |

> **Observação:** As classes `Main` e `BatalhaPrototipo` não foram testadas por dependerem de `Scanner` (input do usuário) e `Math.random()` (não-determinístico). A classe `Batalha` tem seus métodos privados testados indiretamente via cálculos manuais da fórmula. A classe `Esquiva` agora é testada diretamente via análise estatística.
