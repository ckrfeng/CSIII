# Sushi Go! Pass & Play (Java Swing)

A minimal, fully-playable Sushi Go! implementation with pass-and-play UI.

## Features
- 3–5 players (choose on menu)
- Drafting: pick → reveal → pass left; discard final card
- All scoring rules:
  - Maki majority (+6) and second (+3) with tie-splitting
  - Tempura (5 per pair), Sashimi (10 per trio), Dumplings (1/3/6/10/15)
  - Nigiri (1/2/3) with Wasabi tripling on next Nigiri by play order
  - Chopsticks: toggle to play TWO cards; returns a chopsticks card to the passing hand
  - Puddings persist; at game end: most +6, least -6 (ties share)
- Pass-and-play prompt hides hands between turns
- Mouse: click card(s), buttons
- Keyboard: SPACE to continue, ENTER to confirm

## Build & Run

You need JDK 8+.

```bash
cd src
javac *.java
java Main
```

(Optional) Build a jar:
```bash
javac *.java
jar cfe SushiGo.jar Main *.class
java -jar SushiGo.jar
```

## Assets
The UI uses simple vector cards to avoid missing images. You can place your card images under `assets/` and adapt rendering in `Card.paint(...)`.