package org.pknu.weather.member.attandance.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pknu.weather.common.entity.BaseEntity;
import org.pknu.weather.member.entity.Member;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Attendance extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "member_id")
    private Member member;

    // 중복 출석 방지용
    @Builder.Default
    private boolean checkedIn = false;

    public void checkIn() {
        checkedIn = true;
    }
}
