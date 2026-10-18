#!/bin/bash
# Builds compiler.jar and source.zip.
# Usage: ./build.sh /path/to/SaM-2.6.3.jar
# The SaM classes are bundled into compiler.jar so `java -jar compiler.jar in.lo out.sam` works standalone.
set -e
SAM_JAR="${1:-SaM-2.6.3.jar}"
[ -f "$SAM_JAR" ] || { echo "SaM jar not found: $SAM_JAR  (usage: ./build.sh /path/to/SaM-2.6.3.jar)"; exit 1; }
rm -rf out && mkdir -p out
javac -cp "$SAM_JAR" -d out src/assignment2/*.java
(cd out && jar xf "../$SAM_JAR")                # bundle SaM library (fat jar)
rm -rf out/META-INF/*.SF out/META-INF/*.RSA out/META-INF/*.DSA
printf 'Main-Class: assignment2.LiveOak2Compiler\n' > manifest.txt
jar cfm compiler.jar manifest.txt -C out .
rm -f source.zip && zip -qr source.zip src build.sh tests README.md
echo "Built compiler.jar and source.zip"
