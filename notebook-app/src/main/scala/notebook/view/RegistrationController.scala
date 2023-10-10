package notebook.view

import notebook.MainApp
import notebook.model.NotebookUser
import scalafx.scene.control.{Alert, Label, TextField, PasswordField}
import scalafxml.core.macros.sfxml
import scalafx.stage.Stage
import scalafx.event.ActionEvent
import scalafx.Includes._

@sfxml
class RegistrationController(private val usernameField: TextField, private val passwordField: TextField, private val passwordValidationField: TextField) {

    var dialogStage: Stage = null
    private var _notebookUser: NotebookUser = null
    var okClicked = false

    def notebookUser = _notebookUser
    def notebookUser_=(x: NotebookUser) {
        _notebookUser = x

        usernameField.text = _notebookUser.username.value
        passwordField.text = _notebookUser.password.value
    }

    def handleOk(action: ActionEvent) {
        if (isInputValid()) {
            _notebookUser.username.value = usernameField.text.value
            _notebookUser.password.value = passwordField.text.value

            okClicked = true
            dialogStage.close()
        }
    }

    def handleCancel(action: ActionEvent) {
        dialogStage.close()
    }

    def nullChecking(x: String) = x == null || x.length == 0

    def isInputValid(): Boolean = {
        var errorMessage = ""
        if (nullChecking(usernameField.text.value)) {
            println("Empty username field")
            errorMessage += "Empty username field \n"
        }
        if (nullChecking(passwordField.text.value)) {
            println("Empty password field")
            errorMessage += "Empty password field \n"
        }

        if (errorMessage.length() == 0) {
            true
        } else {
            val alert = new Alert(Alert.AlertType.Error) {
                initOwner(dialogStage)
                title = "Invalid fields"
                headerText = "Please correct invalid fields"
                contentText = errorMessage
            }.showAndWait()

            false
        }
    }
    

}