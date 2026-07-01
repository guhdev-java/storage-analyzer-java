package service;

import model.ArquivoInfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class FiltroArquivos {

    public enum Ordenacao {
        TAMANHO_DESC, TAMANHO_ASC, NOME_ASC, NOME_DESC
    }

    private String extensao;
    private Long tamanhoMinBytes;
    private Long tamanhoMaxBytes;
    private String buscaNome;
    private Ordenacao ordenacao = Ordenacao.TAMANHO_DESC;

    // -------------------------------------------------------------------------
    // Builder-style setters
    // -------------------------------------------------------------------------

    public FiltroArquivos comExtensao(String extensao) {
        this.extensao = extensao == null ? null : extensao.toLowerCase().replace(".", "");
        return this;
    }

    public FiltroArquivos comTamanhoMin(double mb) {
        this.tamanhoMinBytes = (long) (mb * 1024 * 1024);
        return this;
    }

    public FiltroArquivos comTamanhoMax(double mb) {
        this.tamanhoMaxBytes = (long) (mb * 1024 * 1024);
        return this;
    }

    public FiltroArquivos comBuscaNome(String texto) {
        this.buscaNome = texto == null ? null : texto.toLowerCase();
        return this;
    }

    public FiltroArquivos comOrdenacao(Ordenacao ordenacao) {
        this.ordenacao = ordenacao;
        return this;
    }

    // -------------------------------------------------------------------------
    // Aplicação
    // -------------------------------------------------------------------------

    public List<ArquivoInfo> aplicar(List<ArquivoInfo> arquivos) {
        Predicate<ArquivoInfo> predicate = construirPredicate();

        List<ArquivoInfo> resultado = new ArrayList<>();
        for (ArquivoInfo a : arquivos) {
            if (predicate.test(a)) resultado.add(a);
        }

        resultado.sort(construirComparador());
        return resultado;
    }

    private Predicate<ArquivoInfo> construirPredicate() {
        Predicate<ArquivoInfo> p = a -> true;

        if (extensao != null && !extensao.isEmpty()) {
            p = p.and(a -> a.getNome().toLowerCase().endsWith("." + extensao));
        }
        if (tamanhoMinBytes != null) {
            p = p.and(a -> a.getTamanho() >= tamanhoMinBytes);
        }
        if (tamanhoMaxBytes != null) {
            p = p.and(a -> a.getTamanho() <= tamanhoMaxBytes);
        }
        if (buscaNome != null && !buscaNome.isEmpty()) {
            p = p.and(a -> a.getNome().toLowerCase().contains(buscaNome));
        }

        return p;
    }

    private Comparator<ArquivoInfo> construirComparador() {
        return switch (ordenacao) {
            case TAMANHO_ASC  -> Comparator.comparingLong(ArquivoInfo::getTamanho);
            case NOME_ASC     -> Comparator.comparing(a -> a.getNome().toLowerCase());
            case NOME_DESC    -> Comparator.comparing((ArquivoInfo a) -> a.getNome().toLowerCase()).reversed();
            default           -> (a, b) -> Long.compare(b.getTamanho(), a.getTamanho());
        };
    }

    public boolean temCriterios() {
        return extensao != null || tamanhoMinBytes != null || tamanhoMaxBytes != null || buscaNome != null;
    }
}