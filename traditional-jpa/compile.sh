#!/usr/bin/env bash

mvn -DskipTests=true clean package && ../compile.sh traditional-jpa com.example.traditional.TraditionalJpaApplication 0.0.1-SNAPSHOT