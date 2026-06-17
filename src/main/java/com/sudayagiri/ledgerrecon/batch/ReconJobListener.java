package com.sudayagiri.ledgerrecon.batch;

import com.sudayagiri.ledgerrecon.model.ReconResult;
import com.sudayagiri.ledgerrecon.model.ReconStatus;
import com.sudayagiri.ledgerrecon.repo.ReconResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

/** Resets state before a run, records bank-only rows after, and prints a reconciliation summary. */
@Component
public class ReconJobListener implements JobExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(ReconJobListener.class);

    private final BankStatementCache bank;
    private final ReconResultRepository repo;

    public ReconJobListener(BankStatementCache bank, ReconResultRepository repo) {
        this.bank = bank;
        this.repo = repo;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        repo.deleteAllInBatch();
        bank.reset();
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        for (BankStatementCache.BankTxn t : bank.unmatched()) {
            repo.save(new ReconResult(t.txnId(), ReconStatus.MISSING_IN_LEDGER,
                    null, t.amount(), null, t.counterparty()));
        }

        long matched = repo.countByStatus(ReconStatus.MATCHED);
        long mismatch = repo.countByStatus(ReconStatus.MISMATCH);
        long missingBank = repo.countByStatus(ReconStatus.MISSING_IN_BANK);
        long missingLedger = repo.countByStatus(ReconStatus.MISSING_IN_LEDGER);

        log.info("=================== RECONCILIATION SUMMARY ===================");
        log.info(" Matched .................. {}", matched);
        log.info(" Mismatched ............... {}", mismatch);
        log.info(" Missing in bank .......... {}", missingBank);
        log.info(" Missing in ledger ........ {}", missingLedger);
        log.info(" Total exceptions ......... {}", mismatch + missingBank + missingLedger);
        log.info(" GET http://localhost:8080/api/recon/summary for JSON");
        log.info("==============================================================");
    }
}
