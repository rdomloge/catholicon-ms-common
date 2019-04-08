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

        
	    stage('Publish') {
      		steps {
      			// To push to Docker hub
    			//withDockerRegistry([ credentialsId: "ef879a02-b51a-49bb-a743-58f46dd8b4c8", url: "" ]) {
          		//	sh 'docker push rdomloge/catholicon-ms-common'
        		//}
        		
        		// To push to local registry
        		script{
	        		docker.withRegistry('https://localhost:5000') {
				        //sh 'docker push rdomloge/catholicon-ms-common'
				        IMAGE.push();
				    }
			    }
      		}
    	}
    	
		stage('Tag & Release') {
			when{
			    branch "master"
			}

		    steps {
		        script {
		            sh "mvn -Dmaven.test.skip=true -DpushChanges=false -DpreparationGoals=initialize release:clean release:prepare release:perform -B"
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
