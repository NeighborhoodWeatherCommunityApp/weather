package org.pknu.weather.member.auth.dto;

public record AppleJwtKeyDTO(String kty,
                             String kid,
                             String alg,
                             String n,
                             String e) {
}
