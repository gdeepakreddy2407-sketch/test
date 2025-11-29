package com.deepak.AuthService.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class JwtService {

    @Value("${auth.jwt.private-key-location:#{null}}")
    private String privateKeyLocation;

    @Value("${auth.jwt.public-key-location:#{null}}")
    private String publicKeyLocation;

    @Value("${auth.jwt.keystore-location:#{null}}")
    private String keystoreLocation;

    @Value("${auth.jwt.keystore-password:#{null}}")
    private String keystorePassword;

    @Value("${auth.jwt.key-alias:#{null}}")
    private String keyAlias;

    @Value("${auth.jwt.key-password:#{null}}")
    private String keyPassword;

    @Value("${auth.jwt.issuer:https://auth-service}")
    private String issuer;

    @Value("${auth.jwt.audience:your-api}")
    private String audience;

    @Value("${auth.jwt.expiration-minutes:15}")
    private int expirationMinutes;

    @Value("${auth.jwk.kid:default-key-id}")
    private String keyId;

    private KeyPair keyPair;
    private JWKSet jwkSet;

    @PostConstruct
    public void init() throws Exception {
        this.keyPair = loadKeyPair();
        this.jwkSet = buildJwkSet();
    }

    /**
     * Load RSA keypair from either:
     * 1. JKS/PKCS12 keystore (if keystore-location is provided)
     * 2. PEM files (if private-key-location and public-key-location are provided)
     */
    public KeyPair loadKeyPair() throws Exception {
        // Option 1: Load from keystore (JKS or PKCS12)
        if (keystoreLocation != null && !keystoreLocation.isEmpty()) {
            return loadKeyPairFromKeystore();
        }

        // Option 2: Load from PEM files
        if (privateKeyLocation != null && publicKeyLocation != null) {
            return loadKeyPairFromPemFiles();
        }

        throw new IllegalStateException(
                "Either keystore configuration or PEM file locations must be provided");
    }

    /**
     * Load keypair from JKS or PKCS12 keystore
     */
    private KeyPair loadKeyPairFromKeystore() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");

        try (FileInputStream fis = new FileInputStream(keystoreLocation)) {
            keyStore.load(fis, keystorePassword.toCharArray());
        }

        Key key = keyStore.getKey(keyAlias, keyPassword.toCharArray());
        if (!(key instanceof PrivateKey)) {
            throw new IllegalStateException("Retrieved key is not a PrivateKey");
        }

        Certificate cert = keyStore.getCertificate(keyAlias);
        PublicKey publicKey = cert.getPublicKey();

        return new KeyPair(publicKey, (PrivateKey) key);
    }

    /**
     * Load keypair from PEM files
     */
    private KeyPair loadKeyPairFromPemFiles() throws Exception {
        RSAPrivateKey privateKey = loadPrivateKey(privateKeyLocation);
        RSAPublicKey publicKey = loadPublicKey(publicKeyLocation);
        return new KeyPair(publicKey, privateKey);
    }

    /**
     * Load RSA private key from PEM file
     */
    private RSAPrivateKey loadPrivateKey(String filename) throws Exception {
        String privateKeyPEM = new String(Files.readAllBytes(Paths.get(filename)));

        // Remove PEM headers/footers and whitespace
        privateKeyPEM = privateKeyPEM
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(privateKeyPEM);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    /**
     * Load RSA public key from PEM file
     */
    private RSAPublicKey loadPublicKey(String filename) throws Exception {
        String publicKeyPEM = new String(Files.readAllBytes(Paths.get(filename)));

        // Remove PEM headers/footers and whitespace
        publicKeyPEM = publicKeyPEM
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(publicKeyPEM);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    /**
     * Create JWT with standard and custom claims
     */
    public String createJwt(String subject, List<String> roles) throws JOSEException {
        Instant now = Instant.now();
        Instant expiration = now.plus(expirationMinutes, ChronoUnit.MINUTES);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(subject)
                .issuer(issuer)
                .audience(audience)
                .expirationTime(Date.from(expiration))
                .issueTime(Date.from(now))
                .jwtID(UUID.randomUUID().toString())
                .claim("roles", roles)
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256)
                        .keyID(keyId)
                        .build(),
                claimsSet
        );

        RSASSASigner signer = new RSASSASigner((RSAPrivateKey) keyPair.getPrivate());
        signedJWT.sign(signer);

        return signedJWT.serialize();
    }

    /**
     * Build JWK Set for JWKS endpoint
     */
    private JWKSet buildJwkSet() throws JOSEException {
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        JWK jwk = new RSAKey.Builder(publicKey)
                .keyUse(KeyUse.SIGNATURE)
                .algorithm(JWSAlgorithm.RS256)
                .keyID(keyId)
                .build();

        return new JWKSet(jwk);
    }

    /**
     * Get JWKS (JSON Web Key Set) for public key exposure
     * This endpoint allows other services to verify JWT signatures
     */
    public JWKSet getJwks() {
        return jwkSet;
    }

    /**
     * Get JWKS as JSON string
     */
    public String getJwksJson() {
        return jwkSet.toString();
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }
}