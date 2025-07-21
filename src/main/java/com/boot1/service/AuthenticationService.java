package com.boot1.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.boot1.Entities.InvalidatedToken;
import com.boot1.Entities.Permission;
import com.boot1.Entities.User;
import com.boot1.dto.request.AuthenticationRequest;
import com.boot1.dto.request.IntrospectRequest;
import com.boot1.dto.request.LogoutRequest;
import com.boot1.dto.request.RefreshRequest;
import com.boot1.dto.response.AuthenticationResponse;
import com.boot1.dto.response.IntrospectResponse;
import com.boot1.dto.response.RefreshResponse;
import com.boot1.exception.ApiException;
import com.boot1.exception.ErrorCode;
import com.boot1.repository.InvalidatedTokenRepository;
import com.boot1.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String signerKey;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long validDuration;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long refreshableDuration;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository
                .findByUsername((request.getUsername()))
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXISTS));
        log.info("User Roles: " + user.getRoles());
        log.info("SignerKey Authenticate : " + signerKey);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!authenticated) {
            throw new ApiException(ErrorCode.UNAUTHENTICATED);
        }
        // generate token
        var token = generateToken(user);
        return AuthenticationResponse.builder().token(token).success(true).build();
    }

    private String generateToken(User user) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("sontaypham")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(refreshableDuration, ChronoUnit.SECONDS).toEpochMilli()))
                .claim("scope", buildScope(user))
                .claim("permission", buildPermissions(user))
                .jwtID(UUID.randomUUID().toString())
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        try {
            jwsObject.sign(new MACSigner(signerKey));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid;
        try {
            verifyToken(token, false);
            isValid = true;
        } catch (ApiException e) {
            isValid = false;
        }
        return IntrospectResponse.builder().valid(isValid).build();
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role -> stringJoiner.add(role.getName()));
        }
        return stringJoiner.toString();
    }

    private List<String> buildPermissions(User user) {
        if (CollectionUtils.isEmpty(user.getRoles())) {
            return Collections.emptyList();
        }
        return user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .distinct()
                .toList();
    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            var signToken = verifyToken(request.getToken(), true);
            String jti = signToken.getJWTClaimsSet().getJWTID();
            Date expTime = signToken.getJWTClaimsSet().getExpirationTime();
            InvalidatedToken invalidatedToken =
                    InvalidatedToken.builder().id(jti).expTime(expTime).build();
            invalidatedTokenRepository.save(invalidatedToken);
        } catch (ApiException e) {
            log.error(e.getMessage());
        }
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(signerKey.getBytes()); // create verifier to get signer_key
        SignedJWT signedJWT = SignedJWT.parse(token); // parse request token
        Date expTime = (isRefresh) // calculator expiration time for refresh token
                ? new Date(signedJWT
                        .getJWTClaimsSet()
                        .getIssueTime()
                        .toInstant()
                        .plus(refreshableDuration, ChronoUnit.SECONDS)
                        .toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime(); // or use expiration time of access token
        var verified = signedJWT.verify(verifier); // verify signer_key
        if (!verified && expTime.after(new Date())) throw new ApiException(ErrorCode.UNAUTHENTICATED); // if token
        // throw exception cause cannot verify || out of date
        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new ApiException(ErrorCode.UNAUTHENTICATED); // throw if already refresh
        return signedJWT; // return new JWT
    }

    public RefreshResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        SignedJWT signJWT = verifyToken(request.getToken(), true);
        String jti = signJWT.getJWTClaimsSet().getJWTID();
        Date expTime = signJWT.getJWTClaimsSet().getExpirationTime();
        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder().id(jti).expTime(expTime).build();
        invalidatedTokenRepository.save(invalidatedToken);
        var username = signJWT.getJWTClaimsSet().getSubject();
        var user =
                userRepository.findByUsername(username).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXISTS));
        var refreshToken = generateToken(user);
        return RefreshResponse.builder().token(refreshToken).success(true).build();
    }
}
