package org.safe.share.document.controller;

import org.safe.share.document.service.DocumentService;
import org.safe.share.dto.DocumentDownload;
import org.safe.share.model.Document;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void upload(@RequestParam("file") MultipartFile file) throws Exception {
        documentService.upload(file);
    }
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> download(@PathVariable Long id) throws Exception {

        DocumentDownload file = documentService.download(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getFileName() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, file.getContentType())
                .body(file.getData());
    }


    @GetMapping
    public List<Document> listMyDocs() {
        return documentService.listMyDocuments();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) throws Exception {
        documentService.delete(id);
    }

}
