# PDA Network

PDA Network is an Android game built with the libGDX framework. The game features a tactical gameplay experience with gang relations, quests, and a dynamic map system.

## Overview

PDA (Personal Digital Assistant) Network is a strategy/tactical game that combines RPG elements with real-time combat mechanics. Players navigate through various maps, manage stalkers, complete quests, and interact with different gangs in a persistent game world.

## Features

- **Dynamic Map System**: Explore various locations with spawn points and interactive elements
- **Gang Relations**: Complex relationship system between different factions
- **Quest System**: Story-driven quests with Lua scripting support
- **Character Management**: Manage stalkers with unique attributes and equipment
- **Real-time Combat**: Tactical battles using Box2D physics engine
- **News & Articles**: In-game news system to track events
- **Multiplayer Support**: Network-enabled gameplay features
- **Cross-platform**: Android and Desktop support

## Technology Stack

### Core Technologies
- **Kotlin** 2.0.0 - Primary programming language
- **libGDX** 1.12.1 - Game development framework
- **Ashley** 1.7.4 - Entity Component System (ECS)
- **Box2D** - Physics engine for combat and movement

### Android
- **Target SDK**: 34
- **Minimum SDK**: 26
- **Gradle**: 8.9.0

### Additional Libraries
- **Dagger 2** - Dependency injection
- **Kotlin Coroutines** - Asynchronous programming
- **LuaJ** 3.0.1 - Lua scripting engine
- **Gson** - JSON serialization
- **Google AdMob** - Advertisement integration

## Project Structure

```
pda/
├── app/              # Android application module
├── core/             # Core game logic and engine
├── model/            # Data models and domain objects
├── desktop/          # Desktop launcher (for testing)
├── buildSrc/         # Build configuration and versioning
├── assets/           # Game assets (sprites, sounds, etc.)
└── .github/          # CI/CD workflows and automation
```

## Building the Project

### Prerequisites
- JDK 17 or higher
- Android SDK with API level 34
- Gradle 8.x (wrapper included)

### Build Commands

#### Android (Debug)
```bash
./gradlew app:assembleDebug
```

#### Android (Release)
```bash
./gradlew app:assembleRelease
```

#### Desktop (for testing)
```bash
./gradlew desktop:run
```

### Running Tests
```bash
./gradlew test
```

## Development Setup

1. Clone the repository:
```bash
git clone https://github.com/artux-net/pda.git
cd pda
```

2. Initialize submodules (if any):
```bash
git submodule update --init --recursive
```

3. Open the project in Android Studio or IntelliJ IDEA

4. Sync Gradle and build the project

## CI/CD

The project uses GitHub Actions for continuous integration and deployment:

- **Beta Build**: Automatically builds and deploys to Google Play Beta on push to master
- **Preview Build**: Creates preview builds for pull requests
- **Tag Build**: Creates release builds when tags are pushed
- **Test**: Runs automated tests on pull requests

## Modules

### App Module
The Android application entry point containing:
- Activities and UI components
- Android-specific implementations
- Resource files and assets

### Core Module
Game logic and engine implementation:
- ECS (Entity Component System) components
- Map and spawn management
- Battle system
- Quest engine
- Notification system

### Model Module
Data models and domain objects:
- Game state models
- User and gang data structures
- Quest and item models
- Map structures

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is proprietary software. All rights reserved.

## Contact

For questions or support, please contact the development team through the GitHub repository issues.

---

**Note**: This is a game in active development. Features and APIs may change without notice.
