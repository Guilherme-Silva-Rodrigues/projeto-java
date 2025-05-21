import java.util.*;

public class Principal {
    private static PessoaDAO dao = new PessoaDAO();
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        int opcao;
        do {
            System.out.println("\n--- MENU ---");
            System.out.println("1 - Cadastrar Pessoa");
            System.out.println("2 - Listar Pessoas");
            System.out.println("3 - Atualizar Pessoa");
            System.out.println("4 - Deletar Pessoa");
            System.out.println("0 - Sair");
            System.out.print("Opcao: ");
            opcao = sc.nextInt();
            sc.nextLine();

            switch (opcao) {
                case 1:
                    cadastrarPessoa();
                    break;
                case 2:
                    listarPessoas();
                    break;
                case 3:
                    atualizarPessoa();
                    break;
                case 4:
                    deletarPessoa();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        } while (opcao != 0);
    }

    private static void cadastrarPessoa() {
        System.out.print("Código: ");
        int codigo = sc.nextInt();
        sc.nextLine();

        if (dao.buscarPorCodigo(codigo) != null) {
            System.out.println("Código já existe!");
            return;
        }

        System.out.print("Nome: ");
        String nome = sc.nextLine();

        System.out.println("Tipo (1-Cliente, 2-Fornecedor, 3-Ambos): ");
        int tipoInt = sc.nextInt();
        sc.nextLine();

        TipoPessoa tipo;
        switch (tipoInt) {
            case 1:
                tipo = TipoPessoa.CLIENTE;
                break;
            case 2:
                tipo = TipoPessoa.FORNECEDOR;
                break;
            case 3:
                tipo = TipoPessoa.AMBOS;
                break;
            default:
                System.out.println("Tipo inválido!");
                return;
        }

        Pessoa p = new Pessoa(codigo, nome, tipo);
        dao.adicionar(p);
        System.out.println("Pessoa cadastrada com sucesso!");
    }

    private static void listarPessoas() {
        List<Pessoa> pessoas = dao.listarTodas();
        if (pessoas.isEmpty()) {
            System.out.println("Nenhuma pessoa cadastrada.");
        } else {
            System.out.println("\nLista de Pessoas:");
            for (Pessoa p : pessoas) {
                System.out.println("Código: " + p.getCodigo() +
                        ", Nome: " + p.getNome() +
                        ", Tipo: " + p.getTipo());
            }
        }
    }

    private static void atualizarPessoa() {
        System.out.print("Código da pessoa a atualizar: ");
        int codigo = sc.nextInt();
        sc.nextLine();

        Pessoa p = dao.buscarPorCodigo(codigo);
        if (p == null) {
            System.out.println("Pessoa não encontrada.");
            return;
        }

        System.out.print("Novo nome (atual: " + p.getNome() + "): ");
        String nome = sc.nextLine();

        System.out.println("Novo tipo (1-Cliente, 2-Fornecedor, 3-Ambos): ");
        int tipoInt = sc.nextInt();
        sc.nextLine();

        TipoPessoa tipo;
        switch (tipoInt) {
            case 1:
                tipo = TipoPessoa.CLIENTE;
                break;
            case 2:
                tipo = TipoPessoa.FORNECEDOR;
                break;
            case 3:
                tipo = TipoPessoa.AMBOS;
                break;
            default:
                System.out.println("Tipo inválido!");
                return;
        }

        if (dao.atualizar(codigo, nome, tipo)) {
            System.out.println("Pessoa atualizada com sucesso!");
        } else {
            System.out.println("Erro ao atualizar pessoa.");
        }
    }

    private static void deletarPessoa() {
        System.out.print("Código da pessoa a deletar: ");
        int codigo = sc.nextInt();
        sc.nextLine();

        if (dao.deletar(codigo)) {
            System.out.println("Pessoa removida com sucesso!");
        } else {
            System.out.println("Pessoa não encontrada.");
        }
    }
}
