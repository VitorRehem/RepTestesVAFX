package pokesal;

/**
 * Verifica se um Pokésal consegue desviar de um golpe (Esquiva).
 * Chance base fixa de 10%.
 */
public class Esquiva {

    private static final double CHANCE_ESQUIVA = 0.10;

    /**
     * Retorna true se o defensor desviou do golpe (dano anulado).
     * @param defensor O Pokesal alvo do ataque.
     * @return true se desviou do golpe.
     */
    public static boolean verificarEsquiva(Pokesal defensor) {
        return Math.random() < CHANCE_ESQUIVA;
    }
}
