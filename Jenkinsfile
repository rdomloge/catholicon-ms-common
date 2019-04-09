def CONTAINER_IP
def IMAGE

pipeline {
    agent any
    
    
    environment {
		registry = "rdomloge/catholicon-ms-common"
		registryCredential = 'rdomloge'
	}

    stages {
        stage ('Initialize') {
            steps {
            	if (git_log.contains('[maven-release-plugin]')) {
					currentBuild.result = 'ABORTED'
					return
				}
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                '''
            }
        }

        stage ('Build') {
            steps {
                sh 'mvn -Dmaven.test.skip=true package'
            }
        }
        
        stage('Unit tests') {
            steps {
                sh 'mvn test'
			}           
            post {
                success {
                    junit 'target/surefire-reports/**/*.xml'
                }
            }
		}
    	
		stage('Tag & Release') {
			when{
			    branch "master"
			}

		    steps {
		        script {
		            sh "mvn -Dmaven.test.skip=true release:clean release:prepare release:perform -B"
		        }

		    }
		    post {
				failure {
			    	script {
						sh 'cat ~/.ssh/id_rsa.pub'
			    	}
		        }
    		}
		}
	}
}
