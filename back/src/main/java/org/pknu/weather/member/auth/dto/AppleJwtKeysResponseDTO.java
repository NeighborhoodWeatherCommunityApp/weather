package org.pknu.weather.member.auth.dto;

import java.util.List;

public record AppleJwtKeysResponseDTO(List<AppleJwtKeyDTO> keys) {
    public AppleJwtKeyDTO getMatchedKey(String kid, String alg)  {
        return keys.stream()
                .filter(key -> key.kid().equals(kid) && key.alg().equals(alg))
                .findAny()
                .orElseThrow();
    }
}
