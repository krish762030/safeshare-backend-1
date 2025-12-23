package org.safe.share.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.safe.share.dto.CreateShareRequest;
import org.safe.share.dto.DocumentDownload;
import org.safe.share.dto.ShareResponse;
import org.safe.share.model.Share;
import org.safe.share.repository.AccessLogRepository;
import org.safe.share.repository.ShareRepository;
import org.safe.share.service.ShareService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ShareController {
    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    private final ShareService shareService;
    private final AccessLogRepository accessLogRepository;
    private final ShareRepository shareRepository;
    public ShareController(ShareService shareService, AccessLogRepository accessLogRepository, ShareRepository shareRepository) {
        this.shareService = shareService;
        this.accessLogRepository = accessLogRepository;
        this.shareRepository = shareRepository;
    }

    @PostMapping("/shares")
    public ShareResponse create(@RequestBody CreateShareRequest req) {
        Share share = shareService.createShare(req);
        return new ShareResponse(
                frontendBaseUrl + "/s/" + share.getToken(),
                share.isOneTime()
        );
    }

    @PostMapping("/s/{token}")
    public ResponseEntity<byte[]> access(
            @PathVariable String token,
            @RequestParam(required = false) String password,
            HttpServletRequest request
    ) throws Exception {

        DocumentDownload file = shareService.access(
                token,
                password,
                request.getRemoteAddr(),
                request.getHeader("User-Agent")
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getFileName() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, file.getContentType())
                .body(file.getData());
    }
    @PostMapping("/shares/{token}/revoke")
    public void revoke(@PathVariable String token) {
        shareService.revoke(token);
    }
}
