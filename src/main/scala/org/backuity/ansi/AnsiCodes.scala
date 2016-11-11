package org.backuity.ansi

object AnsiCodes {

  val BOLD =          "\u001b[1m"
  val BOLD_OFF =      "\u001b[22m"

  val ITALIC =        "\u001b[3m"
  val ITALIC_OFF =    "\u001b[23m"

  val UNDERLINE =     "\u001b[4m"
  val UNDERLINE_OFF = "\u001b[24m"

  val BLINK =         "\u001b[5m"
  val BLINK_OFF =     "\u001b[25m"

  val COLOR_DEFAULT = "\u001b[39m"
}
