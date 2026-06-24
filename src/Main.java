import model.ArquivoInfo;
import service.AnalisadorDisco;
import util.FormatadorTamanho;

import java.util.Scanner;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        AnalisadorDisco analisador = new AnalisadorDisco();
        FormatadorTamanho formatador = new FormatadorTamanho();

        System.out.print("Digite o caminho da pasta/arquivo: ");
        String caminho = sc.nextLine();

        List<ArquivoInfo> arquivos = analisador.analisar(caminho);
        arquivos.sort((a, b ) -> Long.compare(b.getTamanho(), a.getTamanho()));

        System.out.println("\n=== ARQUIVOS ORDENADOS POR TAMANHO ===\n");

        for(ArquivoInfo a : arquivos){
            System.out.println(a.getNome() + " - " + formatador.formatar(a.getTamanho()));
        }
    }
}
