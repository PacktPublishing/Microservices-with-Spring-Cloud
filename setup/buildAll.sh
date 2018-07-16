#!/usr/bin/env bash

unamestr=`uname`

##
## This script builds all maven projects located in ..
##

if [[ "$unamestr" == 'Linux' ]]; then
    BASEDIR=`dirname $0`
elif [[ "$unamestr" == 'Darwin' ]]; then
    BASEDIR=`dirname $(greadlink -f $0)`
fi
cd $BASEDIR

echo Searching for pom files in `$BASEDIR/..`
find .. -name pom.xml -exec ./helper_buildIn.sh {} $BASEDIR/builderrors.log \;

echo "Done compiling!"