package com.sudayagiri.ledgerrecon.batch;

import com.sudayagiri.ledgerrecon.model.LedgerTxn;
import com.sudayagiri.ledgerrecon.model.ReconResult;
import com.sudayagiri.ledgerrecon.model.ReconStatus;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/** Compares each ledger transaction against the bank statement and classifies the outcome. */
@Component
public class ReconProcessor implements ItemProcessor<LedgerTxn, ReconResult> {

    private final BankStatementCache bank;

    public ReconProcessor(BankStatementCache bank) {
        this.bank = bank;
    }

    @Override
    public ReconResult process(LedgerTxn ledger) {
        BankStatementCache.BankTxn match = bank.find(ledger.getTxnId());

        if (match == null) {
            return new ReconResult(ledger.getTxnId(), ReconStatus.MISSING_IN_BANK,
                    ledger.getAmount(), null, null, ledger.getCounterparty());
        }

        bank.markMatched(ledger.getTxnId());

        if (ledger.getAmount().compareTo(match.amount()) == 0) {
            return new ReconResult(ledger.getTxnId(), ReconStatus.MATCHED,
                    ledger.getAmount(), match.amount(), BigDecimal.ZERO, ledger.getCounterparty());
        }

        BigDecimal diff = ledger.getAmount().subtract(match.amount());
        return new ReconResult(ledger.getTxnId(), ReconStatus.MISMATCH,
                ledger.getAmount(), match.amount(), diff, ledger.getCounterparty());
    }
}
