#!/usr/bin/env bash
# Runs maestro/flows/register_to_prologue.yaml against the real dev backend with a
# fresh fake-test-<timestamp>@artux.net account, then deletes that account
# (DELETE api/v1/user/delete) whether the flow passed or failed - it doesn't run
# on every PR (see the flow's own header), so nothing else cleans these up.
#
# Usage: maestro/scripts/run_register_to_prologue.sh
# Requires: maestro CLI on PATH, an already-booted/connected device.
set -u

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
FLOW="$SCRIPT_DIR/../flows/register_to_prologue.yaml"
BASE_URL="${BASE_URL:-https://dev.artux.net/pdanetwork/}"

TIMESTAMP=$(date +%s)
TEST_EMAIL="fake-test-${TIMESTAMP}@artux.net"
TEST_PASSWORD="TestPass${TIMESTAMP}!"

echo "Running $FLOW as $TEST_EMAIL"
maestro test -e TEST_EMAIL="$TEST_EMAIL" -e TEST_PASSWORD="$TEST_PASSWORD" "$FLOW"
FLOW_EXIT_CODE=$?

echo "Deleting test account $TEST_EMAIL"
DELETE_HTTP_STATUS=$(curl -sS -o /dev/null -w "%{http_code}" -X DELETE \
    -u "${TEST_EMAIL}:${TEST_PASSWORD}" \
    "${BASE_URL}api/v1/user/delete")

if [ "$DELETE_HTTP_STATUS" = "200" ]; then
    echo "Test account deleted."
else
    # Non-fatal: e.g. registration itself never got far enough to create the
    # account. Surfaced so it isn't silently left behind, but doesn't change
    # FLOW_EXIT_CODE - a cleanup hiccup shouldn't mask a real flow failure/pass.
    echo "WARNING: account deletion returned HTTP $DELETE_HTTP_STATUS (may be expected if registration itself failed)."
fi

exit $FLOW_EXIT_CODE
