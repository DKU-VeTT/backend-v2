package kr.ac.dankook.VettPlaceService.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "event_record")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventRecord extends BaseEntity{

    @Id
    @Column(name="event_id", nullable = false, updatable = false)
    private String eventId;

}
