[Русский](README.md) | **English** | [Українська](README.uk.md)

# Rubik

[![Build](https://github.com/mag1cmaxx/BCasesRubik/actions/workflows/build.yml/badge.svg)](https://github.com/mag1cmaxx/BCasesRubik/actions/workflows/build.yml)
[![Release](https://img.shields.io/github/v/release/mag1cmaxx/BCasesRubik?label=version&color=blue)](https://github.com/mag1cmaxx/BCasesRubik/releases/latest)
[![Downloads](https://img.shields.io/github/downloads/mag1cmaxx/BCasesRubik/total?color=blue)](https://github.com/mag1cmaxx/BCasesRubik/releases)
![Paper](https://img.shields.io/badge/Paper-1.21.4%2B-2e7d32)
![Java](https://img.shields.io/badge/Java-21-b07219)

An addon for [BCases](https://github.com/By1337/BCases): a 3×3×3 Rubik's cube appears above the case and solves itself.

https://github.com/user-attachments/assets/a96428cb-94e8-4213-95d5-bef07db20eeb

1. The pieces fly in and snap together into a solved cube.
2. The cube spins in the air and scrambles itself. Every layer turn is fully animated.
3. Then it solves itself back. The last few turns are slower.
4. The solved cube flashes, bursts into pieces and leaves the prize inside.

The cube is made of 81 block displays: 27 black pieces and 54 stickers. Everything is sent as packets through VirtualEntityApi, nothing is placed in the world. The prize is picked in advance, like in the other BCases animations.

## Requirements

- Paper 1.21.4 or newer
- BCases 1.2
- VirtualEntityApi that supports your server version

## Installation

1. Download `BCasesRubik-<version>.jar` from the [releases](https://github.com/mag1cmaxx/BCasesRubik/releases/latest).
2. Put it into `plugins/BCases/addons/`.
3. Restart the server or run `/bcases reload`.
4. The file `plugins/BCases/animations/rubik.yml` appears, the animation id is `rubik`.

To try it, look at the case block and run `/bcases play rubik <prize set>`.

## Configuration

Every option is described in `rubik.yml` (the comments there are in Russian). Any option can be removed, the default value is used then.

| Option | Default | What it does |
|---|---|---|
| `height` | 2.3 | height of the cube center above the bottom of the case block |
| `size` | 0.45 | size of one piece in blocks, the whole cube is three times bigger |
| `spin` | 3.0 | rotation speed of the whole cube, degrees per tick |
| `tilt` | 30 | tilt, so the top face is visible |
| `scramble.moves` | 20 | number of turns while scrambling |
| `scramble.ticks` | 2 | ticks per turn while scrambling |
| `solve.ticks` | 4 | ticks per turn while solving |
| `solve.final_ticks` / `solve.final_moves` | 10 / 3 | slow down for the last turns |
| `show_ticks` | 80 | how many ticks the prize is shown |
| `colors.*` | concrete | blocks for the faces and pieces, any block works |

The whole animation takes about `moves × (scramble.ticks + solve.ticks)` ticks plus around 5 seconds for assembling, the flash and the prize.

## Building

```
mvn clean package
```

Java 21 is required. The jar is placed in `target/`.
