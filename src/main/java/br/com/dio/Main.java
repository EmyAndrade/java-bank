package br.com.dio;

import br.com.dio.exception.AccountNotFoundException;
import br.com.dio.exception.NoFundsEnoughException;
import br.com.dio.exception.PixInUseException;
import br.com.dio.model.AccountWallet;
import br.com.dio.repository.AccountRepository;

import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.SECONDS;

public class Main {

    private final static AccountRepository accountRepository = new AccountRepository();
    private final static br.com.dio.repository.InvestmentRepository investmentRepository = new br.com.dio.repository.InvestmentRepository();

    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Olá, seja bem-vindo ao DIO Bank");
        while (true) {
            System.out.println("Selecione a opção desejada:");
            System.out.println("1 - Criar Conta");
            System.out.println("2 - Criar um investimento");
            System.out.println("3 - Fazer um investimento");
            System.out.println("4 - Depositar na conta");
            System.out.println("5 - Sacar da conta");
            System.out.println("6 - Transferir entre contas");
            System.out.println("7 - Investir");
            System.out.println("8 - Sacar investimento");
            System.out.println("9 - Listar contas");
            System.out.println("10 - Listar investimentos");
            System.out.println("11 - Listar carteiras de investimento");
            System.out.println("12 - Atualizar investimentos");
            System.out.println("13 - Histórico de conta");
            System.out.println("14 - Sair");

            var option = scanner.nextInt();
            switch (option) {
                case 1:
                    createAccount();
                    break;
                case 2:createInvestment();
                    break;
                case 3: createWalletInvestment();
                    break;
                case 4:
                    deposit();
                    break;
                case 5:
                    withdraw();
                    break;
                case 6:
                    transferToAccount();
                    break;
                case 7: incInvestment();
                    break;
                case 8: rescueInvestment();
                    break;
                case 9:
                    accountRepository.list().forEach(System.out::println);
                    break;
                case 10:
                    investmentRepository.list().forEach(System.out::println);
                    break;
                case 11: investmentRepository.listWallets().forEach(System.out::println);
                    break;
                case 12: {
                    investmentRepository.updateAmount();
                    System.out.println("Investimentos reajustados");
                    break;
                }
                case 13: checkHistory();
                    break;
                case 14:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opção inválida, tente novamente.");
                    break;
            }
        }
    }

    private static void createAccount() {
        System.out.println("Informe as chaves Pix (separadas por ';'):");
        var pix = Arrays.stream(scanner.next().split(";"))
                .toList();
        System.out.println("Informe o valor inicial do deposito:");
        var amount = scanner.nextLong();
        var wallet = accountRepository.create(pix, amount);
        System.out.println("Conta criada com sucesso: " + wallet);
    }

    private static void createInvestment() {
        System.out.println("Informe a taxa de investimento");
        var tax = scanner.nextInt();
        System.out.println("Informe o valor inicial do deposito:");
        var initialFunds = scanner.nextLong();
        investmentRepository.create(tax, initialFunds);
        System.out.println("Investimento criado com sucesso!");
    }

    private static void withdraw() {
        System.out.println("Informe a chave pix para saque:");
        var pix = scanner.next();

        System.out.println("Informe o valor do saque:");
        long amount;

        try {
            amount = scanner.nextLong();

            if (amount <= 0) {
                System.out.println("Valor inválido para saque. Deve ser maior que zero.");
                return;
            }

            accountRepository.withdraw(pix, amount);
            System.out.println("Saque realizado com sucesso!");

        } catch (NoFundsEnoughException | AccountNotFoundException ex) {
            System.out.println("Erro: " + ex.getMessage());
        } catch (Exception e) {
            System.out.println("Erro inesperado: " + e.getMessage());
            e.printStackTrace(); // útil para depuração
        } finally {
            scanner.nextLine(); // limpa o buffer
        }
    }


    private static void deposit() {
        System.out.println("Informe a chave pix para depósito:");
        var pix = scanner.next();

        System.out.println("Informe o valor do depósito:");
        long amount;

        try {
            amount = scanner.nextLong();

            if (amount <= 0) {
                System.out.println("Valor inválido para depósito. Deve ser maior que zero.");
                return;
            }

            accountRepository.deposit(pix, amount);
            System.out.println("Depósito realizado com sucesso!");

        } catch (AccountNotFoundException ex) {
            System.out.println("Erro: " + ex.getMessage());
        } catch (Exception e) {
            System.out.println("Erro inesperado: " + e.getMessage());
            e.printStackTrace(); // útil para depuração
        } finally {
            scanner.nextLine(); // limpa o buffer após ler long
        }
    }


    private static void transferToAccount() {
        System.out.println("Informe a chave pix da conta de origem:");
        var source = scanner.next();

        System.out.println("Informe a chave pix da conta para envio:");
        var target = scanner.next();

        System.out.println("Informe o valor da transferencia:");
        long amount;

        try {
            amount = scanner.nextLong();

            if (amount <= 0) {
                System.out.println("Valor invalido para transferencia.");
                return;
            }

            accountRepository.transferMoney(source, target, amount);
            System.out.println("Transferencia realizada com sucesso!");

        } catch (AccountNotFoundException | PixInUseException ex) {
            System.out.println("Erro: " + ex.getMessage());
        } catch (Exception e) {
            System.out.println("Erro inesperado: " + e.getMessage());
            e.printStackTrace(); // Ajuda a debugar
        } finally {
            scanner.nextLine(); // limpa o buffer do scanner
        }
    }


    private static void createWalletInvestment() {
        System.out.println("Informe a chave pix da conta:");
        var pix = scanner.next();
        var account = accountRepository.findByPix(pix);
        System.out.println("Informe o id do investimento:");
        var investmentId = scanner.nextInt();
        var investmentWallet = investmentRepository.initInvestment(account, investmentId);
        System.out.println("Carteira de investimento criada com sucesso: " + investmentWallet);
    }

    private static void incInvestment() {
        System.out.println("Informe a chave pix da conta para investimento:");
        var pix = scanner.next();

        System.out.println("Informe o valor do investimento:");
        long amount;

        try {
            amount = scanner.nextLong();

            if (amount <= 0) {
                System.out.println("Valor invalido para investimento. Deve ser maior que zero.");
                return;
            }

            investmentRepository.deposit(pix, amount);
            System.out.println("Investimento realizado com sucesso!");

        } catch (AccountNotFoundException ex) {
            System.out.println("Erro: " + ex.getMessage());
        } catch (Exception e) {
            System.out.println("Erro inesperado: " + e.getMessage());
            e.printStackTrace(); // útil para depuração
        } finally {
            scanner.nextLine(); // limpa o buffer após nextLong
        }
    }


    private static void rescueInvestment() {
        System.out.println("Informe a chave pix para resgate do investimento:");
        var pix = scanner.next();

        System.out.println("Informe o valor do saque:");
        long amount;

        try {
            amount = scanner.nextLong();

            if (amount <= 0) {
                System.out.println("Valor invalido. Deve ser maior que zero.");
                return;
            }

            investmentRepository.withdraw(pix, amount);
            System.out.println("Resgate realizado com sucesso!");

        } catch (NoFundsEnoughException | AccountNotFoundException ex) {
            System.out.println("Erro: " + ex.getMessage());
        } catch (Exception e) {
            System.out.println("Erro inesperado: " + e.getMessage());
            e.printStackTrace(); // opcional para debug
        } finally {
            scanner.nextLine(); // limpa buffer
        }
    }

    private static void checkHistory() {
        System.out.println("Informe a chave pix da conta para verificar extrato:");
        var pix = scanner.next();

        try {
            AccountWallet wallet = accountRepository.findByPix(pix);
            var audit = wallet.getFinancialTransactions();

            if (audit.isEmpty()) {
                System.out.println("Nenhuma transaçao registrada.");
                return;
            }

            var grouped = audit.stream()
                    .collect(Collectors.groupingBy(t -> t.createdAt().truncatedTo(SECONDS)));

            grouped.forEach((timestamp, transactions) -> {
                System.out.println("\nData/Hora: " + timestamp);
                for (var t : transactions) {
                    System.out.println("- " + t.description());
                }
            });

        } catch (AccountNotFoundException ex) {
            System.out.println("Erro: " + ex.getMessage());
        } catch (Exception e) {
            System.out.println("Erro inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

}