package model;

import java.util.Set;

public enum Categoria {

    IMAGENS("Imagens", "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg"),
    VIDEOS("Videos", "mp4", "avi", "mkv", "mov", "wmv", "flv", "webm"),
    DOCUMENTOS("Documentos", "pdf", "doc", "docx", "txt", "odt", "xls", "xlsx", "ppt", "pptx"),
    AUDIO("Audio", "mp3", "wav", "flac", "aac", "ogg", "wma"),
    COMPACTADOS("Compactados", "zip", "rar", "7z", "tar", "gz", "bz2"),
    CODIGO("Codigo", "java", "py", "js", "ts", "html", "css", "cpp", "c", "h", "json", "xml", "yml", "yaml"),
    OUTROS("Outros");

    private final String nome;
    private final Set<String> extensoes;

    Categoria(String nome, String... extensoes) {
        this.nome = nome;
        this.extensoes = Set.of(extensoes);
    }

    public String getNome() {
        return nome;
    }

    public static Categoria de(String nomeArquivo) {
        int i = nomeArquivo.lastIndexOf('.');
        if (i > 0) {
            String ext = nomeArquivo.substring(i + 1).toLowerCase();
            for (Categoria cat : values()) {
                if (cat.extensoes.contains(ext)) {
                    return cat;
                }
            }
        }
        return OUTROS;
    }
}