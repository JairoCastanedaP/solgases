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
        stage('Verify') {
            steps {
                sh 'java -version'
                sh 'mvn -B clean verify'
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
