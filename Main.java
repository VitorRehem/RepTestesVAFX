package pokesal;

import java.util.Arrays;

/**
 * Demonstração do modo PvE: o jogador ("Ana") enfrenta um oponente
 * controlado pelo sistema ("IA"), até um dos dois Pokésal desmaiar.
 */
public class Main {
    public static void main(String[] args) {
        SquirSal squirt = new SquirSal("SquirtSal", 100, 30, 20, 40, TipoElemental.AGUA);
        CharSal charmander = new CharSal("CharmanderSal", 100, 32, 15, 40, TipoElemental.FOGO); // SPD igual para testar o empate

        Treinador jogador = new Treinador("Ana", Arrays.asList(squirt));
        Treinador maquina = new Treinador("IA", Arrays.asList(charmander));

        jogador.adicionarItem(new Potion());
        maquina.adicionarItem(new SuperPotion());

        Batalha batalha = new Batalha(jogador, maquina, Terreno.POCA_DE_CHUVA);

        int turno = 1;
        while (squirt.estaVivo() && charmander.estaVivo() && turno <= 10) {
            System.out.println("\n=== Turno " + turno + " ===");

            // Ação do jogador (aqui fixada em "atacar" para fins de demonstração;
            // numa aplicação real isso viria da entrada do usuário)
            Batalha.Acao acaoJogador = Batalha.Acao.atacar();

            // Ação da IA, decidida automaticamente (modo PvE)
            Batalha.Acao acaoMaquina = OponenteIA.decidirAcao(maquina);

            batalha.executarTurno(acaoJogador, acaoMaquina);
            turno++;
        }

        System.out.println("\n=== Fim de batalha ===");
        System.out.println(squirt);
        System.out.println(charmander);

        if (!squirt.estaVivo()) {
            System.out.println(maquina.getNome() + " venceu a batalha!");
        } else if (!charmander.estaVivo()) {
            System.out.println(jogador.getNome() + " venceu a batalha!");
        } else {
            System.out.println("A batalha atingiu o limite de turnos sem vencedor.");
        }
    }
}