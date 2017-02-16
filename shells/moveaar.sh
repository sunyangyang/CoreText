#!/bin/sh
rm -rf ~/devsoft/projects/android/AndroidRCTeacher/boxbase/library-release.aar
rm -rf ~/devsoft/projects/android/AndroidRCTeacher/coretext/coretext.aar
cp ~/devsoft/projects/android/AndroidKnowboxBase/library/build/outputs/aar/library-release.aar ~/devsoft/projects/android/AndroidRCTeacher/boxbase/library-release.aar
cp ~/devsoft/projects/android/AndroidKnowboxBase/coretext/coretext.aar ~/devsoft/projects/android/AndroidRCTeacher/coretext/coretext.aar
