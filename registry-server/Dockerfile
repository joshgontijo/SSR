FROM tomee:8-jdk-7.0.0-plus
RUN rm -rf /usr/local/tomee/webapps/*
ADD registry-server.war /usr/local/tomee/webapps/ROOT.war
EXPOSE 8080

#FROM jboss/wildfly
#ADD target/registry-server.war /opt/jboss/wildfly/standalone/deployments/registry-server.war
#EXPOSE 8080