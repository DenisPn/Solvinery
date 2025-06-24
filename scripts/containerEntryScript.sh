#! /bin/bash

cd /Solvinery/dev/Backend && mvn generate-sources
cd /Solvinery
exec /bin/bash
java -jar target/*.jar
