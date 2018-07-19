#!/bin/bash

unamestr=`uname`

if [[ "$unamestr" == 'Linux' ]]; then
    BASEDIR=`dirname $0`
elif [[ "$unamestr" == 'Darwin' ]]; then
    BASEDIR=`dirname $(greadlink -f $0)`
fi
cd $BASEDIR

docker-compose up -d --build rabbitmq zipkin redis redisCommander mysqlbm mysqlra mysqlus

