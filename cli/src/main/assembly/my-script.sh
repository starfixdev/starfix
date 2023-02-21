#!/bin/bash

pom_file="./pom.xml"
project_build_finalName="$(grep -oP '(?<=<artifactId>).*(?=</artifactId>)' pom.xml | head -1)-$(grep -oP '(?<=<version>).*(?=</version>)' pom.xml | head -1)-runner"
file_to_check="./target/$project_build_finalName"
file_to_modify="./src/main/assembly/zip-linux.xml"

# echo $(pwd)
if [ ! -f "$file_to_check" ] && [ ! -x "$file_to_check" ]; then
  if grep -q "<!--" $file_to_modify; then
    sed -i '/<files>/,/<\/files>/{s/<!--\s*//; s/\s*-->//;}' $file_to_modify
    echo "rolling back the descriptor file to default."
    exit 0
  else
    echo "$file_to_check not found or not executable, modifying $file_to_modify"
    sed -i -e '/<files>/,/<\/files>/{/<files>/s/^/\t<!--/;/<\/files>/s/$/-->/}' $file_to_modify
    exit 0
  fi
else
  echo "$file_to_check found executable, no changes made to $file_to_modify"
  exit 0
fi
