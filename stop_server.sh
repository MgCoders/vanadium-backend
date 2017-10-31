#!/usr/bin/env bash
#/bin/bash
set -x

cd /home/ubuntu/sulfur-backend-deploy
docker-compose -f docker-compose.production.yml kill
