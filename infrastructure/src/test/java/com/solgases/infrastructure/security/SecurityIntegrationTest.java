package com.solgases.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.solgases.infrastructure.SolgasesApplication;
import com.solgases.infrastructure.TestJwtSecret;
import com.solgases.infrastructure.config.InitialRolePermissionCatalog;
import com.solgases.infrastructure.persistence.entity.PermissionJpaEntity;
import com.solgases.infrastructure.persistence.entity.RoleJpaEntity;
import com.solgases.infrastructure.persistence.entity.UserCredentialJpaEntity;
import com.solgases.infrastructure.persistence.entity.UserJpaEntity;
import com.solgases.infrastructure.persistence.repository.PermissionJpaRepository;
import com.solgases.infrastructure.persistence.repository.RoleJpaRepository;
import com.solgases.infrastructure.persistence.repository.UserCredentialJpaRepository;
import com.solgases.infrastructure.persistence.repository.UserJpaRepository;
import jakarta.servlet.Filter;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * End-to-end checks of the security filter chain against an in-memory H2 database. The permission keys used
 * here (TEST_*) exist only in this test and replace the approved matrix, so that each check controls exactly which
 * permission a request requires. The approved catalog and matrix are covered by their own tests.
 */
@SpringBootTest(classes = SolgasesApplication.class)
@AutoConfigureTestDatabase
@Import(SecurityIntegrationTest.TestMatrixConfiguration.class)
@ExtendWith(OutputCaptureExtension.class)
class SecurityIntegrationTest {

    private static final String TOKEN_PATH = "/api/auth/token";
    private static final String CATEGORY_READ = "TEST_CATEGORY_READ";
    private static final String UNIT_READ = "TEST_UNIT_READ";
    private static final JsonMapper JSON = JsonMapper.builder().build();

    @TestConfiguration
    static class TestMatrixConfiguration {

        @Bean
        @Primary
        EndpointPermissionMatrix testEndpointPermissionMatrix() {
            return new EndpointPermissionMatrix(List.of(
                    new EndpointPermissionRule(HttpMethod.GET, "/api/categories", CATEGORY_READ),
                    new EndpointPermissionRule(HttpMethod.GET, "/api/units-of-measure", UNIT_READ)));
        }
    }

    @DynamicPropertySource
    static void securityProperties(DynamicPropertyRegistry registry) {
        TestJwtSecret.register(registry);
    }

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private UserJpaRepository userRepository;
    @Autowired
    private RoleJpaRepository roleRepository;
    @Autowired
    private PermissionJpaRepository permissionRepository;
    @Autowired
    private UserCredentialJpaRepository credentialRepository;
    @Autowired
    private Argon2PasswordEncoder passwordEncoder;
    @Autowired
    private JwtEncoder jwtEncoder;
    @Autowired
    @Qualifier("endpointPermissionMatrix")
    private EndpointPermissionMatrix productionMatrix;

    private MockMvc mockMvc;
    private String password;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(context.getBean("springSecurityFilterChain", Filter.class))
                .build();
        password = "pw-" + UUID.randomUUID();
    }

    @AfterEach
    void cleanUp() {
        credentialRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
        permissionRepository.deleteAll();
    }

    private RoleJpaEntity role(String key, String... permissionKeys) {
        RoleJpaEntity role = new RoleJpaEntity(key, key.toLowerCase());
        role.replacePermissions(Arrays.stream(permissionKeys)
                .map(permissionKey -> permissionRepository.saveAndFlush(
                        new PermissionJpaEntity(permissionKey, permissionKey.toLowerCase())))
                .toList());
        return roleRepository.saveAndFlush(role);
    }

    private UserJpaEntity userWithCredential(String username, boolean active, RoleJpaEntity... roles) {
        UserJpaEntity user = new UserJpaEntity(username, "Test user");
        user.replaceRoles(List.of(roles));
        if (!active) {
            user.deactivate();
        }
        user = userRepository.saveAndFlush(user);
        credentialRepository.saveAndFlush(new UserCredentialJpaEntity(user, passwordEncoder.encode(password)));
        return user;
    }

    private ResultActions requestToken(String username, String secret) throws Exception {
        return mockMvc.perform(post(TOKEN_PATH).contentType(MediaType.APPLICATION_JSON)
                .content(JSON.writeValueAsString(Map.of("username", username, "password", secret))));
    }

    private String token(String username) throws Exception {
        MvcResult result = requestToken(username, password).andExpect(status().isOk()).andReturn();
        return JSON.readTree(result.getResponse().getContentAsString()).get("accessToken").asString();
    }

    private ResultActions getWithToken(String path, String token) throws Exception {
        return mockMvc.perform(get(path).header(HttpHeaders.AUTHORIZATION, "Bearer " + token));
    }

    private static void expectProblem(ResultActions result, int status) throws Exception {
        result.andExpect(status().is(status))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(status))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").exists());
    }

    @Test
    void anonymousRequestsAreRejectedWith401ProblemDetail() throws Exception {
        // Swagger/OpenAPI is public in local, dev and qa (see ApiDocumentationAccessTest)
        for (String path : List.of("/api/categories", "/api/users", "/api/does-not-exist")) {
            ResultActions result = mockMvc.perform(get(path));
            expectProblem(result, 401);
            result.andExpect(header().string(HttpHeaders.WWW_AUTHENTICATE, "Bearer"))
                    .andExpect(jsonPath("$.detail").value(ProblemDetailSecurityErrorHandler.UNAUTHORIZED_DETAIL));
        }
    }

    @Test
    void validCredentialsReturnA15MinuteTokenThatOnlyIdentifiesTheUser() throws Exception {
        UserJpaEntity user = userWithCredential("alice", true, role("R_TEST", CATEGORY_READ));

        MvcResult result = requestToken("alice", password)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(900))
                .andReturn();

        String[] parts = JSON.readTree(result.getResponse().getContentAsString()).get("accessToken").asString()
                .split("\\.");
        JsonNode header = JSON.readTree(Base64.getUrlDecoder().decode(parts[0]));
        JsonNode claims = JSON.readTree(Base64.getUrlDecoder().decode(parts[1]));
        assertThat(header.get("alg").asString()).isEqualTo("HS256");
        assertThat(claims.propertyNames()).containsExactlyInAnyOrder("sub", "iat", "exp");
        assertThat(claims.get("sub").asString()).isEqualTo(String.valueOf(user.getId()));
        assertThat(claims.get("exp").asLong() - claims.get("iat").asLong()).isEqualTo(900);
    }

    @Test
    void wrongPasswordUnknownUserAndInactiveUserGetTheSame401() throws Exception {
        userWithCredential("alice", true);
        userWithCredential("bob", false);

        for (ResultActions result : List.of(requestToken("alice", "wrong-" + UUID.randomUUID()),
                requestToken("nobody", password), requestToken("bob", password))) {
            expectProblem(result, 401);
            result.andExpect(jsonPath("$.detail").value("Invalid username or password"))
                    .andExpect(jsonPath("$.accessToken").doesNotExist());
        }
    }

    @Test
    void passwordAboveTheMaximumGetsTheSameGeneric401() throws Exception {
        userWithCredential("alice", true);

        for (String oversized : List.of("x".repeat(129), "\uD83D\uDD12".repeat(129), "x".repeat(100_000))) {
            ResultActions result = requestToken("alice", oversized);
            expectProblem(result, 401);
            result.andExpect(jsonPath("$.detail").value("Invalid username or password"));
        }
    }

    @Test
    void passwordMadeOfSpacesIsAValidCredential() throws Exception {
        password = " ".repeat(15);
        userWithCredential("alice", true);

        requestToken("alice", password).andExpect(status().isOk());
        expectProblem(requestToken("alice", " ".repeat(16)), 401);
    }

    @Test
    void blankCredentialsAreRejectedWith400() throws Exception {
        expectProblem(requestToken(" ", ""), 400);
    }

    @Test
    void storedCredentialIsAnArgon2idHashWithTheMinimumCost() {
        UserJpaEntity user = userWithCredential("alice", true);

        String hash = credentialRepository.findById(user.getId()).orElseThrow().getPasswordHash();

        assertThat(hash).startsWith("$argon2id$v=19$m=19456,t=2,p=1$").doesNotContain(password);
    }

    @Test
    void accessIsGrantedOnlyForTheRequiredPermissionAndUnmappedRequestsAreDenied() throws Exception {
        userWithCredential("alice", true, role("R_TEST", CATEGORY_READ));
        String token = token("alice");

        getWithToken("/api/categories", token).andExpect(status().isOk());
        expectProblem(getWithToken("/api/units-of-measure", token), 403);
        // No rule in the matrix: denied by default even for an authenticated user
        expectProblem(getWithToken("/api/products", token), 403);
        getWithToken("/api/units-of-measure", token)
                .andExpect(jsonPath("$.detail").value(ProblemDetailSecurityErrorHandler.FORBIDDEN_DETAIL));
    }

    @Test
    void authenticatedUserWithoutPermissionsIsDenied() throws Exception {
        userWithCredential("alice", true);

        expectProblem(getWithToken("/api/categories", token("alice")), 403);
    }

    @Test
    void permissionChangesAndDeactivationApplyToTheNextRequest() throws Exception {
        RoleJpaEntity role = role("R_TEST", CATEGORY_READ);
        UserJpaEntity user = userWithCredential("alice", true, role);
        String token = token("alice");
        getWithToken("/api/categories", token).andExpect(status().isOk());

        RoleJpaEntity reloaded = roleRepository.findWithPermissionsById(role.getId()).orElseThrow();
        reloaded.replacePermissions(List.of());
        roleRepository.saveAndFlush(reloaded);
        expectProblem(getWithToken("/api/categories", token), 403);

        UserJpaEntity reloadedUser = userRepository.findById(user.getId()).orElseThrow();
        reloadedUser.deactivate();
        userRepository.saveAndFlush(reloadedUser);
        expectProblem(getWithToken("/api/categories", token), 401);
    }

    @Test
    void malformedTamperedForeignAndUnsignedTokensAreRejected() throws Exception {
        UserJpaEntity user = userWithCredential("alice", true, role("R_TEST", CATEGORY_READ));
        String valid = token("alice");
        String tampered = valid.substring(0, valid.length() - 2) + (valid.endsWith("AA") ? "BB" : "AA");
        JwtEncoder otherKeyEncoder = new JwtConfiguration().jwtEncoder(new SecurityProperties(
                new SecurityProperties.Jwt(TestJwtSecret.generate()), new SecurityProperties.Argon2(19456, 2, 1)));
        String foreign = new AccessTokenService(otherKeyEncoder, Clock.systemUTC()).issue(user.getId()).value();
        String unsigned = base64Url("{\"alg\":\"none\"}") + "." + base64Url("{\"sub\":\"" + user.getId()
                + "\",\"exp\":" + Instant.now().plusSeconds(600).getEpochSecond() + "}") + ".";

        for (String token : List.of("not-a-jwt", tampered, foreign, unsigned)) {
            ResultActions result = getWithToken("/api/categories", token);
            expectProblem(result, 401);
            result.andExpect(jsonPath("$.detail").value(ProblemDetailSecurityErrorHandler.UNAUTHORIZED_DETAIL));
        }
    }

    @Test
    void expiredTokenIsRejected() throws Exception {
        UserJpaEntity user = userWithCredential("alice", true, role("R_TEST", CATEGORY_READ));
        Clock anHourAgo = Clock.fixed(Instant.now().minus(Duration.ofHours(1)), ZoneOffset.UTC);
        String expired = new AccessTokenService(jwtEncoder, anHourAgo).issue(user.getId()).value();

        expectProblem(getWithToken("/api/categories", expired), 401);
    }

    @Test
    void productionMatrixUsesOnlyTheApprovedBusinessPermissions() {
        assertThat(productionMatrix.rules()).hasSize(38)
                .extracting(EndpointPermissionRule::permissionKey)
                .allMatch(InitialRolePermissionCatalog.BUSINESS_PERMISSIONS::contains);
    }

    @Test
    void corsIsDisabled() throws Exception {
        userWithCredential("alice", true, role("R_TEST", CATEGORY_READ));
        String token = token("alice");

        mockMvc.perform(options("/api/categories")
                        .header(HttpHeaders.ORIGIN, "https://client.example")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
        mockMvc.perform(get("/api/categories")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .header(HttpHeaders.ORIGIN, "https://client.example"))
                .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    void secretsNeverAppearInResponsesOrLogs(CapturedOutput output) throws Exception {
        userWithCredential("alice", true, role("R_TEST", CATEGORY_READ));
        String token = token("alice");
        String failedLogin = requestToken("alice", "wrong-" + password).andReturn().getResponse().getContentAsString();
        String denied = getWithToken("/api/units-of-measure", token).andReturn().getResponse().getContentAsString();
        String invalid = getWithToken("/api/categories", token + "x").andReturn().getResponse().getContentAsString();

        for (String body : List.of(failedLogin, denied, invalid)) {
            assertThat(body).doesNotContain(password).doesNotContain(token).doesNotContain(TestJwtSecret.VALUE);
        }
        assertThat(output.getAll())
                .doesNotContain(password)
                .doesNotContain(token)
                .doesNotContain(TestJwtSecret.VALUE)
                .doesNotContainIgnoringCase("generated security password");
    }

    private static String base64Url(String json) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(json.getBytes(StandardCharsets.UTF_8));
    }
}
