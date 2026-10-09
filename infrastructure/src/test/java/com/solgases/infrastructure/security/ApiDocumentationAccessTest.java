package com.solgases.infrastructure.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.solgases.infrastructure.SolgasesApplication;
import com.solgases.infrastructure.TestJwtSecret;
import jakarta.servlet.Filter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Swagger UI and OpenAPI are public in local, dev and qa and disabled in prd, while business routes stay
 * protected in every profile. Each profile runs against its own in-memory H2 database; the schema is created by
 * Hibernate because qa and prd only validate it.
 */
class ApiDocumentationAccessTest {

    private static final String API_DOCS = "/v3/api-docs";
    private static final String SWAGGER_UI = "/swagger-ui/index.html";
    private static final String BUSINESS_ROUTE = "/api/categories";

    @SpringBootTest(classes = SolgasesApplication.class, properties = "spring.jpa.hibernate.ddl-auto=create-drop")
    @AutoConfigureTestDatabase
    abstract static class ProfileAccess {

        @DynamicPropertySource
        static void securityProperties(DynamicPropertyRegistry registry) {
            TestJwtSecret.register(registry);
        }

        @Autowired
        private WebApplicationContext context;

        protected MockMvc mockMvc;

        @BeforeEach
        void setUp() {
            mockMvc = MockMvcBuilders.webAppContextSetup(context)
                    .addFilters(context.getBean("springSecurityFilterChain", Filter.class))
                    .build();
        }

        @Test
        void businessRoutesStillRequireAuthentication() throws Exception {
            mockMvc.perform(get(BUSINESS_ROUTE)).andExpect(status().isUnauthorized());
        }
    }

    abstract static class PublicDocumentation extends ProfileAccess {

        @Test
        void openApiAndSwaggerUiArePublic() throws Exception {
            mockMvc.perform(get(API_DOCS)).andExpect(status().isOk());
            mockMvc.perform(get(SWAGGER_UI)).andExpect(status().isOk());
        }
    }

    @Nested
    @ActiveProfiles("local")
    class Local extends PublicDocumentation {
    }

    @Nested
    @ActiveProfiles("dev")
    class Dev extends PublicDocumentation {
    }

    @Nested
    @ActiveProfiles("qa")
    class Qa extends PublicDocumentation {
    }

    @Nested
    @ActiveProfiles("prd")
    class Prd extends ProfileAccess {

        @Test
        void documentationIsDisabledAndNotPublic() throws Exception {
            mockMvc.perform(get(API_DOCS)).andExpect(status().isUnauthorized());
            mockMvc.perform(get(SWAGGER_UI)).andExpect(status().isUnauthorized());
        }
    }
}
