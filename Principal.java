import java.util.List;
import java.util.Scanner;

public class Principal {
    static PessoaDAO dao = new PessoaDAO();
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
            opcao = lerInteiro(""); // Lê a opção do menu

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
        sc.close();
    }

    private static int lerInteiro(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int valor = Integer.parseInt(sc.nextLine());
                return valor;
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, insira um número inteiro.");
            }
        }
    }

    private static String lerStringNaoVazia(String prompt) {
        String valor;
        do {
            System.out.print(prompt);
            valor = sc.nextLine().trim();
            if (valor.isEmpty()) {
                System.out.println("Este campo não pode ser vazio. Tente novamente.");
            }
        } while (valor.isEmpty());
        return valor;
    }

    private static TipoPessoa lerTipoPessoaInput() {
        while (true) {
            // Alinhado com o uso anterior de CLIENTE, FORNECEDOR, AMBOS
            System.out.println("Tipo da Pessoa (1-CLIENTE, 2-FORNECEDOR, 3-AMBOS): ");
            int tipoInt = lerInteiro("");
            switch (tipoInt) {
                case 1: return TipoPessoa.CLIENTE;
                case 2: return TipoPessoa.FORNECEDOR;
                case 3: return TipoPessoa.AMBOS;
                default:
                    System.out.println("Opção de tipo de pessoa inválida! Tente novamente.");
            }
        }
    }

    private static TipoEndereco lerTipoEnderecoInput() {
        while (true) {
            System.out.println("Tipo de Endereço (1-COMERCIAL, 2-RESIDENCIAL, 3-ENTREGA, 4-CORRESPONDENCIA): ");
            int tipoInt = lerInteiro("");

            switch (tipoInt) {
                case 1: return TipoEndereco.COMERCIAL;
                case 2: return TipoEndereco.RESIDENCIAL;
                case 3: return TipoEndereco.ENTREGA;
                case 4: return TipoEndereco.CORRESPONDENCIA;
                default:
                    System.out.println("Opção de tipo de endereço inválida! Tente novamente.");
            }
        }
    }

    private static void cadastrarPessoa() {
        System.out.println("\n--- Cadastrar Nova Pessoa ---");
        int codigo = lerInteiro("Código: ");

        if (dao.buscarPorCodigo(codigo) != null) {
            System.out.println("ERRO: Código já cadastrado!");
            return;
        }

        String nome = lerStringNaoVazia("Nome: ");
        TipoPessoa tipoPessoa = lerTipoPessoaInput();

        System.out.println("\n--- Dados do Endereço ---");
        String rua = lerStringNaoVazia("Rua: ");
        String numero = lerStringNaoVazia("Número: ");
        String cidade = lerStringNaoVazia("Cidade: ");
        TipoEndereco tipoEndereco = lerTipoEnderecoInput();

        try {
            Endereco endereco = new Endereco(rua, numero, cidade, tipoEndereco);
            Pessoa p = new Pessoa(codigo, nome, tipoPessoa, endereco);
            dao.adicionar(p);
            System.out.println("Pessoa cadastrada com sucesso!");
        } catch (IllegalArgumentException e) {
            System.out.println("ERRO ao cadastrar: " + e.getMessage());
        }
    }

    private static void listarPessoas() {
        List<Pessoa> pessoas = dao.listarTodas();
        if (pessoas.isEmpty()) {
            System.out.println("Nenhuma pessoa cadastrada.");
        } else {
            System.out.println("\n--- Lista de Pessoas ---");
            for (Pessoa p : pessoas) {
                System.out.print("Código: " + p.getCodigo() +
                        ", Nome: " + p.getNome() +
                        ", Tipo Pessoa: " + p.getTipo());
                if (p.getEndereco() != null) {
                    Endereco end = p.getEndereco();
                    System.out.print(", Endereço: [" + end.getRua() + ", " + end.getNumero() +
                                     ", " + end.getCidade() + ", TIPO: " + end.getTipo() + "]");
                } else {
                    System.out.print(", Endereço: (Não informado)");
                }
                System.out.println();
            }
        }
    }

    private static void atualizarPessoa() {
        System.out.println("\n--- Atualizar Pessoa ---");
        int codigo = lerInteiro("Código da pessoa a atualizar: ");

        Pessoa pExistente = dao.buscarPorCodigo(codigo);
        if (pExistente == null) {
            System.out.println("Pessoa não encontrada.");
            return;
        }

        System.out.println("Deixe em branco para manter o valor atual (exceto para tipos).");

        System.out.print("Novo nome (atual: " + pExistente.getNome() + "): ");
        String nome = sc.nextLine().trim();
        if (nome.isEmpty()) {
            nome = pExistente.getNome();
        }

        System.out.println("Novo Tipo da Pessoa (atual: " + pExistente.getTipo() + "):");
        TipoPessoa novoTipoPessoa = lerTipoPessoaInput();

        System.out.println("\n--- Atualizar Endereço ---");
        Endereco endAtual = pExistente.getEndereco();
        String rua, numero, cidade;
        TipoEndereco tipoEnd;

        if (endAtual != null) {
            System.out.print("Nova rua (atual: " + endAtual.getRua() + "): ");
            rua = sc.nextLine().trim();
            if (rua.isEmpty()) rua = endAtual.getRua();

            System.out.print("Novo número (atual: " + endAtual.getNumero() + "): ");
            numero = sc.nextLine().trim();
            if (numero.isEmpty()) numero = endAtual.getNumero();

            System.out.print("Nova cidade (atual: " + endAtual.getCidade() + "): ");
            cidade = sc.nextLine().trim();
            if (cidade.isEmpty()) cidade = endAtual.getCidade();

            System.out.println("Novo Tipo de Endereço (atual: " + endAtual.getTipo() + "):");
            tipoEnd = lerTipoEnderecoInput();
        } else {
            System.out.println("Pessoa não possui endereço cadastrado. Deseja adicionar um? (s/n)");
            if (sc.nextLine().trim().equalsIgnoreCase("s")) {
                rua = lerStringNaoVazia("Rua: ");
                numero = lerStringNaoVazia("Número: ");
                cidade = lerStringNaoVazia("Cidade: ");
                tipoEnd = lerTipoEnderecoInput();
            } else {
                pExistente.setNome(nome);
                pExistente.setTipo(novoTipoPessoa);
                pExistente.setEndereco(null);
                if (dao.remover(codigo)) {
                    dao.adicionar(pExistente);
                    System.out.println("Pessoa atualizada (sem endereço).");
                } else {
                     System.out.println("Erro ao atualizar pessoa (sem endereço).");
                }
                return;
            }
        }

        try {
            Endereco novoEndereco = new Endereco(rua, numero, cidade, tipoEnd);
            pExistente.setNome(nome);
            pExistente.setTipo(novoTipoPessoa);
            pExistente.setEndereco(novoEndereco);

            if (dao.remover(codigo)) {
                dao.adicionar(pExistente);
                System.out.println("Pessoa atualizada com sucesso!");
            } else {
                System.out.println("Erro ao atualizar pessoa (falha ao remover/readicionar).");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("ERRO ao atualizar: " + e.getMessage());
        }
    }

    private static void deletarPessoa() {
        System.out.println("\n--- Deletar Pessoa ---");
        int codigo = lerInteiro("Código da pessoa a deletar: ");

        if (dao.remover(codigo)) {
            System.out.println("Pessoa removida com sucesso!");
        } else {
            System.out.println("Pessoa não encontrada ou erro ao remover.");
        }
    }
}