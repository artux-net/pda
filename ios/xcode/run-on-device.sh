#!/bin/bash
# Build tool for the "PDA (Run on Device)" legacy target: builds, installs and launches on
# whichever iOS device MobiVM finds connected - same as running this from a terminal.
set -e
cd "$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/../.."
./gradlew :ios:launchIOSDevice
