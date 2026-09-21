package pokesal;

public class SuperPotion implements ItemBatalha {
    private static final int CURA = 50;

    @Override
    public String getNome() { return "Super Potion"; }

    @Override
    public void usar(Pokesal alvo) {
        alvo.curar(CURA);
        System.out.println(alvo.getNome() + " recuperou " + CURA + " de HP usando Super Potion.");
    }
}