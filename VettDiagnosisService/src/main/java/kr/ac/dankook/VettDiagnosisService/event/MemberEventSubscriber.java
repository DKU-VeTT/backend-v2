package kr.ac.dankook.VettDiagnosisService.event;

import kr.ac.dankook.VettDiagnosisService.entity.Diagnosis;
import kr.ac.dankook.VettDiagnosisService.facade.DiagnosisFacade;
import kr.ac.dankook.VettDiagnosisService.repository.DiagnosisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@Slf4j
@RequiredArgsConstructor
public class MemberEventSubscriber {

    private final DiagnosisRepository diagnosisRepository;
    private final DiagnosisFacade diagnosisFacade;

    @KafkaListener(groupId = "VETT_DIAGNOSIS", topics = "user.event.deleted")
    public void consumeMemberDeleted(String message, Acknowledgment acknowledgment){
        log.info("[VETT_DIAGNOSIS] [Member Event Listener] UserDeleted. UserKey - {}",message);
        List<Diagnosis> diagnoses = diagnosisRepository.findByMemberId(message);
        diagnoses.forEach(i -> diagnosisFacade.deleteDiagnosisResult(i.getId()));
        acknowledgment.acknowledge();
    }

}
