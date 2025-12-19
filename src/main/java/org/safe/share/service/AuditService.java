package org.safe.share.service;
import org.safe.share.model.AccessLog;
import org.safe.share.model.Document;
import org.safe.share.repository.AccessLogRepository;
import org.safe.share.storage.FileStorageService;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private final AccessLogRepository logRepository;
    private final FileStorageService storageService;

    public AuditService(
            AccessLogRepository logRepository,
            FileStorageService storageService
    ) {
        this.logRepository = logRepository;
        this.storageService = storageService;
    }

    public void log(Long shareId, String ip, String userAgent) {
        AccessLog log = new AccessLog();
        log.setShareId(shareId);
        log.setIpAddress(ip);
        log.setUserAgent(userAgent);
        logRepository.save(log);
    }

    public byte[] readDocument(Document doc) throws Exception {
        return storageService.readDecrypted(doc.getFilePath(), doc.getUserId());
    }
}
