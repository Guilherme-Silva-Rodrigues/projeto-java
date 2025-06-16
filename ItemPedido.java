import java.text.DecimalFormat; // Usado para formatar valores double consistentemente no toString()
import java.util.Objects; // Usado para gerar hashCode() e no equals()

public class ItemPedido {
    private int codigoProduto;
    private int quantidade;
    private double precoUnitarioNoPedido; // Preço do produto no momento da venda

    public ItemPedido(int codigoProduto, int quantidade, double precoUnitarioNoPedido) {
        // Validação de entrada para garantir a integridade dos dados
        if (codigoProduto <= 0) {
            throw new IllegalArgumentException("O código do produto deve ser positivo.");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser positiva.");
        }
        if (precoUnitarioNoPedido < 0) {
            throw new IllegalArgumentException("O preço unitário não pode ser negativo.");
        }

        this.codigoProduto = codigoProduto;
        this.quantidade = quantidade;
        this.precoUnitarioNoPedido = precoUnitarioNoPedido;
    }

    // --- Getters ---
    public int getCodigoProduto() {
        return codigoProduto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public double getPrecoUnitarioNoPedido() {
        return precoUnitarioNoPedido;
    }

    public double getSubtotal() {
        return quantidade * precoUnitarioNoPedido;
    }

    // --- Setters (para possíveis atualizações de quantidade ou preço dentro de um item) ---
    public void setQuantidade(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser positiva.");
        }
        this.quantidade = quantidade;
    }

    public void setPrecoUnitarioNoPedido(double precoUnitarioNoPedido) {
        if (precoUnitarioNoPedido < 0) {
            throw new IllegalArgumentException("O preço unitário não pode ser negativo.");
        }
        this.precoUnitarioNoPedido = precoUnitarioNoPedido;
    }

    // --- Sobrescritas de Métodos para Serialização e Comparação ---

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("0.00"); // Formata double para duas casas decimais
        // Formato para armazenamento em arquivo: codigoProduto-quantidade-precoUnitario
        return codigoProduto + "-" + quantidade + "-" + df.format(precoUnitarioNoPedido).replace(",", ".");
    }

    public static ItemPedido fromString(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("A string de ItemPedido não pode estar vazia.");
        }
        String[] partes = texto.split("-");
        if (partes.length != 3) {
            throw new IllegalArgumentException("Formato inválido da string de ItemPedido: " + texto);
        }
        try {
            int codigoProduto = Integer.parseInt(partes[0].trim());
            int quantidade = Integer.parseInt(partes[1].trim());
            double precoUnitario = Double.parseDouble(partes[2].trim());
            return new ItemPedido(codigoProduto, quantidade, precoUnitario);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Erro ao converter número na string do ItemPedido: " + texto, e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemPedido that = (ItemPedido) o;
        // Dois itens são considerados iguais se referem ao mesmo código de produto.
        return codigoProduto == that.codigoProduto;
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigoProduto);
    }
}