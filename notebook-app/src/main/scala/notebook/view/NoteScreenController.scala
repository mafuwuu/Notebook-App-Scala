package notebook.view

import notebook.MainApp
import notebook.model.Note
import scalafx.scene.control.{Alert, Label, TextField, PasswordField, TextArea}
import scalafxml.core.macros.sfxml
import scalafx.stage.Stage
import scalafx.event.ActionEvent
import scalafx.Includes._
import notebook.util.DateUtil._

@sfxml
class NoteScreenController(private val titleField: TextField, private val noteOwnerLabel: Label, private val lastModifiedLabel: Label, private val dateCreatedLabel: Label, private val noteArea: TextArea) {
    var dialogStage: Stage = null
    private var _note: Note = null
    var okClicked = false

    def note = _note
    println(_note)
    def note_=(x: Note) {
        _note = x

        noteArea.text = _note.remark.value
        titleField.text = _note.title.value
        noteOwnerLabel.text = _note.noteOwner.value
        lastModifiedLabel.text = _note.lastModified.value.asString
        dateCreatedLabel.text = _note.dateCreated.value.asString
    }

    def handleOk(action: ActionEvent) {
        _note.remark.value = noteArea.text.value
        _note.title.value = titleField.text.value
        
        okClicked = true
        dialogStage.close()
    }

    def handleCancel(action: ActionEvent) {
        dialogStage.close()
    }

    def nullChecking(x: String) = x == null || x.length == 0


}