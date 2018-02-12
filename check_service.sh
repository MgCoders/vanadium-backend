#!/usr/bin/env bash
#/bin/bash
set -x
cd /home/ubuntu/jee-REPLACE_PROJECT_NAME-deploy
docker-compose --file=docker-compose.production.yml ps -q | xargs docker inspect -f '{{ .State.ExitCode }}' | while read code; do  
    if [ "$code" == "1" ]; then    
       exit -1
    fi
done
