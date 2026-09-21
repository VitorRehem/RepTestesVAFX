package pokesal;

/**
 * Contrato para qualquer item usável em batalha.
 */
public interface ItemBatalha {
    String getNome();
    void usar(Pokesal alvo);
}