public class BatalhaPrototipo {

    public static void main(String[] args) {
        
        System.out.println("Iniciando protótipo de testes...\n");
        
        // 1. SETUP: Criando os monstros com seus atributos base
        Pokesal charSal = new CharSal("CharSal", 100, 30, 20, 50, TipoElemental.FOGO);
        Pokesal bulbaSal = new BulbaSal("BulbaSal", 100, 25, 25, 45, TipoElemental.PLANTA);

        // 2. TESTE DE VANTAGEM ELEMENTAL
        System.out.println("--- Teste: Vantagem Elemental (Fogo x Planta) ---");
        int hpInicialBulba = bulbaSal.getHp();
        
        // Executa o ataque
        charSal.atacar(bulbaSal);
        
        // Calcula matematicamente o que deveria acontecer
        int danoEsperado = (int) ((charSal.getAtk() - (bulbaSal.getDef() * 0.5)) * 2.0);
        int hpEsperado = hpInicialBulba - danoEsperado;
        
        // Validação manual (substitui o assertEquals do JUnit)
        if (bulbaSal.getHp() == hpEsperado) {
            System.out.println("SUCESSO: O dano aplicou o multiplicador x2.0 corretamente.");
        } else {
            System.out.println("ERRO: O dano falhou. HP atual é " + bulbaSal.getHp() + 
                               " mas deveria ser " + hpEsperado); 
        }

        // 3. TESTE DE INICIATIVA (Velocidade)
        System.out.println("\n--- Teste: Ordem de Ataque por SPD ---");
        
        // Validação manual (substitui o assertTrue do JUnit)
        if (charSal.getSpd() > bulbaSal.getSpd()) {
            System.out.println("SUCESSO: CharSal é mais rápido e ataca primeiro.");
        } else {
            System.out.println("ERRO: BulbaSal está mais rápido que o CharSal.");
        }
    }
}