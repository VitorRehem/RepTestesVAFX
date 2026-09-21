package pokesal;

/**
 * Representa os tipos elementais dos Pokésal.
 * Regra de vantagem (triângulo): Água > Fogo > Planta > Água
 */
public enum TipoElemental {
    AGUA, FOGO, PLANTA, NORMAL;

    /**
     * Retorna o multiplicador de dano quando este tipo ataca o tipo "defensor".
     * x2.0 = super efetivo | x0.5 = pouco efetivo | x1.0 = neutro
     */
    public double multiplicadorContra(TipoElemental defensor) {
        if (this == AGUA && defensor == FOGO)   return 2.0;
        if (this == AGUA && defensor == PLANTA) return 0.5;

        if (this == FOGO && defensor == PLANTA) return 2.0;
        if (this == FOGO && defensor == AGUA)   return 0.5;

        if (this == PLANTA && defensor == AGUA) return 2.0;
        if (this == PLANTA && defensor == FOGO) return 0.5;

        return 1.0;
    }
}