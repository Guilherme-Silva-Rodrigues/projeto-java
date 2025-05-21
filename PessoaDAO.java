import java.io.*;
import java.util.*;

public class PessoaDAO {
    private List<Pessoa> pessoas = new ArrayList<>();
    private static final String ARQUIVO = "pessoas.txt";

    public PessoaDAO() {
        carregarArquivo();
    }

    private void carregarArquivo() {
        File file = new File(ARQUIVO);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
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

    public boolean deletar(int codigo) {
        Pessoa p = buscarPorCodigo(codigo);
        if (p != null) {
            pessoas.remove(p);
            salvarArquivo();
            return true;
        }
        return false;
    }

    public Pessoa buscarPorCodigo(int codigo) {
        return pessoas.stream()
                .filter(p -> p.getCodigo() == codigo)
                .findFirst()
                .orElse(null);
    }

    public List<Pessoa> listarTodas() {
        return new ArrayList<>(pessoas);
    }
}
