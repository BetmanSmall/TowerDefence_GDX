#!/usr/bin/sh 
rm -f desktop/build/libs/* 
./gradlew desktop:dist 
java -jar desktop/build/libs/*