package org.pknu.weather.post.tag.entity;

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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pknu.weather.common.entity.BaseEntity;
import org.pknu.weather.location.entity.Location;
import org.pknu.weather.post.tag.enums.DustTag;
import org.pknu.weather.post.tag.enums.HumidityTag;
import org.pknu.weather.post.tag.enums.SkyTag;
import org.pknu.weather.post.tag.enums.TemperatureTag;
import org.pknu.weather.post.tag.enums.WindTag;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Tag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

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
