FROM tomcat:8-jr8
ADD service-registry.war /usr/local/tomcat/webapps/service-registry.war
EXPOSE 8080