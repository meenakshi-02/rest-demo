// Define the start of the Declarative Pipeline
pipeline {
    // Agent: Specifies where the pipeline should run. 'any' means Jenkins will use any available executor (e.g., the Jenkins master server).
    agent any
    
    // START OF TOOLS BLOCK
    // tools {
    //     // Use the names defined in Global Tool Configuration
    //     jdk 'JDK_21' 
    //     maven 'M3_SYS_INSTALL' 
    // }
    
    // Environment: Define variables accessible throughout the pipeline.
    environment {
        // --- AWS Configuration ---
        AWS_ACCOUNT_ID = '637423534623'
        AWS_REGION = 'ap-south-1'
        ECR_REPO = 'my-serav-app'
        // ECR_URI: Constructs the full registry path required by Docker commands.
        ECR_URI = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO}"
        
        // --- EC2 Deployment Host Configuration ---
        EC2_HOST = '65.0.108.253'     // Public IP of the target deployment EC2 instance.
        EC2_USER = 'ec2-user'         // Default username for Amazon Linux AMI.
        // SSH_KEY_CREDENTIAL: The ID of the private key stored securely in Jenkins Credentials.
        SSH_KEY_CREDENTIAL = 'my-app-ec2-ssh-key' 
    }
    
    // Stages: Logical blocks that group steps (tasks) in the CI/CD process.
    stages {
        stage('1. Build & Test') {
    steps {
        script {
            def M2_HOME = tool 'M3_SYS_INSTALL'
            def JAVA_HOME = tool 'JDK_21'
            
            withEnv([
                "PATH+MAVEN=${M2_HOME}/bin",
                "PATH+JAVA=${JAVA_HOME}/bin"
            ]) {
                echo "Running build with PATH including: ${M2_HOME}/bin and ${JAVA_HOME}/bin"
                
                // --- DIAGNOSTIC STEPS ---
                // 1. Check for file permissions and ownership
                sh "ls -ld ${M2_HOME} ${M2_HOME}/bin ${M2_HOME}/bin/mvn"
                
                // 2. Check for missing dynamic dependencies (a library issue)
                // If this command shows 'No such file or directory' it means the executable itself
                // is misconfigured for the current agent's Linux distribution (e.g., missing glibc).
                sh "ldd ${M2_HOME}/bin/mvn || echo 'ldd failed (file likely not a dynamic executable)'"
                
                // 3. Attempt to run using the full, explicit path
                sh "${M2_HOME}/bin/mvn clean package -DskipTests" 
                // --- END DIAGNOSTIC STEPS ---
            }
        }
    }
}
        
        stage('2. Docker Build & Push to ECR') {
            steps {
                script {
                    // --- ECR Login using Jenkins EC2 IAM Role ---
                    // This command uses the attached IAM Role to get a temporary password for ECR.
                    sh "aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
                    
                    // Docker Build: Builds the container image using the Dockerfile in the workspace.
                    // -t: Tags the image with the full ECR URI and 'latest' tag.
                    sh "docker build -t ${ECR_URI}:latest ."
                    
                    // Push to ECR: Uploads the tagged local image to the remote AWS ECR repository.
                    sh "docker push ${ECR_URI}:latest"
                }
            }
        }
        
        stage('3. Deploy on EC2') {
            steps {
                echo "Deploying image ${ECR_URI}:latest to ${EC2_HOST}..."
                
                // sshagent: Exposes the private key stored in Jenkins to the environment.
                sshagent([SSH_KEY_CREDENTIAL]) {
                    // sh: Executes a multi-line script block using the private key.
                    sh """
                    # ssh: Connects to the remote EC2 host using the ec2-user and the injected private key.
                    # -o StrictHostKeyChecking=no: Bypasses the prompt to save the host key (simplifies automation).
                    ssh -o StrictHostKeyChecking=no ${EC2_USER}@${EC2_HOST} '
                    
                    echo "Pulling latest image..."
                    # The deployment EC2 instance must *also* run this login command 
                    # and requires its own IAM Role with ECR ReadOnly access to pull the image.
                    aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com
                    docker pull ${ECR_URI}:latest

                    echo "Stopping old container..."
                    # docker stop/rm: Stops and removes the previous version of the container.
                    # || true: Ensures the pipeline doesn't fail if the container doesn't exist yet (first deployment).
                    docker stop my-serav-app || true
                    docker rm my-serav-app || true

                    echo "Running new container..."
                    # docker run: Starts the new container from the pulled image.
                    # -d: Runs in detached mode (background).
                    # --name: Assigns a fixed name for easy management.
                    # -p 8080:8080: Maps container port 8080 to host port 8080.
                    docker run -d --name my-serav-app -p 8080:8080 ${ECR_URI}:latest
                    '
                    """
                }
            }
        }
    }
}
