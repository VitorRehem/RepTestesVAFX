package pokesal;

public class Potion implements ItemBatalha {
    private static final int CURA = 20;

    @Override
    public String getNome() { return "Potion"; }

    @Override
    public void usar(Pokesal alvo) {
        alvo.curar(CURA);
        System.out.println(alvo.getNome() + " recuperou " + CURA + " de HP usando Potion.");
    }
}