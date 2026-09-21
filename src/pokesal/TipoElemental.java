package pokesal;

/**
 * Tipos elementais dos Pokésal e suas vantagens em combate.
 */
public enum TipoElemental {
    FOGO, AGUA, PLANTA;

    /**
     * Retorna o multiplicador de dano contra o tipo do defensor.
     * Vantagem = 2.0, desvantagem = 0.5, neutro = 1.0.
     */
    public double calcularVantagem(TipoElemental defensor) {
        if (this == FOGO && defensor == PLANTA) return 2.0;
        if (this == FOGO && defensor == AGUA) return 0.5;

        if (this == AGUA && defensor == FOGO) return 2.0;
        if (this == AGUA && defensor == PLANTA) return 0.5;

        if (this == PLANTA && defensor == AGUA) return 2.0;
        if (this == PLANTA && defensor == FOGO) return 0.5;

        return 1.0;
    }
}