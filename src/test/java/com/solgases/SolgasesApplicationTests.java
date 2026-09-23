package com.solgases;

import static org.assertj.core.api.Assertions.assertThat;

import com.solgases.category.repository.CategoryRepository;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Verifies that the application context starts without a database.
 * Datasource, JPA and Hibernate auto-configuration are excluded because no entities exist yet
 * and unit-level tests must not depend on MySQL.
 */
@SpringBootTest(properties = {
        "spring.autoconfigure.exclude="
                + "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,"
                + "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration,"
                + "org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration"
})
class SolgasesApplicationTests {

    // Repositories are not created without JPA auto-configuration, so the one required by the services is mocked
    @MockitoBean
    private CategoryRepository categoryRepository;

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        assertThat(context.getBean(OpenAPI.class).getInfo().getTitle()).isEqualTo("SOLGASES API");
    }
}
