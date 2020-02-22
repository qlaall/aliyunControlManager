FROM openjdk:8

ADD target/aliyunControlManager-1.0-SNAPSHOT.jar /app/app.jar

CMD ["java","-jar","/app/app.jar"]