# vanadium

## Development

* Start Wildfly + DB:
 
    `docker-compose -f docker-compose.development.yml up -d`
    
    or 
    
    `sh start_dev_server.sh`
* Refresh app 

    `mvn package`
    
* Status

    `http://host:port/vanadium/api/status`
    

## Configuration

* Regenerate standalone.xml

    `docker-compose -f docker-compose.updateconf.yml up`
    
## Production

* 
