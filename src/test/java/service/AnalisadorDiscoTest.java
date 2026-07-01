package service;

import model.Categoria;
import model.ResultadoAnalise;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AnalisadorDiscoTest {

    private final AnalisadorDisco analisador = new AnalisadorDisco();

    @Test
    void deveEncontrarTodosOsArquivos(@TempDir Path pasta) throws IOException {
        Files.createFile(pasta.resolve("foto.jpg"));
        Files.createFile(pasta.resolve("video.mp4"));
        Files.createFile(pasta.resolve("doc.pdf"));

        ResultadoAnalise resultado = analisador.analisar(pasta.toString());

        assertEquals(3, resultado.getArquivos().size());
    }

    @Test
    void deveCategorizarCorretamente(@TempDir Path pasta) throws IOException {
        Files.createFile(pasta.resolve("foto.jpg"));
        Files.createFile(pasta.resolve("video.mp4"));
        Files.createFile(pasta.resolve("doc.pdf"));
        Files.createFile(pasta.resolve("arquivo.xyz"));

        ResultadoAnalise resultado = analisador.analisar(pasta.toString());

        Map<String, ?> estatisticas = resultado.getEstatisticas();
        assertTrue(estatisticas.containsKey(Categoria.IMAGENS.getNome()));
        assertTrue(estatisticas.containsKey(Categoria.VIDEOS.getNome()));
        assertTrue(estatisticas.containsKey(Categoria.DOCUMENTOS.getNome()));
        assertTrue(estatisticas.containsKey(Categoria.OUTROS.getNome()));
    }

    @Test
    void deveCalcularTamanhoCorretamente(@TempDir Path pasta) throws IOException {
        Path arquivo = pasta.resolve("teste.txt");
        Files.write(arquivo, "1234567890".getBytes()); // 10 bytes

        ResultadoAnalise resultado = analisador.analisar(pasta.toString());

        assertEquals(10, resultado.getTamanhoTotal());
    }

    @Test
    void deveRetornarListaVazia_quandoPastaVazia(@TempDir Path pasta) {
        ResultadoAnalise resultado = analisador.analisar(pasta.toString());
        assertTrue(resultado.getArquivos().isEmpty());
        assertTrue(resultado.getEstatisticas().isEmpty());
    }

    @Test
    void deveVarrerSubpastas(@TempDir Path pasta) throws IOException {
        Path sub = Files.createDirectory(pasta.resolve("subpasta"));
        Files.createFile(pasta.resolve("raiz.txt"));
        Files.createFile(sub.resolve("sub.txt"));

        ResultadoAnalise resultado = analisador.analisar(pasta.toString());

        assertEquals(2, resultado.getArquivos().size());
    }
}
