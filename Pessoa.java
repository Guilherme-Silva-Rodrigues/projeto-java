import java.util.ArrayList;
import java.util.List;

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

    public int getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public TipoPessoa getTipo() {
        return tipo;
    }

    public List<Endereco> getEnderecos() {
        return enderecos;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setTipo(TipoPessoa tipo) {
        this.tipo = tipo;
    }

    public void adicionarEndereco(Endereco e) {
        enderecos.add(e);
    }

    public boolean removerEndereco(int index) {
        if (index >= 0 && index < enderecos.size()) {
            enderecos.remove(index);
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(codigo).append(";").append(nome).append(";").append(tipo).append(";");
        for (Endereco e : enderecos) {
            sb.append("[").append(e.toString()).append("]");
        }
        return sb.toString();
    }

    public static Pessoa fromString(String linha) {
        String[] partes = linha.split(";", 4);
        int codigo = Integer.parseInt(partes[0]);
        String nome = partes[1];
        TipoPessoa tipo = TipoPessoa.valueOf(partes[2].toUpperCase());

        Pessoa p = new Pessoa(codigo, nome, tipo);

        if (partes.length > 3) {
            String enderecosString = partes[3];
            String[] enderecosSeparados = enderecosString.split("\\]\\[|\\[|\\]");
            for (String eStr : enderecosSeparados) {
                if (!eStr.isBlank()) {
                    Endereco e = Endereco.fromString(eStr);
                    p.adicionarEndereco(e);
                }
            }
        }

        return p;
    }
}
