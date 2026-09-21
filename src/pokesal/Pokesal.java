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

    /** Retorna o SPD efetivo (reduzido pela metade se paralisado). */
    public int getSpdEfetivo() {
        if (status == StatusEfeito.PARALISADO) {
            return spd / 2;
        }
        return spd;
    }

    // Getters e Setters
    public String getNome()       { return nome; }
    public int getHp()            { return hp; }
    public int getHpMaximo()      { return hpMaximo; }
    public int getAtk()           { return atk; }
    public int getDef()           { return def; }
    public int getSpd()           { return spd; }
    public TipoElemental getTipo(){ return tipo; }
    public StatusEfeito getStatus() { return status; }

    public void setAtk(int atk)               { this.atk = atk; }
    public void setStatus(StatusEfeito status) { this.status = status; }

    @Override
    public String toString() {
        return nome + " [HP: " + hp + "/" + hpMaximo
                + " | ATK: " + atk + " | DEF: " + def
                + " | SPD: " + spd + " | Tipo: " + tipo
                + " | Status: " + status + "]";
    }
}
