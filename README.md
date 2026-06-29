# Storage Analyzer

Analisador de armazenamento em disco via linha de comando. Escrito em Java puro, sem dependências externas.

## Funcionalidades

- Varre recursivamente um diretório e lista todos os arquivos ordenados por tamanho (maiores primeiro)
- Classifica arquivos por categoria: **Imagens**, **Videos**, **Documentos** e **Outros**
- Exibe estatísticas agregadas por categoria (quantidade de arquivos e tamanho total)
- Formata tamanhos em B, KB, MB e GB legíveis

## Como usar

Compile e execute com o JDK 26+:

```bash
# Compilar
javac -d out src/app.Main.java src/model/*.java src/service/*.java src/util/*.java

# Executar
java -cp out app.Main
```

O programa solicitará o caminho de uma pasta ou arquivo para análise.

## Exemplo de saída

```
Digite o caminho da pasta/arquivo: /home/usuario/Downloads

Categoria: Imagens
Quantidade: 45
Tamanho total: 125829120 bytes
Categoria: Videos
Quantidade: 8
Tamanho total: 2147483648 bytes
Categoria: Documentos
Quantidade: 12
Tamanho total: 52428800 bytes
Categoria: Outros
Quantidade: 230
Tamanho total: 1073741824 bytes

=== ARQUIVOS ORDENADOS POR TAMANHO ===

video.mp4 - 1.50 GB
backup.zip - 800.00 MB
foto.jpg - 12.50 MB
...
```

## Estrutura do projeto

```
src/
├── app.Main.java                     # Ponto de entrada
├── model/
│   ├── ArquivoInfo.java          # Dados de um arquivo (nome, caminho, tamanho)
│   └── EstatisticaCategoria.java # Acumulador de estatísticas por categoria
├── service/
│   └── AnalisadorDisco.java      # Lógica de varredura e classificação
└── util/
    └── FormatadorTamanho.java    # Conversão de bytes para formato legível
```

## Licença

Este projeto é open source sob a licença MIT.
