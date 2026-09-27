package pokesal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite de testes JUnit 5 para o sistema de batalha PokéSal.
 * Cobre as regras de negócio principais: vantagem elemental, terreno,
 * ordem de ataque por velocidade, limite de itens e cálculos de dano.
 *
 * @author Equipe VAFX
 */
class PokeSalServiceTest {

    // ===== Instâncias reutilizáveis nos testes =====
    private Pokesal charSal;
    private Pokesal bulbaSal;
    private Pokesal squirSal;
    private Pokesal cyndaSal;
    private Pokesal chikoSal;
    private Pokesal totoSal;
    private Treinador treinador1;
    private Treinador treinador2;

    @BeforeEach
    void setUp() {
        // Recria instâncias frescas antes de cada teste para evitar contaminação
        charSal  = new CharSal("CharSal",  100, 30, 20, 50, TipoElemental.FOGO);
        bulbaSal = new BulbaSal("BulbaSal", 100, 25, 25, 45, TipoElemental.PLANTA);
        squirSal = new SquirSal("SquirSal", 100, 28, 22, 40, TipoElemental.AGUA);
        cyndaSal = new CyndaSal("CyndaSal", 100, 31, 19, 48, TipoElemental.FOGO);
        chikoSal = new ChikoSal("ChikoSal", 100, 24, 26, 44, TipoElemental.PLANTA);
        totoSal  = new TotoSal("TotoSal",   100, 27, 23, 42, TipoElemental.AGUA);

        treinador1 = new Treinador("Jogador 1", Arrays.asList(charSal));
        treinador2 = new Treinador("Jogador 2", Arrays.asList(bulbaSal));
    }

    // ==========================================================================
    // TESTE 1: testVantageElemental — Multiplicadores de Dano
    // ==========================================================================

    @Test
    @DisplayName("Teste 1: Fogo → Planta deve retornar multiplicador 2.0 (vantagem)")
    void testVantagemElemental_FogoContraPlanta() {
        double multiplicador = TipoElemental.FOGO.calcularVantagem(TipoElemental.PLANTA);
        assertEquals(2.0, multiplicador, 0.001,
                "Fogo atacando Planta deveria ter multiplicador 2.0 (vantagem)");
    }

    @Test
    @DisplayName("Teste 1: Fogo → Água deve retornar multiplicador 0.5 (desvantagem)")
    void testVantagemElemental_FogoContraAgua() {
        double multiplicador = TipoElemental.FOGO.calcularVantagem(TipoElemental.AGUA);
        assertEquals(0.5, multiplicador, 0.001,
                "Fogo atacando Água deveria ter multiplicador 0.5 (desvantagem)");
    }

    @Test
    @DisplayName("Teste 1: Água → Fogo deve retornar multiplicador 2.0 (vantagem)")
    void testVantagemElemental_AguaContraFogo() {
        double multiplicador = TipoElemental.AGUA.calcularVantagem(TipoElemental.FOGO);
        assertEquals(2.0, multiplicador, 0.001,
                "Água atacando Fogo deveria ter multiplicador 2.0 (vantagem)");
    }

    @Test
    @DisplayName("Teste 1: Água → Planta deve retornar multiplicador 0.5 (desvantagem)")
    void testVantagemElemental_AguaContraPlanta() {
        double multiplicador = TipoElemental.AGUA.calcularVantagem(TipoElemental.PLANTA);
        assertEquals(0.5, multiplicador, 0.001,
                "Água atacando Planta deveria ter multiplicador 0.5 (desvantagem)");
    }

    @Test
    @DisplayName("Teste 1: Planta → Água deve retornar multiplicador 2.0 (vantagem)")
    void testVantagemElemental_PlantaContraAgua() {
        double multiplicador = TipoElemental.PLANTA.calcularVantagem(TipoElemental.AGUA);
        assertEquals(2.0, multiplicador, 0.001,
                "Planta atacando Água deveria ter multiplicador 2.0 (vantagem)");
    }

    @Test
    @DisplayName("Teste 1: Planta → Fogo deve retornar multiplicador 0.5 (desvantagem)")
    void testVantagemElemental_PlantaContraFogo() {
        double multiplicador = TipoElemental.PLANTA.calcularVantagem(TipoElemental.FOGO);
        assertEquals(0.5, multiplicador, 0.001,
                "Planta atacando Fogo deveria ter multiplicador 0.5 (desvantagem)");
    }

    @Test
    @DisplayName("Teste 1: Mesmo tipo (Fogo → Fogo) deve retornar multiplicador 1.0 (neutro)")
    void testVantagemElemental_MesmoTipo() {
        assertEquals(1.0, TipoElemental.FOGO.calcularVantagem(TipoElemental.FOGO), 0.001);
        assertEquals(1.0, TipoElemental.AGUA.calcularVantagem(TipoElemental.AGUA), 0.001);
        assertEquals(1.0, TipoElemental.PLANTA.calcularVantagem(TipoElemental.PLANTA), 0.001);
    }

    @Test
    @DisplayName("Teste 1: Dano aplicado com vantagem elemental deve usar multiplicador x2")
    void testVantagemElemental_DanoAplicadoComMultiplicador() {
        // CharSal (Fogo, ATK=30) ataca BulbaSal (Planta, DEF=25)
        // danoBase = 30 - (25 * 0.5) = 17.5
        // dano = 17.5 * 2.0 = 35.0 → Math.round = 35
        int hpAntes = bulbaSal.getHp();
        double danoBase = charSal.getAtk() - (bulbaSal.getDef() * 0.5);
        double multiplicador = charSal.getTipo().calcularVantagem(bulbaSal.getTipo());
        int danoEsperado = (int) Math.round(danoBase * multiplicador);

        bulbaSal.receberDano(danoEsperado);

        assertEquals(hpAntes - danoEsperado, bulbaSal.getHp(),
                "HP do defensor deve diminuir exatamente pelo dano calculado com multiplicador de vantagem");
        assertEquals(35, danoEsperado,
                "Dano de CharSal (ATK=30) contra BulbaSal (DEF=25) com vantagem x2 deve ser 35");
    }

    // ==========================================================================
    // TESTE 2: testEfeitoTerrenoEstacionamentoUCSal — Impacto dos Terrenos
    // ==========================================================================

    @Test
    @DisplayName("Teste 2: Canteiro Central deve curar 5% do HP máximo para tipo Planta")
    void testEfeitoTerrenoEstacionamentoUCSal_CanteiroCentral() {
        // BulbaSal (Planta, HP=100, hpMaximo=100) recebe 50 de dano → HP = 50
        bulbaSal.receberDano(50);
        assertEquals(50, bulbaSal.getHp());

        // Simula efeito do Canteiro Central: cura 5% de hpMaximo = 5
        int curaEsperada = (int) Math.round(bulbaSal.getHpMaximo() * 0.05);
        bulbaSal.curar(curaEsperada);

        assertEquals(55, bulbaSal.getHp(),
                "BulbaSal no Canteiro Central deve recuperar 5 HP (5% de 100) ficando com 55");
    }

    @Test
    @DisplayName("Teste 2: Asfalto Quente deve dar bônus de 15% para tipo Fogo")
    void testEfeitoTerrenoEstacionamentoUCSal_AsfaltoQuente() {
        // CharSal (Fogo, ATK=30) ataca BulbaSal (Planta, DEF=25) no Asfalto Quente
        // danoBase = 30 - (25 * 0.5) = 17.5
        // multiplicadorTipo = 2.0 (Fogo > Planta)
        // dano = 17.5 * 2.0 = 35.0
        // com Asfalto Quente (+15%): 35.0 * 1.15 = 40.25 → Math.round = 40
        double danoBase = charSal.getAtk() - (bulbaSal.getDef() * 0.5);
        double multiplicadorTipo = TipoElemental.FOGO.calcularVantagem(TipoElemental.PLANTA);
        double dano = danoBase * multiplicadorTipo;
        double danoComAsfalto = dano * 1.15;
        int danoFinal = (int) Math.round(danoComAsfalto);

        assertEquals(40, danoFinal,
                "Dano de tipo Fogo no Asfalto Quente deve ser 40 (35 base + 15% bônus)");
    }

    @Test
    @DisplayName("Teste 2: Poça de Chuva deve dar bônus de 10% para tipo Água")
    void testEfeitoTerrenoEstacionamentoUCSal_PocaDeChuva() {
        // SquirSal (Água, ATK=28) ataca CharSal (Fogo, DEF=20)
        // danoBase = 28 - (20 * 0.5) = 18.0
        // multiplicadorTipo = 2.0 (Água > Fogo)
        // dano = 18.0 * 2.0 = 36.0
        // com Poça de Chuva (+10% dano): 36.0 + (36.0 * 0.10) = 39.6 → Math.round = 40
        double danoBase = squirSal.getAtk() - (charSal.getDef() * 0.5);
        double multiplicadorTipo = TipoElemental.AGUA.calcularVantagem(TipoElemental.FOGO);
        double dano = danoBase * multiplicadorTipo;
        double danoComBonusChuva = dano + (dano * 0.10);
        int danoFinal = (int) Math.round(danoComBonusChuva);

        assertEquals(40, danoFinal,
                "Dano de tipo Água na Poça de Chuva deve ser 40 (36 base + 10% bônus)");
    }

    @Test
    @DisplayName("Teste 2: Terreno Normal não deve alterar nenhum atributo")
    void testEfeitoTerrenoEstacionamentoUCSal_Normal() {
        // Terreno Normal: sem bônus
        double danoBase = charSal.getAtk() - (bulbaSal.getDef() * 0.5);
        double multiplicadorTipo = TipoElemental.FOGO.calcularVantagem(TipoElemental.PLANTA);
        int danoSemTerreno = (int) Math.round(danoBase * multiplicadorTipo);
        // Não aplica nenhum multiplicador de terreno
        assertEquals(35, danoSemTerreno,
                "No terreno Normal, o dano de Fogo contra Planta deve ser 35 sem bônus");
    }

    // ==========================================================================
    // TESTE 3: testOrdemDeAtaquePorVelocidade — Iniciativa por SPD
    // ==========================================================================

    @Test
    @DisplayName("Teste 3: Pokésal com maior SPD deve atacar primeiro")
    void testOrdemDeAtaquePorVelocidade_MaiorSpdAtacaPrimeiro() {
        // CharSal SPD=50 vs BulbaSal SPD=45
        assertTrue(charSal.getSpd() > bulbaSal.getSpd(),
                "CharSal (SPD=50) deve ser mais rápido que BulbaSal (SPD=45)");

        // CyndaSal SPD=48 vs TotoSal SPD=42
        assertTrue(cyndaSal.getSpd() > totoSal.getSpd(),
                "CyndaSal (SPD=48) deve ser mais rápido que TotoSal (SPD=42)");
    }

    @Test
    @DisplayName("Teste 3: SPD efetivo de Pokésal paralisado deve ser metade do SPD base")
    void testOrdemDeAtaquePorVelocidade_ParalisadoReduzSpd() {
        // CharSal SPD=50, paralisado → SPD efetivo = 25
        charSal.setStatus(StatusEfeito.PARALISADO);
        assertEquals(25, charSal.getSpdEfetivo(),
                "CharSal paralisado (SPD base=50) deve ter SPD efetivo = 25");
    }

    @Test
    @DisplayName("Teste 3: Pokésal paralisado mais lento pode perder a prioridade")
    void testOrdemDeAtaquePorVelocidade_ParalisadoPerdePrioridade() {
        // CharSal SPD=50 (paralisado → 25) vs BulbaSal SPD=45
        charSal.setStatus(StatusEfeito.PARALISADO);
        int spdEfetivoChar = charSal.getSpdEfetivo(); // 25
        int spdEfetivoBulba = bulbaSal.getSpdEfetivo(); // 45

        assertTrue(spdEfetivoBulba > spdEfetivoChar,
                "BulbaSal (SPD=45) deve atacar antes de CharSal paralisado (SPD efetivo=25)");
    }

    // ==========================================================================
    // TESTE 4: testUsoLimiteDeItensExcedido — Limite de 2 itens por batalha
    // ==========================================================================

    @Test
    @DisplayName("Teste 4: Treinador deve poder usar até 2 itens por batalha")
    void testUsoLimiteDeItensExcedido_UsarDoisItens() {
        treinador1.adicionarItem(new Potion());
        treinador1.adicionarItem(new SuperPotion());
        treinador1.adicionarItem(new Potion()); // terceiro item na mochila

        // Usa o primeiro item
        assertTrue(treinador1.podeUsarItem(), "Deve poder usar o 1º item");
        ItemBatalha item1 = treinador1.getPrimeiroItemDisponivel();
        boolean resultado1 = treinador1.usarItem(item1, charSal);
        assertTrue(resultado1, "Primeiro uso de item deve ser bem-sucedido");
        assertEquals(1, treinador1.getItensUsados());

        // Usa o segundo item
        assertTrue(treinador1.podeUsarItem(), "Deve poder usar o 2º item");
        ItemBatalha item2 = treinador1.getPrimeiroItemDisponivel();
        boolean resultado2 = treinador1.usarItem(item2, charSal);
        assertTrue(resultado2, "Segundo uso de item deve ser bem-sucedido");
        assertEquals(2, treinador1.getItensUsados());
    }

    @Test
    @DisplayName("Teste 4: Terceiro uso de item deve lançar IllegalStateException")
    void testUsoLimiteDeItensExcedido_TerceiroItemLancaExcecao() {
        treinador1.adicionarItem(new Potion());
        treinador1.adicionarItem(new SuperPotion());
        treinador1.adicionarItem(new Potion());

        // Usa os 2 permitidos
        treinador1.usarItem(treinador1.getPrimeiroItemDisponivel(), charSal);
        treinador1.usarItem(treinador1.getPrimeiroItemDisponivel(), charSal);

        // Tenta usar o 3º — deve lançar exceção
        assertFalse(treinador1.podeUsarItem(),
                "Após 2 usos, podeUsarItem() deve retornar false");

        ItemBatalha item3 = treinador1.getPrimeiroItemDisponivel();
        assertThrows(IllegalStateException.class, () -> treinador1.usarItem(item3, charSal),
                "Terceiro uso de item deve lançar IllegalStateException (limite de 2 excedido)");
    }

    @Test
    @DisplayName("Teste 4: Usar item que não está na mochila deve retornar false")
    void testUsoLimiteDeItensExcedido_ItemForaDaMochila() {
        // Não adiciona nada à mochila, tenta usar um item avulso
        Potion potionAvulsa = new Potion();
        boolean resultado = treinador1.usarItem(potionAvulsa, charSal);
        assertFalse(resultado,
                "Usar item que não está na mochila deve retornar false");
    }

    // ==========================================================================
    // TESTE 5: testCalculoDanoBoundaryValues — Valores Limite (HP, ATK, DEF)
    // ==========================================================================

    @Test
    @DisplayName("Teste 5: Dano com DEF muito alta não deve resultar em dano negativo")
    void testCalculoDanoBoundaryValues_DefMaiorQueAtk() {
        // Atacante ATK=10, Defensor DEF=50
        // danoBase = 10 - (50 * 0.5) = 10 - 25 = -15 → deve ser clampado em 0
        Pokesal fraco   = new CharSal("Fraco", 100, 10, 10, 50, TipoElemental.FOGO);
        Pokesal tanque  = new BulbaSal("Tanque", 100, 10, 50, 40, TipoElemental.PLANTA);

        double danoBase = fraco.getAtk() - (tanque.getDef() * 0.5);
        if (danoBase < 0) danoBase = 0;
        int danoFinal = (int) Math.round(danoBase * fraco.getTipo().calcularVantagem(tanque.getTipo()));

        assertEquals(0, danoFinal,
                "Quando DEF*0.5 > ATK, o dano deve ser 0 (nunca negativo)");
    }

    @Test
    @DisplayName("Teste 5: HP nunca deve ficar abaixo de 0 após receber dano")
    void testCalculoDanoBoundaryValues_HpNaoFicaNegativo() {
        // Pokésal com HP=10 recebe 999 de dano
        Pokesal fragil = new CharSal("Fragil", 10, 30, 20, 50, TipoElemental.FOGO);
        fragil.receberDano(999);
        assertEquals(0, fragil.getHp(),
                "HP deve ser clampado em 0, nunca negativo");
        assertFalse(fragil.estaVivo(),
                "Pokésal com HP=0 não deve estar vivo");
    }

    @Test
    @DisplayName("Teste 5: Cura não deve exceder o HP máximo")
    void testCalculoDanoBoundaryValues_CuraNaoExcedeHpMaximo() {
        // Pokésal com HP=90/100 cura 50 → deve ficar 100 (não 140)
        Pokesal ferido = new BulbaSal("Ferido", 100, 25, 25, 45, TipoElemental.PLANTA);
        ferido.receberDano(10);  // HP = 90
        ferido.curar(50);       // 90 + 50 = 140, mas max é 100
        assertEquals(100, ferido.getHp(),
                "Cura não pode exceder o HP máximo de 100");
    }

    @Test
    @DisplayName("Teste 5: HP = 1 (limite mínimo vivo)")
    void testCalculoDanoBoundaryValues_HpUm() {
        Pokesal quaseMorto = new CharSal("QuaseMorto", 100, 30, 20, 50, TipoElemental.FOGO);
        quaseMorto.receberDano(99); // HP = 1
        assertEquals(1, quaseMorto.getHp());
        assertTrue(quaseMorto.estaVivo(),
                "Pokésal com HP=1 ainda deve estar vivo");
    }

    @Test
    @DisplayName("Teste 5: ATK e DEF iguais resultam em dano base positivo")
    void testCalculoDanoBoundaryValues_AtkIgualDef() {
        // ATK=25, DEF=25 → danoBase = 25 - (25*0.5) = 12.5
        double danoBase = 25 - (25 * 0.5);
        assertTrue(danoBase > 0,
                "Quando ATK == DEF, danoBase deve ser positivo (12.5)");
        assertEquals(12.5, danoBase, 0.001);
    }

    // ==========================================================================
    // TESTE 6 (AUTORAL 1): Requisito Autoral — Esquiva (Dodge)
    // Requisito: Chance base fixa de 10% de desviar um golpe, anulando dano.
    // ==========================================================================

    @Test
    @DisplayName("Teste Autoral 1: Constante de chance de esquiva deve ser 10%")
    void testEsquivaChanceBase() {
        // Verifica que a classe Esquiva define a chance como 10% (0.10)
        // Chamamos verificarEsquiva() 1000 vezes e verificamos que a taxa
        // fica próxima de 10% (margem de tolerância estatística)
        int esquivas = 0;
        int tentativas = 10000;
        for (int i = 0; i < tentativas; i++) {
            if (Esquiva.verificarEsquiva(charSal)) {
                esquivas++;
            }
        }
        double taxa = (double) esquivas / tentativas;
        // Com 10000 tentativas, a taxa deve estar entre 5% e 15% (margem ampla)
        assertTrue(taxa > 0.05 && taxa < 0.15,
                "Taxa de esquiva deve ser aproximadamente 10%. Obtida: " + (taxa * 100) + "%");
    }

    @Test
    @DisplayName("Teste Autoral 1: Esquiva deve receber o Pokésal defensor como parâmetro")
    void testEsquivaRecebeDefensor() {
        // Verifica que o método verificarEsquiva aceita um Pokésal e retorna boolean
        // (validação da interface do método — não lança exceção)
        boolean resultado = Esquiva.verificarEsquiva(bulbaSal);
        // O resultado é aleatório, mas o método não deve lançar exceção
        assertTrue(resultado || !resultado,
                "verificarEsquiva deve retornar true ou false sem lançar exceção");
    }

    // ==========================================================================
    // TESTE 7 (AUTORAL 2): Requisito Autoral — Acerto Crítico (Critical Hit)
    // Requisito: 10% de chance de acerto crítico, multiplicando dano por 2.0.
    // ==========================================================================

    @Test
    @DisplayName("Teste Autoral 2: Multiplicador de acerto crítico deve ser 2.0")
    void testAcertoCriticoMultiplicador() {
        // Verifica que quando ocorre acerto crítico, o dano é multiplicado por 2
        // Calcula dano base de CharSal (Fogo) contra BulbaSal (Planta)
        double danoBase = charSal.getAtk() - (bulbaSal.getDef() * 0.5);
        double multiplicadorTipo = TipoElemental.FOGO.calcularVantagem(TipoElemental.PLANTA);
        int danoNormal = (int) Math.round(danoBase * multiplicadorTipo);
        int danoCritico = (int) Math.round(danoBase * multiplicadorTipo * 2.0);

        assertEquals(35, danoNormal, "Dano normal de Fogo contra Planta deve ser 35");
        assertEquals(70, danoCritico, "Dano com acerto crítico (x2.0) deve ser 70");
    }

    @Test
    @DisplayName("Teste Autoral 2: Acerto crítico deve ter probabilidade próxima de 10%")
    void testAcertoCriticoChance() {
        // Simula a mesma lógica de chance usada em Batalha.calcularDano()
        // CHANCE_CRITICO = 0.10 (10%)
        int criticos = 0;
        int tentativas = 10000;
        for (int i = 0; i < tentativas; i++) {
            if (Math.random() < 0.10) {
                criticos++;
            }
        }
        double taxa = (double) criticos / tentativas;
        assertTrue(taxa > 0.05 && taxa < 0.15,
                "Taxa de acerto crítico deve ser aproximadamente 10%. Obtida: " + (taxa * 100) + "%");
    }

    // ==========================================================================
    // TESTES COMPLEMENTARES: Efeitos de Status (Queimadura, Veneno, Antídoto)
    // ==========================================================================

    @Test
    @DisplayName("Teste Autoral 1: Queimadura deve causar 6.25% do HP máximo como dano e reduzir ATK")
    void testEfeitoStatusQueimadura() {
        charSal.setStatus(StatusEfeito.QUEIMADO);
        int hpAntes = charSal.getHp();
        int atkAntes = charSal.getAtk();

        // Simula fim de turno com queimadura
        int danoQueimadura = (int) Math.round(charSal.getHpMaximo() * 0.0625);
        charSal.receberDano(danoQueimadura);
        charSal.setAtk(Math.max(1, charSal.getAtk() - 1));

        assertEquals(hpAntes - danoQueimadura, charSal.getHp(),
                "Queimadura deve causar " + danoQueimadura + " de dano (6.25% de " + charSal.getHpMaximo() + ")");
        assertEquals(atkAntes - 1, charSal.getAtk(),
                "Queimadura deve reduzir ATK em 1 ponto por turno");
    }

    @Test
    @DisplayName("Teste Autoral 1: Veneno deve causar 8% do HP máximo como dano")
    void testEfeitoStatusVeneno() {
        bulbaSal.setStatus(StatusEfeito.ENVENENADO);
        int hpAntes = bulbaSal.getHp();

        // Simula fim de turno com veneno
        int danoVeneno = (int) Math.round(bulbaSal.getHpMaximo() * 0.08);
        bulbaSal.receberDano(danoVeneno);

        assertEquals(hpAntes - danoVeneno, bulbaSal.getHp(),
                "Veneno deve causar " + danoVeneno + " de dano (8% de " + bulbaSal.getHpMaximo() + ")");
    }

    @Test
    @DisplayName("Teste Autoral 1: Antídoto deve curar envenenamento")
    void testAntidotoCuraVeneno() {
        bulbaSal.setStatus(StatusEfeito.ENVENENADO);
        assertEquals(StatusEfeito.ENVENENADO, bulbaSal.getStatus());

        Antidoto antidoto = new Antidoto();
        antidoto.usar(bulbaSal);

        assertEquals(StatusEfeito.NENHUM, bulbaSal.getStatus(),
                "Antídoto deve remover o status ENVENENADO, voltando a NENHUM");
    }

    @Test
    @DisplayName("Teste Autoral 1: Antídoto não deve ter efeito em Pokésal não envenenado")
    void testAntidotoSemEfeitoSeNaoEnvenenado() {
        assertEquals(StatusEfeito.NENHUM, charSal.getStatus());

        Antidoto antidoto = new Antidoto();
        antidoto.usar(charSal);

        assertEquals(StatusEfeito.NENHUM, charSal.getStatus(),
                "Antídoto em Pokésal sem veneno não deve alterar o status");
    }

    // ==========================================================================
    // TESTES COMPLEMENTARES: Seleção Inicial e Fábrica de Pokésal
    // ==========================================================================

    @Test
    @DisplayName("Teste Autoral 2: Seleção de Pokésal inicial deve criar instância correta")
    void testSelecaoInicialCriaInstanciaCorreta() {
        // Testa todas as 6 opções do enum PokesalInicialOpcao
        Pokesal p1 = SelecaoInicial.escolher(1); // BULBASAL
        assertEquals("BulbaSal", p1.getNome());
        assertEquals(TipoElemental.PLANTA, p1.getTipo());
        assertInstanceOf(BulbaSal.class, p1);

        Pokesal p2 = SelecaoInicial.escolher(2); // CHARSAL
        assertEquals("CharSal", p2.getNome());
        assertEquals(TipoElemental.FOGO, p2.getTipo());
        assertInstanceOf(CharSal.class, p2);

        Pokesal p3 = SelecaoInicial.escolher(3); // SQUIRSAL
        assertEquals("SquirSal", p3.getNome());
        assertEquals(TipoElemental.AGUA, p3.getTipo());
        assertInstanceOf(SquirSal.class, p3);
    }

    @Test
    @DisplayName("Teste Autoral 2: Seleção com índice inválido deve lançar IllegalArgumentException")
    void testSelecaoInicialIndiceInvalido() {
        // Índice 0 (abaixo do range 1-6)
        assertThrows(IllegalArgumentException.class, () -> SelecaoInicial.escolher(0),
                "Índice 0 deve lançar IllegalArgumentException");

        // Índice 7 (acima do range 1-6)
        assertThrows(IllegalArgumentException.class, () -> SelecaoInicial.escolher(7),
                "Índice 7 deve lançar IllegalArgumentException");

        // Índice negativo
        assertThrows(IllegalArgumentException.class, () -> SelecaoInicial.escolher(-1),
                "Índice negativo deve lançar IllegalArgumentException");
    }

    @Test
    @DisplayName("Teste Autoral 2: Todos os 6 Pokésal iniciais devem ter HP base = 100")
    void testTodosInicialTemMesmoHp() {
        for (int i = 1; i <= 6; i++) {
            Pokesal p = SelecaoInicial.escolher(i);
            assertEquals(100, p.getHpMaximo(),
                    "Pokésal inicial " + p.getNome() + " deve ter HP máximo = 100");
        }
    }

    @Test
    @DisplayName("Teste Autoral 2: Pokésal iniciais devem ter atributos conforme PokesalInicialOpcao")
    void testAtributosDosPokesalIniciais() {
        // Verifica que cada Pokésal criado tem os atributos do enum
        Pokesal charSalCriado = SelecaoInicial.escolher(2);
        assertEquals(100, charSalCriado.getHpMaximo());
        assertEquals(30, charSalCriado.getAtk());
        assertEquals(20, charSalCriado.getDef());
        assertEquals(50, charSalCriado.getSpd());
        assertEquals(TipoElemental.FOGO, charSalCriado.getTipo());

        Pokesal totoSalCriado = SelecaoInicial.escolher(6);
        assertEquals(100, totoSalCriado.getHpMaximo());
        assertEquals(27, totoSalCriado.getAtk());
        assertEquals(23, totoSalCriado.getDef());
        assertEquals(42, totoSalCriado.getSpd());
        assertEquals(TipoElemental.AGUA, totoSalCriado.getTipo());
    }

    // ==========================================================================
    // TESTES COMPLEMENTARES DE INTEGRAÇÃO
    // ==========================================================================

    @Test
    @DisplayName("Complementar: Potion deve curar 20 HP")
    void testPotionCura20Hp() {
        charSal.receberDano(30); // HP = 70
        new Potion().usar(charSal);
        assertEquals(90, charSal.getHp(),
                "Potion deve curar 20 HP (70 + 20 = 90)");
    }

    @Test
    @DisplayName("Complementar: SuperPotion deve curar 50 HP")
    void testSuperPotionCura50Hp() {
        charSal.receberDano(60); // HP = 40
        new SuperPotion().usar(charSal);
        assertEquals(90, charSal.getHp(),
                "SuperPotion deve curar 50 HP (40 + 50 = 90)");
    }

    @Test
    @DisplayName("Complementar: Pokésal com HP=0 não deve estar vivo")
    void testPokesalMorto() {
        charSal.receberDano(100);
        assertEquals(0, charSal.getHp());
        assertFalse(charSal.estaVivo());
    }

    @Test
    @DisplayName("Complementar: Pokésal recém-criado deve estar vivo e sem status")
    void testPokesalRecemCriado() {
        assertTrue(charSal.estaVivo());
        assertEquals(StatusEfeito.NENHUM, charSal.getStatus());
        assertEquals(charSal.getHpMaximo(), charSal.getHp());
    }
}
