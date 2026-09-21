package pokesal;

/**
 * Mecânica do Estacionamento da UCSal (Efeito de Terreno).
 */
public enum Terreno {
    NORMAL,
    ASFALTO_QUENTE,     // Dia: golpes do tipo Fogo causam +15% de dano
    POCA_DE_CHUVA,      // Golpes do tipo Água ganham +10% de precisão/dano
    CANTEIRO_CENTRAL    // Pokésal do tipo Planta recuperam 5% do HP máximo por turno
}