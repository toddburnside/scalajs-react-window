package reactST.reactWindow

import scala.scalajs.js
import scala.scalajs.js.|

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom

import reactST.reactWindow.components.FixedSizeList
import reactST.reactWindow.mod._
import js.annotation._

object WindowHelper {
  type ComponentFnResult =
    Unit | Null | Boolean | String | (Byte | Short | Int | Float | Double) | japgolly.scalajs.react.raw.React.Element | japgolly.scalajs.react.raw.recursiveTypeAliases.ChildrenArray[
      Unit | Null | Boolean | String | (Byte | Short | Int | Float | Double) | japgolly.scalajs.react.raw.React.Element
    ]

  /**
   * Helper method for creating a FixedSizeList.
   * It wraps the apply method on the FixedSizeList companion object to make
   * it easier to pass a scalajs component.
   * See https://react-window.now.sh/#/api/FixedSizeList for more details.
   * @param rowComponent Component for rendering the row. A ScalaComponent on which .toJsComponent.raw has been called.
   * @param height Height of list
   * @param itemCount Total number of items in the list. Note that only a few items will be rendered and displayed at a time.
   * @param itemSize Size of a item in the direction being windowed. For vertical lists, this is the row height. For horizontal lists, this is the column width.
   * @param width Width of List
   * @return A FixedSizeList.Builder which can be used to add additional configuration.
   */
  def fixedSizeList(
    rowComponent: js.Function1[ListChildComponentProps, ComponentFnResult],
    height:       Double | String,
    itemCount:    Int,
    itemSize:     Double,
    width:        Double | String
  ): FixedSizeList.Builder = FixedSizeList(
    height = height,
    itemCount = itemCount.toDouble,
    itemSize = itemSize,
    width = width,
    children =
      rowComponent.asInstanceOf[reactST.react.mod.FunctionComponent[ListChildComponentProps]]
  )
}
