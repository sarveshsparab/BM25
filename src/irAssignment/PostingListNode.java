package irAssignment;
import java.io.BufferedWriter;
import java.io.IOException;

public class PostingListNode {

	private int docId;
	private int termFreq;

	/*Constructor for the posting list node
	docId - id of the document the term is present in
	termFreq - the number of times the term is present in the document*/
	public PostingListNode(int docId, int termFreq) {
		this.docId = docId;
		this.termFreq = termFreq;
	}

    //Print the posting list node
	public void printPostingListNode() {
		System.out.print(docId + "->" + termFreq + " ");
	}

	/*Write the posting list node to the file
	bw - the pointer to the buffer where the output contents to the file are being written*/
	public void writePostingListNode(BufferedWriter bw) throws IOException {
		bw.write(Integer.toString(docId) + "->" + Integer.toString(termFreq) + " ");
	}

	//returns the docId
	public int getDocId() {
		return docId;
	}

    //sets the docId
	public void setDocId(int docId) {
		this.docId = docId;
	}

    //returns the term frequency
	public int getTermFreq() {
		return termFreq;
	}

    //sets the term frequency
	public void setTermFreq(int termFreq) {
		this.termFreq = termFreq;
	}

	//increments the term frequency
	public void incrementTermFreq() {
		this.termFreq = this.termFreq + 1;
	}

	@Override
	public String toString() {
		return "PostingListNode [docId=" + docId + ", termFreq=" + termFreq + "]";
	}


}
