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

        stage('SonarQube analysis') {
            steps {
                withSonarQubeEnv('SonarQube SOLGASES') {
                    sh 'mvn -B org.sonarsource.scanner.maven:sonar-maven-plugin:sonar'
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    script {
                        def qualityGate = waitForQualityGate(abortPipeline: false)
                        echo "SonarQube Quality Gate: ${qualityGate.status}"
                        if (qualityGate.status != 'OK') {
                            unstable("SonarQube Quality Gate: ${qualityGate.status}")
                        }
                    }
                }
            }
        }

        stage('Deploy to WebLogic') {
            steps {
                sshagent(credentials: ['weblogic-vm-ssh']) {
                    withCredentials([file(credentialsId: 'weblogic-known-hosts', variable: 'WEBLOGIC_KNOWN_HOSTS')]) {
                        sh '''
                            set -eu

                            WAR_FILE="$WORKSPACE/infrastructure/target/infrastructure-0.0.1-SNAPSHOT.war"
                            REMOTE="jairo@192.168.122.148"
                            REMOTE_DIR="/home/jairo/deploy/solgases"
                            KNOWN_HOSTS_ARGS="-o BatchMode=yes -o StrictHostKeyChecking=yes -o UserKnownHostsFile=$WEBLOGIC_KNOWN_HOSTS"

                            test -f "$WAR_FILE"

                            ssh $KNOWN_HOSTS_ARGS "$REMOTE" "mkdir -p '$REMOTE_DIR' && chmod 700 '$REMOTE_DIR'"
                            scp $KNOWN_HOSTS_ARGS "$WAR_FILE" "$REMOTE:$REMOTE_DIR/infrastructure.war"
                            scp $KNOWN_HOSTS_ARGS "$WORKSPACE/deployment/weblogic_deploy.py" "$REMOTE:$REMOTE_DIR/deploy.py"

                            ssh $KNOWN_HOSTS_ARGS "$REMOTE" \
                                "test -r /home/jairo/.wlst/solgases-userconfig.secure && \
                                 test -r /home/jairo/.wlst/solgases-userkey.secure && \
                                 WLS_USER_CONFIG=/home/jairo/.wlst/solgases-userconfig.secure \
                                 WLS_USER_KEY=/home/jairo/.wlst/solgases-userkey.secure \
                                 WLS_ADMIN_URL=t3://localhost:7001 \
                                 WLS_APP_NAME=solgases \
                                 WLS_APP_TARGET=AdminServer \
                                 WLS_WAR_PATH='$REMOTE_DIR/infrastructure.war' \
                                 /home/jairo/Oracle/Middleware/Oracle_Home/oracle_common/common/bin/wlst.sh \
                                 '$REMOTE_DIR/deploy.py'"
                        '''
                    }
                }
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
