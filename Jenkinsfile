pipeline {
    agent any

    environment {
        // Отключаем демон Gradle в CI, чтобы он не съедал память
        GRADLE_OPTS = '-Dorg.gradle.daemon=false'
    }

    stages {
        stage('1. Checkout SCM') {
            steps {
                // a. Получение исходного кода
                checkout scm
            }
        }

        stage('2. Compile') {
            steps {
                // b. Компиляция кода и тестов
                // ВНИМАНИЕ: Если Jenkins запущен на Windows БЕЗ Docker, замени sh на bat, а ./gradlew на gradlew.bat
                sh './gradlew clean classes testClasses'
            }
        }

        stage('3. Unit Tests') {
            when {
                // c. Только для веток feature/XXX
                branch 'feature/*'
            }
            steps {
                sh './gradlew test'
            }
        }

        stage('4. Static Analysis (Checkstyle)') {
            when {
                // d. Только для ветки develop
                branch 'develop'
            }
            steps {
                // Генерируем XML отчет для Jenkins
                sh './gradlew checkstyleMain'
                // Собираем предупреждения в UI Jenkins
                recordIssues enabledForFailure: true, tool: checkStyle(pattern: '**/build/reports/checkstyle/*.xml')
            }
        }

        stage('5. Coverage & Gate') {
            steps {
                // e. Измерение покрытия
                sh './gradlew jacocoTestReport'
                // f. Проверка критериев (упадет, если < 50%)
                sh './gradlew jacocoTestCoverageVerification'

                // Публикация отчета в UI Jenkins
                jacoco execPattern: '**/*.exec', sourcePattern: '**/src/main/java'
            }
        }

        stage('6. Publish to Maven Local') {
            steps {
                // g. Инсталяция в локальный репозиторий (выполнится, если предыдущие шаги SUCCESS)
                sh './gradlew publishToMavenLocal'
            }
        }

        stage('7. Build & Archive Artifact') {
            steps {
                // h. Сборка Fat JAR и копирование в папку
                sh './gradlew bootJar'
                sh 'mkdir -p ./release-artifacts'
                sh 'cp app/build/libs/*.jar ./release-artifacts/'

                // Архивация артефакта в самом Jenkins (будет доступен для скачивания из UI)
                archiveArtifacts artifacts: 'release-artifacts/*.jar', fingerprint: true
            }
        }
    }

    post {
        always {
            // Сохраняем отчеты тестов и покрытия даже если сборка упала
            junit allowEmptyResults: true, testResults: '**/build/test-results/test/TEST-*.xml'
        }
    }
}