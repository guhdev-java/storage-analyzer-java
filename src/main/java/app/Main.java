package app;

import model.ArquivoInfo;
import model.EstatisticaCategoria;
import model.ResultadoAnalise;
import model.ResultadoLimpeza;
import service.AnalisadorDisco;
import service.DetectorDuplicatas;
import service.ExportadorRelatorio;
import service.FiltroArquivos;
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
    private static final DetectorDuplicatas detector = new DetectorDuplicatas();
    private static final FormatadorTamanho formatador = new FormatadorTamanho();

    public static void main(String[] args) {
        ArgumentosCli cli = ArgumentosCli.parse(args);

        try (Scanner sc = new Scanner(System.in)) {
            String caminho = cli.getCaminho();

            if (caminho == null) {
                caminho = pedirCaminho(sc);
                if (caminho == null) return;
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

    // -------------------------------------------------------------------------
    // Modo script
    // -------------------------------------------------------------------------

    private static void executarModoScript(ArgumentosCli cli, ResultadoAnalise resultado) {
        FiltroArquivos filtro = new FiltroArquivos();

        if (cli.getCategoria() != null) {
            filtro.comBuscaNome(cli.getCategoria());
        }
        if ("nome".equals(cli.getOrdenar())) {
            filtro.comOrdenacao(FiltroArquivos.Ordenacao.NOME_ASC);
        }

        List<ArquivoInfo> ordenados = filtro.aplicar(resultado.getArquivos());

        if (cli.getFormato() != null) {
            exportar(ordenados, cli.getFormato().toLowerCase(), "relatorio." + cli.getFormato().toLowerCase());
        } else {
            System.out.println("\n=== RESULTADO ===\n");
            for (ArquivoInfo a : ordenados) {
                System.out.printf("%-40s %s%n", a.getNome(), formatador.formatar(a.getTamanho()));
            }
        }
    }

    // -------------------------------------------------------------------------
    // Menu interativo
    // -------------------------------------------------------------------------

    private static void menu(Scanner sc, ResultadoAnalise resultado) {
        boolean continuar = true;
        while (continuar) {
            System.out.println("\n========================================");
            System.out.println("1 - Listar todos os arquivos");
            System.out.println("2 - Listar por categoria");
            System.out.println("3 - Estatísticas por categoria");
            System.out.println("4 - Filtro avançado");
            System.out.println("5 - Excluir por categoria");
            System.out.println("6 - Excluir por tamanho mínimo");
            System.out.println("7 - Detectar duplicatas");
            System.out.println("8 - Exportar relatório");
            System.out.println("0 - Sair");
            System.out.println("========================================");
            System.out.print("Escolha: ");

            switch (sc.nextLine().trim()) {
                case "1" -> listarTodos(resultado);
                case "2" -> listarPorCategoria(sc, resultado);
                case "3" -> mostrarEstatisticas(resultado);
                case "4" -> filtroAvancado(sc, resultado);
                case "5" -> excluirPorCategoria(sc, resultado);
                case "6" -> excluirPorTamanho(sc, resultado);
                case "7" -> detectarDuplicatas(sc, resultado);
                case "8" -> exportarInterativo(sc, resultado);
                case "0" -> { System.out.println("Até mais!"); continuar = false; }
                default  -> System.out.println("Opção inválida.");
            }
        }
    }

    // -------------------------------------------------------------------------
    // Listagem
    // -------------------------------------------------------------------------

    private static void listarTodos(ResultadoAnalise resultado) {
        List<ArquivoInfo> arquivos = new ArrayList<>(resultado.getArquivos());
        arquivos.sort((a, b) -> Long.compare(b.getTamanho(), a.getTamanho()));
        System.out.println("\n=== ARQUIVOS ORDENADOS POR TAMANHO ===\n");
        imprimirLista(arquivos);
    }

    private static void listarPorCategoria(Scanner sc, ResultadoAnalise resultado) {
        Map<String, EstatisticaCategoria> estatisticas = resultado.getEstatisticas();
        if (estatisticas.isEmpty()) { System.out.println("Nenhuma categoria encontrada."); return; }

        System.out.println("\nCategorias disponíveis: " + String.join(", ", estatisticas.keySet()));
        System.out.print("Categoria: ");
        String categoria = sc.nextLine().trim();

        List<ArquivoInfo> filtrados = new FiltroArquivos()
                .comOrdenacao(FiltroArquivos.Ordenacao.TAMANHO_DESC)
                .aplicar(resultado.getArquivos().stream()
                        .filter(a -> categoria.equalsIgnoreCase(a.getCategoria().getNome()))
                        .toList());

        if (filtrados.isEmpty()) { System.out.println("Nenhum arquivo para \"" + categoria + "\"."); return; }

        System.out.println("\n=== CATEGORIA: " + categoria + " ===\n");
        imprimirLista(filtrados);
    }

    private static void mostrarEstatisticas(ResultadoAnalise resultado) {
        long totalBytes = resultado.getTamanhoTotal();
        System.out.println("\n=== ESTATÍSTICAS POR CATEGORIA ===\n");
        for (Map.Entry<String, EstatisticaCategoria> entry : resultado.getEstatisticas().entrySet()) {
            EstatisticaCategoria estat = entry.getValue();
            int pct = totalBytes > 0 ? (int) (estat.getTamanhoTotal() * 100 / totalBytes) : 0;
            System.out.printf("%-12s  %4d arquivo(s)  %10s  (%d%%)%n",
                    entry.getKey(), estat.getQuantidadeArquivos(),
                    formatador.formatar(estat.getTamanhoTotal()), pct);
        }
        System.out.println("\nTotal: " + resultado.getArquivos().size() +
                " arquivo(s) — " + formatador.formatar(totalBytes));
    }

    // -------------------------------------------------------------------------
    // Filtro avançado
    // -------------------------------------------------------------------------

    private static void filtroAvancado(Scanner sc, ResultadoAnalise resultado) {
        System.out.println("\n=== FILTRO AVANÇADO ===");
        System.out.println("(Enter para pular qualquer campo)\n");

        FiltroArquivos filtro = new FiltroArquivos();

        System.out.print("Extensão (ex: pdf, jpg): ");
        String ext = sc.nextLine().trim();
        if (!ext.isEmpty()) filtro.comExtensao(ext);

        System.out.print("Busca no nome: ");
        String nome = sc.nextLine().trim();
        if (!nome.isEmpty()) filtro.comBuscaNome(nome);

        System.out.print("Tamanho mínimo (MB): ");
        String minStr = sc.nextLine().trim();
        if (!minStr.isEmpty()) {
            try { filtro.comTamanhoMin(Double.parseDouble(minStr)); }
            catch (NumberFormatException e) { System.out.println("Valor inválido, ignorado."); }
        }

        System.out.print("Tamanho máximo (MB): ");
        String maxStr = sc.nextLine().trim();
        if (!maxStr.isEmpty()) {
            try { filtro.comTamanhoMax(Double.parseDouble(maxStr)); }
            catch (NumberFormatException e) { System.out.println("Valor inválido, ignorado."); }
        }

        System.out.println("\nOrdenação:");
        System.out.println("  1 - Tamanho (maior primeiro)");
        System.out.println("  2 - Tamanho (menor primeiro)");
        System.out.println("  3 - Nome (A-Z)");
        System.out.println("  4 - Nome (Z-A)");
        System.out.print("Escolha (enter para padrão): ");
        switch (sc.nextLine().trim()) {
            case "2" -> filtro.comOrdenacao(FiltroArquivos.Ordenacao.TAMANHO_ASC);
            case "3" -> filtro.comOrdenacao(FiltroArquivos.Ordenacao.NOME_ASC);
            case "4" -> filtro.comOrdenacao(FiltroArquivos.Ordenacao.NOME_DESC);
            default  -> filtro.comOrdenacao(FiltroArquivos.Ordenacao.TAMANHO_DESC);
        }

        List<ArquivoInfo> filtrados = filtro.aplicar(resultado.getArquivos());

        if (filtrados.isEmpty()) { System.out.println("\nNenhum arquivo encontrado."); return; }

        System.out.println("\n=== RESULTADO DO FILTRO: " + filtrados.size() + " arquivo(s) ===\n");
        imprimirLista(filtrados);

        System.out.print("\nExcluir esses arquivos? Digite \"sim\" para confirmar: ");
        if ("sim".equalsIgnoreCase(sc.nextLine().trim())) {
            ResultadoLimpeza limpeza = limpador.excluir(filtrados);
            resultado.getArquivos().removeAll(filtrados);
            imprimirResultadoLimpeza(limpeza);
        }
    }

    // -------------------------------------------------------------------------
    // Exclusão
    // -------------------------------------------------------------------------

    private static void excluirPorCategoria(Scanner sc, ResultadoAnalise resultado) {
        Map<String, EstatisticaCategoria> estatisticas = resultado.getEstatisticas();
        if (estatisticas.isEmpty()) { System.out.println("Nenhuma categoria encontrada."); return; }

        System.out.println("\nCategorias disponíveis: " + String.join(", ", estatisticas.keySet()));
        System.out.print("Categoria a excluir: ");
        String categoria = sc.nextLine().trim();

        List<ArquivoInfo> alvo = resultado.getArquivos().stream()
                .filter(a -> categoria.equalsIgnoreCase(a.getCategoria().getNome()))
                .toList();

        if (!confirmarExclusao(sc, alvo)) return;
        ResultadoLimpeza limpeza = limpador.excluir(alvo);
        resultado.getArquivos().removeAll(alvo);
        imprimirResultadoLimpeza(limpeza);
    }

    private static void excluirPorTamanho(Scanner sc, ResultadoAnalise resultado) {
        System.out.print("\nExcluir arquivos maiores que quantos MB? ");
        double limiteMb;
        try {
            limiteMb = Double.parseDouble(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido.");
            return;
        }

        List<ArquivoInfo> alvo = new FiltroArquivos()
                .comTamanhoMin(limiteMb)
                .aplicar(resultado.getArquivos());

        if (!confirmarExclusao(sc, alvo)) return;
        ResultadoLimpeza limpeza = limpador.excluir(alvo);
        resultado.getArquivos().removeAll(alvo);
        imprimirResultadoLimpeza(limpeza);
    }

    private static boolean confirmarExclusao(Scanner sc, List<ArquivoInfo> alvo) {
        if (alvo.isEmpty()) { System.out.println("Nenhum arquivo encontrado."); return false; }
        long total = alvo.stream().mapToLong(ArquivoInfo::getTamanho).sum();
        System.out.println("\n" + alvo.size() + " arquivo(s) — " + formatador.formatar(total) + " serão excluídos.");
        System.out.print("Tem certeza? Digite \"sim\" para confirmar: ");
        if (!"sim".equalsIgnoreCase(sc.nextLine().trim())) {
            System.out.println("Operação cancelada.");
            return false;
        }
        return true;
    }

    private static void imprimirResultadoLimpeza(ResultadoLimpeza limpeza) {
        System.out.println("Excluídos: " + limpeza.getQuantidadeExcluida() +
                " | Liberado: " + formatador.formatar(limpeza.getTamanhoLiberado()) +
                " | Falhas: " + limpeza.getFalhas());
    }

    // -------------------------------------------------------------------------
    // Duplicatas
    // -------------------------------------------------------------------------

    private static void detectarDuplicatas(Scanner sc, ResultadoAnalise resultado) {
        System.out.println("\n=== DETECTAR DUPLICATAS ===");
        System.out.println("1 - Rápido (nome + tamanho)");
        System.out.println("2 - Preciso (conteúdo — MD5)");
        System.out.print("Modo: ");
        String modo = sc.nextLine().trim();

        Map<String, List<ArquivoInfo>> grupos;
        if ("2".equals(modo)) {
            System.out.println("\nCalculando hashes MD5...");
            grupos = detector.detectarPorHash(resultado.getArquivos());
        } else {
            grupos = detector.detectarPorNomeTamanho(resultado.getArquivos());
        }

        if (grupos.isEmpty()) {
            System.out.println("Nenhuma duplicata encontrada.");
            return;
        }

        long totalDuplicatas = grupos.values().stream()
                .mapToLong(g -> g.stream().skip(1).mapToLong(ArquivoInfo::getTamanho).sum())
                .sum();

        System.out.println("\n" + grupos.size() + " grupo(s) de duplicatas encontrados.");
        System.out.println("Espaço que pode ser liberado: " + formatador.formatar(totalDuplicatas) + "\n");

        int i = 1;
        List<ArquivoInfo> todasDuplicatas = new ArrayList<>();
        for (List<ArquivoInfo> grupo : grupos.values()) {
            System.out.println("Grupo " + i++ + " — " + formatador.formatar(grupo.get(0).getTamanho()) + " cada:");
            for (int j = 0; j < grupo.size(); j++) {
                ArquivoInfo a = grupo.get(j);
                String marcador = j == 0 ? "  [manter] " : "  [duplic.] ";
                System.out.println(marcador + a.getCaminho());
                if (j > 0) todasDuplicatas.add(a);
            }
        }

        if (todasDuplicatas.isEmpty()) return;

        System.out.print("\nExcluir todas as cópias (mantendo uma de cada)? Digite \"sim\": ");
        if ("sim".equalsIgnoreCase(sc.nextLine().trim())) {
            ResultadoLimpeza limpeza = limpador.excluir(todasDuplicatas);
            resultado.getArquivos().removeAll(todasDuplicatas);
            imprimirResultadoLimpeza(limpeza);
        }
    }

    // -------------------------------------------------------------------------
    // Exportação
    // -------------------------------------------------------------------------

    private static void exportarInterativo(Scanner sc, ResultadoAnalise resultado) {
        if (resultado.getArquivos().isEmpty()) { System.out.println("Não há arquivos para exportar."); return; }

        System.out.print("\nFormato (csv/json): ");
        String formato = sc.nextLine().trim().toLowerCase();
        if (!formato.equals("csv") && !formato.equals("json")) {
            System.out.println("Formato inválido.");
            return;
        }

        System.out.print("Nome do arquivo (enter para relatorio." + formato + "): ");
        String nomeArquivo = sc.nextLine().trim();
        if (nomeArquivo.isEmpty()) nomeArquivo = "relatorio." + formato;

        exportar(resultado.getArquivos(), formato, nomeArquivo);
    }

    private static void exportar(List<ArquivoInfo> arquivos, String formato, String destino) {
        try {
            if (formato.equals("csv")) {
                exportador.exportarCsv(arquivos, destino, a -> a.getCategoria().getNome());
            } else {
                exportador.exportarJson(arquivos, destino, a -> a.getCategoria().getNome());
            }
            System.out.println("Relatório exportado em: " + destino);
        } catch (IOException e) {
            System.out.println("Erro ao exportar: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Utilitários
    // -------------------------------------------------------------------------

    private static void imprimirLista(List<ArquivoInfo> arquivos) {
        for (ArquivoInfo a : arquivos) {
            System.out.printf("%-40s %10s  [%s]%n",
                    a.getNome(),
                    formatador.formatar(a.getTamanho()),
                    a.getCategoria().getNome());
        }
    }

    private static String pedirCaminho(Scanner sc) {
        System.out.print("Digite o caminho da pasta/arquivo: ");
        String caminho = sc.nextLine().trim();
        if (caminho.isEmpty()) { System.out.println("Caminho não pode estar vazio."); return null; }
        if (!Files.exists(Paths.get(caminho))) { System.out.println("Caminho não existe: " + caminho); return null; }
        return caminho;
    }
}