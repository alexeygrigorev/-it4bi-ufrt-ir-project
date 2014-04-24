package it4bi.ufrt.ir.service.spell;

public class RankedString implements Comparable<RankedString> {

	private String word;
	private  double score;
	
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public double getRank() {
		return score;
	}
	public void setRank(int rank) {
		this.score = rank;
	}
	public RankedString(String word, double rank) {
		super();
		this.word = word;
		this.score = rank;
	}
	public RankedString() {
		super();
		this.word = null;
		this.score = 0;
	}
	@Override
	public int compareTo(RankedString o) {
		
		if(score == o.score)
			return 0;
		if(score > o.score)
			return -1;
		
		return 1;
		
		
	}
	
	
	
	
}
