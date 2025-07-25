package br.com.dio.repository;

import br.com.dio.exception.AccountNotFoundException;
import br.com.dio.exception.PixInUseException;
import br.com.dio.model.AccountWallet;

import java.util.List;
import java.util.ArrayList;

import static br.com.dio.repository.CommonsRepository.checkFundsForTransaction;

public class AccountRepository {

    private List<AccountWallet> accounts = new ArrayList<>();;

    public AccountWallet create(final List<String> pix, final long initialFuds) {
        var pixInUse = accounts.stream().flatMap(a -> a.getPix().stream()).toList();
        for (var p : pix) {
            if (pixInUse.contains(p)) {
                throw new PixInUseException("Pix ja esta em uso: " + p);

            }
        }
        var newAccount = new AccountWallet(initialFuds, pix);
        accounts.add(newAccount);
        return newAccount;
    }

    public void deposit(final String pix, final long fundsAmount) {
        var target = findByPix(pix);
        target.addMoney(fundsAmount, "Deposito");
    }

    public long withdraw(final String pix, final long amount) {
        var source = findByPix(pix);
        checkFundsForTransaction(source, amount);
        source.reduceMoney(amount);
        return amount;
    }

    public void transferMoney(final String sourcePix, final String targetPix, final long amount) {
        var source = findByPix(sourcePix);
        var target = findByPix(targetPix);
        checkFundsForTransaction(source, amount);
        var messsage = "Pix enviado de '" + sourcePix + "'para '" + targetPix + "'";
        target.addMoney(source.reduceMoney(amount), source.getService(), messsage);
    }

    public AccountWallet findByPix(final String pix) {
        if (!accounts.isEmpty()) {
            var allPix = accounts.stream()
                    .flatMap(a -> a.getPix().stream())
                    .toList();

            if (!allPix.contains(pix)) {
                throw new PixInUseException("Pix nao esta em uso: " + pix); // ajuste na mensagem
            }
        }

        return accounts.stream()
                .filter(a -> a.getPix().contains(pix))
                .findFirst()
                .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada para o Pix: " + pix));
    }

    public List<AccountWallet> list() {
        return this.accounts;
    }

}
