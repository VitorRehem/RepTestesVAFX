package pokesal;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um treinador com sua equipe de Pokésal e mochila de itens.
 * Limite de 2 itens usados por batalha.
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

    /** Retorna o primeiro item da mochila, ou null se vazia. */
    public ItemBatalha getPrimeiroItemDisponivel() {
        return mochila.isEmpty() ? null : mochila.get(0);
    }

    /**
     * Usa um item da mochila no alvo.
     * Retorna false se o limite foi atingido ou o item não está disponível.
     */
    public boolean usarItem(ItemBatalha item, Pokesal alvo) {
        if (!podeUsarItem()) {
            System.out.println(nome + " ja usou o maximo de " + LIMITE_ITENS_POR_BATALHA + " itens nesta batalha!");
            return false;
        }
        if (!mochila.contains(item)) {
            System.out.println("O item " + item.getNome() + " nao esta na mochila de " + nome + ".");
            return false;
        }
        item.usar(alvo);
        mochila.remove(item);
        itensUsados++;
        return true;
    }

    // Getters e Setters
    public String getNome()         { return nome; }
    public Pokesal getAtivo()       { return ativo; }
    public List<Pokesal> getEquipe(){ return equipe; }
    public int getItensUsados()     { return itensUsados; }

    public void setAtivo(Pokesal ativo) { this.ativo = ativo; }
}