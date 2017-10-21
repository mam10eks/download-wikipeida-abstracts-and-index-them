#!/bin/bash -e

mvn clean install

java -jar target/wikipedia-abstract-index-creator-0.0.1-SNAPSHOT-jar-with-dependencies.jar
