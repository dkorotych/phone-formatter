#!/usr/bin/env bash

java $JAVA_OPTS -jar build/libs/phone-formatter-0.1-all.jar
for file in hs_err_pid*.log; do
  if [ -f "$file" ]; then
    cat hs_err_pid*.log
    break
  fi
done
