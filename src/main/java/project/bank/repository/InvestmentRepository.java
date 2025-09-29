package project.bank.repository;

import project.bank.exception.AccountWithInvestmentException;
import project.bank.exception.InvestimentNotFoundException;
import project.bank.exception.WalletNotFoundException;
import project.bank.model.AccountWallet;
import project.bank.model.Investment;
import project.bank.model.InvestmentWallet;

import java.util.ArrayList;
import java.util.List;

import static project.bank.repository.CommonsRepository.checkFundsForTransaction;

public class InvestmentRepository {

    private long nextId = 0;
    private final List<Investment> investments = new ArrayList<>();
    private final List<InvestmentWallet> wallets = new ArrayList<>();

    public Investment create(final long tax, final long initialFunds) {
        this.nextId++;
        var investment = new Investment(this.nextId, tax, initialFunds);
        investments.add(investment);
        return investment;
    }

    public InvestmentWallet initInvestment(final AccountWallet account, final long id) {
        if(!wallets.isEmpty()) {
            var accountInUse = wallets.stream().map(InvestmentWallet::getAccount).toList();
            if (accountInUse.contains(account)) {
                throw new AccountWithInvestmentException("A conta " + account + " já possui um investimento.");
            }
        }
        var investment = findById(id);
        checkFundsForTransaction(account, investment.initialFunds());
        var wallet = new InvestmentWallet(investment, account, investment.initialFunds());
        wallets.add(wallet);
        return wallet;
    }

    public InvestmentWallet deposit(final String pix, final long funds) {
        var wallet = findWalletByAccountPix(pix);
        wallet.addMoney(wallet.getAccount().reduceMoney(funds), wallet.getService(), "Aplicação de investimento");
        return wallet;
    }

    public InvestmentWallet withdraw(final String pix, final long funds) {
        var wallet = findWalletByAccountPix(pix);
        checkFundsForTransaction(wallet, funds);
        wallet.getAccount().addMoney(wallet
                .reduceMoney(funds), wallet.getService(), "Resgate de investimento");
        if (wallet.getFunds() == 0) {
            wallets.remove(wallet);
        }
        return wallet;
    }

    public void updateAmount() {
        wallets.forEach(w -> w.updateAmount(w.getInvestment().tax()));
    }

    public Investment findById(final long id) {
        return investments.stream().filter(a -> a.id() == id)
                .findFirst()
                .orElseThrow(
                        () -> new InvestimentNotFoundException("O investimento '" + id + "' não foi encontrado ")
                );
    }

    public InvestmentWallet findWalletByAccountPix(final String pix) {
        return wallets.stream()
                .filter(w -> w.getAccount().getPix().contains(pix))
                .findFirst()
                .orElseThrow(
                        () -> new WalletNotFoundException("Carteira de investimento não encontrada"));
    }
    public List<Investment> list() {
        return investments;
    }
    public List<InvestmentWallet> listWallets() {
        return wallets;
    }
}
