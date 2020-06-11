#!/usr/bin/env bash
mvn -DskipTests=true clean package && ../compile.sh traditional com.example.traditional.TraditionalApplication 0.0.1-SNAPSHOT