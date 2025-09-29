package project.bank;

import project.bank.exception.AccountNotFoundException;
import project.bank.exception.NoFundsNotFoundException;
import project.bank.repository.AccountRepository;
import project.bank.repository.InvestmentRepository;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

public class Main {

    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final static AccountRepository accountRepository = new AccountRepository();
    private final static InvestmentRepository investmentRepository = new InvestmentRepository();

    public static void main(String[] args) {

        // O ideal é usar try-with-resources para garantir que o Scanner feche
        try (Scanner scanner = new Scanner(System.in)) {

            while (true) {
                System.out.println("\n====================================");
                System.out.println("Olá, seja bem-vindo(a) ao ATOM BANK!");
                System.out.println("====================================");
                System.out.println("1  - Criar conta");
                System.out.println("2  - Criar investimento");
                System.out.println("3  - Iniciar investimento (Carteira)");
                System.out.println("4  - Depositar");
                System.out.println("5  - Sacar");
                System.out.println("6  - Transferir");
                System.out.println("7  - Investir (Aportar)");
                System.out.println("8  - Sacar investimentos (Resgate)");
                System.out.println("9  - Listar contas");
                System.out.println("10 - Listar investimentos disponíveis");
                System.out.println("11 - Listar carteiras com investimentos");
                System.out.println("12 - Atualizar investimentos (Rentabilidade)");
                System.out.println("13 - Histórico de transações");
                System.out.println("14 - Sair");
                System.out.println("====================================");
                System.out.print("Digite a opção desejada: ");

                int option;
                try {
                    // Passa o scanner para o método auxiliar (scanner.nextInt() foi movido)
                    option = getIntInput(scanner);
                } catch (InputMismatchException e) {
                    System.out.println("\nOpção inválida. Por favor, digite um número.");
                    // O método getIntInput já trata o consumo do buffer
                    continue;
                }

                switch (option) {
                    case 1 -> createAccount(scanner);

                    case 2 -> createInvestment(scanner);

                    case 3 -> createWalletInvestment(scanner);

                    case 4 -> deposit(scanner);

                    case 5 -> withdraw(scanner);

                    case 6 -> transferToAccount(scanner);

                    case 7 -> incInvestment(scanner);

                    case 8 -> rescueInvestment(scanner);

                    case 9 -> accountRepository.list().forEach(System.out::println);
                    case 10 -> {
                        System.out.println("\n--- Investimentos Disponíveis ---");
                        investmentRepository.list().forEach(System.out::println);
                        System.out.println("---------------------------------\n");
                    }
                    case 11 -> {
                        System.out.println("\n--- Carteiras de Investimento ---");
                        investmentRepository.listWallets().forEach(System.out::println);
                        System.out.println("---------------------------------\n");
                    }
                    case 12 -> {
                        investmentRepository.updateAmount();
                        System.out.println("\nInvestimentos atualizados com sucesso!");
                    }
                    case 13 -> checkHistory(scanner);
                    case 14 -> System.exit(0);
                    default -> System.out.println("\nOpção inválida, tente novamente.");
                }
            }
        } catch (Exception e) {
            // Este catch pega qualquer falha na inicialização do Scanner ou erro inesperado
            System.err.println("Ocorreu um erro crítico na execução do programa: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Novo método para obter entrada numérica com segurança
    private static int getIntInput(Scanner scanner) {
        if (!scanner.hasNextInt()) {
            scanner.nextLine(); // Limpa a entrada inválida
            throw new InputMismatchException();
        }
        int value = scanner.nextInt();
        scanner.nextLine(); // Consome a nova linha pendente
        return value;
    }

    private static void createAccount(Scanner scanner) {
        System.out.print("Digite as chaves pix (separadas ';'): ");
        var pixInput = Arrays.stream(scanner.nextLine().split(";")).toList();
        System.out.print("Digite o valor inicial do depósito: ");
        var amount = scanner.nextLong();
        scanner.nextLine();
        var wallet = accountRepository.create(pixInput, amount);
        System.out.println("Conta criada com sucesso: " + wallet);
    }

    private static void createInvestment(Scanner scanner) {
        System.out.print("Digite a taxa do investimento (somente o número inteiro): ");
        var tax = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Digite o valor inicial do depósito: ");
        var initialFunds = scanner.nextLong();
        scanner.nextLine();
        var investment = investmentRepository.create(tax, initialFunds);
        System.out.println("Investimento criado com sucesso: " + investment);
    }

    private static void withdraw(Scanner scanner) {
        System.out.print("Digite a chave pix da conta: ");
        var pix = scanner.nextLine();
        System.out.print("Digite o valor do saque: ");
        var amount = scanner.nextLong();
        scanner.nextLine();
        try {
            accountRepository.withdraw(pix, amount);
            System.out.println("Saque de R$" + amount + " realizado com sucesso.");
        } catch (NoFundsNotFoundException | AccountNotFoundException ex) {
            System.out.println("ERRO: " + ex.getMessage());
        }
    }

    private static void deposit(Scanner scanner) {
        System.out.print("Digite a chave pix da conta: ");
        var pix = scanner.nextLine();
        System.out.print("Digite o valor do depósito: ");
        var amount = scanner.nextLong();
        scanner.nextLine();
        try {
            accountRepository.deposit(pix, amount);
            System.out.println("Depósito de R$" + amount + " realizado com sucesso.");
        } catch (AccountNotFoundException ex) {
            System.out.println("ERRO: " + ex.getMessage());
        }
    }

    private static void transferToAccount(Scanner scanner) {
        System.out.print("Digite a chave pix da conta de origem: ");
        var source = scanner.nextLine();
        System.out.print("Digite a chave pix da conta de destino: ");
        var target = scanner.nextLine();
        System.out.print("Digite o valor da transferência: ");
        var amount = scanner.nextLong();
        scanner.nextLine();
        try {
            accountRepository.transferMoney(source, target, amount);
            System.out.println("Transferência de R$" + amount + " realizada com sucesso.");
        } catch (AccountNotFoundException ex) {
            System.out.println("ERRO: " + ex.getMessage());
        }
    }

    private static void createWalletInvestment(Scanner scanner) {
        System.out.print("Informe a chave pix da conta: ");
        var pix = scanner.nextLine();
        var account = accountRepository.findByPix(pix);
        System.out.print("Informe o ID do investimento: ");
        var investmentId = scanner.nextInt();
        scanner.nextLine();
        var investmentWallet = investmentRepository.initInvestment(account, investmentId);
        System.out.println("Carteira de investimento criada: " + investmentWallet);
    }

    private static void incInvestment(Scanner scanner) {
        System.out.print("Digite a chave pix da conta para investimento: ");
        var pix = scanner.nextLine();
        System.out.print("Digite o valor investido (aporte): ");
        var amount = scanner.nextLong();
        scanner.nextLine();
        try {
            investmentRepository.deposit(pix, amount);
            System.out.println("Aporte de R$" + amount + " realizado com sucesso.");
        } catch (AccountNotFoundException ex) {
            System.out.println("ERRO: " + ex.getMessage());
        }
    }

    private static void rescueInvestment(Scanner scanner) {
        System.out.print("Digite a chave pix da conta para resgate do investimento: ");
        var pix = scanner.nextLine();
        System.out.print("Digite o valor do resgate: ");
        var amount = scanner.nextLong();
        scanner.nextLine();
        try {
            investmentRepository.withdraw(pix, amount);
            System.out.println("Resgate de R$" + amount + " realizado com sucesso.");
        } catch (NoFundsNotFoundException | AccountNotFoundException ex) {
            System.out.println("ERRO: " + ex.getMessage());
        }
    }

    private static void checkHistory(Scanner scanner) {
        System.out.print("Informe a chave Pix: ");
        var pix = scanner.nextLine();


        try {
            var sortedHistory = accountRepository.getHistory(pix);

            if (sortedHistory.isEmpty()) {
                System.out.println("Nenhuma transação encontrada para a chave Pix: " + pix);
                return;
            }

            sortedHistory.forEach((key, value) -> {
                if (value.isEmpty()) return;

                System.out.println(key.format(ISO_DATE_TIME));

                // CORREÇÃO: Usa get(0) em vez de getFist()
                System.out.println(value.get(0).transactionId());
                System.out.println(value.get(0).description());

                System.out.println("R$: " + (value.size() / 100) + ", " + (value.size() % 100));
            });

        } catch (AccountNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }
}

