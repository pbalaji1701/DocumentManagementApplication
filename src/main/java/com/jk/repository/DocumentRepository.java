package com.jk.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jk.entity.Document;

@Repository
public interface DocumentRepository {
	
	@Query(value = "SELECT * FROM documents WHERE search_vector @@ to_tsquery(?1)", nativeQuery = true)
    List<Document> searchDocuments(String query);
    
    List<Document> findByAuthor(String author);
    
    List<Document> findByDocumentType(String documentType);

	Page<Document> findAll(PageRequest of);

	Document save(Document document);

}
