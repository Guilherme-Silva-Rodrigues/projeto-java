// Pessoa.java
public class Pessoa {
    private int codigo;
    private String nome;
    private TipoPessoa tipo;
    private Endereco endereco;

   
    public Pessoa(int codigo, String nome, TipoPessoa tipo, Endereco endereco) {
        
        if (nome == null || nome.trim().isEmpty()) throw new IllegalArgumentException("Nome não pode ser vazio.");
        if (tipo == null) throw new IllegalArgumentException("TipoPessoa não pode ser nulo.");
        

        this.codigo = codigo;
        this.nome = nome;
        this.tipo = tipo;
        this.endereco = endereco;
    }

    public int getCodigo() { return codigo; }
    public String getNome() { return nome; }
    public TipoPessoa getTipo() { return tipo; }
    public Endereco getEndereco() { return endereco; } 

    public void setNome(String nome) { this.nome = nome; }
    public void setTipo(TipoPessoa tipo) { this.tipo = tipo; }
    public void setEndereco(Endereco endereco) { this.endereco = endereco; } 

    @Override
    public String toString() {
      
        String enderecoStr = (endereco != null) ? endereco.toString() : "NULL_ENDERECO"; 
        return codigo + ";" + nome + ";" + tipo.name() + ";" + enderecoStr;
    }

    public static Pessoa fromString(String linha) {
        String[] partes = linha.split(";", -1);
        if (partes.length < 3) { 
             throw new IllegalArgumentException("Formato de string de Pessoa inválido (partes insuficientes): " + linha);
        }

        int codigo = Integer.parseInt(partes[0].trim());
        String nome = partes[1];
        TipoPessoa tipo = TipoPessoa.valueOf(partes[2].trim().toUpperCase());
        Endereco endereco = null;

        if (partes.length > 3 && !"NULL_ENDERECO".equals(partes[3])) {
            try {
                
                endereco = Endereco.fromString(partes[3]);
            } catch (IllegalArgumentException e) {
                System.err.println("Erro ao parsear endereço aninhado para Pessoa: " + partes[3] + " - " + e.getMessage());
                
            }
        }
        return new Pessoa(codigo, nome, tipo, endereco);
    }
}