package kr.ac.dankook.VettAuthService.repository;

import kr.ac.dankook.VettAuthService.entity.Outbox;
import kr.ac.dankook.VettAuthService.entity.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxRepository extends JpaRepository<Outbox,String> {
    List<Outbox> findByStatus(OutboxStatus status);
}
