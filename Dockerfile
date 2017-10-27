FROM jboss/wildfly:10.1.0.Final
ADD ./resources/wildfly-customization /opt/jboss/wildfly/customization/
RUN /opt/jboss/wildfly/bin/add-user.sh admin admin --silent
USER root
RUN ["chmod","+x","/opt/jboss/wildfly/customization/execute.sh"]
RUN ["chmod","+x","/opt/jboss/wildfly/customization/deploy.sh"]
USER jboss
RUN /opt/jboss/wildfly/customization/execute.sh
ADD ./target/sulfur.war .
RUN /opt/jboss/wildfly/customization/deploy.sh
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement","0.0.0.0"]
