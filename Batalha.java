package pokesal;

public class Batalha {

    // Chance base de acerto de qualquer ataque (fora de efeitos especiais de terreno).
    private static final double CHANCE_BASE_DE_ACERTO = 0.95;

    public enum TipoAcao { ATACAR, USAR_ITEM }

    /** Representa a ação escolhida por um treinador em um turno. */
    public static class Acao {
        private TipoAcao tipo;
        private ItemBatalha item;
        private Pokesal alvoDoItem;

        public static Acao atacar() {
            Acao a = new Acao();
            a.tipo = TipoAcao.ATACAR;
            return a;
        }

        public static Acao usarItem(ItemBatalha item, Pokesal alvo) {
            Acao a = new Acao();
            a.tipo = TipoAcao.USAR_ITEM;
            a.item = item;
            a.alvoDoItem = alvo;
            return a;
        }

        public TipoAcao getTipo() { return tipo; }
    }

    private Treinador treinador1;
    private Treinador treinador2;
    private Terreno terreno;

    public Batalha(Treinador treinador1, Treinador treinador2, Terreno terreno) {
        this.treinador1 = treinador1;
        this.treinador2 = treinador2;
        this.terreno = terreno;
    }

    /**
     * Executa um turno completo:
     * 1º) resolve itens (prioridade máxima, independente do SPD);
     * 2º) resolve ataques de quem não usou item, na ordem definida pelo SPD
     *     (com sorteio 50/50 em caso de empate);
     * 3º) aplica efeitos de terreno e de status ao final do turno.
     */
    public void executarTurno(Acao acao1, Acao acao2) {
        Pokesal p1 = treinador1.getAtivo();
        Pokesal p2 = treinador2.getAtivo();

        System.out.println("\n--- Novo turno ---");

        // 4. Prioridade máxima: itens são resolvidos ANTES de qualquer ataque,
        // independente do SPD. Usar item consome a ação do treinador nesse turno.
        if (acao1.getTipo() == TipoAcao.USAR_ITEM) {
            treinador1.usarItem(acao1.item, acao1.alvoDoItem);
        }
        if (acao2.getTipo() == TipoAcao.USAR_ITEM) {
            treinador2.usarItem(acao2.item, acao2.alvoDoItem);
        }

        boolean t1Ataca = acao1.getTipo() == TipoAcao.ATACAR;
        boolean t2Ataca = acao2.getTipo() == TipoAcao.ATACAR;

        // 2. Ordem de ataque pelo SPD; empate = sorteio 50/50
        boolean treinador1PrimeiroNoAtaque = decidirPrioridadePorSpd(p1, p2);

        if (treinador1PrimeiroNoAtaque) {
            if (t1Ataca && p1.estaVivo() && p2.estaVivo()) executarAtaque(p1, p2);
            if (t2Ataca && p2.estaVivo() && p1.estaVivo()) executarAtaque(p2, p1);
        } else {
            if (t2Ataca && p2.estaVivo() && p1.estaVivo()) executarAtaque(p2, p1);
            if (t1Ataca && p1.estaVivo() && p2.estaVivo()) executarAtaque(p1, p2);
        }

        // Efeitos de terreno e status são aplicados ao final do turno
        aplicarEfeitosFimDeTurno(p1);
        aplicarEfeitosFimDeTurno(p2);
    }

    /**
     * Decide quem age primeiro entre os dois Pokésal, com base no SPD efetivo.
     * Em caso de empate, sorteia 50/50 (regra da equipe).
     */
    private boolean decidirPrioridadePorSpd(Pokesal p1, Pokesal p2) {
        int spd1 = p1.getSpdEfetivo();
        int spd2 = p2.getSpdEfetivo();

        if (spd1 == spd2) {
            return Math.random() < 0.5;
        }
        return spd1 > spd2;
    }

    private void executarAtaque(Pokesal atacante, Pokesal defensor) {
        if (!atacante.estaVivo()) return;

        // 1. Poça de Chuva: sorteio 50/50 entre buff de PRECISÃO ou de DANO para o tipo Água.
        boolean precisaoGarantida = false;
        double bonusDanoTerreno = 0.0;

        if (terreno == Terreno.POCA_DE_CHUVA && atacante.getTipo() == TipoElemental.AGUA) {
            if (Math.random() < 0.5) {
                precisaoGarantida = true;
                System.out.println("A Poça de Chuva concedeu precisão garantida a " + atacante.getNome() + "!");
            } else {
                bonusDanoTerreno = 0.10;
                System.out.println("A Poça de Chuva concedeu +10% de dano a " + atacante.getNome() + "!");
            }
        }

        boolean acertou = precisaoGarantida || Math.random() < CHANCE_BASE_DE_ACERTO;
        if (!acertou) {
            System.out.println(atacante.getNome() + " atacou, mas errou o golpe!");
            return;
        }

        int dano = calcularDano(atacante, defensor, bonusDanoTerreno);
        defensor.receberDano(dano);
        System.out.println(atacante.getNome() + " atacou " + defensor.getNome()
                + " causando " + dano + " de dano. (" + defensor.getNome() + " HP: "
                + defensor.getHp() + "/" + defensor.getHpMaximo() + ")");
    }

    /**
     * 3. Fórmula de dano definida pela equipe:
     * Dano = ATK do atacante - (DEF do defensor * 0.5)
     * O resultado é multiplicado pela vantagem/desvantagem elemental.
     * Nunca resulta em valor negativo (seria tratado como cura).
     */
    private int calcularDano(Pokesal atacante, Pokesal defensor, double bonusDanoTerreno) {
        double danoBase = atacante.getAtk() - (defensor.getDef() * 0.5);
        if (danoBase < 0) danoBase = 0;

        double multiplicadorTipo = atacante.getTipo().multiplicadorContra(defensor.getTipo());
        double dano = danoBase * multiplicadorTipo;

        // Asfalto Quente (Dia): golpes do tipo Fogo causam +15% de dano
        if (terreno == Terreno.ASFALTO_QUENTE && atacante.getTipo() == TipoElemental.FOGO) {
            dano *= 1.15;
        }

        // Bônus de dano da Poça de Chuva, se foi esse o resultado do sorteio
        dano += dano * bonusDanoTerreno;

        if (dano < 0) dano = 0;

        return (int) Math.round(dano);
    }

    private void aplicarEfeitosFimDeTurno(Pokesal p) {
        if (!p.estaVivo()) return;

        // Canteiro Central: Pokésal do tipo Planta recuperam 5% do HP máximo por turno
        if (terreno == Terreno.CANTEIRO_CENTRAL && p.getTipo() == TipoElemental.PLANTA) {
            int cura = (int) Math.round(p.getHpMaximo() * 0.05);
            p.curar(cura);
            System.out.println(p.getNome() + " recuperou " + cura + " de HP graças ao Canteiro Central.");
        }

        switch (p.getStatus()) {
            case QUEIMADO:
                // Valores de queimadura não foram especificados no documento;
                // mantido como estimativa (~1/16 do HP máx. + leve redução de ATK).
                int danoQueimadura = (int) Math.round(p.getHpMaximo() * 0.0625);
                p.receberDano(danoQueimadura);
                p.setAtk(Math.max(1, p.getAtk() - 1));
                System.out.println(p.getNome() + " sofreu " + danoQueimadura
                        + " de dano por queimadura e teve o ATK reduzido.");
                break;

            case ENVENENADO:
                // Valor de dano de veneno também não especificado no documento; mantido como estimativa.
                int danoVeneno = (int) Math.round(p.getHpMaximo() * 0.08);
                p.receberDano(danoVeneno);
                System.out.println(p.getNome() + " sofreu " + danoVeneno + " de dano por veneno.");
                break;

            case PARALISADO:
                // 3. A paralisia corta o SPD pela metade (já aplicado em getSpdEfetivo()).
                // O documento não menciona chance de travar o turno, então essa mecânica foi removida.
                break;

            case NENHUM:
            default:
                break;
        }

        if (!p.estaVivo()) {
            System.out.println(p.getNome() + " foi derrotado!");
        }
    }

    public Terreno getTerreno() { return terreno; }
}