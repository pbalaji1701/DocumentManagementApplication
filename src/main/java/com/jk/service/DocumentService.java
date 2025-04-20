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
        // Simulate document processing
        document.setSearchVector(generateSearchVector(document.getContent()));
        return CompletableFuture.completedFuture(documentRepository.save(document));
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
}
