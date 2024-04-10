FROM eclipse-temurin:17-jdk-alpine  as builder
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM eclipse-temurin:17-jdk-alpine
COPY --from=builder dependencies/ ./
COPY --from=builder snapshot-dependencies/ ./
COPY --from=builder spring-boot-loader/ ./
COPY --from=builder application/ ./
#RUN rm /BOOT-INF/classes/application.properties
#COPY src/main/resources/application.properties_prd /BOOT-INF/classes/application.properties
#RUN mv /BOOT-INF/classes/application.properties  /BOOT-INF/classes/application.properties_dev
#RUN mv /BOOT-INF/classes/application.properties_prd  /BOOT-INF/classes/application.properties
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
EXPOSE 8080