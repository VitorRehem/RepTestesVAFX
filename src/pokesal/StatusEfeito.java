package pokesal;

/**
 * Efeitos de status que um Pokésal pode sofrer durante a batalha.
 */
public enum StatusEfeito {
    NENHUM,
    QUEIMADO,    // Reduz HP e ATK ao final do turno
    ENVENENADO,  // Causa dano progressivo ao final do turno
    PARALISADO   // Reduz o SPD (e pode impedir a ação)
}