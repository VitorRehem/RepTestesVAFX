package pokesal;

/**
 * Modo de jogo: PvE — o oponente é controlado automaticamente pelo sistema.
 * Esta IA decide a ação do treinador-máquina a cada turno.
 *
 * Regra simples adotada:
 * - Se o Pokésal ativo estiver com HP crítico (<= 30% do HP máximo) e o
 *   treinador ainda puder usar item (limite de 2 por batalha) e tiver algum
 *   item na mochila, a IA usa esse item nele mesmo.
 * - Caso contrário, a IA ataca.
 */
public class OponenteIA {

    private static final double LIMIAR_HP_CRITICO = 0.30; // 30% do HP máximo

    public static Batalha.Acao decidirAcao(Treinador treinadorIA) {
        Pokesal ativo = treinadorIA.getAtivo();

        boolean hpCritico = ativo.getHp() <= ativo.getHpMaximo() * LIMIAR_HP_CRITICO;

        if (hpCritico && treinadorIA.podeUsarItem() && treinadorIA.temItens()) {
            ItemBatalha item = treinadorIA.getPrimeiroItemDisponivel();
            System.out.println("[IA] " + treinadorIA.getNome() + " decidiu usar "
                    + item.getNome() + " em " + ativo.getNome() + ".");
            return Batalha.Acao.usarItem(item, ativo);
        }

        return Batalha.Acao.atacar();
    }
}
