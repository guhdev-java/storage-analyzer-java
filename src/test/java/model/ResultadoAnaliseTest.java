package model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ResultadoAnaliseTest {

    @Test
    void deveCalcularEstatisticasPorCategoria() {
        List<ArquivoInfo> arquivos = List.of(
                new ArquivoInfo("foto.jpg",  "/foto.jpg",  1024, Categoria.IMAGENS),
                new ArquivoInfo("foto2.png", "/foto2.png", 2048, Categoria.IMAGENS),
                new ArquivoInfo("video.mp4", "/video.mp4", 4096, Categoria.VIDEOS)
        );

        ResultadoAnalise resultado = new ResultadoAnalise(new java.util.ArrayList<>(arquivos));
        Map<String, EstatisticaCategoria> estat = resultado.getEstatisticas();

        assertEquals(2, estat.get("Imagens").getQuantidadeArquivos());
        assertEquals(3072, estat.get("Imagens").getTamanhoTotal());
        assertEquals(1, estat.get("Videos").getQuantidadeArquivos());
        assertEquals(4096, estat.get("Videos").getTamanhoTotal());
    }

    @Test
    void deveAtualizarEstatisticasAposRemocao() {
        ArquivoInfo foto = new ArquivoInfo("foto.jpg", "/foto.jpg", 1024, Categoria.IMAGENS);
        ArquivoInfo video = new ArquivoInfo("video.mp4", "/video.mp4", 4096, Categoria.VIDEOS);

        java.util.List<ArquivoInfo> lista = new java.util.ArrayList<>(List.of(foto, video));
        ResultadoAnalise resultado = new ResultadoAnalise(lista);

        lista.remove(foto);

        Map<String, EstatisticaCategoria> estat = resultado.getEstatisticas();
        assertFalse(estat.containsKey("Imagens"));
        assertEquals(1, estat.get("Videos").getQuantidadeArquivos());
    }

    @Test
    void deveCalcularTamanhoTotal() {
        List<ArquivoInfo> arquivos = List.of(
                new ArquivoInfo("a.jpg", "/a.jpg", 1000, Categoria.IMAGENS),
                new ArquivoInfo("b.mp4", "/b.mp4", 2000, Categoria.VIDEOS)
        );

        ResultadoAnalise resultado = new ResultadoAnalise(new java.util.ArrayList<>(arquivos));
        assertEquals(3000, resultado.getTamanhoTotal());
    }
}
