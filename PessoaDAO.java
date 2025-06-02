import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PessoaDAO {
    private static final String ARQUIVO = "pessoas.txt";
    private List<Pessoa> pessoas = new ArrayList<>();

    public PessoaDAO() {
        carregarArquivo();
    }
    public List<Pessoa> listarTodas() {
        return new ArrayList<>(pessoas);
    }
    

    private void carregarArquivo() {
        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                Pessoa p = Pessoa.fromString(linha);
                pessoas.add(p);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void salvarArquivo() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARQUIVO))) {
            for (Pessoa p : pessoas) {
                bw.write(p.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void adicionar(Pessoa p) {
        pessoas.add(p);
        salvarArquivo();
    }

    public boolean atualizar(int codigo, String novoNome, TipoPessoa novoTipo) {
        for (Pessoa p : pessoas) {
            if (p.getCodigo() == codigo) {
                p.setNome(novoNome);
                p.setTipo(novoTipo);
                salvarArquivo();
                return true;
            }
        }
        return false;
    }

    public boolean remover(int codigo) {
        boolean removido = pessoas.removeIf(p -> p.getCodigo() == codigo);
        if (removido) {
            salvarArquivo();
        }
        return removido;
    }

    public List<Pessoa> listar() {
        return pessoas;
    }

    public Pessoa buscarPorCodigo(int codigo) {
        for (Pessoa p : pessoas) {
            if (p.getCodigo() == codigo) {
                return p;
            }
        }
        return null;
    }
    
}
