#!/bin/bash
ARGS="evolve-sc --test --generations 20 --population 50"
JAVA_HOME=/usr/lib/jvm/java-8-oracle ~/netbeans-8.0/java/maven/bin/mvn "-Dexec.args=-classpath %classpath org.clueminer.clustering.benchmark.Main $ARGS" -Dexec.executable=java -Dexec.classpathScope=runtime org.codehaus.mojo:exec-maven-plugin:1.2.1:exec 
