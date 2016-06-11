FROM tomcat
ADD service-registry.war /usr/local/tomcat/webapps/service-registry.war
EXPOSE 8080