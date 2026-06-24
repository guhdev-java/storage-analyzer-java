package model;

public class ArquivoInfo {
    private String nome;
    private String caminho;
    private long tamanho;

    public ArquivoInfo(String nome, String caminho, long tamanho) {
        this.nome = nome;
        this.caminho = caminho;
        this.tamanho = tamanho;
    }
    public String getNome() {
        return nome;
    }
    public String getCaminho() {
        return caminho;
    }
    public long getTamanho() {
        return tamanho;
    }
}
