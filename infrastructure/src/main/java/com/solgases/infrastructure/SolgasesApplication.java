package com.solgases.infrastructure;

import com.solgases.infrastructure.cli.CredentialCommand;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.solgases")
public class SolgasesApplication {

    public static void main(String[] args) {
        SpringApplication application = application(args);
        if (application.getWebApplicationType() == WebApplicationType.NONE) {
            // A local credential command: it exits with the command's result once it finishes
            System.exit(SpringApplication.exit(application.run(args)));
        }
        application.run(args);
    }

    /** Local credential commands start no web server: no Tomcat, servlet filters or HTTP security. */
    public static SpringApplication application(String[] args) {
        SpringApplication application = new SpringApplication(SolgasesApplication.class);
        if (CredentialCommand.isRequested(args)) {
            application.setWebApplicationType(WebApplicationType.NONE);
        }
        return application;
    }
}
