package com.sudayagiri.ledgerrecon.batch;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads the external bank statement into memory once per run, lets the processor
 * look transactions up by id, and tracks which bank rows were matched so the job
 * listener can flag anything left over as MISSING_IN_LEDGER.
 */
@Component
public class BankStatementCache {

    public record BankTxn(String txnId, BigDecimal amount, String counterparty) {}

    private final Map<String, BankTxn> byId = new ConcurrentHashMap<>();
    private final Set<String> matched = ConcurrentHashMap.newKeySet();

    /** Reload the statement and reset match tracking. Called at the start of each job run. */
    public synchronized void reset() {
        byId.clear();
        matched.clear();
        load();
    }

    private void load() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new ClassPathResource("data/bank.csv").getInputStream(), StandardCharsets.UTF_8))) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] f = line.split(",", -1);
                BankTxn txn = new BankTxn(f[0].trim(), new BigDecimal(f[2].trim()), f[3].trim());
                byId.put(txn.txnId(), txn);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load bank statement", e);
        }
    }

    public BankTxn find(String txnId) {
        return byId.get(txnId);
    }

    public void markMatched(String txnId) {
        matched.add(txnId);
    }

    /** Bank rows that no ledger entry matched. */
    public List<BankTxn> unmatched() {
        List<BankTxn> out = new ArrayList<>();
        for (BankTxn t : byId.values()) {
            if (!matched.contains(t.txnId())) {
                out.add(t);
            }
        }
        return out;
    }
}
