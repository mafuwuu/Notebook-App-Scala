package notebook

import notebook.model.Note
import notebook.model.NotebookUser
import notebook.util.Database
import notebook.view.{WelcomeController, RegistrationController, MainNotesScreenController, NoteScreenController}
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.Includes._
import scalafxml.core.{FXMLLoader, FXMLView, NoDependencyResolver}
import javafx.{scene => jfxs}
import scalafx.collections.ObservableBuffer
import scalafx.stage.{Modality, Stage}

object MainApp extends JFXApp {
    Database.setupDB()
    val noteData = new ObservableBuffer[Note]()

    var currentUsername: String = null

    val rootResource = getClass.getResource("view/RootLayout.fxml")
    val loader = new FXMLLoader(rootResource, NoDependencyResolver)
    loader.load()
    val roots = loader.getRoot[javafx.scene.layout.BorderPane]()

    stage = new PrimaryStage {
        title = "NotebookApp"
        scene = new Scene {
            root = roots
        }
    }

    def showWelcome() = {
        val resource = getClass.getResource("view/Welcome.fxml")
        val loader = new FXMLLoader(resource, NoDependencyResolver)
        loader.load()
        val roots = loader.getRoot[jfxs.layout.AnchorPane]()
        this.roots.setCenter(roots)
    }

    def showRegistrationDialog(notebookUser: NotebookUser): Boolean = {
        val resource = getClass.getResourceAsStream("view/Registration.fxml")
        val loader = new FXMLLoader(null, NoDependencyResolver)
        loader.load(resource)
        val roots2 = loader.getRoot[jfxs.Parent]
        val control = loader.getController[RegistrationController#Controller]

        val dialog = new Stage() {
            initModality(Modality.APPLICATION_MODAL)
            initOwner(stage)
            scene = new Scene {
                root = roots2
            }
        }

        control.dialogStage = dialog
        control.notebookUser = notebookUser
        dialog.showAndWait()
        control.okClicked
    }
    
    var mainNotesScreenController: Option[MainNotesScreenController#Controller] = None
    def showMainNotesScreen(username: String) = {
        noteData ++= Note.getAllNotesTargeted(username)
        val resource = getClass.getResource("view/MainNotesScreen.fxml")
        val loader = new FXMLLoader(resource, NoDependencyResolver)
        loader.load()
        val roots = loader.getRoot[jfxs.layout.AnchorPane]()
        val control = loader.getController[MainNotesScreenController#Controller]()
        mainNotesScreenController = Option(control)
        this.roots.setCenter(roots)
        currentUsername = username
    }

    def showEditNoteDialog(note: Note): Boolean = {
        val resource = getClass.getResourceAsStream("view/NoteScreen.fxml")
        val loader = new FXMLLoader(null, NoDependencyResolver)
        loader.load(resource)
        val roots2 = loader.getRoot[jfxs.Parent]
        val control = loader.getController[NoteScreenController#Controller]

        val dialog = new Stage() {
            initModality(Modality.APPLICATION_MODAL)
            initOwner(stage)
            scene = new Scene {
                root = roots2
            }
        }

        control.dialogStage = dialog
        control.note = note
        dialog.showAndWait()
        control.okClicked
    }

    showWelcome()
}