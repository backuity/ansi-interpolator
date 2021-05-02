ansi-interpolator [![Build Status](https://travis-ci.org/backuity/ansi-interpolator.png?branch=master)](https://travis-ci.org/backuity/ansi-interpolator) [<img src="https://img.shields.io/maven-central/v/org.backuity/ansi-interpolator_2.11*.svg?label=latest%20release%20for%202.11"/>](http://search.maven.org/#search%7Cga%7C1%7Cg%3Aorg.backuity%20a%3Aansi-interpolator_2.11*) [<img src="https://img.shields.io/maven-central/v/org.backuity/ansi-interpolator_2.12*.svg?label=latest%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Cg%3Aorg.backuity%20a%3Aansi-interpolator_2.12*)
=================

A scala (2.11+) macro based ansi interpolator.

It has two advantages over non-macro interpolators:
   - is faster and lighter (brings no extra dependency at runtime)
   - detects syntax issues at compile time

# Usage

Add the following dependency to your SBT project:

    "org.backuity" %% "ansi-interpolator" % "1.1" % "provided"

Note about `provided`: see [this stackoverflow answer](http://stackoverflow.com/questions/21515325/add-a-compile-time-only-dependency-in-sbt#answer-21516954)
      for a better SBT dependency scope.

Import the AnsiFormatter and use the `ansi` interpolator:

```scala
import org.backuity.ansi.AnsiFormatter.FormattedHelper

ansi"Text containing ansi tags such as %bold{bold text} or %underline{can be %yellow{nested}}"
```

Here is the list of supported tags:
 - bold
 - underline
 - italic (may not work on every terminal)
 - blink (may not work on every terminal)

And color tags:
 - black
 - red
 - green
 - yellow
 - blue
 - magenta
 - cyan
 - white

# Syntax errors are detected at compile time

```
[error] .../Main.scala:17: Unsupported tag underlin
[error]     println(ansi"Please %underlin{underline this : $str}")
[error]                          ^
```

# Conditional ANSI codes rendering / Non-ANSI terminals support

Use `ansiCond"..."` instead of `ansi"..."` to generate code that detects ANSI support (or lack thereof, e.g log files or windows terminal) at runtime.
If ANSI support isn't detected the `ansiCond` interpolator will output a plain text.

```scala
ansiCond"%red{Hello}, I'm logs and windows %bold{friendly}"
```
