package util;

public class FormatadorTamanho {
    public String formatar(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        double kb = bytes / 1024;
        if (kb < 1024) {
            return String.format("%.2f KB", kb);
        }
        double mb = kb / 1024.0;
        if (mb < 1024) {
            return String.format("%.2f MB", mb);
        }
        double gb = mb / 1024.0;
        return String.format("%.2f GB", gb);
    }
}