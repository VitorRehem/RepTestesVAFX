package pokesal;

import java.util.Scanner;

/**
 * Regra de Negócio 1 (Seleção do Inicial):
 * O treinador deve escolher exatamente 1 Pokésal inicial entre as 6 opções
 * permitidas (ver {@link PokesalInicialOpcao}).
 */
public class SelecaoInicial {

    /** Imprime o menu numerado com as opções disponíveis. */
    public static void exibirOpcoes() {
        System.out.println("Escolha seu Pokésal inicial:");
        PokesalInicialOpcao[] opcoes = PokesalInicialOpcao.values();
        for (int i = 0; i < opcoes.length; i++) {
            System.out.println((i + 1) + ". " + opcoes[i]);
        }
    }

    /**
     * Converte a escolha (1 a 6) na instância do Pokésal correspondente.
     * Lança IllegalArgumentException se o índice for inválido.
     */
    public static Pokesal escolher(int indiceEscolhido) {
        PokesalInicialOpcao[] opcoes = PokesalInicialOpcao.values();
        if (indiceEscolhido < 1 || indiceEscolhido > opcoes.length) {
            throw new IllegalArgumentException(
                    "Opção inválida. Escolha um número entre 1 e " + opcoes.length + ".");
        }
        return opcoes[indiceEscolhido - 1].criarPokesal();
    }

    /**
     * Exibe o menu e lê a escolha do treinador via teclado (Scanner),
     * repetindo a pergunta até receber uma opção válida.
     * Garante que apenas 1 inicial seja retornado.
     */
    public static Pokesal selecionarViaTeclado(Scanner scanner) {
        exibirOpcoes();
        while (true) {
            System.out.print("Digite o número da sua escolha: ");
            String entrada = scanner.nextLine().trim();
            try {
                int indice = Integer.parseInt(entrada);
                Pokesal escolhido = escolher(indice);
                System.out.println("Você escolheu " + escolhido.getNome() + "!");
                return escolhido;
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite apenas o número da opção.");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
