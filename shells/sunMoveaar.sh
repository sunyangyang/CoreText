#!/bin/sh
./gradlew library:assembleRelease
#if [ "$1" = "student" ]
#then
#	rm -rf ~/AndroidRCStudent/base-release/library-release.aar
#	cp ~/AndroidKnowboxBase/library/build/outputs/aar/library-release.aar ~/AndroidRCStudent/libs/library-release.aar
#	echo "student copy done"
#else
	rm -rf ~/AndroidRCTeacher/boxbase/library-release.aar
	cp ~/AndroidKnowboxBase/library/build/outputs/aar/library-release.aar ~/AndroidRCTeacher/library-release/library-release.aar
	echo "teacher copy done"
#fi
