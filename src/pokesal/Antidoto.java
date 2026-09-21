package pokesal;

/**
 * Item que cura o status de envenenamento de um Pokesal.
 */
public class Antidoto implements ItemBatalha {

    @Override
    public String getNome() { return "Antidote"; }

    @Override
    public void usar(Pokesal alvo) {
        if (alvo.getStatus() == StatusEfeito.ENVENENADO) {
            alvo.setStatus(StatusEfeito.NENHUM);
            System.out.println(alvo.getNome() + " foi curado do veneno!");
        } else {
            System.out.println("Antidoto não teve efeito em " + alvo.getNome() + " (não está envenenado).");
        }
    }
}