#!/bin/sh
#./gradlew library:assembleRelease
./gradlew -p library clean build uploadArchives
#if [ "$1" = "student" ]
#then
#	rm -rf ~/AndroidRCStudent/libs/library-release.aar
#	cp ~/AndroidKnowboxBase/library/build/outputs/aar/library-release.aar ~/AndroidRCStudent/libs/library-release-0.2.aar
#
#	rm -rf ~/AndroidRCStudent/dotread/libs/library-release.aar
#	cp ~/AndroidKnowboxBase/library/build/outputs/aar/library-release.aar ~/AndroidRCStudent/dotread/libs/library-release-0.2.aar
#
#	rm -rf ~/AndroidRCStudent/commons/libs/library-release.aar
#	cp ~/AndroidKnowboxBase/library/build/outputs/aar/library-release.aar ~/AndroidRCStudent/commons/libs/library-release-0.2.aar
#	echo "student copy done"
#else
#	rm -rf ~/AndroidRCTeacher/library-release/library-release.aar
#	cp ~/AndroidKnowboxBase/library/build/outputs/aar/library-release.aar ~/AndroidRCTeacher/library-release/library-release-0.2.aar
#	echo "teacher copy done"
#fi
