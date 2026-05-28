pipeline {
    agent any

    environment {
        // Отключаем демон Gradle в CI, чтобы он не съедал память
        GRADLE_OPTS = '-Dorg.gradle.daemon=false'
        // Явно указываем JAVA_HOME (если нужно)
        // JAVA_HOME = tool('JDK_21')
    }

    stages {
        stage('1. Checkout SCM') {
            steps {
                checkout scm
            }
        }

        stage('2. Compile') {
            steps {
                // Для Windows: bat 'gradlew.bat clean classes testClasses'
                // Для Linux/Mac:
                sh './gradlew clean classes testClasses'
            }
        }

        stage('3. Unit Tests') {
            when {
                // ✅ Правильный синтаксис для веток
                expression { env.GIT_BRANCH ==~ /feature\/.*/ }
            }
            steps {
                sh './gradlew test'
            }
        }

        stage('4. Static Analysis (Checkstyle)') {
            when {
                expression { env.GIT_BRANCH == 'develop' }
            }
            steps {
                sh './gradlew checkstyleMain'
                // ✅ Современный способ записи
                recordIssues enabledForFailure: true, tools: [checkStyle(pattern: '**/build/reports/checkstyle/*.xml')]
            }
        }

        stage('5. Coverage & Gate') {
            steps {
                sh './gradlew test jacocoTestReport jacocoTestCoverageVerification'

                // ✅ Используем современный Coverage Plugin
                recordCoverage(tools: [[parser: 'JACOCO']],
                    qualityGates: [[threshold: 50, metric: 'LINE', unstable: true]])
            }
        }

        stage('6. Build & Archive Artifact') {
            steps {
                sh './gradlew :app:bootJar'
                sh 'mkdir -p ./release-artifacts'
                sh 'cp app/build/libs/*.jar ./release-artifacts/'
                archiveArtifacts artifacts: 'release-artifacts/*.jar', fingerprint: true
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: '**/build/test-results/test/*.xml'
        }
        success {
            echo '🎉 Build successful!'
        }
        failure {
            echo '❌ Build failed!'
        }
    }
}