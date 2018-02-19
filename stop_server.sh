#!/usr/bin/env bash
#/bin/bash
set -x

cd /home/ubuntu/jee-REPLACE_PROJECT_NAME-deploy
docker-compose -f docker-compose.production.yml kill
