FROM ubuntu:24.04

RUN apt-get update && apt-get upgrade -y


RUN apt-get install -y \
    default-jre \
    default-jdk \
    build-essential \
    nodejs \
    npm \
    curl \
    maven \
    dbus \
    wget

RUN apt-get install -y \
    libblas3 \
    libboost-program-options1.83.0 \
    libboost-serialization1.83.0 \
    libcliquer1 \
    libgfortran5 \
    liblapack3 \
    libmetis5 \
    libopenblas0 \
    libtbb12 \
    iputils-ping
    



WORKDIR /Solvinery
COPY . .
RUN dpkg -i SCIPOptSuite-9.2.0-Linux-ubuntu24.deb
RUN cd /Solvinery/dev/Frontend && npm install
RUN cd /Solvinery/dev/Backend && mvn compile
RUN cd /Solvinery/dev/Backend && mvn generate-sources

RUN chmod +x ./scripts/containerEntryScript.sh
ENTRYPOINT ["./scripts/containerEntryScript.sh"]

EXPOSE 3000 4000
