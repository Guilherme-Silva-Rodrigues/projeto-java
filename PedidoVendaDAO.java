import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // Usado para operações com streams

public class PedidoVendaDAO {
    private static final String ARQUIVO_PEDIDOS = "pedidos.txt";
    private List<PedidoVenda> pedidos = new ArrayList<>();

    // Dependências injetadas para validar códigos de cliente e produto
    private PessoaDAO pessoaDAO;
    private ProdutoDAO produtoDAO;

    public PedidoVendaDAO(PessoaDAO pessoaDAO, ProdutoDAO produtoDAO) {
        // Injeção de dependência para os DAOs vinculados
        this.pessoaDAO = pessoaDAO;
        this.produtoDAO = produtoDAO;
        carregarPedidos(); // Carrega os pedidos existentes do arquivo na inicialização
    }

    private void carregarPedidos() {
        this.pedidos.clear(); // Limpa a lista atual antes de carregar
        File arquivo = new File(ARQUIVO_PEDIDOS);

        // Informa se o arquivo não existe e inicia com uma lista vazia de pedidos
        if (!arquivo.exists()) {
            System.out.println("Arquivo " + ARQUIVO_PEDIDOS + " não encontrado. Iniciando com uma lista de pedidos vazia.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            int lineNumber = 0;
            while ((linha = br.readLine()) != null) {
                lineNumber++;
                if (linha.trim().isEmpty()) continue; // Pula linhas vazias

                try {
                    PedidoVenda pedido = PedidoVenda.fromString(linha);
                    pedidos.add(pedido);
                } catch (IllegalArgumentException e) {
                    System.err.println("Erro ao processar linha " + lineNumber + " do arquivo " + ARQUIVO_PEDIDOS + ": '" + linha + "'. Erro: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Erro de E/S ao carregar o arquivo " + ARQUIVO_PEDIDOS + ": " + e.getMessage());
            // Em uma aplicação de produção, você pode querer lançar uma exceção de tempo de execução personalizada aqui
        }
    }

    private void salvarPedidos() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARQUIVO_PEDIDOS))) {
            for (PedidoVenda pedido : pedidos) {
                bw.write(pedido.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro de E/S ao salvar o arquivo " + ARQUIVO_PEDIDOS + ": " + e.getMessage());
            // Considere lançar uma exceção de tempo de execução personalizada ou registrar de forma mais robusta
        }
    }

    public boolean adicionar(PedidoVenda pedido) {
        if (pedido == null) {
            System.err.println("Tentativa de adicionar um pedido nulo.");
            return false;
        }
        // Valida se o número do pedido já existe
        if (buscarPorNumero(pedido.getNumeroPedido()).isPresent()) {
            System.err.println("Pedido com número " + pedido.getNumeroPedido() + " já existe.");
            return false;
        }

        // Valida a existência e o tipo do cliente (deve ser CLIENTE ou AMBOS)
        Optional<Pessoa> clienteOpt = pessoaDAO.buscarPorCodigo(pedido.getCodigoCliente());
        if (clienteOpt.isEmpty() ||
            (clienteOpt.get().getTipo() != TipoPessoa.CLIENTE && clienteOpt.get().getTipo() != TipoPessoa.AMBOS)) {
            System.err.println("Cliente com código " + pedido.getCodigoCliente() + " não encontrado ou não é um cliente válido.");
            return false;
        }

        // Valida todos os produtos na lista de itens do pedido
        for (ItemPedido item : pedido.getItens()) {
            if (produtoDAO.buscarPorCodigo(item.getCodigoProduto()).isEmpty()) {
                System.err.println("Produto com código " + item.getCodigoProduto() + " no pedido " + pedido.getNumeroPedido() + " não encontrado. Não é possível adicionar o pedido.");
                return false;
            }
            // Opcional: Você também pode verificar se o preço unitário do item corresponde ao preço atual do produto
            // para alertar/impedir discrepâncias, embora `precoUnitarioNoPedido` seja para capturar o preço no momento da venda.
        }

        pedidos.add(pedido);
        salvarPedidos();
        System.out.println("Pedido " + pedido.getNumeroPedido() + " adicionado com sucesso.");
        return true;
    }

    public List<PedidoVenda> listarTodos() {
        return new ArrayList<>(pedidos); // Retorna uma cópia defensiva
    }

    public Optional<PedidoVenda> buscarPorNumero(int numeroPedido) {
        return pedidos.stream()
                      .filter(p -> p.getNumeroPedido() == numeroPedido)
                      .findFirst();
    }

    public boolean atualizar(PedidoVenda pedidoAtualizado) {
        if (pedidoAtualizado == null) return false;

        Optional<PedidoVenda> pedidoExistenteOpt = buscarPorNumero(pedidoAtualizado.getNumeroPedido());
        if (pedidoExistenteOpt.isPresent()) {
            // Revalida cliente e produtos também para atualizações
            Optional<Pessoa> clienteOpt = pessoaDAO.buscarPorCodigo(pedidoAtualizado.getCodigoCliente());
            if (clienteOpt.isEmpty() ||
                (clienteOpt.get().getTipo() != TipoPessoa.CLIENTE && clienteOpt.get().getTipo() != TipoPessoa.AMBOS)) {
                System.err.println("Na atualização: Cliente com código " + pedidoAtualizado.getCodigoCliente() + " não encontrado ou não é um cliente válido.");
                return false;
            }

            for (ItemPedido item : pedidoAtualizado.getItens()) {
                if (produtoDAO.buscarPorCodigo(item.getCodigoProduto()).isEmpty()) {
                    System.err.println("Na atualização: Produto com código " + item.getCodigoProduto() + " no pedido " + pedidoAtualizado.getNumeroPedido() + " não encontrado. Não é possível atualizar o pedido.");
                    return false;
                }
            }

            // Encontra o índice do pedido existente e o substitui
            for (int i = 0; i < pedidos.size(); i++) {
                if (pedidos.get(i).getNumeroPedido() == pedidoAtualizado.getNumeroPedido()) {
                    pedidos.set(i, pedidoAtualizado);
                    salvarPedidos();
                    System.out.println("Pedido " + pedidoAtualizado.getNumeroPedido() + " atualizado com sucesso.");
                    return true;
                }
            }
        }
        return false;
    }

    public boolean remover(int numeroPedido) {
        boolean removido = pedidos.removeIf(p -> p.getNumeroPedido() == numeroPedido);
        if (removido) {
            salvarPedidos();
            System.out.println("Pedido " + numeroPedido + " removido com sucesso!");
        } else {
            System.out.println("Pedido " + numeroPedido + " não encontrado para remoção.");
        }
        return removido;
    }
}