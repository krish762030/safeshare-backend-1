package org.safe.share.repository;

import org.safe.share.model.Share;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface ShareRepository extends JpaRepository<Share, Long> {
    Optional<Share> findByToken(String token);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Share s where s.token = :token")
    Optional<Share> findByTokenForUpdate(String token);
}
