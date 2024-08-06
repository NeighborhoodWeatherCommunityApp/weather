package org.pknu.weather.tag.domain;
import jakarta.persistence.*;
import lombok.*;
import org.pknu.weather.common.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Tag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "tag_id")
    private Long id;

    private String temperTag;

    private String windTag;

    private String skyTag;

    private String dustTag;

}
