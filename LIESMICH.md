PokeTime
=================

Der Workshop nutzt JavaFX und die feinsten Retro 8-Bit Grafiken um Kindern das Programmieren
beizubringen.

In [PokeTimePraesentation.pdf](PokeTimePraesentation.pdf) sind Anleitungen für das Projekt zu
finden.

## Aufbau

Zurzeit nutzt Poketime OpenJDK 11 und eine aktuelle Maven Version.

Bauen mit
```
mvn package
```

## Starten

PokeTime kann als eigenständige fat-jar gestartet werden. Die pi4j Integration braucht
allerdings noch Umgebungsvariablen um vernünftig auf einem Raspberry Pi 2b zu funktionieren.
Daher sind diese in dem Start Script mit eingebaut

```bash
./start
```

Um das Ganze einfacher zu gestalten ist das Build-Kommando in dem Start Script mit eingebaut.

## Hardware

```bash
Raspberry Pi 3
TSL2561
ADX345
Pi Breakout Board

Steckbrett
Anschlüsse
```

Fröhliches Hacken!   

english version: https://github.com/Devoxx4KidsDE/PokeTime/README.md
