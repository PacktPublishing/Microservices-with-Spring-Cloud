#!/usr/bin/env bash

unamestr=`uname`

if [[ "$unamestr" == 'Linux' ]]; then
    BASEDIR=`dirname $0`
elif [[ "$unamestr" == 'Darwin' ]]; then
    BASEDIR=`dirname $(greadlink -f $0)`
fi
cd $BASEDIR

echo "Stopping: $1"
cd ../$1/target
if [ -f "application.pid" ]; then
echo "Found pid file for $1"
pkill -F application.pid
sleep 1
if [ -f "application.pid" ]; then
    if pgrep -F "application.pid" > /dev/null
    then
        echo "Still running -- forcing kill"
        pkill -9 -F application.pid
    else
        echo "Removing pid file"
        rm application.pid
    fi
fi
else
 echo "Not running"
fi
