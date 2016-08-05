FROM amdatu/amdatu-rti-java8
COPY build/libs/vertx-example-3.3.2-fat.jar /app/app.jar
WORKDIR /app
EXPOSE 8080
CMD java -Xmx128m -Djava.net.preferIPv4Stack=true -jar app.jar