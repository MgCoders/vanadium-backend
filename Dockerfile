FROM jboss/wildfly:10.1.0.Final
ARG DB_HOST
ARG DB_NAME
ARG DB_USER
ARG DB_PASS
ARG LOGSTASH_HOST
ADD ./resources/wildfly-customization /opt/jboss/wildfly/customization/
ADD ./resources/logstash-gelf-1.11.1.tar.gz /opt/jboss/wildfly/modules/system/layers/base
ADD ./resources/postgresql-42.1.4.jar /opt/jboss/
RUN /opt/jboss/wildfly/bin/add-user.sh admin admin --silent
USER root
RUN ["chmod","+x","/opt/jboss/wildfly/customization/execute.sh"]
USER jboss
RUN /opt/jboss/wildfly/customization/execute.sh $DB_HOST $DB_NAME $DB_USER $DB_PASS $LOGSTASH_HOST
RUN rm -rf /opt/jboss/wildfly/standalone/configuration/standalone_xml_history/current
ADD ./target/sulfur.war /opt/jboss/wildfly/standalone/deployments/
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement","0.0.0.0"]
