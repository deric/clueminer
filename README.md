# Clueminer

[![Build Status](https://travis-ci.org/deric/clueminer.png?branch=master)](https://travis-ci.org/deric/clueminer)

Clueminer is a platform for interactive data-mining with special focus on clustering algorithms.


## Building latest development version

Prerequisites:

   * git
   * Java 8 or newer
   * Maven


            git clone git://github.com/deric/clueminer.git
            cd clueminer
            git submodule init
            git submodule update
            mvn clean install

   * Once build you can run Clueminer via Maven

    cd modules/application
    mvn nbm:cluster-app nbm:run-platform

   * or use generated bin file

    bash modules/application/target/clueminer/bin/clueminer

### From NetBeans

   1. open the clueminer directory (which is a maven module) -- this module is called `clueminer-parent`
   2. from dependent modules of `clueminer-parent` open `clueminer-app`
   3. execute "Build with dependencies"
   4. now you can run the main application (run module `clueminer-app`)

## How to increase heap size

In `application/src/main/resources/clueminer.conf` adjust Java options:

    default_options="--branding clueminer -J-Xms24m -J-Xmx2048m"

Note: increasing heap size `J-Xmx` to values bigger than is your actual physical RAM will
cause serious preformance issues!

On Unix systems you can find out your memory size with this command:

```bash
echo $(( $(awk '/MemTotal/{print $2}' /proc/meminfo) >> 10 ))m
```

## Forking

 1. click on fork button on github
 2. add upstream repository

  ```
   $ git remote add upstream https://github.com/deric/clueminer.git
  ```
 3. from time to time merge with upstream

  ```
  $ git fetch upstream
  $ git checkout master
  $ git merge upstream/master
  ```

## Benchmarks

Benchmarks of clustering algorithms are located in `modules/clustering-benchmark`

In order to run benchmarks build an asssembly with Maven:

```
$ cd modules/clustering-benchmark
$ mvn assembly:assembly
```

and run benchmarks (might be computationally expensive):

``
$ java -jar target/*-jar-with-dependencies.jar
``

## R support

In order to enable R code execution JRI is needed.

Debian/Ubuntu:
```
apt install r-cran-rjava
```

## HDF support

Library `libjhdf5` is needed to be present on library path.

Debian/Ubuntu:
```
apt install libjhdf5
```

## OpenGL support

For OpenGL visualizations you'll need native extensions

### Debian/Ubuntu

jzy3d is using jogl library which has native bindings:

    sudo apt-get install libjogl-java

## R support

Currently there are two possibilities how to execute R code from Java:

  * TCP connection to `RServe`
  * load dynamic library `libjri.so`
    * for Debian: `apt install r-cran-rjava`, make sure `R_HOME` is set
    * update classpath `export CLASSPATH=.:/usr/lib/R/site-library/rJava/jri/`
