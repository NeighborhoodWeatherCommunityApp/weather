package org.pknu.weather.member.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.pknu.weather.apipayload.code.status.ErrorStatus;
import org.pknu.weather.common.entity.BaseEntity;
import org.pknu.weather.location.entity.Location;
import org.pknu.weather.post.entity.Post;
import org.pknu.weather.recomandation.entity.Recommendation;
import org.pknu.weather.member.enums.Sensitivity;
import org.pknu.weather.member.exp.Level;
import org.pknu.weather.member.dto.MemberJoinDTO;
import org.pknu.weather.exception.GeneralException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.pknu.weather.member.enums.Role;

@Entity(name = "member")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Post> postList = new ArrayList<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Recommendation> recommendationList = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "member_roles",  // 중간 테이블 이름
            joinColumns = @JoinColumn(name = "member_id")
    )
    @Column(name = "role")
    @Builder.Default
    private Set<Role> roles = new HashSet<>(Set.of(Role.ROLE_MEMBER));

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
    }

    public Level levelUpCheckAndReturn() {
        for (Level level : Level.values()) {
            if (this.exp >= level.getRequiredExp()) {
                this.level = level;
            }
        }
        return level;
    }

    public void decreaseExp(Long minusExp) {
        Long currentLevelMinimumExp = level.getRequiredExp();

        if ((exp + minusExp) < currentLevelMinimumExp) {
            exp = currentLevelMinimumExp;
        } else {
            exp += minusExp;
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Member member = (Member) o;
        return Objects.equals(id, member.id);
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    public void removeRole(Role role) {
        roles.remove(role);
    }

    public boolean hasRole(Role role) {
        return roles.contains(role);
    }

}
