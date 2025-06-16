import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.text.DecimalFormat; // Usado para formatar o montante total no toString()
import java.util.Optional; // Usado para verificar a existência de itens ao adicionar

public class PedidoVenda {
    private int numeroPedido;
    private int codigoCliente; // Vinculado a um cadastro de Pessoa
    private TipoEndereco tipoEnderecoEntrega; // Tipo do endereço de entrega do cliente
    private List<ItemPedido> itens; // Lista de produtos vendidos e suas quantidades
    private double montanteTotal; // Montante total do pedido (R$)

    // --- Construtor ---
    public PedidoVenda(int numeroPedido, int codigoCliente, TipoEndereco tipoEnderecoEntrega, List<ItemPedido> itens) {
        // Validação de entrada para os atributos principais do pedido
        if (numeroPedido <= 0) {
            throw new IllegalArgumentException("O número do pedido deve ser positivo.");
        }
        if (codigoCliente <= 0) {
            throw new IllegalArgumentException("O código do cliente deve ser positivo.");
        }
        if (tipoEnderecoEntrega == null) {
            throw new IllegalArgumentException("O tipo de endereço de entrega não pode ser nulo.");
        }
        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("O pedido deve conter pelo menos um item.");
        }

        this.numeroPedido = numeroPedido;
        this.codigoCliente = codigoCliente;
        this.tipoEnderecoEntrega = tipoEnderecoEntrega;
        this.itens = new ArrayList<>(itens); // Cópia defensiva para evitar modificação externa
        calcularMontanteTotal(); // Calcula o montante total ao criar o pedido
    }

    // --- Getters ---
    public int getNumeroPedido() {
        return numeroPedido;
    }

    public int getCodigoCliente() {
        return codigoCliente;
    }

    public TipoEndereco getTipoEnderecoEntrega() {
        return tipoEnderecoEntrega;
    }

    public List<ItemPedido> getItens() {
        return new ArrayList<>(itens); // Retorna uma cópia defensiva da lista de itens
    }

    public double getMontanteTotal() {
        return montanteTotal;
    }

    // --- Setters (para permitir atualizações em alguns atributos do pedido) ---
    public void setCodigoCliente(int codigoCliente) {
        if (codigoCliente <= 0) {
            throw new IllegalArgumentException("O código do cliente deve ser positivo.");
        }
        this.codigoCliente = codigoCliente;
    }

    public void setTipoEnderecoEntrega(TipoEndereco tipoEnderecoEntrega) {
        if (tipoEnderecoEntrega == null) {
            throw new IllegalArgumentException("O tipo de endereço de entrega não pode ser nulo.");
        }
        this.tipoEnderecoEntrega = tipoEnderecoEntrega;
    }

    public void setItens(List<ItemPedido> itens) {
        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("O pedido deve conter pelo menos um item.");
        }
        this.itens = new ArrayList<>(itens); // Atualiza a lista de itens com uma cópia defensiva
        calcularMontanteTotal(); // Recalcula o montante total após a alteração da lista de itens
    }

    // --- Métodos Auxiliares para Gerenciamento de Itens ---
    public void adicionarItem(ItemPedido item) {
        if (item == null) {
            throw new IllegalArgumentException("O item do pedido não pode ser nulo.");
        }
        // Verifica se o item já existe; se sim, atualiza a quantidade, caso contrário, adiciona um novo.
        Optional<ItemPedido> existingItem = this.itens.stream()
                                                    .filter(i -> i.getCodigoProduto() == item.getCodigoProduto())
                                                    .findFirst();
        if (existingItem.isPresent()) {
            // Uma abordagem simples: atualiza a quantidade. Você pode querer lançar um erro ou
            // lidar com lógica de mesclagem mais complexa dependendo das regras de negócio.
            existingItem.get().setQuantidade(existingItem.get().getQuantidade() + item.getQuantidade());
        } else {
            this.itens.add(item);
        }
        calcularMontanteTotal();
    }

    public boolean removerItem(int codigoProduto) {
        boolean removido = itens.removeIf(item -> item.getCodigoProduto() == codigoProduto);
        if (removido) {
            calcularMontanteTotal();
        }
        return removido;
    }

    // --- Método Privado para Cálculo do Total ---
    private void calcularMontanteTotal() {
        this.montanteTotal = itens.stream()
                                  .mapToDouble(ItemPedido::getSubtotal)
                                  .sum();
    }

    // --- Sobrescritas de Métodos para Serialização e Comparação ---

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("0.00"); // Formata o total para duas casas decimais
        StringBuilder sb = new StringBuilder();
        // Dados base do pedido: numeroPedido;codigoCliente;tipoEnderecoEntrega;montanteTotal;
        sb.append(numeroPedido).append(";")
          .append(codigoCliente).append(";")
          .append(tipoEnderecoEntrega.name()).append(";")
          .append(df.format(montanteTotal).replace(",", ".")).append(";"); // Montante total

        // Adiciona os itens, separados por "|"
        for (int i = 0; i < itens.size(); i++) {
            sb.append(itens.get(i).toString());
            if (i < itens.size() - 1) {
                sb.append("|"); // Separador entre os itens do pedido
            }
        }
        return sb.toString();
    }

    public static PedidoVenda fromString(String linha) {
        if (linha == null || linha.trim().isEmpty()) {
            throw new IllegalArgumentException("A string de PedidoVenda não pode estar vazia.");
        }

        // Divide as partes principais do pedido. Limite 5 para garantir que a string completa dos itens seja capturada.
        String[] partesPrincipais = linha.split(";", 5);
        if (partesPrincipais.length < 4) { // Pelo menos número do pedido, cliente, tipo_endereco, total (itens podem ser string vazia)
            throw new IllegalArgumentException("Formato inválido da string de PedidoVenda. Esperado pelo menos 4 partes principais, recebido: " + partesPrincipais.length + " -> " + linha);
        }

        try {
            int numeroPedido = Integer.parseInt(partesPrincipais[0].trim());
            int codigoCliente = Integer.parseInt(partesPrincipais[1].trim());
            TipoEndereco tipoEnderecoEntrega = TipoEndereco.valueOf(partesPrincipais[2].trim().toUpperCase());
            // O montanteTotal da string é ignorado aqui, pois será recalculado no construtor
            // para garantir a consistência com os itens.
            // double montanteTotalFromFile = Double.parseDouble(partesPrincipais[3].trim());

            List<ItemPedido> itens = new ArrayList<>();
            // Verifica se existe uma parte de itens e se ela não está vazia
            if (partesPrincipais.length == 5 && !partesPrincipais[4].isEmpty()) {
                String[] itensStr = partesPrincipais[4].split("\\|"); // Usa \\| para escapar o caractere pipe
                for (String itemStr : itensStr) {
                    itens.add(ItemPedido.fromString(itemStr.trim()));
                }
            } else if (partesPrincipais.length < 5) {
                // Se a parte dos itens estiver faltando, e o construtor exige itens, lança um erro.
                throw new IllegalArgumentException("PedidoVenda deve conter itens, mas nenhum foi encontrado na string: " + linha);
            }

            // O construtor validará as entradas e recalculará o montanteTotal
            return new PedidoVenda(numeroPedido, codigoCliente, tipoEnderecoEntrega, itens);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Erro ao converter número na string do PedidoVenda: " + linha, e);
        } catch (IllegalArgumentException e) {
            // Re-lança exceções do construtor ou do ItemPedido.fromString com contexto
            throw new IllegalArgumentException("Erro ao analisar PedidoVenda: " + e.getMessage() + " da linha: " + linha, e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PedidoVenda that = (PedidoVenda) o;
        // Objetos PedidoVenda são considerados iguais se tiverem o mesmo número de pedido
        return numeroPedido == that.numeroPedido;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numeroPedido);
    }
}