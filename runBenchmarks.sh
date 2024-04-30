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

# mkdir -p benchmark
# cd benchmark
# rm *.class &>/dev/null
# javac *.java &>/dev/null
# cd ..

# mkdir -p sootOutput
# cd sootOutput
# rm *.class &>/dev/null
# cd ..

javac -cp .:sootclasses-trunk-jar-with-dependencies.jar:benchmark *.java
java -cp .:sootclasses-trunk-jar-with-dependencies.jar:benchmark PA4