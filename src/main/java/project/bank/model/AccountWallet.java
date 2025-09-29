package project.bank.model;

import lombok.Getter;

import java.util.List;

import static project.bank.model.BankService.ACCOUNT;

@Getter
public class AccountWallet extends Wallet {
    private final List<String> pix;

    public AccountWallet(final List<String> pix) {
        super(ACCOUNT);
        this.pix = pix;
    }

    public AccountWallet(final long amount, List<String> pix) {
        super(ACCOUNT);
        this.pix = pix;
        addMoney(amount, "Valor inicial");
    }

    public void addMoney(final long amount, final String description) {
        var money = generateMoney(amount, description);
        this.money.addAll(money);
    }

    @Override
    public String toString() {
        return super.toString() + "AccountWallet{" +
                "pix=" + pix +
                '}';
    }
}

