pipeline {
    // Initial single-user lab pipeline. Move builds to a dedicated agent before accepting untrusted PRs.
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    tools {
        maven 'Maven 3.9'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Compile') {
            steps {
                sh 'java -version'
                sh 'mvn -B clean -DskipTests compile'
            }
        }

        stage('Tests') {
            steps {
                sh 'mvn -B test'
            }
        }

        stage('Package and coverage') {
            steps {
                sh 'mvn -B -DskipTests verify'
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
            archiveArtifacts allowEmptyArchive: true, fingerprint: true,
                artifacts: 'infrastructure/target/infrastructure-0.0.1-SNAPSHOT.war,infrastructure/target/site/jacoco-aggregate/**'
        }
    }
}
