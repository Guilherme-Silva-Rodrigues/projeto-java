import java.util.Objects;

public class Produto {
    private int codigo;
    private String descricao;
    private double custo;
    private double precoVenda;
    private int codigoFornecedor;

    public Produto(int codigo, String descricao, double custo, double precoVenda, int codigoFornecedor) {
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição não pode ser vazia.");
        }
        if (custo < 0) {
            throw new IllegalArgumentException("Custo não pode ser negativo.");
        }
        if (precoVenda < 0) {
            throw new IllegalArgumentException("Preço de venda não pode ser negativo.");
        }
        
        if (codigoFornecedor <= 0) { 
            throw new IllegalArgumentException("Código do fornecedor inválido.");
        }

        this.codigo = codigo;
        this.descricao = descricao;
        this.custo = custo;
        this.precoVenda = precoVenda;
        this.codigoFornecedor = codigoFornecedor;
    }

    
    public int getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getCusto() {
        return custo;
    }

    public double getPrecoVenda() {
        return precoVenda;
    }

    public int getCodigoFornecedor() {
        return codigoFornecedor;
    }

    
    public void setDescricao(String descricao) {
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição não pode ser vazia.");
        }
        this.descricao = descricao;
    }

    public void setCusto(double custo) {
        if (custo < 0) {
            throw new IllegalArgumentException("Custo não pode ser negativo.");
        }
        this.custo = custo;
    }

    public void setPrecoVenda(double precoVenda) {
        if (precoVenda < 0) {
            throw new IllegalArgumentException("Preço de venda não pode ser negativo.");
        }
        this.precoVenda = precoVenda;
    }

    public void setCodigoFornecedor(int codigoFornecedor) {
        if (codigoFornecedor <= 0) {
            throw new IllegalArgumentException("Código do fornecedor inválido.");
        }
        this.codigoFornecedor = codigoFornecedor;
    }

    @Override
    public String toString() {
        
        return codigo + ";" +
               descricao.replace(";", "") + ";" + 
               String.format("%.2f", custo).replace(",", ".") + ";" +
               String.format("%.2f", precoVenda).replace(",", ".") + ";" +
               codigoFornecedor;
    }

    public static Produto fromString(String linha) {
        if (linha == null || linha.trim().isEmpty()) {
            throw new IllegalArgumentException("Linha de produto não pode ser vazia.");
        }
        String[] partes = linha.split(";", -1);
        if (partes.length != 5) {
            throw new IllegalArgumentException("Formato de linha de produto inválido. Esperado 5 partes, recebido: " + partes.length + " -> " + linha);
        }
        try {
            int codigo = Integer.parseInt(partes[0].trim());
            String descricao = partes[1];
            double custo = Double.parseDouble(partes[2].trim()); 
            double precoVenda = Double.parseDouble(partes[3].trim());
            int codigoFornecedor = Integer.parseInt(partes[4].trim());
            return new Produto(codigo, descricao, custo, precoVenda, codigoFornecedor);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Erro ao converter número na linha do produto: " + linha, e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Produto produto = (Produto) o;
        return codigo == produto.codigo; 
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }
}