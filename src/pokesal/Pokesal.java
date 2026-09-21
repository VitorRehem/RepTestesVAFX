package pokesal;

/**
 * Classe base para todos os Pokésal.
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

    /**
     * Construtor da classe Pokesal.
     * @param nome Nome do Pokesal.
     * @param hp Vida inicial e maxima.
     * @param atk Valor de ataque.
     * @param def Valor de defesa.
     * @param spd Valor de velocidade.
     * @param tipo Tipo elemental.
     */
    public Pokesal(String nome, int hp, int atk, int def, int spd, TipoElemental tipo) {
        this.nome = nome;
        this.hpMaximo = hp;
        this.hp = hp;
        this.atk = atk;
        this.def = def;
        this.spd = spd;
        this.tipo = tipo;
        this.status = StatusEfeito.NENHUM;
    }

    /**
     * Verifica se o Pokesal ainda tem HP.
     * @return true se o HP for maior que zero.
     */
    public boolean estaVivo() {
        return hp > 0;
    }

    /**
     * Aplica dano ao Pokesal, reduzindo seu HP.
     * @param dano Quantidade de dano recebida.
     */
    public void receberDano(int dano) {
        hp -= dano;
        if (hp < 0) hp = 0;
    }

    /**
     * Recupera o HP do Pokesal, respeitando o limite maximo.
     * @param quantidade Pontos de HP a curar.
     */
    public void curar(int quantidade) {
        hp = Math.min(hpMaximo, hp + quantidade);
    }

    /** Retorna o SPD efetivo (reduzido pela metade se paralisado). */
    public int getSpdEfetivo() {
        if (status == StatusEfeito.PARALISADO) {
            return spd / 2;
        }
        return spd;
    }

    // Getters e Setters
    /** @return Nome do Pokesal. */
    public String getNome()       { return nome; }
    /** @return HP atual. */
    public int getHp()            { return hp; }
    /** @return HP maximo. */
    public int getHpMaximo()      { return hpMaximo; }
    /** @return Valor de Ataque. */
    public int getAtk()           { return atk; }
    /** @return Valor de Defesa. */
    public int getDef()           { return def; }
    /** @return Velocidade base. */
    public int getSpd()           { return spd; }
    /** @return Tipo elemental. */
    public TipoElemental getTipo(){ return tipo; }
    /** @return Status de efeito atual. */
    public StatusEfeito getStatus() { return status; }

    /** @param atk Novo valor de ataque. */
    public void setAtk(int atk)               { this.atk = atk; }
    /** @param status Novo status de efeito. */
    public void setStatus(StatusEfeito status) { this.status = status; }

    @Override
    public String toString() {
        return nome + " [HP: " + hp + "/" + hpMaximo
                + " | ATK: " + atk + " | DEF: " + def
                + " | SPD: " + spd + " | Tipo: " + tipo
                + " | Status: " + status + "]";
    }
}
