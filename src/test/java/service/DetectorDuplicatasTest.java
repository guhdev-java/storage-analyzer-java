package service;

import model.ArquivoInfo;
import model.Categoria;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DetectorDuplicatasTest {

    private final DetectorDuplicatas detector = new DetectorDuplicatas();

    @Test
    void deveDetectarDuplicatasPorNomeTamanho() {
        List<ArquivoInfo> arquivos = List.of(
                new ArquivoInfo("foto.jpg", "/a/foto.jpg", 1024, Categoria.IMAGENS),
                new ArquivoInfo("foto.jpg", "/b/foto.jpg", 1024, Categoria.IMAGENS),
                new ArquivoInfo("outro.jpg", "/outro.jpg", 1024, Categoria.IMAGENS)
        );

        Map<String, List<ArquivoInfo>> grupos = detector.detectarPorNomeTamanho(arquivos);

        assertEquals(1, grupos.size());
        assertEquals(2, grupos.values().iterator().next().size());
    }

    @Test
    void naoDeveDetectar_quandoNomesIguaisTamanhosDiferentes() {
        List<ArquivoInfo> arquivos = List.of(
                new ArquivoInfo("foto.jpg", "/a/foto.jpg", 1024, Categoria.IMAGENS),
                new ArquivoInfo("foto.jpg", "/b/foto.jpg", 2048, Categoria.IMAGENS)
        );

        Map<String, List<ArquivoInfo>> grupos = detector.detectarPorNomeTamanho(arquivos);

        assertTrue(grupos.isEmpty());
    }

    @Test
    void deveDetectarDuplicatasPorHash(@TempDir Path pasta) throws IOException {
        String conteudo = "conteudo identico";
        Path a = pasta.resolve("arquivo_a.txt");
        Path b = pasta.resolve("arquivo_b.txt");
        Path c = pasta.resolve("arquivo_c.txt");

        Files.writeString(a, conteudo);
        Files.writeString(b, conteudo);
        Files.writeString(c, "conteudo diferente");

        List<ArquivoInfo> arquivos = List.of(
                new ArquivoInfo("arquivo_a.txt", a.toString(), Files.size(a), Categoria.DOCUMENTOS),
                new ArquivoInfo("arquivo_b.txt", b.toString(), Files.size(b), Categoria.DOCUMENTOS),
                new ArquivoInfo("arquivo_c.txt", c.toString(), Files.size(c), Categoria.DOCUMENTOS)
        );

        Map<String, List<ArquivoInfo>> grupos = detector.detectarPorHash(arquivos);

        assertEquals(1, grupos.size());
        assertEquals(2, grupos.values().iterator().next().size());
    }

    @Test
    void deveRetornarVazio_quandoSemDuplicatas() {
        List<ArquivoInfo> arquivos = List.of(
                new ArquivoInfo("a.jpg", "/a.jpg", 100, Categoria.IMAGENS),
                new ArquivoInfo("b.jpg", "/b.jpg", 200, Categoria.IMAGENS)
        );

        Map<String, List<ArquivoInfo>> grupos = detector.detectarPorNomeTamanho(arquivos);
        assertTrue(grupos.isEmpty());
    }
}