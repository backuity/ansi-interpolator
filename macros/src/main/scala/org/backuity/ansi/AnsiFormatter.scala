package org.backuity.ansi

import scala.language.experimental.macros
import scala.reflect.macros.blackbox
import scala.util.control.NonFatal
import AnsiCodes._

object AnsiFormatter {

  implicit class FormattedHelper(val sc: StringContext) extends AnyVal {

    def ansi(args: Any*): String = macro ansiImpl
  }

  def ansiImpl(c: blackbox.Context)(args: c.Tree*) = {
    import c.universe._

    val Apply(_, List(Apply(_, partsTree))) = c.prefix.tree

    val parts = partsTree.map {
      case term @ Literal(Constant(x: String)) => (x, term.pos)
      case term => c.abort(term.pos, "Expected a String")
    }

    val ansiCtx = new AnsiContext()

    val newParts = for( (part,pos) <- parts ) yield {
      try {
        ansiPart(part, ansiCtx)
      } catch {
        case ParsingError(msg, offset) =>
          c.abort(pos.withPoint(pos.end + offset), msg)
      }
    }
    if( !ansiCtx.isEmpty ) {
      val msg = ansiCtx.size match {
        case 1 => "a tag is not closed properly"
        case more => more + " tags are not closed properly"
      }
      c.abort(c.prefix.tree.pos, msg)
    }

    q"""StringContext($newParts : _*).standardInterpolator(Predef.identity, Seq(..$args))"""
  }

  class AnsiContext {

    private var tagStack : List[String] = Nil
    private var colorStack : List[Int] = List(9)

    def size = tagStack.size
    def isEmpty = tagStack.isEmpty
    def color : Int = colorStack.head

    def push(tag: String, color: Int): Unit = {
      tagStack = tag :: tagStack
      colorStack = color :: colorStack
    }

    def pop() : String = {
      val head = tagStack.headOption.getOrElse(sys.error("No more tag found"))
      tagStack = tagStack.tail
      colorStack = colorStack.tail
      head
    }
  }

  val colorCodes = Map(
    "black" -> 0,
    "red" -> 1,
    "green" -> 2,
    "yellow" -> 3,
    "blue" -> 4,
    "magenta" -> 5,
    "cyan" -> 6,
    "white" -> 7)

  /** @return (openCode, closeCode, color) */
  def findCodesFor(tag: String, ctx: AnsiContext) : (String,String, Int) = tag match {
    case "bold"      => (BOLD,      BOLD_OFF,      ctx.color)
    case "italic"    => (ITALIC,    ITALIC_OFF,    ctx.color)
    case "underline" => (UNDERLINE, UNDERLINE_OFF, ctx.color)
    case "blink"     => (BLINK,     BLINK_OFF,     ctx.color)
    case color       => colorCodes.get(color) match {
      case Some(colorCode) => (s"\u001b[3${colorCode}m", s"\u001b[3${ctx.color}m", colorCode)
      case None => sys.error("Unsupported tag " + tag)
    }
  }

  sealed trait Lexeme
  case class StartTag(before: String, after: String, idx : Int) extends Lexeme
  case class CloseTag(before: String, after: String, idx : Int) extends Lexeme
  case class Nothing(content: String) extends Lexeme

  def scan(str: String) : Lexeme = {
    str.indexWhere( c => c == '%' || c == '}' ) match {
      case -1 => Nothing(str)
      case idx if str.charAt(idx) == '%' => StartTag(str.substring(0, idx), str.substring(idx + 1), idx)
      case idx if str.charAt(idx) == '}' => CloseTag(str.substring(0, idx), str.substring(idx + 1), idx)
    }
  }

  case class ParsingError(msg: String, offset: Int) extends Exception(msg)

  def ansiPart(part: String, ctx: AnsiContext, offset: Int = 0) : String = {
    scan(part) match {
      case StartTag(before, after, idx) =>
        try {
            if (after.isEmpty) {
              // trailing '%' are left verbatim
              part
            } else if (after.charAt(0) == ' ') {
              // '%' followed by whitespace are left verbatim
              part
            } else {
              after.indexOf("{") match {
                case -1 => throw ParsingError("missing '{' for tag " + after, idx + offset)
                case bracketIdx =>
                  val tag = after.substring(0, bracketIdx)
                  val (openCode, closeCode, color) = findCodesFor(tag, ctx)

                  // save close code
                  ctx.push(closeCode, color)

                  // replace tag by ansi code
                  before + openCode + ansiPart(after.substring(bracketIdx + 1), ctx,
                    offset = offset + idx + 1 + bracketIdx + 1)
              }
            }
          } catch {
            case err : ParsingError => throw err
            case NonFatal(other) => throw ParsingError(other.getMessage,
              offset = offset + idx + 1)
          }

      case CloseTag(before, after, idx) =>
        before + ctx.pop() + ansiPart(after, ctx,
          offset = offset + idx + 1)

      case Nothing(content) => content
    }
  }
}
