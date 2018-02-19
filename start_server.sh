#!/usr/bin/env bash
#/bin/bash
set -x
echo Logging in to Amazon ECR...
$(aws ecr get-login --region us-east-1)
cd /home/ubuntu/jee-REPLACE_PROJECT_NAME-deploy
cp ../conf/sulfur-backend-deploy.env /home/ubuntu/jee-REPLACE_PROJECT_NAME-deploy/.env
docker-compose -f docker-compose.production.yml pull && docker-compose -f docker-compose.production.yml up -d
