ansi-interpolator
=================

A scala (2.11+) macro based ansi interpolator.

It has two advantages over non-macro interpolators:
   - is faster and lighter (brings no extra dependency at runtime)
   - detects syntax issues at compile time

# Usage

Add the following dependency to your SBT project:

    "org.backuity" %% "ansi-interpolator" % "1.0" % "provided"

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