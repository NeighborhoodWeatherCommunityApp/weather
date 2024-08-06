package org.pknu.weather.recommandation.domain;

import jakarta.persistence.*;
import lombok.*;
import org.pknu.weather.common.BaseEntity;
import org.pknu.weather.member.domain.Member;
import org.pknu.weather.post.domain.Post;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Recommendation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "recommendation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @JoinColumn(name = "post_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;
}
