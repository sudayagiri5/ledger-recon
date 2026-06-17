package com.sudayagiri.ledgerrecon.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "recon_result")
public class ReconResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String txnId;

    @Enumerated(EnumType.STRING)
    private ReconStatus status;

    private BigDecimal ledgerAmount;
    private BigDecimal bankAmount;
    private BigDecimal difference;
    private String counterparty;

    public ReconResult() {}

    public ReconResult(String txnId, ReconStatus status, BigDecimal ledgerAmount,
                       BigDecimal bankAmount, BigDecimal difference, String counterparty) {
        this.txnId = txnId;
        this.status = status;
        this.ledgerAmount = ledgerAmount;
        this.bankAmount = bankAmount;
        this.difference = difference;
        this.counterparty = counterparty;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTxnId() { return txnId; }
    public void setTxnId(String txnId) { this.txnId = txnId; }
    public ReconStatus getStatus() { return status; }
    public void setStatus(ReconStatus status) { this.status = status; }
    public BigDecimal getLedgerAmount() { return ledgerAmount; }
    public void setLedgerAmount(BigDecimal ledgerAmount) { this.ledgerAmount = ledgerAmount; }
    public BigDecimal getBankAmount() { return bankAmount; }
    public void setBankAmount(BigDecimal bankAmount) { this.bankAmount = bankAmount; }
    public BigDecimal getDifference() { return difference; }
    public void setDifference(BigDecimal difference) { this.difference = difference; }
    public String getCounterparty() { return counterparty; }
    public void setCounterparty(String counterparty) { this.counterparty = counterparty; }
}
