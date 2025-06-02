import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProdutoDAO {
    private static final String ARQUIVO_PRODUTOS = "produtos.txt";
    private List<Produto> produtos = new ArrayList<>();
    // Referência ao PessoaDAO para validar código do fornecedor
    private PessoaDAO pessoaDAO;


    public ProdutoDAO(PessoaDAO pessoaDAO) {
        this.pessoaDAO = pessoaDAO; // Injeção de dependência
        carregarProdutos();
    }

    private void carregarProdutos() {
        this.produtos.clear();
        File arquivo = new File(ARQUIVO_PRODUTOS);
        if (!arquivo.exists()) {
            System.out.println("Arquivo " + ARQUIVO_PRODUTOS + " não encontrado. Iniciando com lista de produtos vazia.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            int lineNumber = 0;
            while ((linha = br.readLine()) != null) {
                lineNumber++;
                if (linha.trim().isEmpty()) continue;
                try {
                    Produto p = Produto.fromString(linha);
                    produtos.add(p);
                } catch (IllegalArgumentException e) {
                    System.err.println("Erro ao processar linha " + lineNumber + " do arquivo " + ARQUIVO_PRODUTOS + ": '" + linha + "'. Erro: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Erro de IO ao carregar o arquivo " + ARQUIVO_PRODUTOS + ": " + e.getMessage());
        }
    }

    private void salvarProdutos() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARQUIVO_PRODUTOS))) {
            for (Produto p : produtos) {
                bw.write(p.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro de IO ao salvar o arquivo " + ARQUIVO_PRODUTOS + ": " + e.getMessage());
            // Considerar lançar uma RuntimeException ou uma exceção customizada
        }
    }

    public boolean adicionar(Produto produto) {
        if (produto == null) {
            System.err.println("Tentativa de adicionar produto nulo.");
            return false;
        }
        // Verifica se o código do produto já existe
        if (buscarPorCodigo(produto.getCodigo()).isPresent()) {
            System.err.println("Produto com código " + produto.getCodigo() + " já existe.");
            return false;
        }
        // Verifica se o fornecedor existe e é do tipo FORNECEDOR ou AMBOS
        Pessoa fornecedor = pessoaDAO.buscarPorCodigo(produto.getCodigoFornecedor());
        if (fornecedor == null ||
            (fornecedor.getTipo() != TipoPessoa.FORNECEDOR && fornecedor.getTipo() != TipoPessoa.AMBOS)) {
            System.err.println("Fornecedor com código " + produto.getCodigoFornecedor() + " não encontrado ou não é um fornecedor válido.");
            return false;
        }

        produtos.add(produto);
        salvarProdutos();
        return true;
    }

    public List<Produto> listarTodos() {
        return new ArrayList<>(produtos); // Retorna uma cópia para evitar modificação externa
    }

    public Optional<Produto> buscarPorCodigo(int codigo) {
        return produtos.stream()
                       .filter(p -> p.getCodigo() == codigo)
                       .findFirst();
    }

    public boolean atualizar(Produto produtoAtualizado) {
        if (produtoAtualizado == null) return false;

        Optional<Produto> produtoExistenteOpt = buscarPorCodigo(produtoAtualizado.getCodigo());
        if (produtoExistenteOpt.isPresent()) {
            // Verifica se o novo fornecedor existe e é válido
            Pessoa fornecedor = pessoaDAO.buscarPorCodigo(produtoAtualizado.getCodigoFornecedor());
            if (fornecedor == null ||
                (fornecedor.getTipo() != TipoPessoa.FORNECEDOR && fornecedor.getTipo() != TipoPessoa.AMBOS)) {
                System.err.println("Ao atualizar: Fornecedor com código " + produtoAtualizado.getCodigoFornecedor() + " não encontrado ou não é um fornecedor válido.");
                return false;
            }

            Produto produtoExistente = produtoExistenteOpt.get();
            produtoExistente.setDescricao(produtoAtualizado.getDescricao());
            produtoExistente.setCusto(produtoAtualizado.getCusto());
            produtoExistente.setPrecoVenda(produtoAtualizado.getPrecoVenda());
            produtoExistente.setCodigoFornecedor(produtoAtualizado.getCodigoFornecedor());
            salvarProdutos();
            return true;
        }
        return false;
    }

    public boolean remover(int codigo) {
        boolean removido = produtos.removeIf(p -> p.getCodigo() == codigo);
        if (removido) {
            salvarProdutos();
        }
        return removido;
    }
}