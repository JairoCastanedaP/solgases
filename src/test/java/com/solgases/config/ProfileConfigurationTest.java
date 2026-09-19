package com.solgases.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

class ProfileConfigurationTest {

    private static Properties load(String file) {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(new ClassPathResource(file));
        Properties properties = factory.getObject();
        assertThat(properties).as(file).isNotNull();
        return properties;
    }

    @ParameterizedTest
    @CsvSource({
            "local, update, true",
            "dev, update, true",
            "qa, validate, true",
            "prd, validate, false"
    })
    void profileDefinesSchemaPolicyAndSwaggerVisibility(String profile, String ddlAuto, boolean swaggerEnabled) {
        Properties properties = load("application-" + profile + ".yml");

        assertThat(properties.getProperty("spring.jpa.hibernate.ddl-auto")).isEqualTo(ddlAuto);
        assertThat(properties.getProperty("springdoc.swagger-ui.enabled")).isEqualTo(String.valueOf(swaggerEnabled));
        assertThat(properties.getProperty("springdoc.api-docs.enabled")).isEqualTo(String.valueOf(swaggerEnabled));
    }

    @Test
    void baseConfigurationReadsCredentialsFromEnvironmentOnly() {
        Properties properties = load("application.yml");

        assertThat(properties.getProperty("spring.datasource.username")).isEqualTo("${DB_USERNAME}");
        assertThat(properties.getProperty("spring.datasource.password")).isEqualTo("${DB_PASSWORD}");
    }
}
