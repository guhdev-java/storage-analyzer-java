package app;

import model.ArquivoInfo;
import model.EstatisticaCategoria;
import model.ResultadoAnalise;
import service.AnalisadorDisco;
import util.FormatadorTamanho;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        AnalisadorDisco analisador = new AnalisadorDisco();
        FormatadorTamanho formatador = new FormatadorTamanho();

        try (Scanner sc = new Scanner(System.in)) {
            System.out.print("Digite o caminho da pasta/arquivo: ");
            String caminho = sc.nextLine().trim();

            if (caminho.isEmpty()) {
                System.out.println("Caminho não pode estar vazio.");
                return;
            }
            if (!Files.exists(Paths.get(caminho))) {
                System.out.println("Caminho não existe: " + caminho);
                return;
            }

            ResultadoAnalise resultado = analisador.analisar(caminho);

            List<ArquivoInfo> arquivos = resultado.getArquivos();
            arquivos.sort((a, b) -> Long.compare(b.getTamanho(), a.getTamanho()));

            System.out.println("\n=== ARQUIVOS ORDENADOS POR TAMANHO ===\n");
            for (ArquivoInfo a : arquivos) {
                System.out.println(a.getNome() + " - " + formatador.formatar(a.getTamanho()));
            }

            System.out.println("\n=== ESTATÍSTICAS POR CATEGORIA ===\n");
            for (Map.Entry<String, EstatisticaCategoria> entry : resultado.getEstatisticas().entrySet()) {
                EstatisticaCategoria estat = entry.getValue();
                System.out.println("Categoria: " + entry.getKey());
                System.out.println("  Quantidade: " + estat.getQuantidadeArquivos());
                System.out.println("  Tamanho total: " + formatador.formatar(estat.getTamanhoTotal()));
            }
        }
    }
}