# Clueminer

[![Build Status](https://travis-ci.org/deric/clueminer.png?branch=master)](https://travis-ci.org/deric/clueminer)

Clueminer is a platform for interactive data-mining with special focus on clustering algorithms.


## Building latest development version

Prerequisites:

   * git
   * java (6 or newer)
   * maven


            git clone git://github.com/deric/clueminer.git
            cd clueminer
            git submodule init
            git submodule update
            mvn install

### From NetBeans

   1. open the clueminer directory (which is a maven module) -- this module is called `clueminer-parent`
   2. from dependent modules of `clueminer-parent` open `clueminer-app`
   3. execute "Build with dependencies"
   4. now you can run the main application (run module `clueminer-app`)

## How to increase heap size

In `application/src/main/resources/clueminer.conf` adjust Java options:

    default_options="--branding clueminer -J-Xms24m -J-Xmx2048m"