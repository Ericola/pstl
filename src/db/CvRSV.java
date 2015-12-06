package db;


public class CvRSV implements Comparable<CvRSV> {

	private int		id;
	private String	firstName;
	private String	lastName;
	private Double	rsv;

	public CvRSV( int id, String first_name, String last_name, double r ) {

		this.id = id;
		this.firstName = first_name;
		this.lastName = last_name;
		rsv = r;

	}

	public Double getRsv() {
		return rsv;
	}

	public void setRsv( Double rsv ) {
		this.rsv = rsv;
	}

	public int getId() {
		return id;
	}

	public void setId( int id ) {
		this.id = id;
	}


	public int compareTo( CvRSV o ) {
		return rsv.compareTo( o.getRsv() );
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName( String firstName ) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName( String lastName ) {
		this.lastName = lastName;
	}

}
