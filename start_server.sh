#!/usr/bin/env bash
#/bin/bash
set -x
echo Logging in to Amazon ECR...
$(aws ecr get-login --region $AWS_DEFAULT_REGION)
cp /home/ubuntu/conf/sulfur-backend-deploy.env /home/ubuntu/sulfur-backend-deploy/.env
cd /home/ubuntu/sulfur-backend-deploy
docker-compose -f docker-compose.production.yml build && docker-compose -f docker-compose.production.yml up -d wildfly
