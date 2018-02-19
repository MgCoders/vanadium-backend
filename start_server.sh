#!/usr/bin/env bash
#/bin/bash
set -x
$(aws ecr get-login --region us-east-1  | sed 's/\-e none//g')
docker stack deploy --compose-file=/home/ubuntu/REPLACE_PROJECT_NAME-deploy/docker-compose.testing.yml REPLACE_PROJECT_NAME --with-registry-auth