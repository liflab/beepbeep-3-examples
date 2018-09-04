package jdbc;

import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.jdbc.JdbcSource;
import ca.uqac.lif.cep.tuples.Tuple;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SourceExample
{
  public static void main(String[] args) throws SQLException
  {
    ///
    Connection conn = DriverManager.getConnection(
        "jdbc:mysql//localhost/mydb", "betty", "foo");
    String query = "SELECT * FROM mytable";
    JdbcSource src = new JdbcSource(conn, query);
    Pullable p = src.getPullableOutput();
    while (p.hasNext())
    {
      Tuple t = (Tuple) p.pull();
      System.out.println(t);
    }
    ///
  }
}