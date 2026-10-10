# Sushi Go! AWT - Pass & Play (No Swing)

This version uses plain AWT Frame and Canvas (no Swing). It loads images from the resource path `/Image/...` (matching your provided names).
It fixes score overlap by drawing player score/pudding in a header area above the tableau.

## How to compile & run
Place your `Image` folder inside `res` or package it into the jar under `/Image/` to make getResource work.
From project root:
```
cd src
javac *.java
cd ..
jar cf SushiGoAWT.jar -C src . -C res .
java -cp SushiGoAWT.jar Main
```

If images are missing, the game draws placeholders.