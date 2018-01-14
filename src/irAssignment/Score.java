package irAssignment;

public class Score implements Comparable<Score> {

	private double score;
	private int docId;

	/*Stores the score of document id
	docId - the id of the document for which the score is stored
	score - the score of the document*/
	public Score(double score, int docId) {
		this.score = score;
		this.docId = docId;
	}

	public double getScore() {
		return score;
	}

	public int getDocId() {
		return docId;
	}

	@Override
	public int compareTo(Score o) {
		if(score < o.getScore()) {
			return 1;
		} else {
			return -1;
		}
	}
}
