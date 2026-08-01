FROM amazoncorretto:17
COPY ./target/semProject.jar /tmp
WORKDIR /tmp
ENTRYPOINT ["java", "-jar", "semProject.jar", "db:3306", "5000"]