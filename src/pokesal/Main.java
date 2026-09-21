package pokesal;

import java.util.Arrays;
import java.util.Scanner;

/**
 * Classe principal do jogo PokeSal no modo PvP.
 * Dois jogadores se enfrentam escolhendo acoes pelo terminal.
 */
public class Main {

    private static Scanner scanner = new Scanner(System.in);

    /**
     * Ponto de entrada principal do jogo PvP.
     * @param args Argumentos de linha de comando.
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("       Bem-vindo ao PokeSal - PvP");
        System.out.println("========================================\n");

        // Criacao dos Pokesal via Selecao Inicial (Regra de Negocio 1)
        System.out.println("--- JOGADOR 1 ---");
        Pokesal pokesalJ1 = SelecaoInicial.selecionarViaTeclado(scanner);
        System.out.println("\n--- JOGADOR 2 ---");
        Pokesal pokesalJ2 = SelecaoInicial.selecionarViaTeclado(scanner);

        // Criacao dos treinadores
        Treinador jogador1 = new Treinador("Jogador 1", Arrays.asList(pokesalJ1));
        Treinador jogador2 = new Treinador("Jogador 2", Arrays.asList(pokesalJ2));

        // Itens iniciais de cada jogador
        jogador1.adicionarItem(new Potion());
        jogador1.adicionarItem(new SuperPotion());
        jogador2.adicionarItem(new Potion());
        jogador2.adicionarItem(new SuperPotion());

        // Selecao aleatoria de Terreno (Regra de Negocio 3)
        Terreno[] terrenos = Terreno.values();
        Terreno terrenoAleatorio = terrenos[(int) (Math.random() * terrenos.length)];

        // Iniciar a batalha
        Batalha batalha = new Batalha(jogador1, jogador2, terrenoAleatorio);

        System.out.println(jogador1.getNome() + " escolheu: " + pokesalJ1.getNome());
        System.out.println(jogador2.getNome() + " escolheu: " + pokesalJ2.getNome());
        System.out.println("Terreno sorteado: " + terrenoAleatorio + "\n");

        // Loop principal da batalha
        int turno = 1;
        while (pokesalJ1.estaVivo() && pokesalJ2.estaVivo() && !batalha.isBatalhaEncerrada()) {
            System.out.println("\n============ Turno " + turno + " ============");
            System.out.println(pokesalJ1);
            System.out.println(pokesalJ2);

            Batalha.Acao acao1 = escolherAcao(jogador1);
            Batalha.Acao acao2 = escolherAcao(jogador2);

            batalha.executarTurno(acao1, acao2);
            turno++;
        }

        // Resultado final
        System.out.println("\n========================================");
        System.out.println("            FIM DE BATALHA");
        System.out.println("========================================");
        System.out.println(pokesalJ1);
        System.out.println(pokesalJ2);

        if (batalha.isBatalhaEncerrada()) {
            System.out.println("A batalha foi encerrada por fuga!");
        } else if (!pokesalJ1.estaVivo()) {
            System.out.println(jogador2.getNome() + " venceu a batalha!");
        } else if (!pokesalJ2.estaVivo()) {
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