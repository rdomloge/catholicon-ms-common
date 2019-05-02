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
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                '''
            }
        }

        stage ('Build') {
            steps {
            	script{
            		result = sh (script: "git log -1 | grep '\\[maven-release-plugin\\]'", returnStatus: true) 
					if (result != 0) {
						echo "Performing build..."
		                sh 'mvn -Dmaven.test.skip=true package'
					} else {
	            		echo 'Ignoring release change - not running'
						currentBuild.result = 'ABORTED'
						return
					}
            	}
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
			    changelog '^((?!maven-release-plugin).)*$'
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
		
		stage('Starting downstream jobs') {
			steps {
			    build job: 'catholicon-ms-seasons'
				build job: 'catholicon-ms-leagues'
			}
		}

	}
}
