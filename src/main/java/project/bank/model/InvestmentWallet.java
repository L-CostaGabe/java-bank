package project.bank.model;

import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import static project.bank.model.BankService.INVESTMENT;


@Getter
public class InvestmentWallet extends Wallet {

    private final Investment investment;
    private final AccountWallet account;


    public InvestmentWallet(final Investment investment, final AccountWallet account, final long amount) {
        super(INVESTMENT);
        this.investment = investment;
        this.account = account;
        addMoney(account.reduceMoney(amount), getService(), "Investimento");
    }

    public void updateAmount(final long percent) {
        var amount = getFunds() * percent / 100;
        var history = new MoneyAudit(UUID.randomUUID(), getService(), "Rendimentos", OffsetDateTime.now());
        var money = Stream.generate(() -> new Money(history)).limit(amount).toList();
        this.money.addAll(money);
    }

    @Override
    public String toString() {
        return super.toString() + "InvestmentWallet{" +
                "investment=" + investment +
                ", account=" + account +
                '}';
    }
}
