package notebook.util

import scalikejdbc._
import notebook.model.NotebookUser
import notebook.model.Note

trait Database {
  val derbyDriverClassname = "org.apache.derby.jdbc.EmbeddedDriver"

  val dbURL = "jdbc:derby:notebookDB;create=true;";
  // initialize JDBC driver & connection pool start up database
  Class.forName(derbyDriverClassname)

  ConnectionPool.singleton(dbURL, "me", "mine")

  // ad-hoc session provider on the REPL
  implicit val session = AutoSession

}

object Database extends Database {
/*   
  Loops through table Sequence which contains class names as String. 
  Any missing tables are created by using reflection to get 
  class from String and invoking initTable method from class
 */
  def setupDB() = {
    val tables = Seq("NotebookUser", "Note")
    tables.foreach(table => {
        if(DB.getTable(table).isEmpty) {
            val tempClass = Class.forName(s"notebook.model.${table}")
            val method = tempClass.getDeclaredMethod("initTable")
            method.invoke(tempClass.newInstance())
        }
    })
  }
}
