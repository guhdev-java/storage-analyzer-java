package service;

import model.ArquivoInfo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;

public class ExportadorRelatorio {

    public void exportarCsv(List<ArquivoInfo> arquivos, String caminhoSaida,
                            Function<ArquivoInfo, String> categorizador) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("nome,caminho,tamanho_bytes,categoria\n");

        for (ArquivoInfo a : arquivos) {
            sb.append(escaparCsv(a.getNome())).append(",")
                    .append(escaparCsv(a.getCaminho())).append(",")
                    .append(a.getTamanho()).append(",")
                    .append(escaparCsv(categorizador.apply(a)))
                    .append("\n");
        }

        Files.writeString(Paths.get(caminhoSaida), sb.toString(), StandardCharsets.UTF_8);
    }

    public void exportarJson(List<ArquivoInfo> arquivos, String caminhoSaida,
                             Function<ArquivoInfo, String> categorizador) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");

        for (int i = 0; i < arquivos.size(); i++) {
            ArquivoInfo a = arquivos.get(i);
            sb.append("    {\n")
                    .append("        \"nome\": \"").append(escaparJson(a.getNome())).append("\",\n")
                    .append("        \"caminho\": \"").append(escaparJson(a.getCaminho())).append("\",\n")
                    .append("        \"tamanho\": ").append(a.getTamanho()).append(",\n")
                    .append("        \"categoria\": \"").append(escaparJson(categorizador.apply(a))).append("\"\n")
                    .append("    }").append(i < arquivos.size() - 1 ? "," : "").append("\n");
        }

        sb.append("]\n");
        Files.writeString(Paths.get(caminhoSaida), sb.toString(), StandardCharsets.UTF_8);
    }

    private String escaparCsv(String valor) {
        if (valor.contains(",") || valor.contains("\"") || valor.contains("\n")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }

    private String escaparJson(String valor) {
        return valor.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}