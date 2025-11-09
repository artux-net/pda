# Stalker PDA
[![Google Play](https://img.shields.io/badge/Google_Play-414141?style=flat-square&logo=google-play&logoColor=white)](https://play.google.com/store/apps/details?id=net.artux.pda&hl=ru)
[![Downloads](https://img.shields.io/badge/downloads-50K+-success?style=flat-square)](https://play.google.com/store/apps/details?id=net.artux.pda&hl=ru)

Stalker PDA is an Android game built with the [libGDX](https://libgdx.com) framework. The game features a tactical gameplay experience with quests and interactive map system.

## Overview

PDA (Personal Digital Assistant)  is a strategy/tactical game that combines RPG elements with real-time combat mechanics. Players navigate through various maps, manage stalkers, complete quests, and interact with different gangs in a persistent game world.

## Features

- **Quest System**: Story-driven quests with Lua scripting support
- **Real-time Combat**: Tactical battles using Box2D physics engine
- **Dynamic Map System**: Explore various locations with spawn points and interactive elements
- **Gang Relations**: Complex relationship system between different factions
- **Character Management**: Manage stalkers with unique attributes and equipment
- **News & Articles**: In-game news system to track events

## Technology Stack

### Core Technologies
- **Kotlin** Primary programming language
- **libGDX** Game development framework
- **Ashley** Entity Component System (ECS)
- **Box2D** Physics engine for combat and movement

### Additional Libraries
- **Dagger 2** - Dependency injection
- **Kotlin Coroutines** - Asynchronous programming
- **LuaJ** - Lua scripting engine
- **Gson** - JSON serialization
- **Firebase** - To collect analytics

## Project Structure

```
pda/
├── app/              # Android application module
├── core/             # Core game logic and engine
├── model/            # Data models and domain objects
├── assets/           # Game assets (sprites, sounds, etc.)
└── .github/          # CI/CD workflows and automation
```

## Building the Project

### Build Commands

#### Android (Debug)
```bash
./gradlew app:assembleDebug
```

#### Android (Release)
```bash
./gradlew app:assembleRelease
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

## Contact

For questions or support, please contact the development team through the GitHub repository issues.

---

**Note**: This is a game in development. Features and APIs may change without notice.
