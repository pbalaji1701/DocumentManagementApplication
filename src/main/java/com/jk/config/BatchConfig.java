package com.jk.config;

import javax.swing.text.Document;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
	
	@Bean
    public FlatFileItemReader<Document> reader() {
        return new FlatFileItemReaderBuilder<Document>()
                .name("documentReader")
                .resource(new ClassPathResource("documents.csv"))
                .delimited()
                .names("title", "content", "author", "documentType")
                .targetType(Document.class)
                .build();
    }
    
    @Bean
    public JpaItemWriter<Document> writer(EntityManagerFactory entityManagerFactory) {
        JpaItemWriter<Document> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }
    
    @Bean
    public Step step(JobRepository jobRepository, FlatFileItemReader<Document> reader, JpaItemWriter<Document> writer, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step", jobRepository)
                .<Document, Document>chunk(100, transactionManager)
                .reader(reader)
                .writer(writer)
                .build();
    }

    @Bean
    public Job job(JobRepository jobRepository, Step step) {
        return new JobBuilder("documentImportJob", jobRepository)
                .start(step)
                .build();
    }

}
