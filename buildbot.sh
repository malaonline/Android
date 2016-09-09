#!/bin/sh
set -e

# gradle build
cp /opt/keys-pros/*.properties ./

java -version
javac -version
gradle --version
gradle clean
# only build apk for parents. currently not build apk for teachers.
gradle assembleDevrelease assemblePrdrelease assembleStagerelease
gradle testParentDevreleaseUnitTest testParentStagereleaseUnitTest testParentPrdreleaseUnitTest
