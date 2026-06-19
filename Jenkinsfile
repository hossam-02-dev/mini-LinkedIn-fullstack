pipeline {
    agent any

    environment {
        BACKEND_IMAGE  = 'minilinkedin-backend'
        FRONTEND_IMAGE = 'minilinkedin-frontend'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Backend JAR') {
            steps {
                dir('backend') {
                    sh 'chmod +x mvnw'
                    sh './mvnw clean package -DskipTests'
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
                dir('backend') {
                    sh "docker build -t ${BACKEND_IMAGE}:${BUILD_NUMBER} ."
                }
                dir('frontend') {
                    sh "docker build -t ${FRONTEND_IMAGE}:${BUILD_NUMBER} ."
                }
            }
        }

        stage('Deploy') {
            steps {
                dir('backend') {
                    sh 'docker compose down || true'
                    sh 'docker compose up -d'
                }
            }
        }
    }

    post {
        success {
            echo "Pipeline réussi - Build ${BUILD_NUMBER}"
        }
        failure {
            echo "Echec du pipeline - Build ${BUILD_NUMBER}"
        }
    }
}