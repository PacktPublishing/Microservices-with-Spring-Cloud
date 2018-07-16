#!/usr/bin/env bash

##
## internal helper script to build the maven stuff
##

DIR=`dirname $1`
BUILD_ERROR_LOG=${2=builderrors.log}

cd $DIR

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
