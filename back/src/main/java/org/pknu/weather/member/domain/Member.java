package org.pknu.weather.member.domain;
import jakarta.persistence.*;
import lombok.*;
import org.pknu.weather.common.BaseEntity;
import org.pknu.weather.location.domain.Location;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "member_id")
    private Long id;

    @Column(unique=true)
    private String email;

    @Enumerated(EnumType.STRING)
    private Sensitivity sensitivity;

    @Column(unique=true)
    private String nickname;

    private String profileImage;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;
}
