/**
 * Representa a entidade base de todos os monstros do simulador PokeSal.
 */
public abstract class Pokesal {
    
    // Evitando "Magic Numbers" nas lógicas de batalha
    private static final double FATOR_DEFESA = 0.5;

    private String nome;
    private int hp;
    private int hpMaximo;
    private int atk;
    private int def;
    private int spd;
    private TipoElemental tipo;

    // Construtor obrigatório para inicializar os atributos
    public Pokesal(String nome, int hp, int atk, int def, int spd, TipoElemental tipo) {
        this.nome = nome;
        this.hp = hp;
        this.hpMaximo = hp;
        this.atk = atk;
        this.def = def;
        this.spd = spd;
        this.tipo = tipo;
    }

    // Getters para permitir que outras classes leiam os valores
    public int getHp() { return this.hp; }
    public int getAtk() { return this.atk; }
    public int getDef() { return this.def; }
    public int getSpd() { return this.spd; }
    public TipoElemental getTipo() { return this.tipo; }

    /**
     * Calcula e aplica o dano ao oponente baseado nos atributos base e vantagens elementais.
     *
     * @param oponente Pokesal que receberá o ataque no turno.
     */
    public void atacar(Pokesal oponente) {
        double multiplicadorElemental = this.tipo.calcularVantagem(oponente.getTipo());
        
        // Fórmula de dano base: ATK do atacante menos metade da DEF do defensor
        double danoBruto = this.atk - (oponente.getDef() * FATOR_DEFESA);
        int danoFinal = (int) (danoBruto * multiplicadorElemental);

        if (danoFinal < 0) {
            danoFinal = 0; // Evita curar o oponente com dano negativo
        }

        oponente.receberDano(danoFinal);
    }

    /**
     * Deduz o dano recebido do HP atual, garantindo que o HP não fique negativo.
     * 
     * @param dano Quantidade de dano processado a ser subtraído.
     */
    public void receberDano(int dano) {
        this.hp -= dano;
        if (this.hp < 0) {
            this.hp = 0;
        }
    }
}