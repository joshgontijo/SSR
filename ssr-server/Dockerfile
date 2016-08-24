FROM openjdk:8
ADD target/ssr-server-swarm.jar /ssr-server.jar
EXPOSE 9000
ENTRYPOINT ["java", "-jar", "ssr-server.jar"]