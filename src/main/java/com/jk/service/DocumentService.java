package com.jk.service;


import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jk.entity.Document;
import com.jk.repository.DocumentRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class DocumentService {
	
	@Autowired
	private DocumentRepository documentRepository;
    
    @Async
    @Transactional
    public CompletableFuture<Object> ingestDocument(Document document) {
    	return CompletableFuture.supplyAsync(() -> {
            try {
                // Validate document
                if (document == null || document.getContent() == null || document.getContent().isBlank()) {
                    throw new IllegalArgumentException("Document or its content cannot be null or empty");
                }

                // Normalize content (e.g., trim, lowercase, remove special characters)
                String normalizedContent = normalizeContent(document.getContent());
                document.setContent(normalizedContent);

                // Simulate processing delay (e.g., mimicking text analysis or API calls)
                simulateProcessingDelay();

                // Generate search vector (assumed to be a method that creates a vector for search)
                document.setSearchVector(generateSearchVector(normalizedContent));

                // Save the processed document
                return documentRepository.save(document);
            } catch (Exception e) {
                // Wrap and propagate any errors in the CompletableFuture
                throw new RuntimeException("Failed to process document: " + e.getMessage(), e);
            }
        });
    }
    
    @Async
    public CompletableFuture<List<Document>> searchDocuments(String query) {
        return CompletableFuture.completedFuture(documentRepository.searchDocuments(query));
    }
    
    public Page<Document> getDocumentsByMetadata(String author, String documentType, int page, int size) {
        return documentRepository.findAll(PageRequest.of(page, size));
    }
    
    private String generateSearchVector(String content) {
        // Implement tsvector generation logic
        return content;
    }
    
    private String normalizeContent(String content) {
        // Trim whitespace, convert to lowercase, and remove special characters
        return content.trim()
                     .toLowerCase()
                     .replaceAll("[^a-zA-Z0-9\\s]", "");
    }
    
    private void simulateProcessingDelay() {
        try {
            // Simulate a delay between 100-500ms to mimic real processing
            Thread.sleep(100 + (long) (Math.random() * 400));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Processing interrupted", e);
        }
    }
}
