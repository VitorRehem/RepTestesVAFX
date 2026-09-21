package pokesal;

import java.util.Arrays;
import java.util.Scanner;

/**
 * Classe principal do jogo PokeSal no modo PvP.
 * Dois jogadores se enfrentam escolhendo acoes pelo terminal.
 */
public class Main {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("       Bem-vindo ao PokeSal - PvP");
        System.out.println("========================================\n");

        // Criacao dos Pokesal
        SquirSal squirt = new SquirSal("SquirtSal", 100, 30, 20, 40, TipoElemental.AGUA);
        CharSal charmander = new CharSal("CharmanderSal", 100, 32, 15, 45, TipoElemental.FOGO);

        // Criacao dos treinadores
        Treinador jogador1 = new Treinador("Jogador 1", Arrays.asList(squirt));
        Treinador jogador2 = new Treinador("Jogador 2", Arrays.asList(charmander));

        // Itens iniciais de cada jogador
        jogador1.adicionarItem(new Potion());
        jogador1.adicionarItem(new SuperPotion());
        jogador2.adicionarItem(new Potion());
        jogador2.adicionarItem(new SuperPotion());

        // Iniciar a batalha
        Batalha batalha = new Batalha(jogador1, jogador2, Terreno.POCA_DE_CHUVA);

        System.out.println(jogador1.getNome() + " escolheu: " + squirt.getNome());
        System.out.println(jogador2.getNome() + " escolheu: " + charmander.getNome());
        System.out.println("Terreno: Poca de Chuva\n");

        // Loop principal da batalha
        int turno = 1;
        while (squirt.estaVivo() && charmander.estaVivo() && !batalha.isBatalhaEncerrada()) {
            System.out.println("\n============ Turno " + turno + " ============");
            System.out.println(squirt);
            System.out.println(charmander);

            Batalha.Acao acao1 = escolherAcao(jogador1);
            Batalha.Acao acao2 = escolherAcao(jogador2);

            batalha.executarTurno(acao1, acao2);
            turno++;
        }

        // Resultado final
        System.out.println("\n========================================");
        System.out.println("            FIM DE BATALHA");
        System.out.println("========================================");
        System.out.println(squirt);
        System.out.println(charmander);

        if (batalha.isBatalhaEncerrada()) {
            System.out.println("A batalha foi encerrada por fuga!");
        } else if (!squirt.estaVivo()) {
            System.out.println(jogador2.getNome() + " venceu a batalha!");
        } else if (!charmander.estaVivo()) {
            System.out.println(jogador1.getNome() + " venceu a batalha!");
        }

        scanner.close();
    }

    /**
     * Exibe o menu e le a acao escolhida pelo jogador.
     * Repete ate receber uma opcao valida.
     */
    private static Batalha.Acao escolherAcao(Treinador treinador) {
        Pokesal ativo = treinador.getAtivo();

        while (true) {
            System.out.println("\n--- " + treinador.getNome() + " (" + ativo.getNome() + ") ---");
            System.out.println("1. Atacar");
            System.out.println("2. Usar Item");
            System.out.println("3. Fugir");
            System.out.print("Escolha sua acao: ");

            String entrada = scanner.nextLine().trim();

            switch (entrada) {
                case "1":
                    return Batalha.Acao.atacar();

                case "2":
                    if (!treinador.podeUsarItem()) {
                        System.out.println("Voce ja usou o limite de itens nesta batalha!");
                        break;
                    }
                    if (!treinador.temItens()) {
                        System.out.println("Sua mochila esta vazia!");
                        break;
                    }
                    ItemBatalha item = treinador.getPrimeiroItemDisponivel();
                    System.out.println("Usando: " + item.getNome());
                    return Batalha.Acao.usarItem(item, ativo);

                case "3":
                    return Batalha.Acao.fugir();

                default:
                    System.out.println("Opcao invalida! Digite 1, 2 ou 3.");
            }
        }
    }
}