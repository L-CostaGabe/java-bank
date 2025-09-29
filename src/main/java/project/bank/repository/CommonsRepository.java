package project.bank.repository;

import lombok.NoArgsConstructor;
import project.bank.exception.NoFundsNotFoundException;
import project.bank.model.Money;
import project.bank.model.MoneyAudit;
import project.bank.model.Wallet;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static lombok.AccessLevel.PRIVATE;
import static project.bank.model.BankService.ACCOUNT;


@NoArgsConstructor(access = PRIVATE)
public class CommonsRepository {
    public static void checkFundsForTransaction(final Wallet source, final long amount) {
        if (source.getFunds() < amount) {
            throw new NoFundsNotFoundException("Sua conta não possui saldo suficiente para realizar a transação.");
        }
    }

    public static List<Money> generateMoney(final UUID transactionId, final long funds, String description) {
        var history = new MoneyAudit(transactionId, ACCOUNT,description,OffsetDateTime.now());
        return Stream.generate(() -> new Money(history))
                .limit(funds)
                .toList();

    }
}
