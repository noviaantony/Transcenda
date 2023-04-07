#!/bin/bash

# Save the private key to a temporary file
echo "${BASTION_SSH_PRIVATE_KEY}" > bastion_key
chmod 600 bastion_key

# SSH into Bastion Host
ssh -i "bastion_key" -o "StrictHostKeyChecking=no" -t "${EC2_INSTANCE_USERNAME}@${BASTION_EC2_PUBLIC_IP}" << 'EOF'
  # Perform any required actions on the Bastion Host
  cd /etc
  # Get the list of running instances
  instances=$(aws ec2 describe-instances --filters "Name=instance-state-name,Values=running" "Name=tag:Name,Values=transcenda_backend_final" --query 'Reservations[].Instances[].InstanceId' --output text)

  for instance in $instances; do
    # Get the instance's private IP address
    instance_ip=$(aws ec2 describe-instances --instance-ids $instance --query 'Reservations[].Instances[].PrivateIpAddress' --output text)

    # SSH into the instance and run the deploy.sh script
    sudo ssh -o StrictHostKeyChecking=no -i "Transcenda_backend_final_Key.pem" "ubuntu@${instance_ip}" << 'INNER_EOF'
          # Perform any required actions on the target instance
          cd /etc

          # Run deploy.sh
          bash ./deploy.sh

          # Wait for the application to start (modify the sleep time as needed)
          sleep 30

          # Check the health of the Spring Boot application
          health_check_url="http://localhost:80/api/v1/transcenda/health" # Replace with your application's health check API URL
          max_retries=5
          retries=0

          while [ $retries -lt $max_retries ]; do
            response=$(curl -s -o /dev/null -w "%{http_code}" $health_check_url)

            if [ $response -eq 200 ]; then
              echo "Application is healthy on $instance_ip"
              echo "Waiting for 10 seconds before deploying to the next instance..."
                sleep 10
              break
            else
              echo "Health check failed on $instance_ip. Retrying in 10 seconds..."
              retries=$((retries + 1))
              sleep 10
            fi
          done

          if [ $retries -eq $max_retries ]; then
            echo "Application health check failed on $instance_ip after $max_retries attempts. Please check the instance."
          fi

          # Exit target instance
          exit
INNER_EOF
  done

# Exit Bastion
EOF
