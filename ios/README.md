# iOS (core-only, mocked data)

Runs `core`'s game/map logic on a physical iOS device via RoboVM/MobiVM. There's no
account/login/network layer here - `net.artux.pda.ios.mock.MockDataFactory` hand-builds
the `GameMap`/`StoryModel`/`StoryDataModel`/`ItemsContainerModel`/`Properties` that the
Android app normally gets from a real registered account and passes into `CoreFragment`
via `Intent` extras (see `QuestActivity.startMap()`). `MockPlatformInterface` just logs
instead of talking to a backend.

`core` itself needed zero changes - it's already fully platform-agnostic (no Android
imports anywhere in that dependency chain, confirmed while wiring this up).

## Status

Everything that doesn't require a full Xcode install has been verified on this machine
(Command Line Tools only, no Xcode.app active via `xcode-select`):

- `./gradlew :ios:tasks` - MobiVM plugin resolves and applies correctly
- `./gradlew :ios:compileJava` - **succeeds**: `IOSLauncher`, `MockPlatformInterface`,
  `ConsoleApplicationLogger`, `MockDataFactory` all compile against the real
  `robovm-rt`/`robovm-cocoatouch`/`gdx-backend-robovm`/`gdx-controllers-ios` jars - every
  import, constructor signature and method call in this module is real, not guessed
- `./gradlew :ios:dependencies --configuration natives` - `gdx-platform`,
  `gdx-box2d-platform`, `gdx-freetype-platform` all resolve a `natives-ios` classifier
- `./gradlew :ios:robovmInstall` gets past config parsing, resource bundling and Java
  compilation, and fails only at **code signing**:
  `No provisioning profile and signing identity found that matches bundle ID 'net.artux.pda'`

That's as far as this can go without Xcode. To finish:

1. `xcode-select -p` currently points at Command Line Tools, not the installed
   `/Applications/Xcode.app` - switch it: `sudo xcode-select --switch /Applications/Xcode.app`
   (needs your password; not something to run unattended).
2. Open Xcode once, sign in with your Apple ID (Settings > Accounts), and let it manage
   a development signing certificate + provisioning profile for bundle ID `net.artux.pda`
   (or change the bundle ID in `robovm.xml`/`Info.plist.xml` to one under your own team).
3. Connect your device via USB (or over Wi-Fi once paired) and trust this computer on it.
4. `./gradlew :ios:launchIOSDevice`

## robovm.xml notes

- No `<libs>` section: as of gdx 1.12.1 the natives-ios jars ship prebuilt
  `.xcframework`s plus their own `robovm.xml` fragment, which the MobiVM gradle plugin
  merges in automatically (verified by inspecting those jars' `META-INF/robovm/ios/`
  contents directly) - the old `<libs>libgdx.a</libs>`-style entries from
  older libGDX RoboVM templates don't apply to this version and were removed.
- `gdx-controllers-ios` is the exception: it's a plain, pre-xcframework class jar with
  no bundled fragment, so its frameworks (`GameController`, `CoreBluetooth`,
  `CoreMotion`) are listed by hand. `core`'s `GamepadInputSystem` polls
  `Controllers.getCurrent()` unconditionally every frame, so this dependency (and its
  `forceLinkClasses` entry - `Controllers.initialize()` looks up its platform manager
  by reflection, which RoboVM's dead-code stripping would otherwise drop) isn't optional.

## Not verified (needs Xcode/a device to find out)

- Whether the bundled `.xcframework`s actually link and run correctly at the native
  level - only their presence/packaging was inspected, not an actual link.
- `IOSApplicationConfiguration.orientationLandscape`/`.orientationPortrait` are real
  fields (compiled cleanly against the real `gdx-backend-robovm` jar), but their runtime
  effect wasn't observed.
- Whether the mocked data in `MockDataFactory` is actually enough to render a full map
  and run gameplay systems without crashing - checked several code paths that read
  `Properties`/`StoryDataModel.avatar` at map-load time specifically because they'd NPE
  on an incomplete mock (see the comments in `MockDataFactory`/`robovm.xml`), but there
  could be others further into actual gameplay that weren't reachable through static
  reading alone.

## On-device: confirmed launching (net.artux.pda.flow entry point)

`./gradlew :ios:launchIOSDevice` now builds, installs and launches on a real iPhone
without crashing. It previously died immediately on startup with:

```
java.lang.ExceptionInInitializerError
  at okhttp3.OkHttpClient.<init>(...)
Caused by: java.lang.ArrayIndexOutOfBoundsException: length=0; index=0
  at okhttp3.internal.platform.Platform$Companion.isConscryptPreferred(Platform.kt:202)
```

Cause: robovm-rt's own `java/security/security.properties` registers 5 security
providers (`com.android.org.conscrypt.OpenSSLProvider`,
`org.apache.harmony.security.provider.cert.DRLCertFactory`,
`com.android.org.bouncycastle.jce.provider.BouncyCastleProvider`,
`org.apache.harmony.security.provider.crypto.CryptoProvider`,
`com.android.org.conscrypt.JSSEProvider`) purely via `Class.forName()` inside
`java.security.Security`'s static init - RoboVM's static analysis can't see that
reflective load, so all 5 get stripped and `Security.getProviders()` comes back empty.
`FlowApiClient`'s `OkHttpClient` (built as soon as `PdaFlowGame` is constructed) hits
that empty array in `Platform.isConscryptPreferred()` before a single screen renders.
Fixed by force-linking those 3 packages in `robovm.xml` (see its `forceLinkClasses`).
