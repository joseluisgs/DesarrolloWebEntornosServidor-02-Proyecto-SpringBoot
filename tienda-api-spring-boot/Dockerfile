# Etapa de compilación, un docker especifico, que se etiqueta como build
FROM gradle:jdk25-alpine AS build

# Directorio de trabajo
WORKDIR /app

# Copia los archivos build.gradle y src de nuestro proyecto
COPY build.gradle.kts .
COPY gradlew .
COPY gradle gradle
COPY src src

# Compila y construye el proyecto, podemos evitar los test evitando con -x test
# Para fijar un perfil de compilación, se usa la instrucción
# RUN ./gradlew build -Dspring.profiles.active=dev
RUN ./gradlew build

# Etapa de ejecución, un docker especifico, que se etiqueta como run
# Con una imagen de java, solo neceistamos el jre
# FROM openjdk:17-jdk AS run
FROM eclipse-temurin:25-jre-alpine AS run

# Directorio de trabajo
WORKDIR /app

# Copia el jar de la aplicación, ojo que esta en la etapa de compilación, etiquetado como build
# Cuidado con la ruta definida cuando has copiado las cosas en la etapa de compilación
# Para copiar un archivo de una etapa a otra, se usa la instrucción COPY --from=etapaOrigen
COPY --from=build /app/build/libs/*SNAPSHOT.jar /app/my-app.jar

# Expone el puerto 3000
EXPOSE 3000

# Ejecuta el jar
# Para lanzarlo con un perfil distinto al fijado por defecto, se usa la instrucción
# ENTRYPOINT ["java","-jar","-Dspring.profiles.active=dev","/app/my-app.jar"]
# Para lanzarlo con un perfil distinto al fijado por defecto, se usa la instrucción
# ENTRYPOINT ["java","-jar","/app/my-app.jar"]
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod","/app/my-app.jar"]