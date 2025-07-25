package br.com.dio.model;

import lombok.Getter;

import java.util.List;

import static br.com.dio.model.BankService.ACCOUNT;

@Getter

public class AccountWallet extends Wallet{

    private final List<String> pix;

    public  AccountWallet(final List<String> pix) {
        super(ACCOUNT);
        this.pix = pix;
    }

    public AccountWallet(final long amount, final List<String> pix) {
        super(ACCOUNT);
        this.pix = pix;
        addMoney(amount, "valor de criação da conta");
    }

    public void addMoney(final long amount, final String description) {
        var money = genereteMoney(amount, description);
       this.money.addAll(money);
    }
}
