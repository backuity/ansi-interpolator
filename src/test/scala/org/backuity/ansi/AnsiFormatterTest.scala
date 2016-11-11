package org.backuity.ansi

import org.backuity.ansi.AnsiFormatter.AnsiContext
import org.backuity.matchete.{Matcher, JunitMatchers}
import org.junit.Test

class AnsiFormatterTest extends JunitMatchers {

  import AnsiFormatter.ansiPart
  import org.backuity.ansi.{AnsiCodes => Ansi}

  val ctx = new AnsiContext

  @Test
  def nestedTags(): Unit = {
    ansiPart("%bold{hey %underline{this} is %yellow{the shit}}", ctx) must_==
      Ansi.BOLD + "hey " + Ansi.UNDERLINE + "this" + Ansi.UNDERLINE_OFF + " is " + Console.YELLOW + "the shit" + Ansi.COLOR_DEFAULT + Ansi.BOLD_OFF
  }

  @Test
  def unclosedTagMustStackClosingTagOnTheContext(): Unit = {
    ansiPart("%bold{hey %underline{you}", ctx) must_== Ansi.BOLD + "hey " + Ansi.UNDERLINE + "you" + Ansi.UNDERLINE_OFF
    ctx.pop() must_== Ansi.BOLD_OFF
  }

  @Test
  def doublePercentMustEscapePercent(): Unit = {
    ansiPart("a double %%price", ctx) must_== "a double %price"
  }

  @Test
  def aLoneBracketShouldBeLeftVerbatim(): Unit = {
    ansiPart("a lone } bracket", ctx) must_== "a lone } bracket"
  }

  @Test
  def singlePercentMustBeLeftVerbatim(): Unit = {
    ansiPart("a trailing %", ctx) must_== "a trailing %"
    ansiPart("a % alone", ctx) must_== "a % alone"
  }

  import AnsiFormatter.ParsingError

  def throwAParsingErrorWith(offset: Int, message: String) = throwA[ParsingError].`with`("a correct offset and message") {
    case ParsingError(actualMsg, actualOffset) =>
      actualOffset must_== offset
      actualMsg must_== message
  }

  @Test
  def missingOpenBracket(): Unit = {
    ansiPart("a missing %red bracket", ctx) must throwAParsingErrorWith(
      offset = "a missing %r".length,
      message = "missing '{' for tag red"
    )
  }

  @Test
  def missingOpenTag(): Unit = {
    ansiPart("a missing red{bracket} should break", ctx) must throwAParsingErrorWith(
      offset = "a missing red{bracket}".length,
      message = "missing open tag"
    )
  }
}
