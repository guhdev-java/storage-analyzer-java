package service;

import model.ArquivoInfo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetectorDuplicatas {

    /**
     * Detecção rápida: agrupa por nome + tamanho.
     * Não lê conteúdo — instantâneo para qualquer volume.
     */
    public Map<String, List<ArquivoInfo>> detectarPorNomeTamanho(List<ArquivoInfo> arquivos) {
        Map<String, List<ArquivoInfo>> grupos = new HashMap<>();

        for (ArquivoInfo a : arquivos) {
            String chave = a.getNome().toLowerCase() + "|" + a.getTamanho();
            grupos.computeIfAbsent(chave, k -> new ArrayList<>()).add(a);
        }

        grupos.entrySet().removeIf(e -> e.getValue().size() < 2);
        return grupos;
    }

    /**
     * Detecção precisa: agrupa por hash MD5 do conteúdo.
     * Confirma duplicatas reais mesmo com nomes diferentes.
     */
    public Map<String, List<ArquivoInfo>> detectarPorHash(List<ArquivoInfo> arquivos) {
        Map<String, List<ArquivoInfo>> grupos = new HashMap<>();
        int processados = 0;

        for (ArquivoInfo a : arquivos) {
            processados++;
            if (processados % 100 == 0) {
                System.out.printf("\r  Calculando hashes: %d/%d", processados, arquivos.size());
            }

            try {
                String hash = calcularMd5(a.getCaminho());
                grupos.computeIfAbsent(hash, k -> new ArrayList<>()).add(a);
            } catch (IOException | NoSuchAlgorithmException e) {
                System.err.println("\nErro ao calcular hash de " + a.getNome() + ": " + e.getMessage());
            }
        }

        if (!arquivos.isEmpty()) System.out.print("\r");
        grupos.entrySet().removeIf(e -> e.getValue().size() < 2);
        return grupos;
    }

    private String calcularMd5(String caminho) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        try (InputStream is = Files.newInputStream(Paths.get(caminho))) {
            byte[] buffer = new byte[8192];
            int lido;
            while ((lido = is.read(buffer)) != -1) {
                md.update(buffer, 0, lido);
            }
        }
        byte[] digest = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}