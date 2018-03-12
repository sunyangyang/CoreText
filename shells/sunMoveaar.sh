#!/bin/sh
./gradlew library:assembleRelease
#if [ "$1" = "student" ]
#then
	rm -rf ~/AndroidRCStudent/libs/library-release.aar
	cp ~/AndroidKnowboxBase/library/build/outputs/aar/library-release-0.1.aar ~/AndroidRCStudent/libs/library-release-0.1.aar

	rm -rf ~/AndroidRCStudent/dotread/libs/library-release.aar
	cp ~/AndroidKnowboxBase/library/build/outputs/aar/library-release-0.1.aar ~/AndroidRCStudent/dotread/libs/library-release-0.1.aar

	rm -rf ~/AndroidRCStudent/commons/libs/library-release.aar
	cp ~/AndroidKnowboxBase/library/build/outputs/aar/library-release-0.1.aar ~/AndroidRCStudent/commons/libs/library-release-0.1.aar
	echo "student copy done"
#else
#	rm -rf ~/AndroidRCTeacher/boxbase/library-release.aar
#	cp ~/AndroidKnowboxBase/library/build/outputs/aar/library-release.aar ~/AndroidRCTeacher/library-release/library-release.aar
#	echo "teacher copy done"
#fi
