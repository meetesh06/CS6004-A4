#!/bin/bash

export JAVA_HOME=/jenkins/openj9/openj9-openjdk-jdk8/build/linux-x86_64-normal-server-release/images/j2sdk-image
export PATH=$JAVA_HOME/bin:$PATH

JAVA_VER=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | awk -F '.' '{sub("^$", "0", $2); print $1$2}')

if [ "$JAVA_VER" -eq 18 ]; then
    echo "USING JAVA VERSION: $JAVA_VER"
else
    echo "UNSUPPORTED JAVA VERSION: $JAVA_VER, expected Java 18."
    exit 1    
fi

# set -e

echo "(>)[Compiling PA4]"
rm -rf *.class
javac -cp .:sootclasses-trunk-jar-with-dependencies.jar *.java
echo "(/>)[PA4 COMPILED SUCCESSFULLY]"

for folder in benchmarks/*/; do
  # Extract folder name from the full path
  folder_name=$(basename "$folder")
  echo "(>)[Processing benchmark: $folder_name]"

  rm -rf $folder/transformedClasses &>/dev/null
  mkdir $folder/transformedClasses &>/dev/null
  rm -rf $folder/*.class &>/dev/null
  rm $folder/OUTPUT* &>/dev/null
  rm $folder/*.csv &>/dev/null
  rm $folder/*.png &>/dev/null

  echo "static,virtual" >$folder/final_call_siteinfo.csv

  echo "  (>)[Compiling benchmark]"
  javac -cp $folder $folder/*.java &>$folder/OUTPUT_benchmark_compilation
  echo "  (/)[Compilation Successful]"

  echo "  (>)[Running time benchmark for baseline program]"
  for i in {1..10}; do
    { time java -Xint -cp $folder Harness ; } 2>>$folder/OUTPUT_baseline_time
  done
  echo "  (/)[Successful]"

  echo "  (>)[Collecting baseline statistics]"
  ENABLE_INSTR=1 java -Xint -cp $folder Harness >/dev/null 2>$folder/OUTPUT_baseline_stat_raw
  python3 helpers/cumulateResults.py $folder/OUTPUT_baseline_stat_raw &>> $folder/final_call_siteinfo.csv
  echo "  (/)[Successful]"

  echo "  (>)[Transforming classes > $folder/transformedClasses]"
  java -cp .:sootclasses-trunk-jar-with-dependencies.jar PA4 $folder $folder/transformedClasses &>$folder/OUTPUT_transformation
  echo "  (/)[Transformation Successful]"

  echo "  (>)[Running time benchmark for transformed program]"
  for i in {1..10}; do
    { time java -Xint -cp $folder/transformedClasses Harness ; } 2>>$folder/OUTPUT_transformed_time
  done
  echo "  (/)[Successful]"

  echo "  (>)[Collecting transformed statistics]"
  ENABLE_INSTR=1 java -Xint -cp $folder/transformedClasses Harness >/dev/null 2>$folder/OUTPUT_transformed_stat_raw
  python3 helpers/cumulateResults.py $folder/OUTPUT_transformed_stat_raw &>> $folder/final_call_siteinfo.csv
  echo "  (/)[Successful]"

  echo "  (>)[Generating final_times.csv]"
  python3 helpers/cumulateTimes.py $folder/OUTPUT_baseline_time $folder/OUTPUT_transformed_time $folder/final_times.csv
  echo "  (/)[Successful]"

  rm -rf $folder/*.class
done

