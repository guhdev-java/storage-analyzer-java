package app;

import java.util.HashMap;
import java.util.Map;

public class ArgumentosCli {

    private final Map<String, String> opcoes = new HashMap<>();
    private String caminhoPosicional;

    public static ArgumentosCli parse(String[] args) {
        ArgumentosCli resultado = new ArgumentosCli();

        int i = 0;
        while (i < args.length) {
            String atual = args[i];

            if (atual.startsWith("--")) {
                String chave = atual.substring(2);
                String valor = (i + 1 < args.length && !args[i + 1].startsWith("--")) ? args[++i] : "";
                resultado.opcoes.put(chave, valor);
            } else if (resultado.caminhoPosicional == null) {
                resultado.caminhoPosicional = atual;
            }
            i++;
        }

        return resultado;
    }

    public boolean temArgumentos() {
        return caminhoPosicional != null || !opcoes.isEmpty();
    }

    public String getCaminho() {
        if (opcoes.containsKey("caminho")) {
            return opcoes.get("caminho");
        }
        return caminhoPosicional;
    }

    public String getCategoria() {
        return opcoes.get("categoria");
    }

    public String getFormato() {
        return opcoes.get("formato");
    }

    public String getOrdenar() {
        return opcoes.getOrDefault("ordenar", "tamanho");
    }

    public boolean isModoScript() {
        return opcoes.containsKey("categoria") || opcoes.containsKey("formato") || opcoes.containsKey("ordenar");
    }
}