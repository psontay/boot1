package com.boot1.service;

import com.boot1.Entities.InvalidatedToken;
import com.boot1.Entities.User;
import com.boot1.dto.request.AuthenticationRequest;
import com.boot1.dto.request.IntrospectRequest;
import com.boot1.dto.request.LogoutRequest;
import com.boot1.dto.response.AuthenticationResponse;
import com.boot1.dto.response.IntrospectResponse;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.boot1.Entities.Permission;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;
    @NonFinal
    @Value("${spring.jwt.signerKey}")
    protected String SIGNER_KEY;
    public AuthenticationResponse authenticate(AuthenticationRequest request){
        var user =
                userRepository.findByUsername((request.getUsername())).orElseThrow(() -> new ApiException(
                        ErrorCode.USER_NOT_EXISTS));
        log.info("User Roles: " + user.getRoles());

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if ( !authenticated ) {
            throw new ApiException(ErrorCode.UNAUTHENTICATED);
        }
        // generate token
        var token = generateToken(user);
        return AuthenticationResponse.builder().token(token).success(true).build();
    }
    private String generateToken(User user) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet =
                new JWTClaimsSet.Builder()
                        .subject(user.getUsername())
                        .issuer("sontaypham")
                        .issueTime(new Date())
                        .expirationTime(new Date(
                        Instant.now().plus(1 , ChronoUnit.HOURS).toEpochMilli()
                ))
                        .claim("scope" , buildScope(user))
                        .claim("permission" ,buildPermissions(user))
                        .jwtID(UUID.randomUUID().toString())
                        .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject( jwsHeader , payload);
        try {
            jwsObject.sign( new MACSigner(SIGNER_KEY));
            return jwsObject.serialize();
        }catch(JOSEException e) {
            throw new RuntimeException(e);
        }
    }
    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid;
        try {
            verifyToken(token);
            isValid = true;
        }catch( ApiException e ) {
            isValid = false;
        }
        return IntrospectResponse.builder().valid(isValid).build();
    }
    private String buildScope ( User user ) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role -> stringJoiner.add(role.getName()));
        }
        return stringJoiner.toString();
    }
    private List<String> buildPermissions ( User user )  {
        if ( CollectionUtils.isEmpty(user.getRoles())) {
            return Collections.emptyList();
        }
        return user.getRoles().stream()
                   .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .distinct()
                .toList();
    }
    public void logout( LogoutRequest request) throws ParseException , JOSEException {
        var signToken = verifyToken(request.getToken());
        String jti = signToken.getJWTClaimsSet().getJWTID();
        Date expTime = signToken.getJWTClaimsSet().getExpirationTime();
        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                                                            .id(jti)
                                                            .expTime(expTime)
                                                            .build();
        invalidatedTokenRepository.save(invalidatedToken);
    }
    private SignedJWT verifyToken ( String token ) throws  JOSEException , ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        var verified = signedJWT.verify(verifier);
        if ( !verified && expTime.after(new Date()) ) throw new ApiException(ErrorCode.UNAUTHENTICATED);
        if ( invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()) ) throw new ApiException(ErrorCode.UNAUTHENTICATED);
        return signedJWT;
    }
}
