#!/usr/bin/env bash
# Runs maestro/flows/register_to_prologue.yaml against the real dev backend with a
# fresh fake-test-<timestamp>@artux.net account. The account is intentionally left
# behind afterwards (not deleted here) so it stays log-in-able for the rest of the
# testing session, e.g. to keep reproducing a bug after the app crashes mid-flow -
# pdanetwork's own daily TestUserCleanupJob sweeps up anything matching this email
# pattern (fake-test-*@artux.net, e2e-test-*@example.com) once it's 2+ hours old.
#
# Usage: maestro/scripts/run_register_to_prologue.sh
# Requires: maestro CLI installed, an already-booted/connected device.
set -u

# The Gradle daemon (and other non-login shells) often don't inherit the PATH
# entry the official installer (get.maestro.mobile.dev) adds to shell rc
# files, so `maestro` can be missing here even though it works in a terminal.
export PATH="$PATH:$HOME/.maestro/bin"

if ! command -v maestro >/dev/null 2>&1; then
    echo "ERROR: maestro CLI not found (checked PATH and ~/.maestro/bin)." >&2
    echo "Install it with: curl -Ls \"https://get.maestro.mobile.dev\" | bash" >&2
    exit 127
fi

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
FLOW="$SCRIPT_DIR/../flows/register_to_prologue.yaml"

TIMESTAMP=$(date +%s)
TEST_EMAIL="fake-test-${TIMESTAMP}@artux.net"
TEST_PASSWORD="TestPass${TIMESTAMP}!"

echo "Running $FLOW as $TEST_EMAIL"
maestro test -e TEST_EMAIL="$TEST_EMAIL" -e TEST_PASSWORD="$TEST_PASSWORD" "$FLOW"
FLOW_EXIT_CODE=$?

echo "Account left in place for continued testing: $TEST_EMAIL / $TEST_PASSWORD"
echo "(auto-deleted by pdanetwork's daily TestUserCleanupJob once idle 2+ hours)"

exit $FLOW_EXIT_CODE
