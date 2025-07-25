package br.com.dio.repository;

import br.com.dio.exception.AccounWithInvestmentException;
import br.com.dio.exception.InvestmentNotFoundException;
import br.com.dio.exception.PixInUseException;
import br.com.dio.exception.WalletNotFoundException;
import br.com.dio.model.AccountWallet;
import br.com.dio.model.InvestmentWallet;
import br.com.dio.model.Investment;

import java.util.ArrayList;
import java.util.List;

import static br.com.dio.repository.CommonsRepository.checkFundsForTransaction;

public class InvestmentRepository {

    private final List<Investment> investments = new ArrayList<>();
    private final List<InvestmentWallet> wallets = new ArrayList<>();
    private long nextId;

    public Investment create(final long tax, final long initialFunds){
       this.nextId ++;
        var investment = new Investment(this.nextId, tax, initialFunds);
        investments.add(investment);
        return investment;
    }

    public InvestmentWallet initInvestment(final AccountWallet account, final long id) {
        var accountsInUse = wallets.stream().map(InvestmentWallet::getAccount).toList();
        if (accountsInUse.contains(account)) {
            throw new AccounWithInvestmentException("A conta já está em uso: " + account);
        }
        var investment = findById(id);
        checkFundsForTransaction(account, investment.initialFunds());
        var wallet = new InvestmentWallet(investment, account, investment.initialFunds());
        wallets.add(wallet);
        return wallet;
    }


    public InvestmentWallet deposit(final String pix, final long funds) {
        var wallet = findWalletByAccountPix(pix);
        wallet.addMoney(wallet.getAccount().reduceMoney(funds), wallet.getService(), "Investimento");
        return wallet;
    }

    public InvestmentWallet withdraw(final String pix, final long funds) {
      var wallet = findWalletByAccountPix(pix);
      checkFundsForTransaction(wallet, funds);
      wallet.getAccount().addMoney(wallet.reduceMoney(funds), wallet.getService(), "Saque de investimmentos");
      if (wallet.getFunds() == 0) {
          wallets.remove(wallet);
      }
      return wallet;
    }

    public void updateAmount (){
        wallets.forEach(w -> w.updateAmount(w.getInvestment().tax()));
    }

    public Investment findById(final Long id) {
        return investments.stream()
                .filter(a -> a.id() == id)
                .findFirst()
                .orElseThrow(() -> new InvestmentNotFoundException("Investimento '"+ id + "' não foi encontrado"));
    }

    public InvestmentWallet findWalletByAccountPix(final String pix) {
        return wallets.stream()
                .filter(w -> w.getAccount().getPix().contains(pix))
                .findFirst()
                .orElseThrow(
                        () -> new WalletNotFoundException("A carteira nao foi encontrada "));

    }

    public List<InvestmentWallet> listWallets() {
        return this.wallets;
    }

    public List<Investment> list() {
        return this.investments;
    }
}
