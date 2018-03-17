#!/usr/bin/env bash

##
## internal helper script to build the maven stuff
##

DIR=`dirname $1`
BUILD_ERROR_LOG=${2=builderrors.log}
TYPE=$3

cd $DIR
#When filter is set and file is not present skip dir
if [ -n "${TYPE+set}" ]; then
    if [ ! -f "$TYPE.marker" ]; then
        echo "$TYPE.marker not present in $DIR, skipping"
        exit 0
    fi
    echo "$TYPE.marker present in $DIR, compiling!"
fi

mvn clean install -DskipTests -DskipITs

EXIT=$?
if [ $EXIT -eq 0 ]
then
  echo "Success!"
else
  echo "Logging error for $DIR" >&2
  #logging errors is necessary because this is called in a find -exec which doesn't stop on errors
  #and your shell history is most likely not big enough, so you can see where to look for errors
  echo "`date +%H:%M:%S` : Error while building $DIR" >> $BUILD_ERROR_LOG
fi
exit $EXIT
