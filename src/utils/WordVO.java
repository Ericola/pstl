package utils;

public class WordVO {

	private String	word;
	private int		weight;

	public WordVO( String word, int weight ) {
		this.word = word;
		this.weight = weight;
	}

	public String getWord() {
		return word;
	}

	public void setWord( String word ) {
		this.word = word;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight( int weight ) {
		this.weight = weight;
	}

}
