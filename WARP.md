# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview

Northstar Redux is a Minecraft mod that adds space exploration, planetary dimensions, and space technology to Minecraft. Built on Minecraft Forge 1.20.1, it integrates with the Create mod and GeckoLib for animations. The mod features custom gravity systems, oxygen mechanics, space suits, rockets, and multiple planetary dimensions (Mars, Venus, Moon, Mercury) with unique biomes, creatures, and resources.

## Development Commands

### Building and Running
```bash
# Build the mod
.\gradlew build

# Run the mod in development (client)
.\gradlew runClient

# Run the mod in development (server) 
.\gradlew runServer

# Generate data (recipes, loot tables, etc.)
.\gradlew runData

# Clean build artifacts
.\gradlew clean
```

### Code Quality
```bash
# Check for issues (no dedicated linting setup detected)
.\gradlew check

# Refresh dependencies
.\gradlew --refresh-dependencies
```

## Core Architecture

### Package Structure
- **com.lightning.northstar** - Root package
  - **block/** - All block implementations (crops, simple blocks, tech blocks)
    - **tech/** - Complex technological blocks (machines, engines, computers)
    - **simple/** - Basic decorative and functional blocks  
    - **crops/** - Plant and agricultural blocks
    - **entity/** - Block entities for tile entity functionality
  - **item/** - Items, armor, tools, and enchantments
  - **entity/** - Living entities (Mars worms, Venus creatures, etc.)
  - **contraptions/** - Create mod integration for rockets and moving structures
  - **world/** - Dimension, biome, and world generation features
  - **client/** - Client-side rendering, models, and effects
  - **config/** - Configuration management
  - **content/** - Registration of all mod content (registries)
  - **data/** - Data generation and fuel system
  - **mixin/** - Mixins for core game modifications (gravity, oxygen, temperature)

### Key Systems

**Gravity System**: Custom gravity values per dimension (Mars: 0.37g, Venus: 0.89g, etc.) implemented via mixins affecting entities, projectiles, and falling blocks.

**Oxygen & Temperature**: Environmental systems that affect player survival, integrated with space suits and life support equipment.

**Create Integration**: Extensive integration with Create mod for kinetic power, contraptions, and rocket systems. Uses Create's Registrate system.

**GeckoLib Integration**: Animated models for entities and some block entities using GeckoLib animation framework.

**Multi-Dimensional**: Custom planetary dimensions with unique world generation, biomes, structures, and creatures.

## Key Technologies & Dependencies

- **Minecraft Forge 1.20.1** (47.4.0) - Mod framework
- **Create Mod** (6.0.6-205) - Kinetic systems and contraptions
- **GeckoLib** (4.7.2) - Entity and block entity animations  
- **Architectury** - Multi-platform development tools
- **MixinExtras** - Advanced mixin capabilities
- **Ponder** - In-game documentation system
- **JEI** - Recipe viewing integration

## Development Patterns

### Registration System
Uses Create's Registrate system extensively. All content registration happens in the `content/` package through static registration methods called from the main mod constructor.

### Mixin Usage
Heavy use of mixins for core game modifications:
- Gravity modifications for all entity types
- Oxygen and temperature systems affecting living entities
- Block behavior modifications for environmental effects
- Client-side rendering modifications

### Recipe System
Custom recipe types for mod-specific machines:
- Electrolysis recipes for splitting compounds
- Engraving recipes for circuit creation
- Freezing recipes for ice box functionality

### Data Generation
Automated data generation for configured features and damage types. Run `.\gradlew runData` when adding new world generation features or damage types.

## Contributing Guidelines

- Use IntelliJ IDEA (mentioned in README)
- Work off the default branch unless changes are branch-specific
- Keep commits focused and descriptive
- Test changes thoroughly - ensure code compiles and features work as expected
- For large changes, discuss first via GitHub issues or Discord
- Always test mod compatibility with required dependencies (Create, GeckoLib)

## Platform Information

- **Target Minecraft Version**: 1.20.1
- **Platform**: Forge (Fabric planned when Create-Fabric updates to 6.0)
- **Java Version**: 17
- **Planned Updates**: 1.21.1 support, data-driven planets, oxygen system redesign