/*
 * Copyright 2022 Adrian Herscu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.aherscu.qa.tester.utils.rest;

import static dev.aherscu.qa.tester.utils.StringUtilsExtensions.*;

import java.lang.SuppressWarnings;
import java.math.*;
import java.nio.charset.*;
import java.security.*;
import java.text.*;
import java.time.*;
import java.util.*;

import javax.crypto.*;
import javax.crypto.spec.*;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.tuple.*;

import com.amazonaws.auth.*;
import com.amazonaws.regions.*;
import com.amazonaws.services.cognitoidp.*;
import com.amazonaws.services.cognitoidp.model.*;
import com.google.common.cache.*;

import edu.umd.cs.findbugs.annotations.*;
import lombok.*;
import lombok.extern.slf4j.*;

/**
 * Secure Remote Password (SRP) implementation for AWS Cognito.
 *
 * @see <a href=
 *      "https://github.com/aws-samples/aws-cognito-java-desktop-app/blob/master/src/main/java/com/amazonaws/sample/cognitoui/AuthenticationHelper.java">source
 *      on github</a>
 */
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Slf4j
@SuppressFBWarnings(value = "USBR_UNNECESSARY_STORE_BEFORE_RETURN",
    justification = "hashcode implemented by lombok")
@SuppressWarnings("ClassWithTooManyMethods")
public class AwsCognitoSrpAuthenticator {

    private static final String                                          HEX_N;
    private static final BigInteger                                      N;
    private static final Pair<BigInteger, BigInteger>                    aA;
    private static final BigInteger                                      g;
    private static final Cache<CachingKey, RespondToAuthChallengeResult> cache;

    static {
        HEX_N =
            "FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD1"
                + "29024E088A67CC74020BBEA63B139B22514A08798E3404DD"
                + "EF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245"
                + "E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7ED"
                + "EE386BFB5A899FA5AE9F24117C4B1FE649286651ECE45B3D"
                + "C2007CB8A163BF0598DA48361C55D39A69163FA8FD24CF5F"
                + "83655D23DCA3AD961C62F356208552BB9ED529077096966D"
                + "670C354E4ABC9804F1746C08CA18217C32905E462E36CE3B"
                + "E39E772C180E86039B2783A2EC07A28FB5C55DF06F4C52C9"
                + "DE2BCBF6955817183995497CEA956AE515D2261898FA0510"
                + "15728E5A8AAAC42DAD33170D04507A33A85521ABDF1CBA64"
                + "ECFB850458DBEF0A8AEA71575D060C7DB3970F85A6E1E4C7"
                + "ABF5AE8CDB0933D71E8C94E04A25619DCEE3D2261AD2EE6B"
                + "F12FFA06D98A0864D87602733EC86A64521F2B18177B200C"
                + "BBE117577A615D6C770988C0BAD946E208E24FA074E5AB31"
                + "43DB5BFCE0FD108E4B82D120A93AD2CAFFFFFFFFFFFFFFFF";

        N = new BigInteger(HEX_N, 16);
        g = BigInteger.valueOf(2);
        aA = aA(N, g);
        cache = CacheBuilder.newBuilder()
            .expireAfterWrite(Duration
                .ofMinutes(Long.parseLong(System
                    .getProperty("authentication.caching.minutes", "10"))))
            .build();
    }
    @Builder.Default
    @EqualsAndHashCode.Include
    private final Algorithm algorithm = Algorithm.HMACSHA256;
    @ToString.Include
    @EqualsAndHashCode.Include
    private final String    clientId;
    @ToString.Include
    @EqualsAndHashCode.Include
    private final Regions   region;
    @ToString.Include
    @EqualsAndHashCode.Include
    private final String    secretKey;
    @ToString.Include
    @EqualsAndHashCode.Include
    private final String    userPoolId;

    private static Pair<BigInteger, BigInteger> aA(
        final BigInteger N,
        final BigInteger g) {
        BigInteger a, A;
        do {
            a = new BigInteger(1024, sha1PRNG()).mod(N);
            A = g.modPow(a, N);
        } while (A.mod(N).equals(BigInteger.ZERO));
        return Pair.of(a, A);
    }

    private static Format challengeTimeFormat() {
        val formatTimestamp =
            new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy", Locale.US);
        formatTimestamp
            .setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
        return formatTimestamp;
    }

    private static BigInteger k(final BigInteger N, final BigInteger g) {
        return new BigInteger(1, messageFor(N.toByteArray(), g.toByteArray()));
    }

    /**
     * Without parameters, prints a help message.
     *
     * @param args
     *            command line arguments as supplied by the system
     */
    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    public static void main(final String[] args) {
        System.out.println("HTTPS_PROXY=" + System.getenv("HTTPS_PROXY"));
        System.out.println("JRE Path: " + System.getProperty("java.home"));
        val options = new Options();
        options.addRequiredOption("p", "pool", true, "User pool ID");
        options.addRequiredOption("c", "client", true, "Client ID");
        options.addRequiredOption("r", "region", true, "Region ID");
        options.addRequiredOption("u", "username", true, "User name");
        options.addRequiredOption("s", "secret", true, "User secret password");

        try {
            val commandLine = new DefaultParser().parse(options, args);
            val token = AwsCognitoSrpAuthenticator.builder()
                .region(
                    Regions.fromName(commandLine.getOptionValue("region")))
                .clientId(commandLine.getOptionValue("client"))
                .userPoolId(commandLine.getOptionValue("pool"))
                .build()
                .authChallengeResultWithoutCaching(
                    commandLine.getOptionValue("username"),
                    commandLine.getOptionValue("secret"))
                .getAuthenticationResult()
                .getIdToken();

            System.out.println(">>>");
            System.out.println(token);
            System.out.println("<<<");

            System.exit(0);
        } catch (final Throwable t) {
            System.err.println("ERROR >>> " + t.getMessage());

            new HelpFormatter()
                .printHelp("aws-cognito-srp-authenticator", options);

            System.exit(-1);
        }
    }

    private static byte[] messageFor(final byte[]... byteArrays) {
        val messageDigest = sha256Digest();

        for (final byte[] byteArray : byteArrays) {
            messageDigest.update(byteArray);
        }

        return messageDigest.digest();
    }

    @SneakyThrows
    private static SecureRandom sha1PRNG() {
        return SecureRandom.getInstance("SHA1PRNG");
    }

    @SneakyThrows
    private static MessageDigest sha256Digest() {
        return MessageDigest.getInstance("SHA-256");
    }

    private static BigInteger u(
        final BigInteger B,
        final Pair<BigInteger, BigInteger> aA) {
        val u = new BigInteger(1,
            messageFor(aA.getRight().toByteArray(), B.toByteArray()));

        if (u.equals(BigInteger.ZERO))
            throw new SecurityException("Hash of A and B cannot be zero");

        return u;
    }

    private static byte[] utf8BytesFrom(final String string) {
        return string.getBytes(StandardCharsets.UTF_8);
    }

    private static String utf8StringFrom(final byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Method to orchestrate the SRP Authentication
     *
     * @param userName
     *            Username for the SRP request
     * @param password
     *            Password for the SRP request
     * @return the JWT token if the request is successful else null.
     */
    @SneakyThrows
    public RespondToAuthChallengeResult authChallengeResult(
        final String userName,
        final String password) {
        return cache.get(new CachingKey(this, userName, password),
            () -> authChallengeResultWithoutCaching(userName, password));
    }

    public RespondToAuthChallengeResult authChallengeResultWithoutCaching(
        final String userName, final String password) {
        log.debug(
            "retrieving authentication challenge result for {} with {}:{}",
            this, userName, password);

        val cognitoIdentityProvider = cognitoIdentityProvider();

        // ISSUE https://github.com/aws/aws-sdk-java/issues/2603
        // unable to use amazon sdk via corporate proxy with self-signed
        // certificates
        val initiateAuthResult = cognitoIdentityProvider
            .initiateAuth(initiateUserSrpAuthRequest(userName));

        if (!ChallengeNameType.PASSWORD_VERIFIER.toString()
            .equals(initiateAuthResult.getChallengeName()))
            throw new SecurityException("challenge failed");

        return cognitoIdentityProvider
            .respondToAuthChallenge(
                respondToAuthChallengeRequest(
                    new AuthenticationChallenge(initiateAuthResult),
                    password));
    }

    /**
     * Override to configure an AWS Cognito Identity provider.
     *
     * @return configured AWS Cognito Identity provider
     */
    protected AWSCognitoIdentityProvider cognitoIdentityProvider() {
        return AWSCognitoIdentityProviderClientBuilder
            .standard()
            .withCredentials(
                new AWSStaticCredentialsProvider(
                    new AnonymousAWSCredentials()))
            .withRegion(region)
            .build();
    }

    @SneakyThrows
    private byte[] getPasswordAuthenticationKey(
        final String userId,
        final String userPassword,
        final BigInteger B,
        final BigInteger salt) {
        // Authenticate the password
        // u = H(A, B)

        // x = H(salt | H(poolName | userId | ":" | password))

        val x = x(userId, userPassword, salt);
        val S = B.subtract(k(N, g).multiply(g.modPow(x, N)))
            .modPow(aA.getLeft().add(u(B, aA).multiply(x)), N).mod(N);

        return new Hkdf(S.toByteArray(), u(B, aA).toByteArray())
            .deriveKey();
    }

    @SneakyThrows
    private Mac initHmacSha256With(final Key key) {
        val mac = Mac.getInstance(algorithm.name());
        mac.init(key);
        return mac;
    }

    private InitiateAuthRequest initiateUserSrpAuthRequest(
        final String userName) {

        val initiateAuthRequest = new InitiateAuthRequest();
        initiateAuthRequest.setAuthFlow(AuthFlowType.USER_SRP_AUTH);
        initiateAuthRequest.setClientId(clientId);

        if (isNotBlank(secretKey)) {
            initiateAuthRequest.addAuthParametersEntry("SECRET_HASH",
                secretHashFor(
                    new SecretKeySpec(utf8BytesFrom(secretKey),
                        algorithm.name()),
                    userName));
        }

        initiateAuthRequest.addAuthParametersEntry("USERNAME", userName);
        // noinspection MagicNumber
        initiateAuthRequest.addAuthParametersEntry("SRP_A",
            aA.getRight().toString(16));
        return initiateAuthRequest;
    }

    private byte[] macFor(final Key key, final byte[]... byteArrays) {
        val mac = initHmacSha256With(key);

        for (final byte[] byteArray : byteArrays) {
            mac.update(byteArray);
        }

        return mac.doFinal();
    }

    @SuppressWarnings("serial")
    @SneakyThrows
    private RespondToAuthChallengeRequest respondToAuthChallengeRequest(
        final AuthenticationChallenge challenge,
        final String password) {

        val timestamp = challengeTimeFormat().format(new Date());

        val respondToAuthChallengeRequest = new RespondToAuthChallengeRequest();
        respondToAuthChallengeRequest.setClientId(clientId);
        respondToAuthChallengeRequest.setSession(challenge.session());
        respondToAuthChallengeRequest.setChallengeName(challenge.name());
        respondToAuthChallengeRequest.setChallengeResponses(
            new HashMap<String, String>() {
                {
                    put("PASSWORD_CLAIM_SECRET_BLOCK", challenge.secretBlock());
                    put("USERNAME", challenge.username());
                    put("TIMESTAMP", timestamp);
                    put("PASSWORD_CLAIM_SIGNATURE",
                        utf8StringFrom(
                            Base64.getEncoder().encode(
                                macFor(
                                    new SecretKeySpec(
                                        getPasswordAuthenticationKey(
                                            challenge.userIdForSRP(),
                                            password,
                                            challenge.srpB(),
                                            challenge.salt()),
                                        algorithm.name()),
                                    userPoolIdNoise(),
                                    utf8BytesFrom(challenge.userIdForSRP()),
                                    Base64.getDecoder()
                                        .decode(challenge.secretBlock()),
                                    utf8BytesFrom(timestamp)))));
                }
            });

        return respondToAuthChallengeRequest;
    }

    @SneakyThrows
    private String secretHashFor(final Key key, final String userName) {
        return Base64.getEncoder()
            .encodeToString(
                macFor(key, utf8BytesFrom(userName), utf8BytesFrom(clientId)));
    }

    private byte[] userIdHash(
        final String userId,
        final String userPassword) {
        return messageFor(
            userPoolIdNoise(),
            utf8BytesFrom(userId),
            utf8BytesFrom(COLON),
            utf8BytesFrom(userPassword));
    }

    private byte[] userPoolIdNoise() {
        return utf8BytesFrom(substringAfter(userPoolId, "_"));
    }

    private BigInteger x(
        final String userId,
        final String userPassword,
        final BigInteger salt) {
        return new BigInteger(1,
            messageFor(salt.toByteArray(), userIdHash(userId, userPassword)));
    }

    /**
     * Algorithms to be used for getting secret keys and generating HMAC.
     *
     * @author Adrian Herscu
     *
     */
    public enum Algorithm {
        /**
         * The only algorithm tested to work with AWS Cognito.
         */
        HMACSHA256
    }

    @RequiredArgsConstructor
    final static class AuthenticationChallenge {
        private final InitiateAuthResult challenge;

        String name() {
            return challenge.getChallengeName();
        }

        BigInteger salt() {
            // noinspection MagicNumber
            return new BigInteger(
                challenge.getChallengeParameters().get("SALT"),
                16);
        }

        String secretBlock() {
            return challenge.getChallengeParameters().get("SECRET_BLOCK");
        }

        String session() {
            return challenge.getSession();
        }

        BigInteger srpB() {
            val srpB = new BigInteger(
                challenge.getChallengeParameters().get("SRP_B"),
                16);

            if (srpB.mod(N).equals(BigInteger.ZERO))
                throw new SecurityException("SRP error, B cannot be zero");

            return srpB;
        }

        String userIdForSRP() {
            return challenge.getChallengeParameters().get("USER_ID_FOR_SRP");
        }

        String username() {
            return challenge.getChallengeParameters().get("USERNAME");
        }
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @SuppressFBWarnings(value = "USBR_UNNECESSARY_STORE_BEFORE_RETURN",
        justification = "hashcode implemented by lombok")
    static class CachingKey {
        final AwsCognitoSrpAuthenticator authenticator;
        final String                     userName, password;
    }

    @SuppressFBWarnings(value = "IMA_INEFFICIENT_MEMBER_ACCESS",
        justification = "no real performance impact as this class does I/O")
    final class Hkdf {
        static final String DERIVED_KEY_INFO = "Caldera Derived Key";
        static final int    DERIVED_KEY_SIZE = 16;

        static final int    MAX_KEY_SIZE     = 255;
        final byte[]        EMPTY_ARRAY      = new byte[0];
        final SecretKey     privateKey;

        @SneakyThrows
        Hkdf(final byte[] ikm, final byte[] salt) {
            privateKey = new SecretKeySpec(
                initHmacSha256With(new SecretKeySpec(salt, algorithm.name()))
                    .doFinal(ikm),
                algorithm.name());
        }

        byte[] deriveKey() {
            return deriveKey(utf8BytesFrom(DERIVED_KEY_INFO), DERIVED_KEY_SIZE);
        }

        @SneakyThrows
        byte[] deriveKey(final byte[] info, final int length) {
            val result = new byte[length];

            val mac = initHmacSha256With(privateKey);
            if (length > MAX_KEY_SIZE * mac.getMacLength())
                throw new IllegalArgumentException(
                    "Requested keys may not be longer than 255 times the underlying HMAC length.");

            {
                var t = EMPTY_ARRAY;
                var loc = 0;

                for (byte i = 1; loc < length; i++) {
                    mac.update(t);
                    mac.update(info);
                    mac.update(i);
                    t = mac.doFinal();

                    for (int x = 0; x < t.length && loc < length; x++, loc++) {
                        result[loc] = t[x];
                    }
                }
            }

            return result;
        }
    }
}
