#build
FROM maven:3.8-eclipse-temurin-21 AS build
WORKDIR /Solvinery
COPY . .
RUN cd dev/Backend && mvn clean generate-sources package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /Solvinery/dev/Backend/target/*.jar app.jar

RUN apt-get update && apt-get install -y \
    libblas3 \
    libboost-program-options1.83.0 \
    libboost-serialization1.83.0 \
    libcliquer1 \
    libgfortran5 \
    liblapack3 \
    libmetis5 \
    libopenblas0 \
    libtbb12 \
    libmpfr6 \
    libquadmath0 \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

#copy and install SCIP, delete deb file afterwards
COPY SCIPOptSuite-9.2.0-Linux-ubuntu24.deb /tmp/
RUN dpkg -i /tmp/SCIPOptSuite-9.2.0-Linux-ubuntu24.deb \
    && rm /tmp/SCIPOptSuite-9.2.0-Linux-ubuntu24.deb

ENV PORT=4000
EXPOSE ${PORT}

CMD ["java", "-jar", "app.jar"]