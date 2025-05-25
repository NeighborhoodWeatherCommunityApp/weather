package org.pknu.weather.domain;

import jakarta.persistence.*;
import lombok.*;
import org.pknu.weather.domain.common.TermsType;

@Builder
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Terms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "terms_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private TermsType termsType;

    @Column(nullable = false)
    private Boolean isRequired;

    private Integer termsVersion;
}
