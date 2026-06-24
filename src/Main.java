import model.ArquivoInfo;
import service.AnalisadorDisco;

import java.util.Scanner;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        AnalisadorDisco analisador = new AnalisadorDisco();

        System.out.print("Digite o caminho da pasta/arquivo: ");
        String caminho = sc.nextLine();

        List<ArquivoInfo> arquivos = analisador.analisar(caminho);

        System.out.println("Arquivos encontrados: " +  arquivos.size());

        for(ArquivoInfo a : arquivos){
            System.out.println(a.getNome() + " - " + a.getTamanho() + " bytes");
        }
    }
}
