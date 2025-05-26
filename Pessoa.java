import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Pessoa {

    private int codigo;
    private String nome;
    private TipoPessoa tipo;
    private List<Endereco> enderecos;

    public Pessoa(int codigo, String nome, TipoPessoa tipo) {
        this.codigo = codigo;
        this.nome = nome;
        this.tipo = tipo;
        this.enderecos = new ArrayList<>();
    }

    // Métodos de acesso (getters e setters)
    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TipoPessoa getTipo() {
        return tipo;
    }

    public void setTipo(TipoPessoa tipo) {
        this.tipo = tipo;
    }

    public List<Endereco> getEnderecos() {
        return enderecos;
    }

    // Métodos para gerenciar endereços
    public void adicionarEndereco(Endereco endereco) {
        enderecos.add(endereco);
    }

    public boolean removerEndereco(int index) {
        if (index >= 0 && index < enderecos.size()) {
            enderecos.remove(index);
            return true;
        }
        return false;
    }

    // Serialização dos dados da pessoa + endereços
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(codigo).append(";")
          .append(nome).append(";")
          .append(tipo);

        for (Endereco e : enderecos) {
            sb.append(";").append(e.toString());
        }
        return sb.toString();
    }

    // Conversão de uma linha do arquivo para um objeto Pessoa
    public static Pessoa fromString(String linha) {
        String[] partes = linha.split(";");
        int codigo = Integer.parseInt(partes[0]);
        String nome = partes[1];
        TipoPessoa tipo = TipoPessoa.valueOf(partes[2]);

        Pessoa p = new Pessoa(codigo, nome, tipo);

        // Verifica se existem endereços na linha
        for (int i = 3; i + 4 < partes.length; i += 5) {
            String cep = partes[i];
            String logradouro = partes[i + 1];
            String numero = partes[i + 2];
            String complemento = partes[i + 3];
            TipoEndereco tipoEndereco = TipoEndereco.valueOf(partes[i + 4]);

            Endereco e = new Endereco(cep, logradouro, numero, complemento, tipoEndereco);
            p.adicionarEndereco(e);
        }

        return p;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pessoa pessoa)) return false;
        return codigo == pessoa.codigo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }
}
