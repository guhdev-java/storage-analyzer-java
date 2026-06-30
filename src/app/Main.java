package app;

import model.ArquivoInfo;
import model.EstatisticaCategoria;
import model.ResultadoAnalise;
import model.ResultadoLimpeza;
import service.AnalisadorDisco;
import service.ExportadorRelatorio;
import service.LimpadorArquivos;
import util.FormatadorTamanho;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static final AnalisadorDisco analisador = new AnalisadorDisco();
    private static final LimpadorArquivos limpador = new LimpadorArquivos();
    private static final ExportadorRelatorio exportador = new ExportadorRelatorio();
    private static final FormatadorTamanho formatador = new FormatadorTamanho();

    public static void main(String[] args) {
        ArgumentosCli cli = ArgumentosCli.parse(args);

        try (Scanner sc = new Scanner(System.in)) {
            String caminho = cli.getCaminho();

            if (caminho == null) {
                caminho = pedirCaminho(sc);
                if (caminho == null) {
                    return;
                }
            } else if (!Files.exists(Paths.get(caminho))) {
                System.out.println("Caminho não existe: " + caminho);
                return;
            }

            System.out.println("\nAnalisando, aguarde...");
            ResultadoAnalise resultado = analisador.analisar(caminho);
            System.out.println("Concluído: " + resultado.getArquivos().size() + " arquivos encontrados.");

            if (cli.isModoScript()) {
                executarModoScript(cli, resultado);
            } else {
                menu(sc, resultado);
            }
        }
    }

    private static void executarModoScript(ArgumentosCli cli, ResultadoAnalise resultado) {
        List<ArquivoInfo> arquivos = resultado.getArquivos();

        if (cli.getCategoria() != null) {
            String categoria = cli.getCategoria();
            arquivos = arquivos.stream()
                    .filter(a -> categoria.equalsIgnoreCase(descobrirCategoriaParaExibicao(a.getNome())))
                    .toList();
        }

        String ordenar = cli.getOrdenar();
        List<ArquivoInfo> ordenados = new ArrayList<>(arquivos);
        switch (ordenar) {
            case "nome" -> ordenados.sort((a, b) -> a.getNome().compareToIgnoreCase(b.getNome()));
            default -> ordenados.sort((a, b) -> Long.compare(b.getTamanho(), a.getTamanho()));
        }

        if (cli.getFormato() != null) {
            String formato = cli.getFormato().toLowerCase();
            String nomeArquivo = "relatorio." + formato;
            try {
                if (formato.equals("csv")) {
                    exportador.exportarCsv(ordenados, nomeArquivo, Main::descobrirCategoriaParaExibicao);
                    System.out.println("Relatório exportado em: " + nomeArquivo);
                } else if (formato.equals("json")) {
                    exportador.exportarJson(ordenados, nomeArquivo, Main::descobrirCategoriaParaExibicao);
                    System.out.println("Relatório exportado em: " + nomeArquivo);
                } else {
                    System.out.println("Formato inválido: " + formato + ". Use csv ou json.");
                }
            } catch (IOException e) {
                System.out.println("Erro ao exportar: " + e.getMessage());
            }
        } else {
            System.out.println("\n=== RESULTADO ===\n");
            for (ArquivoInfo a : ordenados) {
                System.out.println(a.getNome() + " - " + formatador.formatar(a.getTamanho()));
            }
        }
    }

    private static String pedirCaminho(Scanner sc) {
        System.out.print("Digite o caminho da pasta/arquivo: ");
        String caminho = sc.nextLine().trim();

        if (caminho.isEmpty()) {
            System.out.println("Caminho não pode estar vazio.");
            return null;
        }
        if (!Files.exists(Paths.get(caminho))) {
            System.out.println("Caminho não existe: " + caminho);
            return null;
        }
        return caminho;
    }

    private static void menu(Scanner sc, ResultadoAnalise resultado) {
        boolean continuar = true;
        while (continuar) {
            System.out.println("\n========================================");
            System.out.println("1 - Listar todos os arquivos");
            System.out.println("2 - Listar arquivos de uma categoria");
            System.out.println("3 - Ver estatísticas por categoria");
            System.out.println("4 - Excluir arquivos de uma categoria");
            System.out.println("5 - Excluir arquivos maiores que X MB");
            System.out.println("6 - Exportar relatório (CSV ou JSON)");
            System.out.println("0 - Sair");
            System.out.println("========================================");
            System.out.print("Escolha: ");

            String opcao = sc.nextLine().trim();

            switch (opcao) {
                case "1" -> listarTodos(resultado);
                case "2" -> listarPorCategoria(sc, resultado);
                case "3" -> mostrarEstatisticas(resultado);
                case "4" -> excluirPorCategoria(sc, resultado);
                case "5" -> excluirPorTamanho(sc, resultado);
                case "6" -> exportarRelatorio(sc, resultado);
                case "0" -> {
                    System.out.println("Até mais!");
                    continuar = false;
                }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private static void listarTodos(ResultadoAnalise resultado) {
        List<ArquivoInfo> arquivos = resultado.getArquivos();
        arquivos.sort((a, b) -> Long.compare(b.getTamanho(), a.getTamanho()));

        System.out.println("\n=== ARQUIVOS ORDENADOS POR TAMANHO ===\n");
        for (ArquivoInfo a : arquivos) {
            System.out.println(a.getNome() + " - " + formatador.formatar(a.getTamanho()));
        }
    }

    private static void listarPorCategoria(Scanner sc, ResultadoAnalise resultado) {
        Map<String, EstatisticaCategoria> estatisticas = resultado.getEstatisticas();

        if (estatisticas.isEmpty()) {
            System.out.println("Nenhuma categoria encontrada.");
            return;
        }

        System.out.println("\nCategorias disponíveis: " + String.join(", ", estatisticas.keySet()));
        System.out.print("Digite a categoria: ");
        String categoria = sc.nextLine().trim();

        List<ArquivoInfo> filtrados = resultado.getArquivos().stream()
                .filter(a -> categoria.equalsIgnoreCase(descobrirCategoriaParaExibicao(a.getNome())))
                .sorted((a, b) -> Long.compare(b.getTamanho(), a.getTamanho()))
                .toList();

        if (filtrados.isEmpty()) {
            System.out.println("Nenhum arquivo encontrado para a categoria \"" + categoria + "\".");
            return;
        }

        System.out.println("\n=== ARQUIVOS DA CATEGORIA: " + categoria + " ===\n");
        for (ArquivoInfo a : filtrados) {
            System.out.println(a.getNome() + " - " + formatador.formatar(a.getTamanho()));
        }
    }

    private static void mostrarEstatisticas(ResultadoAnalise resultado) {
        System.out.println("\n=== ESTATÍSTICAS POR CATEGORIA ===\n");
        for (Map.Entry<String, EstatisticaCategoria> entry : resultado.getEstatisticas().entrySet()) {
            EstatisticaCategoria estat = entry.getValue();
            System.out.println("Categoria: " + entry.getKey());
            System.out.println("  Quantidade: " + estat.getQuantidadeArquivos());
            System.out.println("  Tamanho total: " + formatador.formatar(estat.getTamanhoTotal()));
        }
    }

    private static void excluirPorCategoria(Scanner sc, ResultadoAnalise resultado) {
        Map<String, EstatisticaCategoria> estatisticas = resultado.getEstatisticas();

        if (estatisticas.isEmpty()) {
            System.out.println("Nenhuma categoria encontrada.");
            return;
        }

        System.out.println("\nCategorias disponíveis: " + String.join(", ", estatisticas.keySet()));
        System.out.print("Digite a categoria a excluir: ");
        String categoria = sc.nextLine().trim();

        List<ArquivoInfo> alvo = resultado.getArquivos().stream()
                .filter(a -> categoria.equalsIgnoreCase(descobrirCategoriaParaExibicao(a.getNome())))
                .toList();

        if (alvo.isEmpty()) {
            System.out.println("Nenhum arquivo encontrado para a categoria \"" + categoria + "\".");
            return;
        }

        long tamanhoTotal = alvo.stream().mapToLong(ArquivoInfo::getTamanho).sum();
        System.out.println("\n" + alvo.size() + " arquivo(s) serão excluídos, liberando " +
                formatador.formatar(tamanhoTotal) + ".");
        System.out.print("Tem certeza? Digite \"sim\" para confirmar: ");

        if (!"sim".equalsIgnoreCase(sc.nextLine().trim())) {
            System.out.println("Operação cancelada.");
            return;
        }

        ResultadoLimpeza limpeza = limpador.excluir(alvo);
        resultado.getArquivos().removeAll(alvo);
        EstatisticaCategoria estat = estatisticas.get(categoria);
        if (estat != null) {
            for (ArquivoInfo a : alvo) {
                estat.removerArquivo(a.getTamanho());
            }
        }
        System.out.println("Excluídos: " + limpeza.getQuantidadeExcluida() +
                " | Liberado: " + formatador.formatar(limpeza.getTamanhoLiberado()) +
                " | Falhas: " + limpeza.getFalhas());
    }

    private static void excluirPorTamanho(Scanner sc, ResultadoAnalise resultado) {
        System.out.print("\nExcluir arquivos maiores que quantos MB? ");
        String entrada = sc.nextLine().trim();

        double limiteMb;
        try {
            limiteMb = Double.parseDouble(entrada);
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido.");
            return;
        }

        long limiteBytes = (long) (limiteMb * 1024 * 1024);

        List<ArquivoInfo> alvo = resultado.getArquivos().stream()
                .filter(a -> a.getTamanho() > limiteBytes)
                .toList();

        if (alvo.isEmpty()) {
            System.out.println("Nenhum arquivo maior que " + limiteMb + " MB encontrado.");
            return;
        }

        long tamanhoTotal = alvo.stream().mapToLong(ArquivoInfo::getTamanho).sum();
        System.out.println("\n" + alvo.size() + " arquivo(s) serão excluídos, liberando " +
                formatador.formatar(tamanhoTotal) + ".");
        System.out.print("Tem certeza? Digite \"sim\" para confirmar: ");

        if (!"sim".equalsIgnoreCase(sc.nextLine().trim())) {
            System.out.println("Operação cancelada.");
            return;
        }

        ResultadoLimpeza limpeza = limpador.excluir(alvo);
        resultado.getArquivos().removeAll(alvo);
        for (ArquivoInfo a : alvo) {
            String categoria = descobrirCategoriaParaExibicao(a.getNome());
            EstatisticaCategoria estat = resultado.getEstatisticas().get(categoria);
            if (estat != null) {
                estat.removerArquivo(a.getTamanho());
            }
        }
        System.out.println("Excluídos: " + limpeza.getQuantidadeExcluida() +
                " | Liberado: " + formatador.formatar(limpeza.getTamanhoLiberado()) +
                " | Falhas: " + limpeza.getFalhas());
    }

    private static void exportarRelatorio(Scanner sc, ResultadoAnalise resultado) {
        if (resultado.getArquivos().isEmpty()) {
            System.out.println("Não há arquivos para exportar.");
            return;
        }

        System.out.print("\nFormato (csv/json): ");
        String formato = sc.nextLine().trim().toLowerCase();

        if (!formato.equals("csv") && !formato.equals("json")) {
            System.out.println("Formato inválido. Use \"csv\" ou \"json\".");
            return;
        }

        System.out.print("Nome do arquivo de saída (ex: relatorio." + formato + "): ");
        String nomeArquivo = sc.nextLine().trim();

        if (nomeArquivo.isEmpty()) {
            nomeArquivo = "relatorio." + formato;
        }

        try {
            if (formato.equals("csv")) {
                exportador.exportarCsv(resultado.getArquivos(), nomeArquivo, Main::descobrirCategoriaParaExibicao);
            } else {
                exportador.exportarJson(resultado.getArquivos(), nomeArquivo, Main::descobrirCategoriaParaExibicao);
            }
            System.out.println("Relatório exportado em: " + nomeArquivo);
        } catch (IOException e) {
            System.out.println("Erro ao exportar: " + e.getMessage());
        }
    }

    private static String descobrirCategoriaParaExibicao(String nome) {
        String nomeLower = nome.toLowerCase();
        if (nomeLower.endsWith(".jpg") || nomeLower.endsWith(".jpeg") || nomeLower.endsWith(".png") || nomeLower.endsWith(".gif")) {
            return "Imagens";
        }
        if (nomeLower.endsWith(".mp4") || nomeLower.endsWith(".avi") || nomeLower.endsWith(".mkv")) {
            return "Videos";
        }
        if (nomeLower.endsWith(".pdf") || nomeLower.endsWith(".doc") || nomeLower.endsWith(".docx") || nomeLower.endsWith(".txt")) {
            return "Documentos";
        }
        return "Outros";
    }
}