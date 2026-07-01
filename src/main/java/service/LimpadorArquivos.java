package service;

import model.ArquivoInfo;
import model.ResultadoLimpeza;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


public class LimpadorArquivos {
    public ResultadoLimpeza excluir(List<ArquivoInfo> arquivos) {
        ResultadoLimpeza resultado = new ResultadoLimpeza();

        for (ArquivoInfo arquivo : arquivos) {
            Path caminho = Paths.get(arquivo.getCaminho());
            try {
                Files.delete(caminho);
                resultado.registrarExclusao(arquivo.getTamanho());
            } catch (IOException e) {
                System.err.println("Erro ao excluir " + arquivo.getNome() + ": " +  e.getMessage());
                resultado.registrarFalha();
            }
        }
        return resultado;
    }
}
