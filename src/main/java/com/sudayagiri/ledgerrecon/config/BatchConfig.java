package com.sudayagiri.ledgerrecon.config;

import com.sudayagiri.ledgerrecon.batch.ReconJobListener;
import com.sudayagiri.ledgerrecon.batch.ReconProcessor;
import com.sudayagiri.ledgerrecon.model.LedgerTxn;
import com.sudayagiri.ledgerrecon.model.ReconResult;
import com.sudayagiri.ledgerrecon.repo.ReconResultRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

    @Bean
    public FlatFileItemReader<LedgerTxn> ledgerReader() {
        return new FlatFileItemReaderBuilder<LedgerTxn>()
                .name("ledgerReader")
                .resource(new ClassPathResource("data/ledger.csv"))
                .linesToSkip(1)
                .delimited()
                .names("txnId", "date", "amount", "counterparty")
                .targetType(LedgerTxn.class)
                .build();
    }

    @Bean
    public RepositoryItemWriter<ReconResult> reconWriter(ReconResultRepository repo) {
        return new RepositoryItemWriterBuilder<ReconResult>()
                .repository(repo)
                .methodName("save")
                .build();
    }

    @Bean
    public Step reconcileStep(JobRepository jobRepository,
                              PlatformTransactionManager txManager,
                              FlatFileItemReader<LedgerTxn> ledgerReader,
                              ReconProcessor processor,
                              RepositoryItemWriter<ReconResult> reconWriter) {
        return new StepBuilder("reconcileStep", jobRepository)
                .<LedgerTxn, ReconResult>chunk(50, txManager)
                .reader(ledgerReader)
                .processor(processor)
                .writer(reconWriter)
                .build();
    }

    @Bean
    public Job reconciliationJob(JobRepository jobRepository,
                                 Step reconcileStep,
                                 ReconJobListener listener) {
        return new JobBuilder("reconciliationJob", jobRepository)
                .listener(listener)
                .start(reconcileStep)
                .build();
    }
}
