package com.solgases.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.solgases.infrastructure.persistence.repository.CategoryJpaRepository;
import com.solgases.infrastructure.persistence.repository.InventoryJpaRepository;
import com.solgases.infrastructure.persistence.repository.InventoryMovementJpaRepository;
import com.solgases.infrastructure.persistence.repository.PermissionJpaRepository;
import com.solgases.infrastructure.persistence.repository.ProductJpaRepository;
import com.solgases.infrastructure.persistence.repository.RoleJpaRepository;
import com.solgases.infrastructure.persistence.repository.UnitOfMeasureJpaRepository;
import com.solgases.infrastructure.persistence.repository.UserJpaRepository;
import jakarta.persistence.EntityManager;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Checks that every REST operation is documented in the generated OpenAPI specification, without a database:
 * a summary, a success response, the generic 500 ProblemDetail response and, when the operation accepts client
 * input, its client error responses.
 * The specification is also written to target/openapi/openapi.json, which is the source used to regenerate
 * the Postman collection in docs/postman.
 */
@SpringBootTest(classes = SolgasesApplication.class, properties = {
        "spring.autoconfigure.exclude="
                + "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,"
                + "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration,"
                + "org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration"
})
class OpenApiDocumentationTest {

    private static final List<String> HTTP_METHODS = List.of("get", "post", "put", "patch", "delete");
    private static final String PROBLEM_DETAIL_REF = "#/components/schemas/ProblemDetail";

    @MockitoBean
    private CategoryJpaRepository categoryRepository;
    @MockitoBean
    private ProductJpaRepository productRepository;
    @MockitoBean
    private UnitOfMeasureJpaRepository unitOfMeasureRepository;
    @MockitoBean
    private InventoryJpaRepository inventoryRepository;
    @MockitoBean
    private InventoryMovementJpaRepository inventoryMovementRepository;
    @MockitoBean
    private UserJpaRepository userRepository;
    @MockitoBean
    private RoleJpaRepository roleRepository;
    @MockitoBean
    private PermissionJpaRepository permissionRepository;
    @MockitoBean
    private EntityManager entityManager;

    @Autowired
    private WebApplicationContext context;

    @Test
    void everyOperationHasSummaryAndDocumentedResponses() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        String json = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        Path output = Path.of("target", "openapi", "openapi.json");
        Files.createDirectories(output.getParent());
        Files.writeString(output, json);

        JsonNode spec = JsonMapper.builder().build().readTree(json);
        JsonNode paths = spec.get("paths");
        List<String> undocumented = new ArrayList<>();
        int operations = 0;
        for (Map.Entry<String, JsonNode> path : paths.properties()) {
            for (String method : HTTP_METHODS) {
                JsonNode operation = path.getValue().get(method);
                if (operation == null) {
                    continue;
                }
                operations++;
                String name = method.toUpperCase() + " " + path.getKey();
                if (!operation.hasNonNull("summary") || operation.get("summary").asString().isBlank()) {
                    undocumented.add(name + ": missing summary");
                }
                JsonNode responses = operation.get("responses");
                if (responses == null || !hasResponse(responses, "2")) {
                    undocumented.add(name + ": missing success response");
                } else if (acceptsClientInput(operation) && !hasResponse(responses, "4")) {
                    undocumented.add(name + ": missing client error responses");
                }
                if (responses != null && !documentsGenericServerError(responses)) {
                    undocumented.add(name + ": missing generic 500 ProblemDetail response");
                }
            }
        }

        assertThat(spec.at("/components/schemas/ProblemDetail").isObject()).isTrue();
        assertThat(paths.propertyNames()).allMatch(path -> path.startsWith("/api/"));
        assertThat(operations).isPositive();
        assertThat(undocumented).isEmpty();
    }

    private static boolean hasResponse(JsonNode responses, String statusClass) {
        return responses.propertyNames().stream().anyMatch(code -> code.startsWith(statusClass));
    }

    // Every operation can fail unexpectedly; GlobalExceptionHandler answers with a ProblemDetail
    private static boolean documentsGenericServerError(JsonNode responses) {
        return PROBLEM_DETAIL_REF.equals(responses.at("/500/content/application~1problem+json/schema/$ref").asString(""));
    }

    // Operations without parameters or body (plain listings) cannot fail because of client input
    private static boolean acceptsClientInput(JsonNode operation) {
        return operation.has("requestBody") || (operation.has("parameters") && !operation.get("parameters").isEmpty());
    }
}
