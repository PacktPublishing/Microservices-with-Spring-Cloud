#!/usr/bin/env bash

unamestr=`uname`

if [[ "$unamestr" == 'Linux' ]]; then
    BASEDIR=`dirname $0`
elif [[ "$unamestr" == 'Darwin' ]]; then
    BASEDIR=`dirname $(greadlink -f $0)`
fi
cd $BASEDIR

docker-compose down

DIRS=(configserver serviceregistry reverseproxy bookmarks users)
for i in "${DIRS[@]}"
do
  ./stopService.sh $i
done
