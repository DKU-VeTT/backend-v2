package kr.ac.dankook.VettDiagnosisService.event;

import kr.ac.dankook.VettDiagnosisService.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DiagnosisCompensationListener {

    private final StorageService storageService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void onRollback(DiagnosisCompensationEvent event) {
        List<String> ids = event.getIds();
        storageService.deleteFiles(ids.stream().map(ObjectId::new).toList());
    }
}
