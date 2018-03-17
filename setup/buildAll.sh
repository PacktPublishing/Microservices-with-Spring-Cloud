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

usage(){
  cat << EOF
usage: $0 options

This builds all services or services of a given type.

OPTIONS:
   -h      Show this message
   -t      type to install like INFRA or SERVICE, optional
EOF
exit
}

while getopts "t:h" opt; do
  case $opt in
    t)
      TYPE=$OPTARG
      ;;
    h)
      usage
      ;;
    \?)
      echo "Invalid option: -$OPTARG" >&2
      usage
      ;;
  esac
done

echo Searching for pom files in `$BASEDIR/..`
find .. -name pom.xml -exec ./helper_buildIn.sh {} $BASEDIR/builderrors.log $TYPE \;

echo "Done compiling!"