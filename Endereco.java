// Endereco.java
public class Endereco {
    private String rua;
    private String numero;
    private String cidade;
    private TipoEndereco tipo; // NOVO CAMPO

    // Construtor atualizado
    public Endereco(String rua, String numero, String cidade, TipoEndereco tipo) {
        // Validação básica (pode ser mais robusta)
        if (rua == null || rua.trim().isEmpty()) throw new IllegalArgumentException("Rua não pode ser vazia.");
        if (numero == null || numero.trim().isEmpty()) throw new IllegalArgumentException("Número não pode ser vazio.");
        if (cidade == null || cidade.trim().isEmpty()) throw new IllegalArgumentException("Cidade não pode ser vazia.");
        if (tipo == null) throw new IllegalArgumentException("Tipo de endereço não pode ser nulo.");

        this.rua = rua;
        this.numero = numero;
        this.cidade = cidade;
        this.tipo = tipo;
    }

    public String getRua() { return rua; }
    public String getNumero() { return numero; }
    public String getCidade() { return cidade; }
    public TipoEndereco getTipo() { return tipo; } // NOVO GETTER

    public void setRua(String rua) { this.rua = rua; }
    public void setNumero(String numero) { this.numero = numero; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public void setTipo(TipoEndereco tipo) { this.tipo = tipo; } // NOVO SETTER

    @Override
    public String toString() {
        // Cuidado com o delimitador ',' se a cidade puder ter vírgula.
        // Usar um delimitador menos comum ou escapar vírgulas seria mais robusto.
        // Por simplicidade, vamos manter a vírgula, mas adicionando o tipo.
        return rua + "," + numero + "," + cidade + "," + tipo.name(); // Adiciona o nome do enum
    }

    public static Endereco fromString(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return null; // Ou lançar exceção, dependendo da sua lógica para endereços opcionais
        }
        String[] partes = texto.split(",", -1); // -1 para incluir partes vazias no final
        if (partes.length != 4) {
            System.err.println("Formato de string de endereço inválido: " + texto + " (esperado 4 partes)");
            // Lançar exceção ou retornar null, dependendo da robustez desejada
            throw new IllegalArgumentException("Formato de string de endereço inválido. Partes: " + partes.length);
        }
        try {
            String rua = partes[0];
            String numero = partes[1];
            String cidade = partes[2];
            TipoEndereco tipo = TipoEndereco.valueOf(partes[3].trim().toUpperCase()); // Converte string para enum
            return new Endereco(rua, numero, cidade, tipo);
        } catch (IllegalArgumentException e) {
            System.err.println("Erro ao converter string para Endereco: " + texto + " - " + e.getMessage());
            throw e; // Re-lança para que o chamador saiba do problema
        }
    }
}