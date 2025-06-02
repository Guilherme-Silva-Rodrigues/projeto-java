import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Principal {
    static PessoaDAO pessoaDAO = new PessoaDAO();
    static ProdutoDAO produtoDAO = new ProdutoDAO(pessoaDAO);
    private static Scanner sc = new Scanner(System.in);
    private static DecimalFormat df = new DecimalFormat("#,##0.00");


    public static void main(String[] args) {
        int opcao;
        do {
            System.out.println("\n--- MENU PRINCIPAL ---");
            System.out.println("1 - Gerenciar Pessoas");
            System.out.println("2 - Gerenciar Produtos");
            System.out.println("0 - Sair");
            System.out.print("Opcao: ");
            opcao = lerInteiro("");

            switch (opcao) {
                case 1:
                    menuPessoas();
                    break;
                case 2:
                    menuProdutos();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        } while (opcao != 0);
        sc.close();
    }

    private static double lerDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                double valor = Double.parseDouble(sc.nextLine().replace(',', '.'));
                return valor;
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, insira um número decimal (ex: 10.99 ou 10,99).");
            }
        }
    }

    private static int lerInteiro(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int valor = Integer.parseInt(sc.nextLine());
                return valor;
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, insira um número inteiro.");
            }
        }
    }

    private static String lerStringNaoVazia(String prompt) {
        String valor;
        do {
            System.out.print(prompt);
            valor = sc.nextLine().trim();
            if (valor.isEmpty()) {
                System.out.println("Este campo não pode ser vazio. Tente novamente.");
            }
        } while (valor.isEmpty());
        return valor;
    }

    private static TipoPessoa lerTipoPessoaInput() {
        while (true) {
            System.out.println("Tipo da Pessoa (1-CLIENTE, 2-FORNECEDOR, 3-AMBOS): ");
            int tipoInt = lerInteiro("");
            switch (tipoInt) {
                case 1: return TipoPessoa.CLIENTE;
                case 2: return TipoPessoa.FORNECEDOR;
                case 3: return TipoPessoa.AMBOS;
                default:
                    System.out.println("Opção de tipo de pessoa inválida! Tente novamente.");
            }
        }
    }

    private static TipoEndereco lerTipoEnderecoInput() {
        while (true) {
            System.out.println("Tipo de Endereço (1-COMERCIAL, 2-RESIDENCIAL, 3-ENTREGA, 4-CORRESPONDENCIA): ");
            int tipoInt = lerInteiro("");
            switch (tipoInt) {
                case 1: return TipoEndereco.COMERCIAL;
                case 2: return TipoEndereco.RESIDENCIAL;
                case 3: return TipoEndereco.ENTREGA;
                case 4: return TipoEndereco.CORRESPONDENCIA;
                default:
                    System.out.println("Opção de tipo de endereço inválida! Tente novamente.");
            }
        }
    }

    private static void menuPessoas() {
        int opcao;
        do {
            System.out.println("\n--- MENU PESSOAS ---");
            System.out.println("1 - Cadastrar Pessoa");
            System.out.println("2 - Listar Pessoas");
            System.out.println("3 - Atualizar Pessoa");
            System.out.println("4 - Deletar Pessoa");
            System.out.println("0 - Voltar ao Menu Principal");
            System.out.print("Opcao: ");
            opcao = lerInteiro("");

            switch (opcao) {
                case 1:
                    cadastrarPessoa();
                    break;
                case 2:
                    listarPessoas();
                    break;
                case 3:
                    atualizarPessoa();
                    break;
                case 4:
                    deletarPessoa();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        } while (opcao != 0);
    }

    private static void cadastrarPessoa() {
        System.out.println("\n--- Cadastrar Nova Pessoa ---");
        int codigo = lerInteiro("Código: ");

        if (pessoaDAO.buscarPorCodigo(codigo) != null) {
            System.out.println("ERRO: Código de pessoa já cadastrado!");
            return;
        }

        String nome = lerStringNaoVazia("Nome: ");
        TipoPessoa tipoPessoa = lerTipoPessoaInput();

        System.out.println("\n--- Dados do Endereço da Pessoa ---");
        System.out.print("Deseja cadastrar endereço para esta pessoa? (s/n): ");
        String cadastraEndereco = sc.nextLine().trim();
        Endereco endereco = null;
        if (cadastraEndereco.equalsIgnoreCase("s")) {
            String rua = lerStringNaoVazia("Rua: ");
            String numero = lerStringNaoVazia("Número: ");
            String cidade = lerStringNaoVazia("Cidade: ");
            TipoEndereco tipoEndereco = lerTipoEnderecoInput();
            try {
                 endereco = new Endereco(rua, numero, cidade, tipoEndereco);
            } catch (IllegalArgumentException e) {
                System.out.println("ERRO ao criar endereço: " + e.getMessage());
                return;
            }
        }

        try {
            Pessoa p = new Pessoa(codigo, nome, tipoPessoa, endereco);
            pessoaDAO.adicionar(p);
            System.out.println("Pessoa cadastrada com sucesso!");
        } catch (IllegalArgumentException e) {
            System.out.println("ERRO ao cadastrar pessoa: " + e.getMessage());
        }
    }

    private static void listarPessoas() {
        List<Pessoa> pessoas = pessoaDAO.listarTodas();
        if (pessoas.isEmpty()) {
            System.out.println("Nenhuma pessoa cadastrada.");
        } else {
            System.out.println("\n--- Lista de Pessoas ---");
            for (Pessoa p : pessoas) {
                System.out.print("Código: " + p.getCodigo() +
                        ", Nome: " + p.getNome() +
                        ", Tipo Pessoa: " + p.getTipo());
                if (p.getEndereco() != null) {
                    Endereco end = p.getEndereco();
                    System.out.print(", Endereço: [" + end.getRua() + ", " + end.getNumero() +
                                     ", " + end.getCidade() + ", TIPO: " + end.getTipo() + "]");
                } else {
                    System.out.print(", Endereço: (Não informado)");
                }
                System.out.println();
            }
        }
    }

    private static void atualizarPessoa() {
        System.out.println("\n--- Atualizar Pessoa ---");
        int codigo = lerInteiro("Código da pessoa a atualizar: ");

        Pessoa pExistente = pessoaDAO.buscarPorCodigo(codigo);
        if (pExistente == null) {
            System.out.println("Pessoa não encontrada.");
            return;
        }

        System.out.println("Deixe em branco para manter o valor atual (exceto para tipos).");

        System.out.print("Novo nome (atual: " + pExistente.getNome() + "): ");
        String nome = sc.nextLine().trim();
        if (nome.isEmpty()) {
            nome = pExistente.getNome();
        }

        System.out.println("Novo Tipo da Pessoa (atual: " + pExistente.getTipo() + "):");
        TipoPessoa novoTipoPessoa = lerTipoPessoaInput();
        
        Endereco enderecoAtualizado = pExistente.getEndereco();
        System.out.println("\n--- Atualizar Endereço da Pessoa ---");
        System.out.print("Deseja modificar o endereço? (s/n): ");
        if (sc.nextLine().trim().equalsIgnoreCase("s")) {
            if (enderecoAtualizado == null) {
                 System.out.println("Cadastrando novo endereço para a pessoa:");
            }
            String rua = lerStringNaoVazia("Rua (atual: " + (enderecoAtualizado != null ? enderecoAtualizado.getRua() : "N/A") + "): ");
            String numero = lerStringNaoVazia("Número (atual: " + (enderecoAtualizado != null ? enderecoAtualizado.getNumero() : "N/A") + "): ");
            String cidade = lerStringNaoVazia("Cidade (atual: " + (enderecoAtualizado != null ? enderecoAtualizado.getCidade() : "N/A") + "): ");
            TipoEndereco tipoEnd = lerTipoEnderecoInput();
            try {
                enderecoAtualizado = new Endereco(rua, numero, cidade, tipoEnd);
            } catch (IllegalArgumentException e) {
                System.out.println("ERRO ao criar/atualizar endereço: " + e.getMessage());
                return;
            }
        }

        try {
            pExistente.setNome(nome);
            pExistente.setTipo(novoTipoPessoa);
            pExistente.setEndereco(enderecoAtualizado);

            if (pessoaDAO.remover(codigo)) {
                pessoaDAO.adicionar(pExistente);
                System.out.println("Pessoa atualizada com sucesso!");
            } else {
                System.out.println("Erro ao atualizar pessoa (falha ao remover/readicionar).");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("ERRO ao atualizar pessoa: " + e.getMessage());
        }
    }

    private static void deletarPessoa() {
        System.out.println("\n--- Deletar Pessoa ---");
        int codigo = lerInteiro("Código da pessoa a deletar: ");

        if (pessoaDAO.remover(codigo)) {
            System.out.println("Pessoa removida com sucesso!");
        } else {
            System.out.println("Pessoa não encontrada ou erro ao remover.");
        }
    }

    private static void menuProdutos() {
        int opcao;
        do {
            System.out.println("\n--- MENU PRODUTOS ---");
            System.out.println("1 - Cadastrar Produto");
            System.out.println("2 - Listar Produtos");
            System.out.println("3 - Atualizar Produto");
            System.out.println("4 - Deletar Produto");
            System.out.println("0 - Voltar ao Menu Principal");
            System.out.print("Opcao: ");
            opcao = lerInteiro("");

            switch (opcao) {
                case 1:
                    cadastrarProduto();
                    break;
                case 2:
                    listarProdutos();
                    break;
                case 3:
                    atualizarProduto();
                    break;
                case 4:
                    deletarProduto();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        } while (opcao != 0);
    }

    private static void cadastrarProduto() {
        System.out.println("\n--- Cadastrar Novo Produto ---");
        int codigo = lerInteiro("Código do Produto: ");

        if (produtoDAO.buscarPorCodigo(codigo).isPresent()) {
            System.out.println("ERRO: Produto com código " + codigo + " já existe.");
            return;
        }

        String descricao = lerStringNaoVazia("Descrição: ");
        double custo = lerDouble("Custo (R$): ");
        double precoVenda = lerDouble("Preço de Venda (R$): ");

        System.out.println("--- Fornecedores Disponíveis ---");
        List<Pessoa> fornecedores = pessoaDAO.listarTodas().stream()
            .filter(p -> p.getTipo() == TipoPessoa.FORNECEDOR || p.getTipo() == TipoPessoa.AMBOS)
            .toList();

        if (fornecedores.isEmpty()) {
            System.out.println("Nenhum fornecedor cadastrado. Cadastre um fornecedor primeiro.");
            return;
        }
        for (Pessoa f : fornecedores) {
            System.out.println("Código: " + f.getCodigo() + " - Nome: " + f.getNome());
        }
        int codigoFornecedor = lerInteiro("Código do Fornecedor: ");

        try {
            Produto produto = new Produto(codigo, descricao, custo, precoVenda, codigoFornecedor);
            if (produtoDAO.adicionar(produto)) {
                System.out.println("Produto cadastrado com sucesso!");
            } else {
                System.out.println("Falha ao cadastrar produto. Verifique os dados e se o fornecedor é válido.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("ERRO ao criar produto: " + e.getMessage());
        }
    }

    private static void listarProdutos() {
        List<Produto> produtos = produtoDAO.listarTodos();
        if (produtos.isEmpty()) {
            System.out.println("Nenhum produto cadastrado.");
        } else {
            System.out.println("\n--- Lista de Produtos ---");
            for (Produto p : produtos) {
                System.out.print("Código: " + p.getCodigo() +
                                 ", Descrição: " + p.getDescricao() +
                                 ", Custo: R$" + df.format(p.getCusto()) +
                                 ", Preço Venda: R$" + df.format(p.getPrecoVenda()));
                Pessoa fornecedor = pessoaDAO.buscarPorCodigo(p.getCodigoFornecedor());
                if (fornecedor != null) {
                    System.out.print(", Fornecedor: " + fornecedor.getNome() + " (Cód: " + p.getCodigoFornecedor() + ")");
                } else {
                    System.out.print(", Fornecedor Cód: " + p.getCodigoFornecedor() + " (Não encontrado)");
                }
                System.out.println();
            }
        }
    }

    private static void atualizarProduto() {
        System.out.println("\n--- Atualizar Produto ---");
        int codigo = lerInteiro("Código do produto a atualizar: ");

        Optional<Produto> produtoOpt = produtoDAO.buscarPorCodigo(codigo);
        if (produtoOpt.isEmpty()) {
            System.out.println("Produto não encontrado.");
            return;
        }
        Produto produtoExistente = produtoOpt.get();

        System.out.println("Deixe em branco para manter o valor atual (exceto para código do fornecedor).");

        System.out.print("Nova descrição (atual: " + produtoExistente.getDescricao() + "): ");
        String descricao = sc.nextLine().trim();
        if (descricao.isEmpty()) descricao = produtoExistente.getDescricao();

        System.out.print("Novo custo (R$) (atual: " + df.format(produtoExistente.getCusto()) + "): ");
        String custoStr = sc.nextLine().trim();
        double custo = produtoExistente.getCusto();
        if (!custoStr.isEmpty()) {
            try {
                custo = Double.parseDouble(custoStr.replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Custo inválido, mantendo o atual.");
            }
        }

        System.out.print("Novo preço de venda (R$) (atual: " + df.format(produtoExistente.getPrecoVenda()) + "): ");
        String precoVendaStr = sc.nextLine().trim();
        double precoVenda = produtoExistente.getPrecoVenda();
        if (!precoVendaStr.isEmpty()) {
            try {
                precoVenda = Double.parseDouble(precoVendaStr.replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Preço de venda inválido, mantendo o atual.");
            }
        }

        System.out.println("--- Fornecedores Disponíveis ---");
         List<Pessoa> fornecedores = pessoaDAO.listarTodas().stream()
            .filter(p -> p.getTipo() == TipoPessoa.FORNECEDOR || p.getTipo() == TipoPessoa.AMBOS)
            .toList();

        if (fornecedores.isEmpty()) {
            System.out.println("Nenhum fornecedor cadastrado. Não é possível alterar o fornecedor do produto.");
        } else {
             for (Pessoa f : fornecedores) {
                System.out.println("Código: " + f.getCodigo() + " - Nome: " + f.getNome());
            }
        }
        int codigoFornecedor = lerInteiro("Novo código do Fornecedor (atual: " + produtoExistente.getCodigoFornecedor() + "): ");

        try {
            Produto produtoAtualizado = new Produto(codigo, descricao, custo, precoVenda, codigoFornecedor);
            if (produtoDAO.atualizar(produtoAtualizado)) {
                System.out.println("Produto atualizado com sucesso!");
            } else {
                System.out.println("Falha ao atualizar produto. Verifique os dados e se o fornecedor é válido.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("ERRO ao atualizar produto: " + e.getMessage());
        }
    }

    private static void deletarProduto() {
        System.out.println("\n--- Deletar Produto ---");
        int codigo = lerInteiro("Código do produto a deletar: ");

        if (produtoDAO.remover(codigo)) {
            System.out.println("Produto removido com sucesso!");
        } else {
            System.out.println("Produto não encontrado ou erro ao remover.");
        }
    }
}