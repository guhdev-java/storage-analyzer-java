package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoriaTest {

    @Test
    void deveReconhecerImagens() {
        assertEquals(Categoria.IMAGENS, Categoria.de("foto.jpg"));
        assertEquals(Categoria.IMAGENS, Categoria.de("foto.JPEG"));
        assertEquals(Categoria.IMAGENS, Categoria.de("banner.png"));
        assertEquals(Categoria.IMAGENS, Categoria.de("animacao.gif"));
        assertEquals(Categoria.IMAGENS, Categoria.de("imagem.webp"));
    }

    @Test
    void deveReconhecerVideos() {
        assertEquals(Categoria.VIDEOS, Categoria.de("filme.mp4"));
        assertEquals(Categoria.VIDEOS, Categoria.de("clip.avi"));
        assertEquals(Categoria.VIDEOS, Categoria.de("serie.mkv"));
        assertEquals(Categoria.VIDEOS, Categoria.de("video.MOV"));
    }

    @Test
    void deveReconhecerDocumentos() {
        assertEquals(Categoria.DOCUMENTOS, Categoria.de("relatorio.pdf"));
        assertEquals(Categoria.DOCUMENTOS, Categoria.de("texto.txt"));
        assertEquals(Categoria.DOCUMENTOS, Categoria.de("planilha.xlsx"));
        assertEquals(Categoria.DOCUMENTOS, Categoria.de("apresentacao.pptx"));
    }

    @Test
    void deveReconhecerAudio() {
        assertEquals(Categoria.AUDIO, Categoria.de("musica.mp3"));
        assertEquals(Categoria.AUDIO, Categoria.de("podcast.flac"));
        assertEquals(Categoria.AUDIO, Categoria.de("som.wav"));
    }

    @Test
    void deveReconhecerCompactados() {
        assertEquals(Categoria.COMPACTADOS, Categoria.de("backup.zip"));
        assertEquals(Categoria.COMPACTADOS, Categoria.de("arquivo.rar"));
        assertEquals(Categoria.COMPACTADOS, Categoria.de("pacote.7z"));
        assertEquals(Categoria.COMPACTADOS, Categoria.de("dados.tar"));
    }

    @Test
    void deveReconhecerCodigo() {
        assertEquals(Categoria.CODIGO, Categoria.de("Main.java"));
        assertEquals(Categoria.CODIGO, Categoria.de("script.py"));
        assertEquals(Categoria.CODIGO, Categoria.de("index.html"));
        assertEquals(Categoria.CODIGO, Categoria.de("config.json"));
    }

    @Test
    void deveRetornarOutros_quandoExtensaoDesconhecida() {
        assertEquals(Categoria.OUTROS, Categoria.de("arquivo.xyz"));
        assertEquals(Categoria.OUTROS, Categoria.de("semExtensao"));
        assertEquals(Categoria.OUTROS, Categoria.de(".oculto"));
    }

    @Test
    void deveSerCaseInsensitive() {
        assertEquals(Categoria.IMAGENS, Categoria.de("FOTO.JPG"));
        assertEquals(Categoria.VIDEOS, Categoria.de("FILME.MP4"));
        assertEquals(Categoria.CODIGO, Categoria.de("MAIN.JAVA"));
    }
}
