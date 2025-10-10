package kr.ac.dankook.VettAuthService.repository;

import kr.ac.dankook.VettAuthService.entity.outbox.Outbox;
import kr.ac.dankook.VettAuthService.entity.outbox.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxRepository extends JpaRepository<Outbox,String> {
    List<Outbox> findByStatus(OutboxStatus status);
}
