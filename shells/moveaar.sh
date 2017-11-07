#!/bin/sh
./gradlew library:assembleRelease
if [ "$1" = "student" ]
then
	rm -rf ~/devsoft/projects/android/AndroidRCStudent/base-release/library-release.aar
	cp ~/devsoft/projects/android/AndroidKnowboxBase/library/build/outputs/aar/library-release.aar ~/devsoft/projects/android/AndroidRCStudent/base-release/library-release.aar
	echo "student copy done"
else
	rm -rf ~/devsoft/projects/android/AndroidRCTeacher/boxbase/library-release.aar
	cp ~/devsoft/projects/android/AndroidKnowboxBase/library/build/outputs/aar/library-release.aar ~/devsoft/projects/android/AndroidRCTeacher/library-release/library-release.aar
	echo "teacher copy done"
fi
