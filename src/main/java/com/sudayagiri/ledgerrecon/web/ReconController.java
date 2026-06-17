package com.sudayagiri.ledgerrecon.web;

import com.sudayagiri.ledgerrecon.model.ReconResult;
import com.sudayagiri.ledgerrecon.model.ReconStatus;
import com.sudayagiri.ledgerrecon.repo.ReconResultRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recon")
public class ReconController {

    private final ReconResultRepository repo;
    private final JobLauncher jobLauncher;
    private final Job reconciliationJob;

    public ReconController(ReconResultRepository repo, JobLauncher jobLauncher, Job reconciliationJob) {
        this.repo = repo;
        this.jobLauncher = jobLauncher;
        this.reconciliationJob = reconciliationJob;
    }

    @GetMapping("/summary")
    public Map<String, Long> summary() {
        Map<String, Long> out = new LinkedHashMap<>();
        for (ReconStatus s : ReconStatus.values()) {
            out.put(s.name(), repo.countByStatus(s));
        }
        return out;
    }

    @GetMapping("/exceptions")
    public List<ReconResult> exceptions() {
        return repo.findByStatusNot(ReconStatus.MATCHED);
    }

    @PostMapping("/run")
    public Map<String, String> run() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLong("runAt", System.currentTimeMillis())
                .toJobParameters();
        var execution = jobLauncher.run(reconciliationJob, params);
        Map<String, String> out = new LinkedHashMap<>();
        out.put("jobId", String.valueOf(execution.getJobId()));
        out.put("status", execution.getStatus().toString());
        return out;
    }
}
