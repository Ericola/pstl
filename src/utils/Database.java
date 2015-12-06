package utils;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;

public class Database {

	/** Parametres connexion à la bd **/
	private static final String	MONGO_DB_NAME	= "pstl";
	private static final String	MONGO_HOST		= "localhost";
	private static final int	MONGO_PORT		= 27017;

	private static DataSource	dataSource;
	private static MongoClient	mongo;

	private Database() {
	}

	public static Connection getConnection() throws SQLException, NamingException {
		if ( dataSource == null ) {
			dataSource = ( DataSource ) new InitialContext().lookup( "java:comp/env/jdbc/db" );
		}
		return dataSource.getConnection();

	}

	public static DB getMongoDb() throws UnknownHostException, MongoException {
		mongo = new MongoClient( MONGO_HOST, MONGO_PORT );
		return mongo.getDB( MONGO_DB_NAME );
	}

	public static void closeMongo() {
		mongo.close();
	}

}