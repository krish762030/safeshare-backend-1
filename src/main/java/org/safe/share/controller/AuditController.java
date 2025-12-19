package org.safe.share.controller;

import org.safe.share.common.security.SecurityUtils;
import org.safe.share.document.repository.DocumentRepository;
import org.safe.share.dto.AccessLogResponse;
import org.safe.share.model.AccessLog;
import org.safe.share.model.Document;
import org.safe.share.model.Share;
import org.safe.share.repository.AccessLogRepository;
import org.safe.share.repository.ShareRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shares")
public class AuditController {

    private final ShareRepository shareRepository;
    private final DocumentRepository documentRepository;
    private final AccessLogRepository accessLogRepository;

    public AuditController(
            ShareRepository shareRepository,
            DocumentRepository documentRepository,
            AccessLogRepository accessLogRepository
    ) {
        this.shareRepository = shareRepository;
        this.documentRepository = documentRepository;
        this.accessLogRepository = accessLogRepository;
    }

    @GetMapping("/{token}/logs")
    public List<AccessLogResponse> getLogs(@PathVariable String token) {

        Long userId = SecurityUtils.getCurrentUserId();

        Share share = shareRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Share link not found"));

        Document doc = documentRepository.findById(share.getDocumentId())
                .orElseThrow(() -> new RuntimeException("Document not found"));

        // ✅ Only owner can see logs
        if (!doc.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        List<AccessLog> logs = accessLogRepository.findByShareId(share.getId());

        return logs.stream()
                .map(l -> new AccessLogResponse(l.getIpAddress(), l.getUserAgent(), l.getAccessedAt()))
                .toList();
    }
}
