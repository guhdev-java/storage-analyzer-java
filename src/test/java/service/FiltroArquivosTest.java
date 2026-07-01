package service;

import model.ArquivoInfo;
import model.Categoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FiltroArquivosTest {

    private List<ArquivoInfo> arquivos;

    @BeforeEach
    void setUp() {
        arquivos = List.of(
                new ArquivoInfo("foto.jpg",     "/foto.jpg",     1024,        Categoria.IMAGENS),
                new ArquivoInfo("video.mp4",    "/video.mp4",    1024 * 1024, Categoria.VIDEOS),
                new ArquivoInfo("relatorio.pdf","/relatorio.pdf", 512 * 1024, Categoria.DOCUMENTOS),
                new ArquivoInfo("backup.zip",   "/backup.zip",   5 * 1024 * 1024, Categoria.COMPACTADOS),
                new ArquivoInfo("nota.txt",     "/nota.txt",     256,         Categoria.DOCUMENTOS)
        );
    }

    @Test
    void deveFiltrarPorExtensao() {
        List<ArquivoInfo> resultado = new FiltroArquivos()
                .comExtensao("pdf")
                .aplicar(arquivos);
        assertEquals(1, resultado.size());
        assertEquals("relatorio.pdf", resultado.get(0).getNome());
    }

    @Test
    void deveFiltrarPorExtensaoComPonto() {
        List<ArquivoInfo> resultado = new FiltroArquivos()
                .comExtensao(".jpg")
                .aplicar(arquivos);
        assertEquals(1, resultado.size());
        assertEquals("foto.jpg", resultado.get(0).getNome());
    }

    @Test
    void deveFiltrarPorTamanhoMin() {
        List<ArquivoInfo> resultado = new FiltroArquivos()
                .comTamanhoMin(1.0)
                .aplicar(arquivos);
        assertEquals(2, resultado.size()); // video.mp4 (1MB) e backup.zip (5MB)
    }

    @Test
    void deveFiltrarPorTamanhoMax() {
        List<ArquivoInfo> resultado = new FiltroArquivos()
                .comTamanhoMax(0.001)
                .aplicar(arquivos);
        assertEquals(2, resultado.size()); // foto.jpg (1KB) e nota.txt (256B)
    }

    @Test
    void deveFiltrarPorBuscaNome() {
        List<ArquivoInfo> resultado = new FiltroArquivos()
                .comBuscaNome("rel")
                .aplicar(arquivos);
        assertEquals(1, resultado.size());
        assertEquals("relatorio.pdf", resultado.get(0).getNome());
    }

    @Test
    void deveCombinarFiltros() {
        List<ArquivoInfo> resultado = new FiltroArquivos()
                .comTamanhoMin(0.4)
                .comTamanhoMax(2.0)
                .aplicar(arquivos);
        assertEquals(2, resultado.size()); // relatorio.pdf e video.mp4
    }

    @Test
    void deveOrdenarPorNomeAsc() {
        List<ArquivoInfo> resultado = new FiltroArquivos()
                .comOrdenacao(FiltroArquivos.Ordenacao.NOME_ASC)
                .aplicar(arquivos);
        assertEquals("backup.zip", resultado.get(0).getNome());
        assertEquals("video.mp4", resultado.get(resultado.size() - 1).getNome());
    }

    @Test
    void deveOrdenarPorTamanhoDesc() {
        List<ArquivoInfo> resultado = new FiltroArquivos()
                .comOrdenacao(FiltroArquivos.Ordenacao.TAMANHO_DESC)
                .aplicar(arquivos);
        assertEquals("backup.zip", resultado.get(0).getNome());
        assertEquals("nota.txt", resultado.get(resultado.size() - 1).getNome());
    }

    @Test
    void deveRetornarTodos_quandoSemCriterios() {
        List<ArquivoInfo> resultado = new FiltroArquivos().aplicar(arquivos);
        assertEquals(arquivos.size(), resultado.size());
    }
}