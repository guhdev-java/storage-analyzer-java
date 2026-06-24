package service;

import model.ArquivoInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class AnalisadorDisco {

    public List<ArquivoInfo> analisar (String caminho) {
        List<ArquivoInfo> arquivos = new ArrayList<>();

        Path pasta = Paths.get(caminho);

        try (Stream<Path> caminhos = Files.walk(pasta)) {

            caminhos
                    .filter(Files::isRegularFile)
                    .forEach(arquivo -> {
                        try {
                            String nome = arquivo.getFileName().toString();
                            String caminhoCompleto = arquivo.toString();
                            long tamanho = Files.size(arquivo);

                            ArquivoInfo info =  new ArquivoInfo(nome, caminhoCompleto, tamanho);

                            arquivos.add(info);

                        } catch (IOException e) {
                            System.out.println("Erro ao ler arquivo: " + e.getMessage());
                        }
                    });
        } catch (IOException e) {
            System.out.println("Erro ao ler pasta: " + e.getMessage());
        }
        return arquivos;
    }
}