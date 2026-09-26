#!/bin/bash
# Build tool for the "PDA (Run in Simulator)" legacy target.
set -e
cd "$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/../.."
./gradlew :ios:launchIPhoneSimulator
