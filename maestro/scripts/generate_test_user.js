// Unique credentials per run, so re-running the flow never collides with a
// previously registered account on the real dev backend.
var stamp = Date.now();
output.email = "fake-test-" + stamp + "@artux.net";
output.password = "TestPass" + stamp + "!";
