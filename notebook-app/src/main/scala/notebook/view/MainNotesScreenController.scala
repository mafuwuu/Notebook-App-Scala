package notebook.view

import notebook.model.NotebookUser
import notebook.model.Note
import notebook.MainApp
import scalafx.scene.control.{Alert, Label, TableColumn, TableView}
import scalafxml.core.macros.sfxml
import scalafx.beans.property.StringProperty
import notebook.util.DateUtil._
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.scene.control.Alert.AlertType
import scalafx.collections.ObservableBuffer

@sfxml
class MainNotesScreenController(private val noteTable: TableView[Note], private val idColumn: TableColumn[Note, String], private val lastModifiedColumn: TableColumn[Note, String], private val titleColumn: TableColumn[Note, String], private val textBodyLabel: Label) {

    noteTable.items = MainApp.noteData
    idColumn.cellValueFactory = a => a.value.noteId
    lastModifiedColumn.cellValueFactory = b => b.value.lastModified.asString
    titleColumn.cellValueFactory = c => c.value.title

    private def showNoteDetails(note: Option[Note]) = {
        note match {
            case Some(x) =>
                println(x.noteId)
                println(x.noteOwner)
                println(x.remark)
                println(x.title)
                println(x.lastModified)
                println(x.dateCreated)
                textBodyLabel.text <== x.remark
            case None =>
                textBodyLabel.text.unbind()
        }
    }

    showNoteDetails(None)
    noteTable.selectionModel.value.selectedItem.onChange(
    (_, _, z) => {
      showNoteDetails(Option(z))
        }
    )

    def handleNewNote(action: ActionEvent) = {
        val note = new Note("","", MainApp.currentUsername, "")
        val okClicked = MainApp.showEditNoteDialog(note)
        if (okClicked) {
            println("NOTEHERE")
            println(note)
            // println(note.noteId)
            
            MainApp.noteData += note
            note.saveNote()
            // println(note.noteOwner)
            // println(note.title)
            // println(note.lastModified)
        }
    }

    def handleDeleteNote(action: ActionEvent) = {
        val selectedIndex = noteTable.selectionModel().selectedIndex.value
        if (selectedIndex >= 0) {
            val note = noteTable.items().remove(selectedIndex)
            note.deleteNote()
        } else {
            val alert = new Alert(AlertType.Warning) {
                initOwner(MainApp.stage)
                title = "No selection"
                headerText = "No note selected!"
                contentText = "Please select a person in the table to delete"
            }.showAndWait()
        }
    }

    def handleEditNote(action: ActionEvent) = {
        val selectedNote = noteTable.selectionModel().selectedItem.value
        if (selectedNote != null) {
            val okClicked = MainApp.showEditNoteDialog(selectedNote)
            if (okClicked) {
                showNoteDetails(Some(selectedNote))
                selectedNote.saveNote()
            }
        } else {
            val alert = new Alert(AlertType.Warning) {
                initOwner(MainApp.stage)
                title = "No selection"
                headerText = "No note selected!"
                contentText = "Please select a person in the table to edit"
            }.showAndWait()
        }
    }


}