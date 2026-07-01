# Storage Cleaner & Analyzer

Ferramenta CLI em **Java 21** para analisar, categorizar e limpar arquivos no disco. Zero dependências externas.

## Funcionalidades

- **Varredura recursiva** de diretórios com barra de progresso
- **Classificação** em 7 categorias: Imagens, Vídeos, Documentos, Áudio, Compactados, Código e Outros
- **Estatísticas** por categoria (quantidade, tamanho total, percentual)
- **Filtros** por extensão, nome, tamanho mínimo/máximo e ordenação
- **Detecção de duplicatas** (rápida por nome+tamanho ou precisa por hash MD5)
- **Exclusão** por categoria, tamanho mínimo ou seleção filtrada
- **Exportação** de relatórios em CSV ou JSON
- **Modo script** não-interativo via argumentos CLI

## Como usar

### Compilar e executar

```bash
# Compilar
mvn clean package

# Executar (interativo)
java -jar target/storage-cleaner.jar

# Executar (modo script)
java -jar target/storage-cleaner.jar --caminho /home/usuario --categoria IMAGENS --formato json
```

### Comandos CLI

| Argumento | Descrição |
|-----------|-----------|
| `--caminho` | Caminho do diretório a analisar |
| `--categoria` | Filtrar por categoria (IMAGENS, VIDEOS, etc.) |
| `--formato` | Formato do relatório: `csv` ou `json` |
| `--ordenar` | Ordenação: `tamanho` ou `nome` |

## Menu interativo

```
=== STORAGE CLEANER & ANALYZER ===
1. Listar arquivos por tamanho
2. Listar arquivos por categoria
3. Estatísticas por categoria
4. Filtros avançados
5. Detectar duplicatas
6. Excluir arquivos
7. Exportar relatório
0. Sair
```

## Estrutura

```
src/
├── main/java/
│   ├── app/       # Entrada e CLI
│   ├── model/     # Dados do domínio
│   ├── service/   # Lógica de negócio
│   └── util/      # Formatadores
└── test/java/     # Testes unitários (JUnit 5)
```

## Licença

MIT
