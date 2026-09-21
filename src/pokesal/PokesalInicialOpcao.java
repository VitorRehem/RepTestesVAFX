package pokesal;

/**
 * Opções de Pokésal inicial que o treinador pode escolher.
 * Cada opção sabe como criar a instância do Pokésal correspondente.
 */
public enum PokesalInicialOpcao {
    BULBASAL("BulbaSal", 100, 25, 25, 45, TipoElemental.PLANTA),
    CHARSAL("CharSal", 100, 30, 20, 50, TipoElemental.FOGO),
    SQUIRSAL("SquirSal", 100, 28, 22, 40, TipoElemental.AGUA),
    CHIKOSAL("ChikoSal", 100, 24, 26, 44, TipoElemental.PLANTA),
    CYNDASAL("CyndaSal", 100, 31, 19, 48, TipoElemental.FOGO),
    TOTOSAL("TotoSal", 100, 27, 23, 42, TipoElemental.AGUA);

    private final String nome;
    private final int hp;
    private final int atk;
    private final int def;
    private final int spd;
    private final TipoElemental tipo;

    PokesalInicialOpcao(String nome, int hp, int atk, int def, int spd, TipoElemental tipo) {
        this.nome = nome;
        this.hp = hp;
        this.atk = atk;
        this.def = def;
        this.spd = spd;
        this.tipo = tipo;
    }

    /** Cria a instância do Pokésal correspondente a esta opção. */
    public Pokesal criarPokesal() {
        switch (this) {
            case BULBASAL:  return new BulbaSal(nome, hp, atk, def, spd, tipo);
            case CHARSAL:   return new CharSal(nome, hp, atk, def, spd, tipo);
            case SQUIRSAL:  return new SquirSal(nome, hp, atk, def, spd, tipo);
            case CHIKOSAL:  return new ChikoSal(nome, hp, atk, def, spd, tipo);
            case CYNDASAL:  return new CyndaSal(nome, hp, atk, def, spd, tipo);
            case TOTOSAL:   return new TotoSal(nome, hp, atk, def, spd, tipo);
            default: throw new IllegalStateException("Opção desconhecida: " + this);
        }
    }

    @Override
    public String toString() {
        return nome + " [HP:" + hp + " ATK:" + atk + " DEF:" + def + " SPD:" + spd + " Tipo:" + tipo + "]";
    }
}
