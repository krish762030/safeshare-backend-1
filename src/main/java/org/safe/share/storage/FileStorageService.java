package org.safe.share.storage;

import org.safe.share.common.security.CryptoUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.storage.base-path}")
    private String basePath;

    @Value("${app.crypto.secret}")
    private String secret;

    public String saveEncrypted(MultipartFile file, Long userId) throws Exception {

        Path dir = Path.of(basePath);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        byte[] raw = file.getBytes();
        byte[] key = CryptoUtil.deriveKey(secret + userId);
        byte[] encrypted = CryptoUtil.encrypt(raw, key);

        String name = UUID.randomUUID() + ".enc";
        Path fullPath = dir.resolve(name);

        Files.write(fullPath, encrypted);

        return fullPath.toString();
    }

    public byte[] readDecrypted(String path, Long userId) throws Exception {

        byte[] encrypted = Files.readAllBytes(Path.of(path));
        byte[] key = CryptoUtil.deriveKey(secret + userId);

        return CryptoUtil.decrypt(encrypted, key);
    }
}
