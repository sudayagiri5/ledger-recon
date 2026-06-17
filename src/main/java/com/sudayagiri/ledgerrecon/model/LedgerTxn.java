package com.sudayagiri.ledgerrecon.model;

import java.math.BigDecimal;

/** A single transaction row from the internal ledger feed. */
public class LedgerTxn {
    private String txnId;
    private String date;
    private BigDecimal amount;
    private String counterparty;

    public LedgerTxn() {}

    public String getTxnId() { return txnId; }
    public void setTxnId(String txnId) { this.txnId = txnId; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCounterparty() { return counterparty; }
    public void setCounterparty(String counterparty) { this.counterparty = counterparty; }
}
