package notebook.view

import notebook.MainApp
import notebook.model.NotebookUser
import scalafx.scene.control.{TextField, PasswordField}
import scalafxml.core.macros.sfxml
import scalafx.scene.control.Alert
import scalafx.event.ActionEvent
import scalafx.stage.Stage

@sfxml
class WelcomeController(private val usernameField: TextField, private val passwordField: TextField) {

    var dialogStage: Stage = null

    def handleLogin(): Unit = {
        val okClicked = NotebookUser.checkLoginCredentials(usernameField.text, passwordField.text)

        if(okClicked) {
            MainApp.showMainNotesScreen(usernameField.text.value)
            // println("Welcome password check success")
            // println(usernameField.text.value)
        } else {
            val alert = new Alert(Alert.AlertType.Error) {
                initOwner(dialogStage)
                title = "Invalid credentials"
                headerText = "Incorrect username or password"
                contentText = "Please provide correct username or password"
            }.showAndWait()
        }
        // println("btn works")
    }

    def handleRegister(action: ActionEvent) = {
        val notebookUser = new NotebookUser("","")
        val okClicked = MainApp.showRegistrationDialog(notebookUser)
        if (okClicked) {
            try {
                notebookUser.saveUser()
            } catch {
                case e: Exception => {
                    val alert = new Alert(Alert.AlertType.Error) {
                        initOwner(dialogStage)
                        title = "Registration error"
                        headerText = "Username already taken"
                        contentText = "Please provide new username and password"
                    }.showAndWait()
                }
            }
        }
    }
}