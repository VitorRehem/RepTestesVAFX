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

    /**
     * Construtor do Treinador.
     * @param nome O nome do treinador.
     * @param equipe A lista de Pokesal do treinador.
     */
    public Treinador(String nome, List<Pokesal> equipe) {
        this.nome = nome;
        this.equipe = equipe;
        this.ativo = equipe.isEmpty() ? null : equipe.get(0);
        this.mochila = new ArrayList<>();
        this.itensUsados = 0;
    }

    /**
     * Adiciona um item a mochila do treinador.
     * @param item O item a ser guardado.
     */
    public void adicionarItem(ItemBatalha item) {
        mochila.add(item);
    }

    /**
     * Verifica se o treinador ainda nao atingiu o limite de itens da batalha.
     * @return true se puder usar mais itens.
     */
    public boolean podeUsarItem() {
        return itensUsados < LIMITE_ITENS_POR_BATALHA;
    }

    /**
     * Verifica se ha itens na mochila.
     * @return true se a mochila nao estiver vazia.
     */
    public boolean temItens() {
        return !mochila.isEmpty();
    }

    /** 
     * Retorna o primeiro item da mochila, ou null se vazia. 
     * @return O primeiro item disponivel.
     */
    public ItemBatalha getPrimeiroItemDisponivel() {
        return mochila.isEmpty() ? null : mochila.get(0);
    }

    /**
     * Usa um item da mochila no alvo.
     * Retorna false se o limite foi atingido ou o item nao esta disponivel.
     * @param item O item a ser usado.
     * @param alvo O Pokesal que recebera o efeito do item.
     * @return true se o uso foi bem-sucedido.
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
    /** @return O nome do treinador. */
    public String getNome()         { return nome; }
    /** @return O Pokesal ativo no momento. */
    public Pokesal getAtivo()       { return ativo; }
    /** @return A equipe completa do treinador. */
    public List<Pokesal> getEquipe(){ return equipe; }
    /** @return A quantidade de itens ja usados nesta batalha. */
    public int getItensUsados()     { return itensUsados; }

    /** @param ativo Define o novo Pokesal ativo. */
    public void setAtivo(Pokesal ativo) { this.ativo = ativo; }
}