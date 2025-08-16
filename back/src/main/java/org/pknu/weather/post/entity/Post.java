package org.pknu.weather.post.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.pknu.weather.common.entity.BaseEntity;
import org.pknu.weather.recomandation.entity.Recommendation;
import org.pknu.weather.tag.entity.Tag;
import org.pknu.weather.location.entity.Location;
import org.pknu.weather.post.enums.PostType;

import java.util.ArrayList;
import java.util.List;
import org.pknu.weather.member.entity.Member;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder.Default
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<Recommendation> recommendationList = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'WEATHER'")
    @Builder.Default
    private PostType postType = PostType.WEATHER;

    public void addTag(Tag tag) {
        this.tag = tag;
    }
}
