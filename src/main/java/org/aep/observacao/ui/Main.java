package org.aep.observacao.ui;

import org.aep.observacao.model.*;
import org.aep.observacao.service.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static ServicoSolicitacoes servico = new ServicoSolicitacoes();
    private static FilaAtendimento fila = new FilaAtendimento(servico);
    private static List<Categoria> categorias = Arrays.asList(
            new Categoria("Iluminação", 7),
            new Categoria("Buraco", 5),
            new Categoria("Limpeza", 3),
            new Categoria("Saúde", 1),
            new Categoria("Segurança Escolar", 2)
    );
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        inicializarDados();
        while (true) {
            System.out.println("=== ObservAção ===");
            System.out.println("1. Seção Cliente");
            System.out.println("2. Seção Servidor Público");
            System.out.println("0. Sair");
            System.out.print("Escolha > ");
            String input = scanner.nextLine();
            int opcao;
            try {
                opcao = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Opção inválida. Digite um número.");
                continue;
            }
            switch (opcao) {
                case 1:
                    menuCliente();
                    break;
                case 2:
                    menuServidor();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    return;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    private static void inicializarDados() {
        // Dados iniciais podem ser adicionados aqui se necessário
    }

    private static void menuCliente() {
        while (true) {
            System.out.println("\n=== Seção Cliente ===");
            System.out.println("1. Cadastrar Solicitação");
            System.out.println("2. Consultar Minhas Solicitações");
            System.out.println("3. Consultar Solicitação por Código");
            System.out.println("0. Voltar");
            System.out.print("Escolha > ");
            String input = scanner.nextLine();
            int opcao;
            try {
                opcao = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Opção inválida. Digite um número.");
                continue;
            }
            switch (opcao) {
                case 1:
                    cadastrarSolicitacao();
                    break;
                case 2:
                    consultarSolicitacoes();
                    break;
                case 3:
                    consultarSolicitacaoPorCodigo();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    private static void cadastrarSolicitacao() {
        System.out.println("Cadastrar Solicitação");
        System.out.println("Categorias:");
        for (int i = 0; i < categorias.size(); i++) {
            System.out.println((i + 1) + ". " + categorias.get(i));
        }
        System.out.print("Escolha categoria > ");
        String input = scanner.nextLine();
        int catIndex;
        try {
            catIndex = Integer.parseInt(input) - 1;
            if (catIndex < 0 || catIndex >= categorias.size()) {
                System.out.println("Categoria inválida.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida.");
            return;
        }
        Categoria categoria = categorias.get(catIndex);

        System.out.print("Descrição > ");
        String descricao = scanner.nextLine();

        System.out.print("Localização > ");
        String localizacao = scanner.nextLine();

        System.out.println("Prioridade: 1. Baixa, 2. Média, 3. Alta");
        System.out.print("Escolha prioridade > ");
        input = scanner.nextLine();
        int prio;
        try {
            prio = Integer.parseInt(input);
            if (prio < 1 || prio > 3) {
                System.out.println("Prioridade inválida.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida.");
            return;
        }
        Prioridade prioridade = Prioridade.values()[prio - 1];

        System.out.print("Anônimo? (s/n) > ");
        boolean anonimo = scanner.nextLine().equalsIgnoreCase("s");

        Usuario usuario = null;
        if (!anonimo) {
            System.out.print("Nome > ");
            String nome = scanner.nextLine();
            System.out.print("Email > ");
            String email = scanner.nextLine();
            System.out.print("Telefone > ");
            String telefone = scanner.nextLine();
            usuario = new Usuario(1, nome, email, telefone); // simples, sem gerenciamento de id
        }

        mostrarAguardeOperacaoBanco();
        Solicitacao solicitacao = servico.criarSolicitacao(categoria, descricao, localizacao, prioridade, usuario, anonimo);
        System.out.println("Solicitação criada com protocolo: " + solicitacao.getProtocolo());
    }

    private static void consultarSolicitacoes() {
        // Apresenta todas as solicitações disponíveis (a lógica de negócio fica no service)
        List<Solicitacao> solicitacoes = servico.listarSolicitacoes(null, null, null);
        if (solicitacoes.isEmpty()) {
            System.out.println("Nenhuma solicitação encontrada.");
            return;
        }
        System.out.println("Solicitações:");
        for (int i = 0; i < solicitacoes.size(); i++) {
            Solicitacao s = solicitacoes.get(i);
            String anon = s.isAnonimo() ? "(Anônimo) " : "";
            System.out.println((i + 1) + ". " + anon + "Protocolo: " + s.getProtocolo() + " - Status: " + s.getStatus() + " - Categoria: " + s.getCategoria().getNome());
        }
        System.out.print("Escolha o número da solicitação para ver detalhes (ou 0 para voltar) > ");
        String input = scanner.nextLine();
        try {
            int escolha = Integer.parseInt(input);
            if (escolha == 0) return;
            if (escolha < 1 || escolha > solicitacoes.size()) {
                System.out.println("Escolha inválida.");
                return;
            }
            Solicitacao sol = solicitacoes.get(escolha - 1);
            System.out.println(sol);
            System.out.println("Histórico:");
            servico.getHistorico(sol.getId()).forEach(System.out::println);
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida.");
        }
    }

    private static void consultarSolicitacaoPorCodigo() {
        System.out.print("Informe o código/protocolo da solicitação > ");
        String protocolo = scanner.nextLine().trim();

        if (protocolo.isEmpty()) {
            System.out.println("Protocolo não pode ser vazio.");
            return;
        }

        Solicitacao solicitacao = servico.buscarPorProtocolo(protocolo);
        if (solicitacao == null) {
            System.out.println("Solicitação não encontrada para o código informado.");
            return;
        }

        System.out.println("Detalhes da solicitação:");
        System.out.println(solicitacao);
        System.out.println("Histórico de status:");
        servico.getHistorico(solicitacao.getId()).forEach(System.out::println);
    }

    private static void menuServidor() {
        while (true) {
            System.out.println("\n=== Seção Servidor Público ===");
            System.out.println("1. Listar Solicitações");
            System.out.println("2. Atualizar Status");
            System.out.println("3. Ver Detalhes de Solicitação");
            System.out.println("0. Voltar");
            System.out.print("Escolha > ");
            String input = scanner.nextLine();
            int opcao;
            try {
                opcao = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Opção inválida. Digite um número.");
                continue;
            }
            switch (opcao) {
                case 1:
                    listarSolicitacoes();
                    break;
                case 2:
                    atualizarStatus();
                    break;
                case 3:
                    verDetalhes();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    private static void listarSolicitacoes() {
        System.out.println("Listar Solicitações");
        System.out.println("1. Todas");
        System.out.println("2. Por Prioridade");
        System.out.println("3. Por Bairro");
        System.out.println("4. Por Categoria");
        System.out.print("Escolha > ");
        String input = scanner.nextLine();
        int filtro;
        try {
            filtro = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida.");
            return;
        }
        List<Solicitacao> lista = null;
        switch (filtro) {
            case 1:
                lista = servico.listarSolicitacoes(null, null, null);
                break;
            case 2:
                System.out.println("Prioridade: 1. Baixa, 2. Média, 3. Alta");
                System.out.print("Escolha prioridade > ");
                input = scanner.nextLine();
                int prio;
                try {
                    prio = Integer.parseInt(input);
                    if (prio < 1 || prio > 3) {
                        System.out.println("Prioridade inválida.");
                        return;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Entrada inválida.");
                    return;
                }
                lista = fila.getFilaPorPrioridade(Prioridade.values()[prio - 1]);
                break;
            case 3:
                System.out.print("Bairro > ");
                String bairro = scanner.nextLine();
                lista = fila.getFilaPorBairro(bairro);
                break;
            case 4:
                System.out.println("Categorias:");
                for (int i = 0; i < categorias.size(); i++) {
                    System.out.println((i + 1) + ". " + categorias.get(i));
                }
                System.out.print("Escolha categoria > ");
                input = scanner.nextLine();
                int cat;
                try {
                    cat = Integer.parseInt(input) - 1;
                    if (cat < 0 || cat >= categorias.size()) {
                        System.out.println("Categoria inválida.");
                        return;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Entrada inválida.");
                    return;
                }
                lista = fila.getFilaPorCategoria(categorias.get(cat));
                break;
            default:
                System.out.println("Filtro inválido.");
                return;
        }
        if (lista != null) {
            lista.forEach(s -> System.out.println(s.getProtocolo() + " - " + s.getStatus()));
        }
    }

    private static void atualizarStatus() {
        System.out.print("Protocolo da solicitação > ");
        String protocolo = scanner.nextLine();
        Solicitacao sol = servico.buscarPorProtocolo(protocolo);
        if (sol == null) {
            System.out.println("Solicitação não encontrada.");
            return;
        }
        System.out.println("Status atual: " + sol.getStatus());
        System.out.println("Novo status: 1. Triagem, 2. Em Execução, 3. Resolvido, 4. Encerrado");
        System.out.print("Escolha novo status > ");
        String input = scanner.nextLine();
        int statusIndex;
        try {
            statusIndex = Integer.parseInt(input);
            if (statusIndex < 1 || statusIndex > 4) {
                System.out.println("Status inválido.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida.");
            return;
        }
        Status novoStatus = Status.values()[statusIndex]; // TRIAGEM is 1, etc.
        System.out.print("Responsável > ");
        String responsavel = scanner.nextLine();
        System.out.print("Comentário > ");
        String comentario = scanner.nextLine();
        mostrarAguardeOperacaoBanco();
        boolean sucesso = servico.atualizarStatus(sol.getId(), novoStatus, responsavel, comentario);
        if (sucesso) {
            System.out.println("Status atualizado.");
        } else {
            System.out.println("Erro ao atualizar.");
        }
    }

    private static void mostrarAguardeOperacaoBanco() {
        System.out.println("Aguarde... processando e salvando no banco de dados.");
    }

    private static void verDetalhes() {
        System.out.print("Protocolo da solicitação > ");
        String protocolo = scanner.nextLine();
        Solicitacao sol = servico.buscarPorProtocolo(protocolo);
        if (sol == null) {
            System.out.println("Solicitação não encontrada.");
            return;
        }
        System.out.println(sol);
        System.out.println("Histórico:");
        servico.getHistorico(sol.getId()).forEach(System.out::println);
    }
}