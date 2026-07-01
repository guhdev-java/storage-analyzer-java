package model;

public class ResultadoLimpeza {
    private int quantidadeExcluida;
    private long tamanhoLiberado;
    private int falhas;

    public void registrarExclusao(long tamanho) {
        quantidadeExcluida++;
        tamanhoLiberado += tamanho;
    }

    public void registrarFalha() {
        falhas++;
    }

    public int getQuantidadeExcluida() {
        return quantidadeExcluida;
    }

    public long getTamanhoLiberado() {
        return tamanhoLiberado;
    }

    public int getFalhas() {
        return falhas;
    }
}
