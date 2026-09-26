#!/bin/bash
# Build tool for the "PDA (Build)" legacy target in PDA.xcodeproj. Xcode substitutes its own
# $(ACTION) ("build", "clean", ...) as $1 - see PBXLegacyTarget's buildArgumentsString.
set -e
cd "$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/../.."

case "$1" in
  clean)
    ./gradlew :ios:clean
    ;;
  *)
    # robovmInstall: compiles and installs the .app to ios/build/robovm/ without launching
    # anything - the fast "does it still build" loop. Use one of the run-*.sh targets/schemes
    # to also install+launch on a device or in the simulator.
    ./gradlew :ios:robovmInstall
    ;;
esac
