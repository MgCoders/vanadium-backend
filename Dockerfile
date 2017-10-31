FROM jboss/wildfly:10.1.0.Final
ADD ./resources/wildfly-configuration/wildfly-conf.tar.gz /opt/jboss/wildfly/standalone
ADD ./resources/logstash-gelf-1.11.1.tar.gz /opt/jboss/wildfly/modules/system/layers/base
ADD ./resources/wildfly-configuration/postgresql-42.1.4.module.tar.gz /opt/jboss/wildfly/modules
RUN /opt/jboss/wildfly/bin/add-user.sh admin admin --silent
USER jboss
COPY ./resources/deployments/sulfur.war /opt/jboss/wildfly/standalone/deployments/sulfur.war
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement","0.0.0.0"]
