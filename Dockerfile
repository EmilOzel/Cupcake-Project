FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S cupcake && adduser -S cupcake -G cupcake
WORKDIR /app
COPY --chown=cupcake:cupcake target/lifehack.jar /app/app.jar
USER cupcake
EXPOSE 7070
ENTRYPOINT ["java", "-Xms24m", "-Xmx64m", "-XX:MaxMetaspaceSize=64m", "-XX:ReservedCodeCacheSize=24m", "-Xss512k", "-jar", "/app/app.jar"]
