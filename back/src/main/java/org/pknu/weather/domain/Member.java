package org.pknu.weather.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.domain.common.Sensitivity;
import org.pknu.weather.domain.exp.Level;
import org.pknu.weather.dto.MemberJoinDTO;
import org.pknu.weather.exception.GeneralException;

@Entity(name = "member")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'NONE'")
    private Sensitivity sensitivity;

    @Column(unique = true)
    private String nickname;

    @ColumnDefault("'https://weather-pknu-bucket.s3.ap-northeast-2.amazonaws.com/basic.png'")
    private String profileImage;

    @ColumnDefault("'basic.png'")
    private String profileImageName;

    @Builder.Default
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Level level = Level.LV1;

    @Builder.Default
    @Column(nullable = false)
    private Long exp = 0L;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "location_id")
    private Location location;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<Post> postList = new ArrayList<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<Recommendation> recommendationList = new ArrayList<>();

    public void changeLocation(Location location) {
        this.location = location;
    }

    public void setMemberInfo(MemberJoinDTO memberJoinDTO) {
        if (memberJoinDTO.getNickname() != null && !memberJoinDTO.getNickname().isEmpty()) {
            this.nickname = memberJoinDTO.getNickname();
        }

        if (memberJoinDTO.getSensitivity() != null) {
            this.sensitivity = memberJoinDTO.getSensitivity();
        }

        if (memberJoinDTO.getProfileImg() != null && !memberJoinDTO.getProfileImg().isEmpty()) {
            this.profileImage = memberJoinDTO.getImgPath();
            this.profileImageName = memberJoinDTO.getImgName();
        }
    }

    /**
     * 사용자 경험치를 증가시키는 메서드 만렙인 경우 증가하지 않음
     *
     * @param addedExp exp 증가량
     */
    public void addExp(long addedExp) {
        Long maxLevelExp = Level.getMaxLevel().getRequiredExp();

        if ((exp + addedExp) >= maxLevelExp) {
            exp = maxLevelExp;
        } else {
            exp += addedExp;
        }

        levelUpdateCheck();
    }

    private void levelUpdateCheck() {
        for (Level level : Level.values()) {
            if (this.exp >= level.getRequiredExp()) {
                this.level = level;
            }
        }
    }

    public void minusExp(Long minusExp) {
        Long currentLevelMinimumExp = level.getRequiredExp();

        if ((exp + minusExp) < currentLevelMinimumExp) {
            exp = currentLevelMinimumExp;
        } else {
            exp -= minusExp;
        }

        levelUpdateCheck();
    }

    @PrePersist
    @PreUpdate
    private void validExp() {
        if (exp < 0) {
            throw new GeneralException(ErrorStatus._EXP_NOT_NEGATIVE);
        }

        if (exp > Level.getMaxLevel().getRequiredExp()) {
            throw new GeneralException(ErrorStatus._EXP_NOT_EXCEED);
        }
    }
}
