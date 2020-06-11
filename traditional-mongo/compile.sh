#!/usr/bin/env bash
mvn -DskipTests=true clean package && ../compile.sh traditional-mongo com.example.traditional.TraditionalMongoApplication 0.0.1-SNAPSHOT