package db;

import com.mongodb.DBObject;


public class ObjetRSV implements Comparable<ObjetRSV>{
	
	
	private DBObject dbo;
	private Double rsv;
	
	
	public ObjetRSV(DBObject d, double r) {
		super();
		this.dbo = d;
		rsv = r;
		
	}
	
	
	public DBObject getDbo() {
		return dbo;
	}

	public void setDbo(DBObject dbo) {
		this.dbo = dbo;
	}

	public Double getRsv() {
		return rsv;
	}

	public void setRsv(Double rsv) {
		this.rsv = rsv;
	}

	public int compareTo(ObjetRSV o) {
		return rsv.compareTo(o.getRsv());
	}

	
	

}
