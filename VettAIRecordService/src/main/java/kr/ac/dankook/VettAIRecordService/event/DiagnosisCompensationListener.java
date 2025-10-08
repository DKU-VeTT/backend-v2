package kr.ac.dankook.VettAIRecordService.event;

import kr.ac.dankook.VettAIRecordService.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;


@Component
@RequiredArgsConstructor
@Slf4j
public class DiagnosisCompensationListener {

    private final StorageService storageService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void onRollback(DiagnosisCompensationEvent event) {
        String imageId = event.getImageId();
        storageService.deleteFile(new ObjectId(imageId));
    }
}
