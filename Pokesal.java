<<<<<<< HEAD
package pokesal;

/**
 * Classe base para todos os Pokésal (ex: SquirSal, CharSal, BulbaSal...).
 */
public abstract class Pokesal {
    protected String nome;
    protected int hpMaximo;
    protected int hp;
    protected int atk;
    protected int def;
    protected int spd;
    protected TipoElemental tipo;
    protected StatusEfeito status;

    public Pokesal(String nome, int hp, int atk, int def, int spd, TipoElemental tipo) {
        this.nome = nome;
        this.hpMaximo = hp;
        this.hp = hp;
=======
    // Representação de identidades

    private String nome;
    private int hp;
    private int atk;
    private int def;
    private int speed;
    private TipoElemental tipo;
    private EfeitoStatus statusAtual = EfeitoStatus.NENHUM;
    private int turnosEnvenenado = 0;



    // Construtor obrigatório para inicializar os atributos
    public Pokesal(String nome, int hp, int atk, int def, int spd, TipoElemental tipo) {
        this.nome = nome;
        this.hp = hp;
        this.hpMaximo = hp;
>>>>>>> ccc4ba58bcfc9db25795e1df5fe34d1ffed2a774
        this.atk = atk;
        this.def = def;
        this.spd = spd;
        this.tipo = tipo;
<<<<<<< HEAD
        this.status = StatusEfeito.NENHUM;
    }

    public boolean estaVivo() {
        return hp > 0;
    }

    public void receberDano(int dano) {
        hp -= dano;
        if (hp < 0) hp = 0;
    }

    public void curar(int quantidade) {
        hp = Math.min(hpMaximo, hp + quantidade);
    }

    /** SPD efetivo, já considerando redução por paralisia. */
    public int getSpdEfetivo() {
        if (status == StatusEfeito.PARALISADO) {
            return spd / 2;
        }
        return spd;
    }

    // ----- getters e setters -----
    public String getNome() { return nome; }

    public int getHp() { return hp; }

    public int getHpMaximo() { return hpMaximo; }

    public int getAtk() { return atk; }
    public void setAtk(int atk) { this.atk = atk; }

    public int getDef() { return def; }

    public int getSpd() { return spd; }

    public TipoElemental getTipo() { return tipo; }

    public StatusEfeito getStatus() { return status; }
    public void setStatus(StatusEfeito status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("%s [HP: %d/%d | ATK: %d | DEF: %d | SPD: %d | Tipo: %s | Status: %s]",
                nome, hp, hpMaximo, atk, def, spd, tipo, status);
    }
}
=======
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
>>>>>>> ccc4ba58bcfc9db25795e1df5fe34d1ffed2a774
