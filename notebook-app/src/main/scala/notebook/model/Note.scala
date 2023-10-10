package notebook.model

import scalafx.beans.property.{StringProperty, IntegerProperty, ObjectProperty}
import java.time.OffsetDateTime
import notebook.util.Database
import notebook.util.DateUtil._
import scalikejdbc._
import scala.util.{Try, Success, Failure}

class Note(val noteIdS: String, val remarkS: String, val noteOwnerS: String, val titleS: String) extends Database {
    def this() = this(null, null, null, null)

    var noteId = new StringProperty(noteIdS)
    // IntegerProperty can't bind to Integer or Number for some reason
    // https://github.com/scalafx/scalafx/issues/243 seems like an unfixed bug
    // have to use String unfortunately will figure out later
    var remark = new StringProperty(remarkS)
    var noteOwner = new StringProperty(noteOwnerS)
    var title = new StringProperty(titleS)
    var lastModified = ObjectProperty[OffsetDateTime](OffsetDateTime.now())
    var dateCreated = ObjectProperty[OffsetDateTime](OffsetDateTime.now()) 

    def saveNote(): Try[Int] = {
        println("Saving note...")
        // check if noteId is empty string first, if it's empty means new
        // if it isn't empty means we're selecting it from list of notes on the main note screen
        if(this.noteId.value == "") {
            Try(DB autoCommit { implicit session =>
                sql"""
                    INSERT INTO NOTE (REMARK, NOTEOWNER, TITLE, LASTMODIFIED, DATECREATED)
                    VALUES (${remark.value}, ${noteOwner.value}, ${title.value}, ${lastModified.value.asString}, ${dateCreated.value.asString})
                """.update.apply()
            }) match {
                case Failure(e) =>
                    println("Insertion failed")
                    println(e)
                    Failure(e)
                case Success(e) =>
                    println("Insertion success")
                    Success(e)
            }
        } else {
            lastModified = ObjectProperty[OffsetDateTime](OffsetDateTime.now())
            Try(DB autoCommit { implicit session =>
                sql"""
                    update note
                    set
                    remark = ${remark.value},
                    lastModified = ${lastModified.value.asString}
                    where id = ${noteId.value.toInt}
                """.update.apply()
            }) match {
                case Failure(e) => 
                    println("Update failed")
                    Failure(e)
                case Success(e) => 
                    println("Update success")
                    Success(e)
            }
        }
    }

    def deleteNote(): Try[Int] = {
            if (isExistNote) {
                Try(DB autoCommit { implicit session =>
                sql"""
                delete from note where
                id = ${noteId.value.toInt}
                """.update.apply()
                // scalikejdbc Update API implements java.sql.PreparedStatement#executeUpdate() which returns int
                // hence why Try[Int] is used. Either an Int is returned, or an exception.
                // http://scalikejdbc.org/documentation/operations.html <--- look for Update API section
                // https://docs.oracle.com/javase%2F7%2Fdocs%2Fapi%2F%2F/java/sql/PreparedStatement.html#executeUpdate()
            })
        } else {
            throw new Exception(s"Note deletion failed, note $noteId not found.")
        }
    }

    def isExistNote: Boolean = {
        println("Checking note existence...")
        DB readOnly { implicit session =>
        sql"""
            select * from note where id = ${noteId.value}
        """.map(elem => elem.int("id").toString).single.apply()
        } match {
            case Some(x) => true
            case None => false
        }
    }
}

object Note extends Database {
    def apply (noteIdS: String, remarkS: String, noteOwnerS: String, titleS: String, lastModifiedS: String, dateCreatedS: String): Note = {
        new Note(noteIdS, remarkS, noteOwnerS, titleS) {
            lastModified.value = lastModifiedS.parseOffsetDateTime
            dateCreated.value = dateCreatedS.parseOffsetDateTime
        }
    }

    def initTable() = {
        DB autoCommit { implicit session => 
        // probably better to use id as foreign key...
        // but username is already unique so should be fine
        sql"""
        CREATE TABLE NOTE (
            ID INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
            REMARK VARCHAR(256),
            LASTMODIFIED VARCHAR(64),
            TITLE VARCHAR(64),
            DATECREATED VARCHAR(64),
            NOTEOWNER VARCHAR(64),
            CONSTRAINT FK_NOTEOWNER
                FOREIGN KEY(NOTEOWNER) REFERENCES NOTEBOOKUSER(USERNAME)
        )
        """.execute.apply()
        }
    }

    def getAllNotesTargeted(noteOwner: String): List[Note] = {
        DB readOnly { implicit session => 
        sql"SELECT * FROM NOTE WHERE NOTEOWNER = ${noteOwner}".map(elem =>
        Note(elem.int("id").toString, elem.string("remark"), elem.string("noteOwner"), elem.string("title"), elem.string("lastModified"), elem.string("dateCreated"))
            ).list.apply()
        }
    }

    // def getAllNotes(): List[Note] = {
    //     DB readOnly { implicit session => 
    //     sql"select * from note".map(elem =>
    //     Note(elem.int("id").toString, elem.string("remark"), elem.string("lastModified"), elem.string("dateCreated"), elem.string("noteOwner"))
    //         ).list.apply()
    //     }
    // }
}