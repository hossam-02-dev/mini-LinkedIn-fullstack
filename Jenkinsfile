stage('Build Frontend') {
    steps {
        dir('frontend') {
            sh 'npm cache clean --force'
            sh 'npm install --legacy-peer-deps'
            sh 'npm run build'
        }
    }
}