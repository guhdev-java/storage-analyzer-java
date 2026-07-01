package service;

import model.ArquivoInfo;
import model.Categoria;
import model.ResultadoAnalise;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class AnalisadorDisco {

    private static final int INTERVALO_PROGRESSO = 50;

    public ResultadoAnalise analisar(String caminho) {
        Path pasta = Paths.get(caminho);
        List<ArquivoInfo> arquivos = new ArrayList<>();

        try {
            int total = contarArquivos(pasta);
            AtomicInteger processados = new AtomicInteger(0);

            try (Stream<Path> caminhos = Files.walk(pasta)) {
                caminhos
                        .filter(Files::isRegularFile)
                        .forEach(arquivo -> {
                            try {
                                String nome = arquivo.getFileName().toString();
                                String caminhoCompleto = arquivo.toString();
                                long tamanho = Files.size(arquivo);
                                Categoria categoria = Categoria.de(nome);
                                arquivos.add(new ArquivoInfo(nome, caminhoCompleto, tamanho, categoria));

                                int n = processados.incrementAndGet();
                                if (total > INTERVALO_PROGRESSO && n % INTERVALO_PROGRESSO == 0) {
                                    exibirProgresso(n, total);
                                }
                            } catch (IOException e) {
                                System.err.println("Erro ao ler arquivo: " + e.getMessage());
                            }
                        });
            }

            if (total > INTERVALO_PROGRESSO) {
                System.out.print("\r");
            }

        } catch (IOException e) {
            System.err.println("Erro ao ler pasta: " + e.getMessage());
        }

        return new ResultadoAnalise(arquivos);
    }

    private int contarArquivos(Path pasta) throws IOException {
        try (Stream<Path> caminhos = Files.walk(pasta)) {
            return (int) caminhos.filter(Files::isRegularFile).count();
        }
    }

    private void exibirProgresso(int processados, int total) {
        int pct = (int) ((processados / (double) total) * 100);
        int barras = pct / 5;
        String barra = "█".repeat(barras) + "░".repeat(20 - barras);
        System.out.printf("\r  [%s] %d%% (%d/%d)", barra, pct, processados, total);
    }
}