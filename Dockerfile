FROM maven:3.5-jdk-8 as BUILD
COPY src /usr/src/myapp/src
COPY pom.xml /usr/src/myapp
RUN mvn -f /usr/src/myapp/pom.xml clean package
FROM jboss/wildfly:10.1.0.Final
ADD ./resources/wildfly-customization /opt/jboss/wildfly/customization/
ADD ./resources/wildfly-configuration/standalone.xml /opt/jboss/wildfly/standalone/configuration/
ADD ./resources/logstash-gelf-1.11.1.tar.gz /opt/jboss/wildfly/modules/system/layers/base
ADD ./resources/postgresql-42.1.4.jar /opt/jboss/
RUN /opt/jboss/wildfly/bin/add-user.sh admin admin --silent
USER jboss
COPY --from=BUILD /usr/src/myapp/target/sulfur.war /opt/jboss/wildfly/standalone/deployments/
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement","0.0.0.0"]
