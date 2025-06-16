import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Principal {
    static PessoaDAO pessoaDAO = new PessoaDAO();
    static ProdutoDAO produtoDAO = new ProdutoDAO(pessoaDAO);
    static PedidoVendaDAO pedidoVendaDAO = new PedidoVendaDAO(pessoaDAO, produtoDAO);
    private static Scanner sc = new Scanner(System.in);
    private static DecimalFormat df = new DecimalFormat("#,##0.00");


    public static void main(String[] args) {
        int opcao;
        do {
            System.out.println("\n--- MENU PRINCIPAL ---");
            System.out.println("1 - Gerenciar Pessoas");
            System.out.println("2 - Gerenciar Produtos");
            System.out.println("3 - Gerenciar Pedidos");
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
                case 3:
                    menuPedidos();
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

        if (pessoaDAO.buscarPorCodigo(codigo).isPresent()) {
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

        Optional<Pessoa> pExistenteOpt = pessoaDAO.buscarPorCodigo(codigo);
        if (pExistenteOpt.isEmpty()) {
            System.out.println("Pessoa não encontrada.");
            return;
        }
        Pessoa pExistente = pExistenteOpt.get();

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

            if (pessoaDAO.atualizar(pExistente)) {
                System.out.println("Pessoa atualizada com sucesso!");
            } else {
                System.out.println("Erro ao atualizar pessoa (problema no DAO).");
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
        List<Produto> produtos = produtoDAO.listarTodas();
        if (produtos.isEmpty()) {
            System.out.println("Nenhum produto cadastrado.");
        } else {
            System.out.println("\n--- Lista de Produtos ---");
            for (Produto p : produtos) {
                System.out.print("Código: " + p.getCodigo() +
                                 ", Descrição: " + p.getDescricao() +
                                 ", Custo: R$" + df.format(p.getCusto()) +
                                 ", Preço Venda: R$" + df.format(p.getPrecoVenda()));
                Optional<Pessoa> fornecedorOpt = pessoaDAO.buscarPorCodigo(p.getCodigoFornecedor());
                if (fornecedorOpt.isPresent()) {
                    Pessoa fornecedor = fornecedorOpt.get();
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
        
        Optional<Pessoa> novoFornecedorOpt = pessoaDAO.buscarPorCodigo(codigoFornecedor);
        if (novoFornecedorOpt.isEmpty() || (novoFornecedorOpt.get().getTipo() != TipoPessoa.FORNECEDOR && novoFornecedorOpt.get().getTipo() != TipoPessoa.AMBOS)) {
            System.out.println("Fornecedor selecionado inválido ou não encontrado. Mantendo o fornecedor atual.");
            codigoFornecedor = produtoExistente.getCodigoFornecedor();
        }

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

    private static void menuPedidos() {
        int opcao;
        do {
            System.out.println("\n--- MENU PEDIDOS DE Venda ---");
            System.out.println("1 - Cadastrar Pedido");
            System.out.println("2 - Listar Pedidos");
            System.out.println("3 - Atualizar Pedido");
            System.out.println("4 - Deletar Pedido");
            System.out.println("0 - Voltar ao Menu Principal");
            System.out.print("Opcao: ");
            opcao = lerInteiro("");

            switch (opcao) {
                case 1:
                    cadastrarPedido();
                    break;
                case 2:
                    listarPedidos();
                    break;
                case 3:
                    atualizarPedido();
                    break;
                case 4:
                    deletarPedido();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        } while (opcao != 0);
    }

    private static void cadastrarPedido() {
        System.out.println("\n--- Cadastrar Novo Pedido de Venda ---");
        int numeroPedido = lerInteiro("Número do Pedido: ");

        if (pedidoVendaDAO.buscarPorNumero(numeroPedido).isPresent()) {
            System.out.println("ERRO: Pedido com número " + numeroPedido + " já existe.");
            return;
        }

        System.out.println("\n--- Selecionar Cliente ---");
        List<Pessoa> clientes = pessoaDAO.listarTodas().stream()
                .filter(p -> p.getTipo() == TipoPessoa.CLIENTE || p.getTipo() == TipoPessoa.AMBOS)
                .toList();

        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado. Cadastre um cliente primeiro.");
            return;
        }
        clientes.forEach(c -> System.out.println("Código: " + c.getCodigo() + " - Nome: " + c.getNome()));
        int codigoCliente = lerInteiro("Código do Cliente: ");

        Optional<Pessoa> clienteOpt = pessoaDAO.buscarPorCodigo(codigoCliente);
        if (clienteOpt.isEmpty() || (clienteOpt.get().getTipo() != TipoPessoa.CLIENTE && clienteOpt.get().getTipo() != TipoPessoa.AMBOS)) {
            System.out.println("ERRO: Cliente não encontrado ou não é um tipo de cliente válido.");
            return;
        }
        Pessoa cliente = clienteOpt.get();

        System.out.println("\n--- Endereço de Entrega ---");
        TipoEndereco tipoEnderecoEntrega = lerTipoEnderecoInput();

        List<ItemPedido> itensPedido = new ArrayList<>();
        System.out.println("\n--- Adicionar Itens ao Pedido ---");
        while (true) {
            System.out.print("Deseja adicionar um produto ao pedido? (s/n): ");
            if (!sc.nextLine().trim().equalsIgnoreCase("s")) {
                break;
            }

            listarProdutos();
            int codigoProduto = lerInteiro("Código do Produto: ");
            Optional<Produto> produtoOpt = produtoDAO.buscarPorCodigo(codigoProduto);

            if (produtoOpt.isEmpty()) {
                System.out.println("Produto não encontrado. Tente novamente.");
                continue;
            }
            Produto produto = produtoOpt.get();

            int quantidade = lerInteiro("Quantidade para " + produto.getDescricao() + ": ");

            try {
                ItemPedido item = new ItemPedido(produto.getCodigo(), quantidade, produto.getPrecoVenda());
                Optional<ItemPedido> existingItem = itensPedido.stream()
                                                                .filter(i -> i.getCodigoProduto() == item.getCodigoProduto())
                                                                .findFirst();
                if (existingItem.isPresent()) {
                    existingItem.get().setQuantidade(existingItem.get().getQuantidade() + item.getQuantidade());
                    System.out.println("Quantidade do item " + produto.getDescricao() + " atualizada.");
                } else {
                    itensPedido.add(item);
                    System.out.println("Item " + produto.getDescricao() + " adicionado.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("ERRO ao adicionar item: " + e.getMessage());
            }
        }

        if (itensPedido.isEmpty()) {
            System.out.println("Pedido de Venda deve conter pelo menos um item. Cancelando cadastro.");
            return;
        }

        try {
            PedidoVenda novoPedido = new PedidoVenda(numeroPedido, codigoCliente, tipoEnderecoEntrega, itensPedido);
            if (pedidoVendaDAO.adicionar(novoPedido)) {
                System.out.println("Pedido de Venda cadastrado com sucesso!");
            } else {
                System.out.println("Falha ao cadastrar Pedido de Venda. Verifique os dados.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("ERRO ao cadastrar Pedido de Venda: " + e.getMessage());
        }
    }

    private static void listarPedidos() {
        List<PedidoVenda> pedidos = pedidoVendaDAO.listarTodos();
        if (pedidos.isEmpty()) {
            System.out.println("Nenhum pedido de venda cadastrado.");
        } else {
            System.out.println("\n--- Lista de Pedidos de Venda ---");
            for (PedidoVenda pedido : pedidos) {
                System.out.println("Número Pedido: " + pedido.getNumeroPedido());
                System.out.print("  Cliente: ");
                Optional<Pessoa> clienteOpt = pessoaDAO.buscarPorCodigo(pedido.getCodigoCliente());
                clienteOpt.ifPresentOrElse(
                    c -> System.out.println(c.getNome() + " (Cód: " + c.getCodigo() + ")"),
                    () -> System.out.println("Cód: " + pedido.getCodigoCliente() + " (Não encontrado)")
                );
                System.out.println("  Endereço de Entrega (Tipo): " + pedido.getTipoEnderecoEntrega());
                System.out.println("  Montante Total: R$" + df.format(pedido.getMontanteTotal()));
                System.out.println("  Itens do Pedido:");
                if (pedido.getItens().isEmpty()) {
                    System.out.println("    (Nenhum item)");
                } else {
                    for (ItemPedido item : pedido.getItens()) {
                        Optional<Produto> produtoOpt = produtoDAO.buscarPorCodigo(item.getCodigoProduto());
                        String produtoDesc = produtoOpt.map(Produto::getDescricao).orElse("Produto Desconhecido");
                        System.out.println("    - Produto: " + produtoDesc + " (Cód: " + item.getCodigoProduto() +
                                           "), Quantidade: " + item.getQuantidade() +
                                           ", Preço Unitário: R$" + df.format(item.getPrecoUnitarioNoPedido()) +
                                           ", Subtotal: R$" + df.format(item.getSubtotal()));
                    }
                }
                System.out.println("------------------------------------");
            }
        }
    }

    private static void atualizarPedido() {
        System.out.println("\n--- Atualizar Pedido de Venda ---");
        int numeroPedido = lerInteiro("Número do Pedido a atualizar: ");

        Optional<PedidoVenda> pedidoOpt = pedidoVendaDAO.buscarPorNumero(numeroPedido);
        if (pedidoOpt.isEmpty()) {
            System.out.println("Pedido não encontrado.");
            return;
        }
        PedidoVenda pedidoExistente = pedidoOpt.get();

        System.out.println("Deixe campos em branco para manter os valores atuais, exceto para números ou tipos.");

        System.out.print("Deseja mudar o cliente do pedido (s/n)? (Atual: " + pedidoExistente.getCodigoCliente() + "): ");
        if (sc.nextLine().trim().equalsIgnoreCase("s")) {
            System.out.println("--- Selecionar Novo Cliente ---");
            List<Pessoa> clientes = pessoaDAO.listarTodas().stream()
                    .filter(p -> p.getTipo() == TipoPessoa.CLIENTE || p.getTipo() == TipoPessoa.AMBOS)
                    .toList();
            if (clientes.isEmpty()) {
                System.out.println("Nenhum cliente disponível. Cliente não será alterado.");
            } else {
                clientes.forEach(c -> System.out.println("Código: " + c.getCodigo() + " - Nome: " + c.getNome()));
                int novoCodigoCliente = lerInteiro("Novo Código do Cliente: ");
                Optional<Pessoa> novoClienteOpt = pessoaDAO.buscarPorCodigo(novoCodigoCliente);
                if (novoClienteOpt.isPresent() && (novoClienteOpt.get().getTipo() == TipoPessoa.CLIENTE || novoClienteOpt.get().getTipo() == TipoPessoa.AMBOS)) {
                    pedidoExistente.setCodigoCliente(novoCodigoCliente);
                    System.out.println("Cliente atualizado para " + novoClienteOpt.get().getNome());
                } else {
                    System.out.println("Novo cliente inválido ou não encontrado. Cliente não será alterado.");
                }
            }
        }

        System.out.print("Deseja mudar o tipo de endereço de entrega (s/n)? (Atual: " + pedidoExistente.getTipoEnderecoEntrega() + "): ");
        if (sc.nextLine().trim().equalsIgnoreCase("s")) {
            TipoEndereco novoTipoEndereco = lerTipoEnderecoInput();
            pedidoExistente.setTipoEnderecoEntrega(novoTipoEndereco);
            System.out.println("Tipo de endereço de entrega atualizado para " + novoTipoEndereco);
        }

        System.out.println("\n--- Gerenciar Itens do Pedido ---");
        List<ItemPedido> itensAtuaisTrabalho = new ArrayList<>(pedidoExistente.getItens());

        while (true) {
            System.out.println("\nOpções de itens:");
            System.out.println("1 - Adicionar/Atualizar Item");
            System.out.println("2 - Remover Item");
            System.out.println("0 - Concluir Edição de Itens");
            int itemOpcao = lerInteiro("Opção: ");

            if (itemOpcao == 0) break;

            switch (itemOpcao) {
                case 1:
                    listarProdutos();
                    int codProdAddItem = lerInteiro("Código do Produto a adicionar/atualizar: ");
                    Optional<Produto> prodToAddOpt = produtoDAO.buscarPorCodigo(codProdAddItem);
                    if (prodToAddOpt.isEmpty()) {
                        System.out.println("Produto não encontrado.");
                        break;
                    }
                    Produto prodToAdd = prodToAddOpt.get();
                    int qtdAddItem = lerInteiro("Quantidade para " + prodToAdd.getDescricao() + ": ");

                    try {
                        ItemPedido newItem = new ItemPedido(prodToAdd.getCodigo(), qtdAddItem, prodToAdd.getPrecoVenda());
                        Optional<ItemPedido> existingItemInList = itensAtuaisTrabalho.stream()
                                                                             .filter(i -> i.getCodigoProduto() == newItem.getCodigoProduto())
                                                                             .findFirst();
                        if (existingItemInList.isPresent()) {
                            existingItemInList.get().setQuantidade(newItem.getQuantidade());
                            existingItemInList.get().setPrecoUnitarioNoPedido(newItem.getPrecoUnitarioNoPedido());
                            System.out.println("Item " + prodToAdd.getDescricao() + " atualizado.");
                        } else {
                            itensAtuaisTrabalho.add(newItem);
                            System.out.println("Item " + prodToAdd.getDescricao() + " adicionado.");
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("ERRO ao adicionar/atualizar item: " + e.getMessage());
                    }
                    break;
                case 2:
                    int codProdRemover = lerInteiro("Código do Produto a remover: ");
                    boolean itemRemovido = itensAtuaisTrabalho.removeIf(item -> item.getCodigoProduto() == codProdRemover);
                    if (itemRemovido) {
                        System.out.println("Item removido com sucesso.");
                    } else {
                        System.out.println("Item não encontrado no pedido.");
                    }
                    break;
                default:
                    System.out.println("Opção inválida para itens.");
            }
        }

        if (itensAtuaisTrabalho.isEmpty()) {
            System.out.println("ERRO: Um pedido de venda não pode ficar sem itens. Atualização cancelada.");
            return;
        }

        try {
            pedidoExistente.setItens(itensAtuaisTrabalho);

            if (pedidoVendaDAO.atualizar(pedidoExistente)) {
                System.out.println("Pedido de Venda atualizado com sucesso!");
            } else {
                System.out.println("Falha ao atualizar Pedido de Venda. Verifique os dados.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("ERRO ao finalizar atualização do pedido: " + e.getMessage());
        }
    }

    private static void deletarPedido() {
        System.out.println("\n--- Deletar Pedido de Venda ---");
        int numeroPedido = lerInteiro("Número do Pedido a deletar: ");

        if (pedidoVendaDAO.remover(numeroPedido)) {
            System.out.println("Pedido de Venda removido com sucesso!");
        } else {
            System.out.println("Pedido de Venda não encontrado ou erro ao remover.");
        }
    }
}