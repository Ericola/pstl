package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.naming.NamingException;

import utils.Database;

public class IndexTools {
	public static class Offer {
		public static void index( String content, String offerId ) throws SQLException, NamingException {

			Connection con = Database.getConnection();

			String cont = content.toLowerCase();

			List<String> wordsTab = Arrays.asList( cont.split( "[\\s,.?!/]+" ) );
			Set<String> uniqueWordsTab = new HashSet<>( wordsTab );

			// Df nombre d'offres contenant le mot
			// Tf Nombre de fois que le mot apparait dans l'offre

			for ( String word : uniqueWordsTab ) {
				// Update DF
				PreparedStatement s = con.prepareStatement( "SELECT * FROM odf WHERE word = ?" );
				s.setString( 1, word );
				ResultSet rs = s.executeQuery();

				//Check if already exists
				if ( rs.next() ) {
					int df = rs.getInt( "df" ) + 1;
					s = con.prepareStatement( "UPDATE odf SET df = '" + df + "' WHERE word = ?" );
					s.setString( 1, word );
				} else {// Insert otherwise
					s = con.prepareStatement( "INSERT INTO odf VALUES(?, 1)" );
					s.setString( 1, word );
				}

				//Execute update or insert
				s.executeUpdate();

				// Update TF
				s = con.prepareStatement( "SELECT * FROM otf WHERE word = ? AND offer = '" + offerId + "'" );
				s.setString( 1, word );
				rs = s.executeQuery();
				//Check if already exists
				if ( rs.next() ) {
					int tf = rs.getInt( "tf" ) + Collections.frequency( wordsTab, word );
					s = con.prepareStatement( "UPDATE otf SET tf = '" + tf + "' WHERE word = ?" );
					s.setString( 1, word );
				} else {// Insert otherwise
					s = con.prepareStatement( "INSERT INTO otf VALUES(?, '" + offerId + "',"
							+ Collections.frequency( wordsTab, word ) + ")" );
					s.setString( 1, word );
				}
				s.executeUpdate();

				s.close();

			}

			con.close();

			Database.closeMongo();
		}

		public static void index( String[] content, String offerId ) throws SQLException, NamingException {

			try ( Connection con = Database.getConnection() ) {

				for ( int i = 0; i < content.length; i++ )
					content[i] = content[i].toLowerCase();

				for ( String word : content ) {

					// Update DF
					String sql;
					try ( PreparedStatement s = con.prepareStatement( "SELECT * FROM odf WHERE word = ?" ) ) {
						s.setString( 1, word );
						ResultSet rs = s.executeQuery();

						// Check if already exists
						int df = 0;
						if ( rs.next() ) {
							df = rs.getInt( "df" ) + 1;
							sql = "UPDATE odf SET df = '" + df + "' WHERE word = ?";
						} else
							// Insert otherwise
							sql = "INSERT INTO odf VALUES(?, 1)";
					}
					try ( PreparedStatement s = con.prepareStatement( sql ) ) {
						s.setString( 1, word );

						// Execute update or insert
						s.executeUpdate();
					}
					// Update TF
					try ( PreparedStatement s = con.prepareStatement( "SELECT * FROM otf WHERE word = ? AND offer = '"
							+ offerId + "'" ) ) {
						s.setString( 1, word );
						ResultSet rs = s.executeQuery();

						// Check if already exists
						if ( rs.next() ) {
							int tf = rs.getInt( "tf" ) + 1;
							sql = "UPDATE otf SET tf = '" + tf + "' WHERE word = ?";

						} else {// Insert otherwise
							sql = "INSERT INTO otf VALUES(?, '" + offerId + "', 1)";
						}
					}

					try ( PreparedStatement s = con.prepareStatement( sql ) ) {
						s.setString( 1, word );
						s.executeUpdate();

					}
				}
			}// close connection

			Database.closeMongo();
		}

		public static void unindex( String content, String offerId ) throws SQLException, NamingException {

			try ( Connection con = Database.getConnection() ) {

				String cont = content.toLowerCase();

				List<String> wordsTab = Arrays.asList( cont.split( "[\\s,.?!/]+" ) );
				Set<String> uniqueWordsTab = new HashSet<>( wordsTab );
				String sql;
				for ( String word : uniqueWordsTab ) {
					// Update DF
					int df = 0;
					try ( PreparedStatement s = con.prepareStatement( "SELECT * FROM odf WHERE  word = ?" ) ) {
						s.setString( 1, word );
						ResultSet rs = s.executeQuery();

						//Check if already exists
						if ( rs.next() )
							df = rs.getInt( "df" ) - 1;
					}

					if ( df == 0 )
						sql = "DELETE FROM odf WHERE  word = ?";
					else
						sql = "UPDATE odf SET df = '" + df + "' WHERE  word = ?";

					//Execute update or insert
					try ( PreparedStatement s = con.prepareStatement( sql ) ) {
						s.setString( 1, word );
						s.executeUpdate();

					}
					int tf = 0;
					try ( PreparedStatement s = con.prepareStatement( "SELECT * FROM otf WHERE offer = '" + offerId
							+ "' AND word = ?" ) ) {
						s.setString( 1, word );
						ResultSet rs = s.executeQuery();
						//Check if already exists
						if ( rs.next() )
							tf = rs.getInt( "tf" ) - Collections.frequency( wordsTab, word );
					}
					if ( tf == 0 )
						sql = "DELETE FROM otf WHERE offer = '" + offerId + "' AND word = ?";
					else
						sql = "UPDATE otf SET tf = '" + tf + "' WHERE offer = '" + offerId + "' AND word = ?";

					try ( PreparedStatement s = con.prepareStatement( "UPDATE otf SET tf = '" + tf
							+ "' WHERE offer = '" + offerId + "' AND word = ?" ) ) {
						s.setString( 1, word );
						s.executeUpdate();
					}

				}
			} // connexion closed
			Database.closeMongo();
		}

		public static void unindex( String[] content, String offerId ) throws NamingException, SQLException {

			try ( Connection con = Database.getConnection() ) {

				for ( int i = 0; i < content.length; i++ )
					content[i] = content[i].toLowerCase();
				List<String> wordstab = Arrays.asList( content );
				Set<String> uniqueWordsTab = new HashSet<>( wordstab );
				for ( String word : uniqueWordsTab ) {

					// Update DF
					int df = 0;
					try ( PreparedStatement s = con.prepareStatement( "SELECT * FROM odf WHERE word = ?" ) ) {
						s.setString( 1, word );
						ResultSet rs = s.executeQuery();

						//Check if already exists
						if ( rs.next() ) {
							df = rs.getInt( "df" ) - 1;
						}
					}

					String sql;
					if ( df == 0 )
						sql = "DELETE FROM odf WHERE word = ?";
					else
						sql = "UPDATE odf SET df = '" + df + "' WHERE word = ?";

					try ( PreparedStatement s = con.prepareStatement( sql ) ) {
						s.setString( 1, word );
						//Execute update or insert
						s.executeUpdate();
					}

					int tf = 0;
					try ( PreparedStatement s = con.prepareStatement( "SELECT * FROM otf WHERE offer = '" + offerId
							+ "' AND word = ?" ) ) {
						s.setString( 1, word );
						ResultSet rs = s.executeQuery();
						//Check if already exists
						if ( rs.next() )
							tf = rs.getInt( "tf" ) - Collections.frequency( wordstab, word );
					}

					if ( tf == 0 )
						sql = "DELETE FROM otf WHERE offer = '" + offerId + "' AND word = ?";

					else
						sql = "UPDATE otf SET tf = '" + tf + "' WHERE offer = '" + offerId + "' AND word = ?";

					try ( PreparedStatement s = con.prepareStatement( sql ) ) {
						s.setString( 1, word );
						s.executeUpdate();
					}

				}
			}
			Database.closeMongo();
		}
	}

	public static class CV {
		public static void index( String[] content, int cv ) throws SQLException, NamingException {

			try ( Connection con = Database.getConnection() ) {

				for ( int i = 0; i < content.length; i++ )
					content[i] = content[i].toLowerCase();

				for ( String word : content ) {

					// Update DF
					String sql;
					try ( PreparedStatement s = con.prepareStatement( "SELECT * FROM cdf WHERE word = ?" ) ) {
						s.setString( 1, word );
						ResultSet rs = s.executeQuery();

						// Check if already exists
						int df = 0;
						if ( rs.next() ) {
							df = rs.getInt( "df" ) + 1;
							sql = "UPDATE cdf SET df = '" + df + "' WHERE word = ?";
						} else
							// Insert otherwise
							sql = "INSERT INTO cdf VALUES(?, 1)";
					}
					try ( PreparedStatement s = con.prepareStatement( sql ) ) {
						s.setString( 1, word );

						// Execute update or insert
						s.executeUpdate();
					}
					// Update TF
					try ( PreparedStatement s = con
							.prepareStatement( "SELECT * FROM ctf WHERE word = ? AND cv = " + cv ) ) {
						s.setString( 1, word );
						ResultSet rs = s.executeQuery();

						// Check if already exists
						if ( rs.next() ) {
							int tf = rs.getInt( "tf" ) + 1;
							sql = "UPDATE ctf SET tf = '" + tf + "' WHERE word = ?";

						} else {// Insert otherwise
							sql = "INSERT INTO ctf VALUES(?, " + cv + ", 1)";
						}
					}

					try ( PreparedStatement s = con.prepareStatement( sql ) ) {
						s.setString( 1, word );
						s.executeUpdate();

					}
				}
			}// close connection


		}

		public static void unindex( String[] content, int cv ) throws NamingException, SQLException {

			try ( Connection con = Database.getConnection() ) {

				for ( int i = 0; i < content.length; i++ )
					content[i] = content[i].toLowerCase();
				List<String> wordstab = Arrays.asList( content );
				Set<String> uniqueWordsTab = new HashSet<>( wordstab );
				for ( String word : uniqueWordsTab ) {

					// Update DF
					int df = 0;
					try ( PreparedStatement s = con.prepareStatement( "SELECT * FROM cdf WHERE word = ?" ) ) {
						s.setString( 1, word );
						ResultSet rs = s.executeQuery();

						//Check if already exists
						if ( rs.next() ) {
							df = rs.getInt( "df" ) - 1;
						}
					}

					String sql;
					if ( df == 0 )
						sql = "DELETE FROM cdf WHERE word = ?";
					else
						sql = "UPDATE cdf SET df = '" + df + "' WHERE word = ?";

					try ( PreparedStatement s = con.prepareStatement( sql ) ) {
						s.setString( 1, word );
						//Execute update or insert
						s.executeUpdate();
					}

					int tf = 0;
					try ( PreparedStatement s = con.prepareStatement( "SELECT * FROM ctf WHERE cv = " + cv
							+ " AND word = ?" ) ) {
						s.setString( 1, word );
						ResultSet rs = s.executeQuery();
						//Check if already exists
						if ( rs.next() )
							tf = rs.getInt( "tf" ) - Collections.frequency( wordstab, word );
					}

					if ( tf == 0 )
						sql = "DELETE FROM ctf WHERE cv = " + cv + " AND word = ?";

					else
						sql = "UPDATE ctf SET tf = '" + tf + "' WHERE cv = " + cv + " AND word = ?";

					try ( PreparedStatement s = con.prepareStatement( sql ) ) {
						s.setString( 1, word );
						s.executeUpdate();
					}

				}
			}

		}

	}

}
