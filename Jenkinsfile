pipeline {
    agent any

    environment {
        DOCKER_IMAGE_BACKEND = 'insurance-claims-backend'
        DOCKER_IMAGE_FRONTEND = 'insurance-claims-frontend'
        SONAR_HOST = 'http://sonarqube:9000'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                echo 'Source code checked out'
            }
        }

        stage('Build Backend') {
            steps {
                dir('backend') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Test Backend') {
            steps {
                dir('backend') {
                    sh 'mvn test'
                }
            }
            post {
                always {
                    junit 'backend/target/surefire-reports/*.xml'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    dir('backend') {
                        sh """
                            mvn sonar:sonar \
                              -Dsonar.projectKey=insurance-claims-processing \
                              -Dsonar.projectName='Insurance Claims Processing Platform' \
                              -Dsonar.host.url=${SONAR_HOST}
                        """
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Build Frontend') {
            steps {
                dir('frontend') {
                    sh 'npm install'
                    sh 'npm run build'
                }
            }
        }

        stage('Docker Build') {
            steps {
                sh "docker build -f Dockerfile.backend -t ${DOCKER_IMAGE_BACKEND}:${BUILD_NUMBER} ."
                sh "docker build -f Dockerfile.frontend -t ${DOCKER_IMAGE_FRONTEND}:${BUILD_NUMBER} ."
                sh "docker tag ${DOCKER_IMAGE_BACKEND}:${BUILD_NUMBER} ${DOCKER_IMAGE_BACKEND}:latest"
                sh "docker tag ${DOCKER_IMAGE_FRONTEND}:${BUILD_NUMBER} ${DOCKER_IMAGE_FRONTEND}:latest"
            }
        }

        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker-hub-creds',
                                                  usernameVariable: 'DOCKER_USER',
                                                  passwordVariable: 'DOCKER_PASS')]) {
                    sh "docker login -u $DOCKER_USER -p $DOCKER_PASS"
                    sh "docker push ${DOCKER_IMAGE_BACKEND}:${BUILD_NUMBER}"
                    sh "docker push ${DOCKER_IMAGE_FRONTEND}:${BUILD_NUMBER}"
                }
            }
        }

        stage('Deploy') {
            steps {
                sh "docker-compose down || true"
                sh "docker-compose up -d"
                echo "Deployed Insurance Claims Platform build ${BUILD_NUMBER}"
            }
        }
    }

    post {
        success {
            echo "Pipeline completed successfully for build ${BUILD_NUMBER}"
        }
        failure {
            echo "Pipeline failed for build ${BUILD_NUMBER}"
        }
    }
}
