package service;

import model.ArquivoInfo;
import model.EstatisticaCategoria;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class AnalisadorDisco {

    public List<ArquivoInfo> analisar (String caminho) {
        List<ArquivoInfo> arquivos = new ArrayList<>();
        Map<String, EstatisticaCategoria> estatisticas = new HashMap<>();

        Path pasta = Paths.get(caminho);

        try (Stream<Path> caminhos = Files.walk(pasta)) {

            caminhos
                    .filter(Files::isRegularFile)
                    .forEach(arquivo -> {
                        try {
                            String nome = arquivo.getFileName().toString();
                            String caminhoCompleto = arquivo.toString();
                            long tamanho = Files.size(arquivo);

                            String categoria = descobrirCategoria(nome);
                            estatisticas.putIfAbsent(categoria, new EstatisticaCategoria());
                            estatisticas.get(categoria)
                                    .adicionarArquivo(tamanho);

                            ArquivoInfo info =  new ArquivoInfo(nome, caminhoCompleto, tamanho);

                            arquivos.add(info);

                        } catch (IOException e) {
                            System.out.println("Erro ao ler arquivo: " + e.getMessage());
                        }
                    });
        } catch (IOException e) {
            System.out.println("Erro ao ler pasta: " + e.getMessage());
        }

        for (Map.Entry<String, EstatisticaCategoria> entry : estatisticas.entrySet()){

            String categoria = entry.getKey();
            EstatisticaCategoria info = entry.getValue();

            System.out.println("Categoria: " + categoria);
            System.out.println("Quantidade: " + info.getQuantidadeArquivos());
            System.out.println("Tamanho total: " + info.getTamanhoTotal() + " bytes");
        }

        return arquivos;
    }
    private String descobrirCategoria(String nomeArquivo) {
        String nome =  nomeArquivo.toLowerCase();

        if (nome.endsWith(".jpg") ||
            nome.endsWith(".jpeg") ||
            nome.endsWith(".png") ||
            nome.endsWith(".gif")) {

            return "Imagens";
        }

        if (nome.endsWith(".mp4") ||
            nome.endsWith(".avi") ||
            nome.endsWith(".mkv")) {

            return "Videos";
        }

        if (nome.endsWith(".pdf") ||
            nome.endsWith(".doc") ||
            nome.endsWith(".docx") ||
            nome.endsWith(".txt")) {

            return "Documentos";
        }
        return "Outros";
    }
}