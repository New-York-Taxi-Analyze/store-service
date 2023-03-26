FROM eclipse-temurin:17-jdk

COPY core/build/libs/core-0.0.1-SNAPSHOT.jar core.jar

ENTRYPOINT ["java","-jar","/core.jar"]
