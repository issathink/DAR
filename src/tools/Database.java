package tools;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * <b>Database est la classe permettant d'etablir une connection avec une
 * SGBD.</b>
 * 
 * @param jndiname
 *            - Le nom de la jndi
 * @return Une connection au SGBD.
 * 
 */

public class Database {

	private DataSource dataSource;

	public Database(String jndiname) throws SQLException {
		try {
			dataSource = (DataSource) new InitialContext()
					.lookup("java:comp/env/" + jndiname);
		} catch (NamingException e) {
			// Handle error that it’s not configured in JNDI.
			throw new SQLException(jndiname + " is missing in JNDI! : "
					+ e.getMessage());
		}
	}

	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

}