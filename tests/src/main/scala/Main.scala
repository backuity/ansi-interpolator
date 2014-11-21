import org.backuity.ansi.AnsiFormatter

object Main {

  def main(args: Array[String]) {

    import AnsiFormatter._

    val str = "haHA"

    println(ansi"%yellow{$str} %bold{%red{so%underline{me}}th}ing %blue{blue}%bold{box}")

    s"ah $str hello"
    StringContext("ah ", " hello").standardInterpolator(identity, Seq(str))

//    println(ansi"%bold{%redx{$str}")
//    println(ansi"%bro{$str}")
  }
}
