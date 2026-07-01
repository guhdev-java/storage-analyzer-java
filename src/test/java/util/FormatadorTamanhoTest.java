package util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FormatadorTamanhoTest {

    private FormatadorTamanho formatador;

    @BeforeEach
    void setUp() {
        formatador = new FormatadorTamanho();
    }

    @Test
    void deveRetornarBytes_quandoMenorQue1024() {
        assertEquals("0 B", formatador.formatar(0));
        assertEquals("1 B", formatador.formatar(1));
        assertEquals("1023 B", formatador.formatar(1023));
    }

    @Test
    void deveRetornarKilobytes_quandoEntre1KBe1MB() {
        assertEquals("1.00 KB", formatador.formatar(1024));
        assertEquals("1.50 KB", formatador.formatar(1536));
        assertEquals("1023.00 KB", formatador.formatar(1024 * 1023));
    }

    @Test
    void deveRetornarMegabytes_quandoEntre1MBe1GB() {
        assertEquals("1.00 MB", formatador.formatar(1024L * 1024));
        assertEquals("2.50 MB", formatador.formatar(1024L * 1024 * 2 + 1024 * 512));
    }

    @Test
    void deveRetornarGigabytes_quandoAcimaDe1GB() {
        assertEquals("1.00 GB", formatador.formatar(1024L * 1024 * 1024));
        assertEquals("2.00 GB", formatador.formatar(1024L * 1024 * 1024 * 2));
    }
}
