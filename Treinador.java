package pokesal;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um treinador, sua equipe de Pokésal e sua mochila de itens.
 * Regra: no máximo 2 itens usados por batalha.
 */
public class Treinador {
    private static final int LIMITE_ITENS_POR_BATALHA = 2;

    private String nome;
    private List<Pokesal> equipe;
    private Pokesal ativo;
    private List<ItemBatalha> mochila;
    private int itensUsados;

    public Treinador(String nome, List<Pokesal> equipe) {
        this.nome = nome;
        this.equipe = equipe;
        this.ativo = equipe.isEmpty() ? null : equipe.get(0);
        this.mochila = new ArrayList<>();
        this.itensUsados = 0;
    }

    public void adicionarItem(ItemBatalha item) {
        mochila.add(item);
    }

    public boolean podeUsarItem() {
        return itensUsados < LIMITE_ITENS_POR_BATALHA;
    }

    public boolean temItens() {
        return !mochila.isEmpty();
    }

    /** Retorna o primeiro item disponível na mochila (útil para a IA), ou null se vazia. */
    public ItemBatalha getPrimeiroItemDisponivel() {
        return mochila.isEmpty() ? null : mochila.get(0);
    }

    /**
     * Usa um item da mochila em um alvo. Retorna false se o limite já foi atingido
     * ou o item não estiver disponível.
     */
    public boolean usarItem(ItemBatalha item, Pokesal alvo) {
        if (!podeUsarItem()) {
            System.out.println(nome + " já usou o máximo de " + LIMITE_ITENS_POR_BATALHA + " itens nesta batalha!");
            return false;
        }
        if (!mochila.contains(item)) {
            System.out.println("O item " + item.getNome() + " não está na mochila de " + nome + ".");
            return false;
        }
        item.usar(alvo);
        mochila.remove(item);
        itensUsados++;
        return true;
    }

    public String getNome() { return nome; }

    public Pokesal getAtivo() { return ativo; }
    public void setAtivo(Pokesal ativo) { this.ativo = ativo; }

    public List<Pokesal> getEquipe() { return equipe; }

    public int getItensUsados() { return itensUsados; }
}