public class Pessoa {
    private int codigo;
    private String nome;
    private TipoPessoa tipo;

    public Pessoa(int codigo, String nome, TipoPessoa tipo) {
        this.codigo = codigo;
        this.nome = nome;
        this.tipo = tipo;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public TipoPessoa getTipo() {
        return tipo;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setTipo(TipoPessoa tipo) {
        this.tipo = tipo;
    }
    
    public static Pessoa fromString(String linha) {
    String[] partes = linha.split(";");
    int codigo = Integer.parseInt(partes[0]);
    String nome = partes[1];
    TipoPessoa tipo = TipoPessoa.valueOf(partes[2]);
    return new Pessoa(codigo, nome, tipo);
}
}

enum TipoPessoa {
    CLIENTE, FORNECEDOR, AMBOS
}
