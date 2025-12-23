package org.safe.share.service;

import org.safe.share.common.security.SecurityUtils;
import org.safe.share.document.repository.DocumentRepository;
import org.safe.share.dto.CreateShareRequest;
import org.safe.share.dto.DocumentDownload;
import org.safe.share.model.Document;
import org.safe.share.model.Share;
import org.safe.share.repository.ShareRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ShareService {

    private final ShareRepository shareRepository;
    private final DocumentRepository documentRepository;
    private final AuditService auditService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public ShareService(
            ShareRepository shareRepository,
            DocumentRepository documentRepository,
            AuditService auditService
    ) {
        this.shareRepository = shareRepository;
        this.documentRepository = documentRepository;
        this.auditService = auditService;
    }

    public Share createShare(CreateShareRequest req) {

        Long userId = SecurityUtils.getCurrentUserId();

        Document doc = documentRepository.findById(req.documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        if (!doc.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        Share share = new Share();
        share.setDocumentId(doc.getId());
        share.setToken(UUID.randomUUID().toString());
        share.setExpiresAt(LocalDateTime.now().plusMinutes(req.expiryMinutes));
        share.setRevoked(false);
        share.setUsed(false);
        share.setOneTime(req.oneTime);

        shareRepository.save(share);

        return share;
    }

    public DocumentDownload access(String token, String password, String ip, String userAgent)
    throws Exception {

        Share share = shareRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid link"));

        if (share.isOneTime() && share.isUsed()) {
            throw new RuntimeException("This one-time link has already been used");
        }


        if (share.isRevoked() || share.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Link expired or revoked");
        }
        if (share.getPasswordHash() != null) {
            if (password == null || !encoder.matches(password, share.getPasswordHash())) {
                throw new RuntimeException("Invalid password");
            }
        }
        auditService.log(share.getId(), ip, userAgent);

        Document doc = documentRepository.findById(share.getDocumentId())
                .orElseThrow(() -> new RuntimeException("Document missing"));
        byte[] data = auditService.readDocument(doc);

        return new DocumentDownload(
                data,
                doc.getFileName(),
                doc.getContentType() != null
                        ? doc.getContentType()
                        : "application/octet-stream"
        );
    }

    public void revoke(String token) {
        Share share = shareRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Not found"));
        share.setRevoked(true);
        if (share.isOneTime()) {
            share.setUsed(true);
            shareRepository.save(share);
        }
    }
}
