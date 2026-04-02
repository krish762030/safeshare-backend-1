package org.safe.share.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.safe.share.document.repository.DocumentRepository;
import org.safe.share.dto.DocumentDownload;
import org.safe.share.model.Document;
import org.safe.share.model.Share;
import org.safe.share.repository.ShareRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShareServiceTest {

    @Mock
    private ShareRepository shareRepository;

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private ShareService shareService;

    private Share oneTimeShare;
    private Document document;

    @BeforeEach
    void setUp() {
        oneTimeShare = new Share();
        oneTimeShare.setId(10L);
        oneTimeShare.setDocumentId(20L);
        oneTimeShare.setToken("token-123");
        oneTimeShare.setOneTime(true);
        oneTimeShare.setUsed(false);
        oneTimeShare.setRevoked(false);
        oneTimeShare.setExpiresAt(LocalDateTime.now().plusMinutes(10));

        document = new Document();
        document.setId(20L);
        document.setUserId(30L);
        document.setFileName("report.pdf");
        document.setContentType("application/pdf");
    }

    @Test
    void access_marksOneTimeShareAsUsed() throws Exception {
        when(shareRepository.findByToken("token-123")).thenReturn(Optional.of(oneTimeShare));
        when(documentRepository.findById(20L)).thenReturn(Optional.of(document));
        when(auditService.readDocument(document)).thenReturn(new byte[]{1, 2, 3});

        DocumentDownload result = shareService.access("token-123", null, "127.0.0.1", "test-agent");

        assertArrayEquals(new byte[]{1, 2, 3}, result.getData());
        assertTrue(oneTimeShare.isUsed(), "one-time links should be marked as used after access");
        verify(shareRepository).save(oneTimeShare);
    }

    @Test
    void revoke_alwaysPersistsRevocation() {
        Share regularShare = new Share();
        regularShare.setToken("token-456");
        regularShare.setOneTime(false);
        regularShare.setRevoked(false);

        when(shareRepository.findByToken("token-456")).thenReturn(Optional.of(regularShare));

        shareService.revoke("token-456");

        assertTrue(regularShare.isRevoked());
        verify(shareRepository).save(regularShare);
    }

    @Test
    void access_rejectsAlreadyUsedOneTimeLink() {
        oneTimeShare.setUsed(true);
        when(shareRepository.findByToken("token-123")).thenReturn(Optional.of(oneTimeShare));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> shareService.access("token-123", null, "127.0.0.1", "test-agent"));

        assertEquals("This one-time link has already been used", ex.getMessage());
        verify(documentRepository, never()).findById(any());
    }
}
