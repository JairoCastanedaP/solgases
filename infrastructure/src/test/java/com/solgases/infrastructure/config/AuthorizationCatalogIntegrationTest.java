package com.solgases.infrastructure.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.solgases.application.dto.RolePermissionSeedCatalog;
import com.solgases.application.dto.RolePermissionSeedResult;
import com.solgases.application.port.in.LoadRolePermissionSeedUseCase;
import com.solgases.application.port.out.RolePersistencePort;
import com.solgases.domain.model.Permission;
import com.solgases.infrastructure.SolgasesApplication;
import com.solgases.infrastructure.TestJwtSecret;
import com.solgases.infrastructure.api.rest.AuthenticationController;
import com.solgases.infrastructure.security.PermissionMatrixAuthorizationManager;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * Checks the approved catalog and matrix against the running application: every REST operation except the token
 * operation is covered by the matrix, and the initial roles are loaded idempotently with their permissions.
 */
@SpringBootTest(classes = SolgasesApplication.class)
@AutoConfigureTestDatabase
class AuthorizationCatalogIntegrationTest {

    private static final String REST_PACKAGE = "com.solgases.infrastructure.api.rest";

    @DynamicPropertySource
    static void securityProperties(DynamicPropertyRegistry registry) {
        TestJwtSecret.register(registry);
    }

    @Autowired
    @Qualifier("requestMappingHandlerMapping")
    private RequestMappingHandlerMapping handlerMapping;
    @Autowired
    private PermissionMatrixAuthorizationManager authorizationManager;
    @Autowired
    private LoadRolePermissionSeedUseCase loadRolePermissionSeedUseCase;
    @Autowired
    private RolePermissionSeedCatalog catalog;
    @Autowired
    private RolePersistencePort rolePersistencePort;

    @Test
    void everyRestOperationExceptTheTokenOperationIsCoveredByTheMatrix() {
        String[] everyPermission = InitialRolePermissionCatalog.BUSINESS_PERMISSIONS.toArray(String[]::new);
        TestingAuthenticationToken authentication = new TestingAuthenticationToken("5", null, everyPermission);
        authentication.setAuthenticated(true);
        List<String> operations = new ArrayList<>();
        List<String> uncovered = new ArrayList<>();

        handlerMapping.getHandlerMethods().forEach((info, handler) -> {
            if (!handler.getBeanType().getPackageName().equals(REST_PACKAGE)) {
                return;
            }
            for (String operation : operations(info)) {
                String method = operation.substring(0, operation.indexOf(' '));
                String path = operation.substring(operation.indexOf(' ') + 1);
                if (method.equals("POST") && path.equals(AuthenticationController.TOKEN_PATH)) {
                    continue;
                }
                operations.add(operation);
                MockHttpServletRequest request = new MockHttpServletRequest(method, path.replaceAll("\\{[^}]+}", "1"));
                if (!authorizationManager.authorize(() -> authentication, new RequestAuthorizationContext(request))
                        .isGranted()) {
                    uncovered.add(operation);
                }
            }
        });

        assertThat(uncovered).isEmpty();
        assertThat(operations).hasSize(38);
    }

    private static List<String> operations(RequestMappingInfo info) {
        List<String> operations = new ArrayList<>();
        for (RequestMethod method : info.getMethodsCondition().getMethods()) {
            for (String pattern : info.getPatternValues()) {
                operations.add(method.name() + " " + pattern);
            }
        }
        return operations;
    }

    @Test
    void initialRolesAreLoadedIdempotentlyWithTheirPermissions() {
        // Other test classes may have cleaned the database, so the seed is run once before checking it
        loadRolePermissionSeedUseCase.execute(catalog);

        assertThat(rolePersistencePort.findByKey("ADMIN").orElseThrow().permissions())
                .extracting(Permission::key)
                .containsExactlyInAnyOrderElementsOf(InitialRolePermissionCatalog.BUSINESS_PERMISSIONS);
        assertThat(rolePersistencePort.findByKey("VIEWER").orElseThrow().permissions())
                .extracting(Permission::key)
                .containsExactlyInAnyOrder("CATALOG_READ", "INVENTORY_READ");
        assertThat(rolePersistencePort.findByKey("INVENTORY_OPERATOR")).isEmpty();

        assertThat(loadRolePermissionSeedUseCase.execute(catalog)).isEqualTo(new RolePermissionSeedResult(0, 0));
    }
}
