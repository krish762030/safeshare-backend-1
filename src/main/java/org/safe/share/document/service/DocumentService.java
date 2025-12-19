package org.safe.share.document.service;

import org.safe.share.common.security.SecurityUtils;
import org.safe.share.document.repository.DocumentRepository;
import org.safe.share.dto.DocumentDownload;
import org.safe.share.model.Document;
import org.safe.share.storage.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final FileStorageService storageService;

    public DocumentService(DocumentRepository documentRepository,
                           FileStorageService storageService) {
        this.documentRepository = documentRepository;
        this.storageService = storageService;
    }

    public void upload(MultipartFile file) throws Exception {

        Long userId = SecurityUtils.getCurrentUserId();

        String path = storageService.saveEncrypted(file, userId);

        Document doc = new Document();
        doc.setUserId(userId);
        doc.setFileName(file.getOriginalFilename());
        doc.setContentType(file.getContentType());
        doc.setFilePath(path);
        doc.setEncrypted(true);

        documentRepository.save(doc);
    }

    public DocumentDownload download(Long docId) throws Exception {

        Long userId = SecurityUtils.getCurrentUserId();

        Document doc = documentRepository.findById(docId)
                .orElseThrow(() -> new RuntimeException("Not found"));

        if (!doc.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        byte[] data = storageService.readDecrypted(doc.getFilePath(), userId);

        return new DocumentDownload(
                data,
                doc.getFileName(),
                doc.getContentType() != null
                        ? doc.getContentType()
                        : "application/octet-stream"
        );
    }



    public List<Document> listMyDocuments() {
        Long userId = SecurityUtils.getCurrentUserId();
        return documentRepository.findByUserId(userId);
    }

    public void delete(Long docId) throws Exception {
        Long userId = SecurityUtils.getCurrentUserId();

        Document doc = documentRepository.findById(docId)
                .orElseThrow(() -> new RuntimeException("Not found"));

        if (!doc.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        // delete file from disk
        try {
            Files.deleteIfExists(Path.of(doc.getFilePath()));
        } catch (Exception e) {
            // keep going even if file missing; MVP-safe
        }

        // delete from DB
        documentRepository.delete(doc);
    }

}
