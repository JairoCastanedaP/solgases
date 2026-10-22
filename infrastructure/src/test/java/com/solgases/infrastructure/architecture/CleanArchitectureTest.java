package com.solgases.infrastructure.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * Dependency rules between the domain, application and infrastructure modules (ADR-0006).
 * Runs from infrastructure because it is the only module whose test classpath contains the three of them.
 * Only production classes are imported; test classes and test libraries are not analyzed.
 */
@AnalyzeClasses(packages = "com.solgases", importOptions = ImportOption.DoNotIncludeTests.class)
class CleanArchitectureTest {

    private static final String DOMAIN = "com.solgases.domain..";
    private static final String APPLICATION = "com.solgases.application..";
    private static final String INFRASTRUCTURE = "com.solgases.infrastructure..";

    @ArchTest
    static final ArchRule domainDoesNotDependOnApplicationOrInfrastructure = noClasses()
            .that().resideInAPackage(DOMAIN)
            .should().dependOnClassesThat().resideInAnyPackage(APPLICATION, INFRASTRUCTURE);

    // Allow-list instead of a list of forbidden frameworks: Spring, Jakarta Persistence, Hibernate, Jackson,
    // Lombok or any other library is rejected without naming it. Only java.* (java..) is allowed; other JDK
    // packages such as javax.*, jdk.* or sun.* are rejected as well.
    @ArchTest
    static final ArchRule domainDependsOnlyOnItselfAndJavaPackages = classes()
            .that().resideInAPackage(DOMAIN)
            .should().onlyDependOnClassesThat().resideInAnyPackage(DOMAIN, "java..");

    // spring-tx (@Transactional on use cases) is a documented exception in application and is not checked here
    @ArchTest
    static final ArchRule applicationDoesNotDependOnInfrastructure = noClasses()
            .that().resideInAPackage(APPLICATION)
            .should().dependOnClassesThat().resideInAPackage(INFRASTRUCTURE);

    // Allowed directions: infrastructure -> application -> domain, and infrastructure -> domain
    @ArchTest
    static final ArchRule dependenciesPointTowardsTheDomain = layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer("Domain").definedBy(DOMAIN)
            .layer("Application").definedBy(APPLICATION)
            .layer("Infrastructure").definedBy(INFRASTRUCTURE)
            .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer()
            .whereLayer("Application").mayOnlyBeAccessedByLayers("Infrastructure")
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure");
}
