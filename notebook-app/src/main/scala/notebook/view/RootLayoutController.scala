package notebook.view

import notebook.MainApp
import scalafx.event.ActionEvent
import scalafxml.core.macros.sfxml

@sfxml
class RootLayoutController(){
  def handleClose(): Unit = {
    System.exit(0)
  }
//   def handleDelete(action: ActionEvent): Unit = {
//     //you call the handle delete in personoveriew
//     MainApp.overviewController match {
//       case Some(contrl) =>
//         contrl.handleDeletePerson(action)
//       case None =>
//     }
//   }
}