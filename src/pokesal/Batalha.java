package pokesal;

/**
 * Controla a logica de uma batalha PvP entre dois treinadores.
 */
public class Batalha {

    private static final double CHANCE_ACERTO = 0.95;  // 95% de chance de acertar
    private static final double CHANCE_FUGA   = 0.30;  // 30% de chance se mais lento
    private static final double CHANCE_CRITICO = 0.10;  // 10% de chance de critico
    private static final double MULT_CRITICO   = 2.0;   // Dano x2 no critico
    private static final double MULTIPLICADOR_DEFESA = 0.5;
    private static final double BONUS_DANO_POCA_CHUVA = 0.10;
    private static final double MULTIPLICADOR_FOGO_ASFALTO = 1.15;
    private static final double CURA_TERRENO_PLANTA = 0.05;
    private static final double DANO_STATUS_QUEIMADURA = 0.0625;
    private static final double DANO_STATUS_VENENO = 0.08;

    /**
     * Tipos de acao que um jogador pode escolher.
     */
    public enum TipoAcao { ATACAR, USAR_ITEM, FUGIR }

    /**
     * Representa a acao escolhida por um treinador em um turno.
     */
    public static class Acao {
        private TipoAcao tipo;
        private ItemBatalha item;
        private Pokesal alvoDoItem;

        /**
         * Cria uma acao do tipo atacar.
         * @return Acao configurada.
         */
        public static Acao atacar() {
            Acao a = new Acao();
            a.tipo = TipoAcao.ATACAR;
            return a;
        }

        /**
         * Cria uma acao do tipo usar item.
         * @param item O item a ser usado.
         * @param alvo O alvo do item.
         * @return Acao configurada.
         */
        public static Acao usarItem(ItemBatalha item, Pokesal alvo) {
            Acao a = new Acao();
            a.tipo = TipoAcao.USAR_ITEM;
            a.item = item;
            a.alvoDoItem = alvo;
            return a;
        }

        /**
         * Cria uma acao do tipo fugir.
         * @return Acao configurada.
         */
        public static Acao fugir() {
            Acao a = new Acao();
            a.tipo = TipoAcao.FUGIR;
            return a;
        }

        /**
         * Retorna o tipo de acao escolhida.
         * @return TipoAcao O tipo da acao.
         */
        public TipoAcao getTipo() { return tipo; }
    }

    private Treinador treinador1;
    private Treinador treinador2;
    private Terreno terreno;
    private boolean batalhaEncerrada = false;

    /**
     * Construtor da batalha.
     * @param treinador1 Primeiro competidor.
     * @param treinador2 Segundo competidor.
     * @param terreno O ambiente onde a batalha ocorre.
     */
    public Batalha(Treinador treinador1, Treinador treinador2, Terreno terreno) {
        this.treinador1 = treinador1;
        this.treinador2 = treinador2;
        this.terreno = terreno;
    }

    /**
     * Executa um turno completo da batalha.
     * Ordem: fuga > itens > ataques (por SPD) > efeitos de fim de turno.
     */
    public void executarTurno(Acao acao1, Acao acao2) {
        Pokesal p1 = treinador1.getAtivo();
        Pokesal p2 = treinador2.getAtivo();

        System.out.println("\n--- Novo turno ---");

        // 1. Fuga tem prioridade sobre tudo
        if (acao1.getTipo() == TipoAcao.FUGIR) {
            if (tentarFuga(p1, p2)) {
                batalhaEncerrada = true;
                return;
            }
        }
        if (acao2.getTipo() == TipoAcao.FUGIR) {
            if (tentarFuga(p2, p1)) {
                batalhaEncerrada = true;
                return;
            }
        }

        // 2. Itens sao resolvidos antes dos ataques
        if (acao1.getTipo() == TipoAcao.USAR_ITEM) {
            treinador1.usarItem(acao1.item, acao1.alvoDoItem);
        }
        if (acao2.getTipo() == TipoAcao.USAR_ITEM) {
            treinador2.usarItem(acao2.item, acao2.alvoDoItem);
        }

        // 3. Ataques na ordem do SPD
        boolean t1Ataca = acao1.getTipo() == TipoAcao.ATACAR;
        boolean t2Ataca = acao2.getTipo() == TipoAcao.ATACAR;
        boolean p1Primeiro = decidirPrioridade(p1, p2);

        if (p1Primeiro) {
            if (t1Ataca && p1.estaVivo() && p2.estaVivo()) executarAtaque(p1, p2);
            if (t2Ataca && p2.estaVivo() && p1.estaVivo()) executarAtaque(p2, p1);
        } else {
            if (t2Ataca && p2.estaVivo() && p1.estaVivo()) executarAtaque(p2, p1);
            if (t1Ataca && p1.estaVivo() && p2.estaVivo()) executarAtaque(p1, p2);
        }

        // 4. Efeitos de status e terreno no fim do turno
        aplicarEfeitosFimDeTurno(p1);
        aplicarEfeitosFimDeTurno(p2);
    }

    // ==================== METODOS PRIVADOS ====================

    /**
     * Decide quem ataca primeiro com base no SPD.
     * Empate = sorteio 50/50.
     */
    private boolean decidirPrioridade(Pokesal p1, Pokesal p2) {
        int spd1 = p1.getSpdEfetivo();
        int spd2 = p2.getSpdEfetivo();

        if (spd1 == spd2) {
            return Math.random() < 0.5;
        }
        return spd1 > spd2;
    }

    /**
     * Tenta fugir da batalha.
     * Se o fugitivo for mais rapido (SPD >= oponente), fuga garantida.
     * Se mais lento, 30% de chance.
     */
    private boolean tentarFuga(Pokesal fugitivo, Pokesal oponente) {
        int spdFugitivo = fugitivo.getSpdEfetivo();
        int spdOponente = oponente.getSpdEfetivo();

        if (spdFugitivo >= spdOponente) {
            System.out.println(fugitivo.getNome() + " e mais rapido e fugiu com sucesso!");
            return true;
        }

        boolean conseguiu = Math.random() < CHANCE_FUGA;

        if (conseguiu) {
            System.out.println(fugitivo.getNome() + " teve sorte e conseguiu fugir!");
        } else {
            System.out.println(fugitivo.getNome() + " tentou fugir, mas nao conseguiu!");
        }
        return conseguiu;
    }

    /**
     * Executa um ataque de um Pokesal contra outro.
     * Verifica acerto, esquiva, calcula dano e aplica.
     */
    private void executarAtaque(Pokesal atacante, Pokesal defensor) {
        if (!atacante.estaVivo()) return;

        // Efeito do terreno Poca de Chuva para tipo Agua
        boolean precisaoGarantida = false;
        double bonusDanoTerreno = 0.0;

        if (terreno == Terreno.POCA_DE_CHUVA && atacante.getTipo() == TipoElemental.AGUA) {
            if (Math.random() < 0.5) {
                precisaoGarantida = true;
                System.out.println("A Poca de Chuva concedeu precisao garantida a " + atacante.getNome() + "!");
            } else {
                bonusDanoTerreno = BONUS_DANO_POCA_CHUVA;
                System.out.println("A Poca de Chuva concedeu +10% de dano a " + atacante.getNome() + "!");
            }
        }

        // Verificar se o golpe acertou
        boolean acertou = precisaoGarantida || Math.random() < CHANCE_ACERTO;
        if (!acertou) {
            System.out.println(atacante.getNome() + " atacou, mas errou o golpe!");
            return;
        }

        // Verificar esquiva do defensor (10% de chance)
        if (Esquiva.verificarEsquiva(defensor)) {
            System.out.println(defensor.getNome() + " desviou do golpe de " + atacante.getNome() + "! Dano anulado.");
            return;
        }

        // Calcular e aplicar dano
        int dano = calcularDano(atacante, defensor, bonusDanoTerreno);
        defensor.receberDano(dano);
        System.out.println(atacante.getNome() + " atacou " + defensor.getNome()
                + " causando " + dano + " de dano. (" + defensor.getNome()
                + " HP: " + defensor.getHp() + "/" + defensor.getHpMaximo() + ")");
    }

    /**
     * Formula de dano: ATK - (DEF * 0.5), multiplicado pela vantagem elemental.
     * Inclui bonus de terreno e chance de acerto critico.
     */
    private int calcularDano(Pokesal atacante, Pokesal defensor, double bonusDanoTerreno) {
        double danoBase = atacante.getAtk() - (defensor.getDef() * MULTIPLICADOR_DEFESA);
        if (danoBase < 0) danoBase = 0;

        double multiplicadorTipo = atacante.getTipo().calcularVantagem(defensor.getTipo());
        double dano = danoBase * multiplicadorTipo;

        // Bonus do terreno Asfalto Quente para tipo Fogo (+15%)
        if (terreno == Terreno.ASFALTO_QUENTE && atacante.getTipo() == TipoElemental.FOGO) {
            dano *= MULTIPLICADOR_FOGO_ASFALTO;
        }

        // Bonus da Poca de Chuva (se sorteado)
        dano += dano * bonusDanoTerreno;

        // Acerto critico: 10% de chance, dano x2
        if (Math.random() < CHANCE_CRITICO) {
            dano *= MULT_CRITICO;
            System.out.println(">>> ACERTO CRITICO! Dano de " + atacante.getNome() + " multiplicado por " + MULT_CRITICO + "!");
        }

        if (dano < 0) dano = 0;

        return (int) Math.round(dano);
    }

    /**
     * Aplica efeitos de status e terreno no fim do turno.
     */
    private void aplicarEfeitosFimDeTurno(Pokesal p) {
        if (!p.estaVivo()) return;

        // Canteiro Central: tipo Planta recupera 5% do HP maximo
        if (terreno == Terreno.CANTEIRO_CENTRAL && p.getTipo() == TipoElemental.PLANTA) {
            int cura = (int) Math.round(p.getHpMaximo() * CURA_TERRENO_PLANTA);
            p.curar(cura);
            System.out.println(p.getNome() + " recuperou " + cura + " de HP gracas ao Canteiro Central.");
        }

        switch (p.getStatus()) {
            case QUEIMADO:
                int danoQueimadura = (int) Math.round(p.getHpMaximo() * DANO_STATUS_QUEIMADURA);
                p.receberDano(danoQueimadura);
                p.setAtk(Math.max(1, p.getAtk() - 1));
                System.out.println(p.getNome() + " sofreu " + danoQueimadura + " de dano por queimadura.");
                break;

            case ENVENENADO:
                int danoVeneno = (int) Math.round(p.getHpMaximo() * DANO_STATUS_VENENO);
                p.receberDano(danoVeneno);
                System.out.println(p.getNome() + " sofreu " + danoVeneno + " de dano por veneno.");
                break;

            case PARALISADO:
                // SPD reduzido pela metade (ja aplicado em getSpdEfetivo)
                break;

            case NENHUM:
            default:
                break;
        }

        if (!p.estaVivo()) {
            System.out.println(p.getNome() + " foi derrotado!");
        }
    }

    // Getters

    /**
     * Retorna o terreno atual.
     * @return O terreno da batalha.
     */
    public Terreno getTerreno()          { return terreno; }

    /**
     * Retorna o estado da batalha.
     * @return true se a batalha acabou.
     */
    public boolean isBatalhaEncerrada()  { return batalhaEncerrada; }
}