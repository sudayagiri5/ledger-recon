package com.sudayagiri.ledgerrecon.repo;

import com.sudayagiri.ledgerrecon.model.ReconResult;
import com.sudayagiri.ledgerrecon.model.ReconStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReconResultRepository extends JpaRepository<ReconResult, Long> {
    long countByStatus(ReconStatus status);
    List<ReconResult> findByStatusNot(ReconStatus status);
    List<ReconResult> findByStatus(ReconStatus status);
}
