package com.jk.controller;


import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jk.entity.Document;
import com.jk.service.DocumentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@Tag(name = "Document Management", description = "APIs for document ingestion and Q&A")
public class DocumentController {
	
	@Autowired
	private DocumentService documentService;
    
    @PostMapping("/ingest")
    @Operation(summary = "Ingest a new document")
    public CompletableFuture<Object> ingestDocument(@RequestBody Document document) {
        return documentService.ingestDocument(document)
                .thenApply(ResponseEntity::ok);
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search documents by keyword")
    public CompletableFuture<ResponseEntity<List<Document>>> searchDocuments(@RequestParam String query) {
        return documentService.searchDocuments(query)
                .thenApply(ResponseEntity::ok);
    }
    
    @GetMapping
    @Operation(summary = "Get documents with filters")
    public ResponseEntity<Page<Document>> getDocuments(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String documentType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(documentService.getDocumentsByMetadata(author, documentType, page, size));
    }

}
