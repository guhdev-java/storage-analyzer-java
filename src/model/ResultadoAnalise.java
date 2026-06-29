package model;

import java.util.List;
import java.util.Map;

public class ResultadoAnalise {
    private List<ArquivoInfo> arquivos;
    private Map<String, EstatisticaCategoria> estatisticas;

    public ResultadoAnalise(List<ArquivoInfo> arquivos, Map<String, EstatisticaCategoria> estatisticas) {
        this.arquivos = arquivos;
        this.estatisticas = estatisticas;
    }

    public List<ArquivoInfo> getArquivos() {
        return arquivos;
    }

    public Map<String, EstatisticaCategoria> getEstatisticas() {
        return estatisticas;
    }
}
