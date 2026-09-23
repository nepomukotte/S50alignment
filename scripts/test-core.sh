#!/usr/bin/env sh
set -eu
OUT="${TMPDIR:-/tmp}/s50polaralign-core-test"
mkdir -p "$OUT"
if command -v javac >/dev/null 2>&1; then
  javac -d "$OUT" app/src/main/java/edu/gatech/s50polaralign/core/*.java core-test/CoreWorkflowTest.java
else
  java -m jdk.compiler/com.sun.tools.javac.Main -d "$OUT" app/src/main/java/edu/gatech/s50polaralign/core/*.java core-test/CoreWorkflowTest.java
fi
java -cp "$OUT" CoreWorkflowTest
