package model;

public class ArquivoInfo {
    private String nome;
    private String caminho;
    private long tamanho;
    private Categoria categoria;

    public ArquivoInfo(String nome, String caminho, long tamanho, Categoria categoria) {
        this.nome = nome;
        this.caminho = caminho;
        this.tamanho = tamanho;
        this.categoria = categoria;
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

    public Categoria getCategoria() {
        return categoria;
    }
}