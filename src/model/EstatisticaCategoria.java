package model;

public class EstatisticaCategoria {
    private int quantidadeArquivos;
    private long tamanhoTotal;

    public void adicionarArquivo(long tamanho) {
        quantidadeArquivos++;
        tamanhoTotal = tamanho;
    }

    public void removerArquivo(long tamanho) {
        quantidadeArquivos--;
        tamanhoTotal -= tamanho;
    }

    public int getQuantidadeArquivos() {
        return quantidadeArquivos;
    }

    public long getTamanhoTotal() {
        return tamanhoTotal;
    }
}