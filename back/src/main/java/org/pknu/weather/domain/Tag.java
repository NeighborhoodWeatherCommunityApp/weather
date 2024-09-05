package org.pknu.weather.domain;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.pknu.weather.domain.tag.*;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @Enumerated(EnumType.STRING)
    private TemperatureTag temperTag;

    @Enumerated(EnumType.STRING)
    private WindTag windTag;

    @Enumerated(EnumType.STRING)
    private HumidityTag humidityTag;

    @Enumerated(EnumType.STRING)
    private SkyTag skyTag;

    @Enumerated(EnumType.STRING)
    private DustTag dustTag;
}
