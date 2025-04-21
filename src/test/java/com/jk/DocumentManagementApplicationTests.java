package com.jk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.jk.controller.DocumentController;
import com.jk.entity.Document;
import com.jk.repository.DocumentRepository;
import com.jk.service.DocumentService;
import org.springframework.http.MediaType;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DocumentManagementApplicationTests {

	@Autowired
    private MockMvc mockMvc;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentController documentController;

    @Test
    public void testIngestDocument_InvalidInput() throws Exception {
        String invalidDocumentJson = """
            {
                "title": "",
                "content": "Invalid document",
                "author": "John Doe"
            }
            """;

        mockMvc.perform(post("/api/documents/ingest")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidDocumentJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testSearchDocuments_Success() throws Exception {
        Document document = new Document();
        document.setTitle("Search Test");
        document.setContent("This is a searchable document");
        document.setAuthor("Jane Doe");
        document.setDocumentType("DOCX");
        documentRepository.save(document);

        mockMvc.perform(get("/api/documents/search")
                .param("query", "searchable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Search Test"));
    }

    @Test
    public void testSearchDocuments_NoResults() throws Exception {
        mockMvc.perform(get("/api/documents/search")
                .param("query", "nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void testGetDocumentsByMetadata_Pagination() throws Exception {
        for (int i = 0; i < 15; i++) {
            Document document = new Document();
            document.setTitle("Document " + i);
            document.setContent("Content " + i);
            document.setAuthor("Author");
            document.setDocumentType("PDF");
            documentRepository.save(document);
        }

        mockMvc.perform(get("/api/documents")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.totalElements").value(15));
    }

    @Test
    public void testDocumentService_IngestAsync() throws Exception {
        Document document = new Document();
        document.setTitle("Async Test");
        document.setContent("Async document content");
        document.setAuthor("Async Author");
        document.setDocumentType("TXT");

        CompletableFuture<Document> future = documentService.ingestDocument(document);
        Document savedDocument = future.get();

        assertNotNull(savedDocument.getId());
        assertEquals("Async Test", savedDocument.getTitle());
    }

    @Test
    public void testDocumentService_SearchAsync() throws Exception {
        Document document = new Document();
        document.setTitle("Search Async Test");
        document.setContent("Async searchable content");
        document.setAuthor("Async Author");
        document.setDocumentType("PDF");
        documentRepository.save(document);

        CompletableFuture<List<Document>> future = documentService.searchDocuments("searchable");
        List<Document> results = future.get();

        assertEquals(1, results.size());
        assertEquals("Search Async Test", results.get(0).getTitle());
    }

    @Test
    public void testDocumentRepository_FindByAuthor() {
        Document document = new Document();
        document.setTitle("Author Test");
        document.setContent("Content for author test");
        document.setAuthor("Specific Author");
        document.setDocumentType("DOCX");
        documentRepository.save(document);

        List<Document> documents = documentRepository.findByAuthor("Specific Author");
        assertEquals(1, documents.size());
        assertEquals("Author Test", documents.get(0).getTitle());
    }


}
