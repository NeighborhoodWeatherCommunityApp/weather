package org.pknu.weather.member.auth.generator;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;
import org.pknu.weather.member.auth.dto.AppleJwtKeyDTO;
import org.pknu.weather.member.auth.dto.AppleJwtKeysResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class ApplePublicKeyGenerator {
    public PublicKey generatePublicKey(Map<String, String> tokenHeader, AppleJwtKeysResponseDTO applePublicKeys)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        AppleJwtKeyDTO publicKey = applePublicKeys.getMatchedKey(tokenHeader.get("kid"), tokenHeader.get("alg"));

        return getPublicKey(publicKey);
    }

    private PublicKey getPublicKey(AppleJwtKeyDTO publicKey)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] nBytes = Base64.getUrlDecoder().decode(publicKey.n());
        byte[] eBytes = Base64.getUrlDecoder().decode(publicKey.e());

        RSAPublicKeySpec publicKeySpec =
                new RSAPublicKeySpec(new BigInteger(1, nBytes), new BigInteger(1, eBytes));

        KeyFactory keyFactory = KeyFactory.getInstance(publicKey.kty());
        return keyFactory.generatePublic(publicKeySpec);
    }
}
