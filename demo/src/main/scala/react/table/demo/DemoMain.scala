package react.table.demo

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom

import reactST.reactWindow.WindowHelper
import reactST.reactWindow.mod._
import js.annotation._

@JSExportTopLevel("DemoMain")
object DemoMain {

  val row = ScalaComponent
    .builder[ListChildComponentProps]
    .render_P(props => <.div(^.style := props.style, s"Row ${props.index}"))
    .build
    .toJsComponent
    .raw

  @JSExport
  def main(): Unit = {

    val container = Option(dom.document.getElementById("root")).getOrElse {
      val elem = dom.document.createElement("div")
      elem.id = "root"
      dom.document.body.appendChild(elem)
      elem
    }

    val fixedList =
      WindowHelper.fixedSizeList(height = 150,
                                 itemCount = 1000,
                                 itemSize = 35,
                                 width = 300,
                                 rowComponent = row
      )

    <.div(
      <.h1("Demo for scalajs-react-window"),
      fixedList
    )
      .renderIntoDOM(container)

    ()
  }
}
