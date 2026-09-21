package pokesal;

/**
 * Tipos elementais dos Pokésal e suas vantagens em combate.
 */
public enum TipoElemental {
    FOGO, AGUA, PLANTA;

    private static final double VANTAGEM = 2.0;
    private static final double DESVANTAGEM = 0.5;
    private static final double NEUTRO = 1.0;

    /**
     * Retorna o multiplicador de dano contra o tipo do defensor.
     * Vantagem = 2.0, desvantagem = 0.5, neutro = 1.0.
     * @param defensor O tipo elemental que vai receber o dano.
     * @return O multiplicador de dano.
     */
    public double calcularVantagem(TipoElemental defensor) {
        if (this == FOGO && defensor == PLANTA) return VANTAGEM;
        if (this == FOGO && defensor == AGUA) return DESVANTAGEM;

        if (this == AGUA && defensor == FOGO) return VANTAGEM;
        if (this == AGUA && defensor == PLANTA) return DESVANTAGEM;

        if (this == PLANTA && defensor == AGUA) return VANTAGEM;
        if (this == PLANTA && defensor == FOGO) return DESVANTAGEM;

        return NEUTRO;
    }
}