pipeline {
    agent any

    environment {
        BACKEND_IMAGE  = 'minilinkedin-backend'
        // FRONTEND_IMAGE n'est plus nécessaire
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
                    sh 'npm install --legacy-peer-deps'
                    sh 'npm run build'
                }
            }
        }

        stage('Docker Build') {
            steps {
                // On ne construit que l'image backend
                sh "docker build -t ${BACKEND_IMAGE}:${BUILD_NUMBER} ./backend"
                // La ligne de build frontend est SUPPRIMÉE
            }
        }

        stage('Deploy') {
            steps {
                sh 'docker compose down || true'
                sh 'docker compose up -d'
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