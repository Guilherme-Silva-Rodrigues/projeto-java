public class Endereco {
    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;
    private TipoEndereco tipo;

    public Endereco(String cep, String logradouro, String numero, String complemento, TipoEndereco tipo) {
        this.cep = cep;
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.tipo = tipo;
    }

    public String getCep() {
        return cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public TipoEndereco getTipo() {
        return tipo;
    }

    @Override
    public String toString() {
        return cep + "," + logradouro + "," + numero + "," + complemento + "," + tipo;
    }

    public static Endereco fromString(String str) {
        String[] partes = str.split(",");
        return new Endereco(
            partes[0],
            partes[1],
            partes[2],
            partes[3],
            TipoEndereco.valueOf(partes[4])
        );
    }
}
