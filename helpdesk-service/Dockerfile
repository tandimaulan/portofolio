FROM git.voltras.net:5005/devops/repo/maven:3.9.4-sapmachine-21 AS buildmaven

ENV TZ=Asia/Jakarta
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN mkdir -p /home/app
COPY src /home/app/src
COPY pom.xml /home/app
COPY ci_settings.xml /home/app

WORKDIR /home/app
RUN mvn -s ci_settings.xml clean package

FROM git.voltras.net:5005/devops/repo/java:21-jdk-ubuntu
ARG PROJECT_NAME
ENV PROJECT_NAME=$PROJECT_NAME

COPY --from=buildmaven /home/app/target/*.jar target/$PROJECT_NAME.jar
EXPOSE 8080
CMD ["sh", "-c", "java -jar target/${PROJECT_NAME}.jar"]
