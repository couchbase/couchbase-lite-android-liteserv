#!/bin/bash

THIS_DIR=`dirname $0`

if [[ ! $1 ]] ; then echo "usage:  $0  build_number, like 1.0-1234" ; exit 99 ; fi

REVISION=$1

pushd ${THIS_DIR}  2>&1 > /dev/null

mvn --settings ./settings.xml --quiet -DREVISION=${REVISION} clean package

popd               2>&1 > /dev/null
echo ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ done making android_zipfile
