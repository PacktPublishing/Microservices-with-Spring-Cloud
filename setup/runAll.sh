#!/usr/bin/env bash

unamestr=`uname`

if [[ "$unamestr" == 'Linux' ]]; then
    BASEDIR=`dirname $0`
elif [[ "$unamestr" == 'Darwin' ]]; then
    BASEDIR=`dirname $(greadlink -f $0)`
fi
cd $BASEDIR

docker-compose up -d

DIRS=(configserver serviceregistry reverseproxy bookmarks users)
for i in "${DIRS[@]}"
do
  echo ""
  echo "############################"
  echo "Make sure $i is stopped"
  ./stopService.sh $i
  echo "Start: $i"
  cd ../$i
  mvn clean install  -DskipTests -DskipITs
  cd target
  echo "Starting $i"
  ./*.jar start 2>run.log >run.log &
  #give the service a little bit of time
  sleep 5
  echo "Done starting $i"
  cd $BASEDIR
done
