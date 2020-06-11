#!/usr/bin/env bash
mvn -DskipTests=true clean package && ../compile.sh reactive com.example.reactive.ReactiveApplication 0.0.1-SNAPSHOT

