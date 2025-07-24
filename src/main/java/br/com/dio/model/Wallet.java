package br.com.dio.model;

import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.UUID;
import java.time.OffsetDateTime;

@ToString
public abstract class Wallet {

    @Getter
    private final BankService service;

    protected final List<Money> money;


    public Wallet(BankService serviceType) {
        this.service = serviceType;
        this.money = new ArrayList<>();
    }

    protected List<Money> genereteMoney(final long amount, final String description) {
        var history = new MoneyAudit(
                UUID.randomUUID(),
                service,
                description,
                OffsetDateTime.now());

                return Stream.generate(() -> new Money(history)).limit(amount).toList();
    }

    public long getFunds() {
        return money.size();
    }

    public void addMoney(final List<Money> money, final BankService service, final String description) {
        var history = new MoneyAudit(
                UUID.randomUUID(),
                service,
                description,
                OffsetDateTime.now());

        money.forEach(m -> m.addHistory(history));
        this.money.addAll(money);
    }

    public List<Money> reduceMoney(final long amount) {
        List<Money> toRemove = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            toRemove.add(money.removeFirst());
        }
        return toRemove;
    }

    public List<MoneyAudit> getFinancialTransactions() {
        return money.stream()
                .flatMap(m -> m.getHistory().stream())
                .toList();
    }
}
