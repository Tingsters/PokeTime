PokeTime
====================

Workshop built using JavaFX and the very finest retro 8-bit graphics for teaching kids programming.

See [PokeTimePraesentation.pdf](PokeTimePraesentation.pdf) for instructions on the project.

## Build

Currently PokeTime is built with OpenJDK 11 (depending on OpenJFX) and a recent Maven version.

Build with 
```
mvn package
```

## Run

PokeTime can be run as a standalone fat-jar. However, the pi4j integration needs some environment variables to
work properly on a Raspberry Pi 2b, so we included them in a start script:

```bash
./start
```

For simplicity the build command is included in the start script.

## Parts

These parts were used for our setup:

```bash
Raspberry Pi 3
TSL2561
ADX345
Pi Breakout Board

Breadboard
Connectors
```


Happy hacking!
