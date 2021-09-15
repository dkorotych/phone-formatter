#!/usr/bin/env bash

java --add-opens java.base/java.lang=ALL-UNNAMED -jar build/libs/phone-formatter-0.1-all.jar
for file in hs_err_pid*.log; do
  if [ -f "$file" ]; then
    cat hs_err_pid*.log
    break
  fi
done
