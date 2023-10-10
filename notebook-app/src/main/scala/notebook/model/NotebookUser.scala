package notebook.model

import scalafx.beans.property.{StringProperty, IntegerProperty, ObjectProperty}
import java.time.OffsetDateTime
import notebook.util.Database
import notebook.util.DateUtil._
import scalikejdbc._
import scala.util.{Try, Success, Failure}

class NotebookUser(val usernameS: String, val passwordS: String) extends Database {
// class NotebookUser(val notebookUserId: Int, val usernameS: String, val passwordS: String) extends Database {
    def this() = this(null, null)

    // var userId = ObjectProperty[Int](notebookUserId)
    var username = new StringProperty(usernameS)
    var password = new StringProperty(passwordS)

    def saveUser(): Try[Int] = {
        if(!(isExistUser)) {
            Try(DB autoCommit { implicit session => 
            sql"""
                INSERT INTO NOTEBOOKUSER(USERNAME, PASSWORD) VALUES (${username.value}, ${password.value})
            """.update.apply()
            })
        } else {
            throw new Exception("Username already exists. Please choose use a different username.")
        }
    }

    // checks for any existing username
    def isExistUser: Boolean = {
        DB readOnly { implicit session => 
        sql"""
            SELECT * FROM NOTEBOOKUSER WHERE USERNAME = ${username.value}
        """.map(elem => elem.string("USERNAME")).single.apply()
        } match {
            case Some(x) => true
            case None => false
        }
    }
}

object NotebookUser extends Database {
    def apply(usernameS: String, passwordS: String): NotebookUser = {
        new NotebookUser()
    }

    def initTable() = {
        DB autoCommit { implicit session => 
        sql"""
        CREATE TABLE NOTEBOOKUSER (
            NOTEBOOKUSER_ID INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
            USERNAME VARCHAR(64) NOT NULL UNIQUE,
            PASSWORD VARCHAR(100)
        )
        """.execute.apply()
        }
    }

    def checkLoginCredentials(username: StringProperty, password: StringProperty) = {
        DB readOnly { implicit session => 
        sql"""
            SELECT * FROM NOTEBOOKUSER WHERE
            USERNAME = ${username.value} AND PASSWORD = ${password.value}
        """.map(elem => (elem.string("USERNAME"), elem.string("PASSWORD"))).single.apply()
        } match {
            case Some(x) => true
            case None => false
        }
    }
}