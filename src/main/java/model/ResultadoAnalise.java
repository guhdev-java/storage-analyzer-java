package model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResultadoAnalise {
    private final List<ArquivoInfo> arquivos;

    public ResultadoAnalise(List<ArquivoInfo> arquivos) {
        this.arquivos = arquivos;
    }

    public List<ArquivoInfo> getArquivos() {
        return arquivos;
    }

    public Map<String, EstatisticaCategoria> getEstatisticas() {
        Map<String, EstatisticaCategoria> mapa = new LinkedHashMap<>();
        for (ArquivoInfo a : arquivos) {
            String nome = a.getCategoria().getNome();
            mapa.computeIfAbsent(nome, k -> new EstatisticaCategoria())
                .adicionarArquivo(a.getTamanho());
        }
        return mapa;
    }

    public long getTamanhoTotal() {
        return arquivos.stream().mapToLong(ArquivoInfo::getTamanho).sum();
    }
}