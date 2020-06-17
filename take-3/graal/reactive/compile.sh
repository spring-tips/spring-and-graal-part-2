#!/usr/bin/env bash

ARTIFACT=${1}
MAINCLASS=${2}
VERSION=${3}

JAR="${ARTIFACT}-${VERSION}.jar"

rm -rf target
mkdir -p target/native-image
mvn -ntp package  
rm -f $ARTIFACT
cd target/native-image
jar -xvf ../$JAR  
cp -R META-INF BOOT-INF/classes

LIBPATH=`find BOOT-INF/lib | tr '\n' ':'`
CP=BOOT-INF/classes:$LIBPATH
GRAALVM_VERSION=`native-image --version`

time native-image \
  --verbose \
  -H:EnableURLProtocols=http \
  -H:+RemoveSaturatedTypeFlows \
  -H:Name=$ARTIFACT \
  -Dspring.native.verbose=true \
  -Dspring.native.remove-jmx-support=true \
  -Dspring.native.remove-spel-support=true \
  -Dspring.native.remove-yaml-support=true \
  -cp $CP $MAINCLASS  

 # -Dspring.native.remove-xml-support=true \
