package irAssignment;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InvertedIndexNode {

	private int termId;
	private List<PostingListNode> postingList;

	/*Creates an inverted index node
	termId - the id of the term which is represented by this inverted index node*/
	public InvertedIndexNode(int termId) {
		this.termId = termId;
		postingList = new ArrayList<PostingListNode>();
	}

	//Returns the posting list corresponding to this inverted index node
	public List<PostingListNode> getPostingList() {
		return postingList;
	}

	/*Writes the inverted index node to a file
	bw - the pointer to the buffer where the output contents to the file are being written*/
	public void writeInvertedIndexNode(BufferedWriter bw) throws IOException {
		bw.write(Integer.toString(termId) + " ");
		for(int i = 0; i < postingList.size(); i++) {
			postingList.get(i).writePostingListNode(bw);
		}
		bw.newLine();
	}

	//Adds a posting list node to the posting list corresponding to this inverted index node
	public void addIntoPostingList(PostingListNode pln) {
		postingList.add(pln);
	}

	//Prints the inverted index node
	public void printInvertedIndexNode() {
		System.out.print(termId + " ");
		for(int i = 0; i < postingList.size(); i++) {
			postingList.get(i).printPostingListNode();
		}
		System.out.println();
	}

	/*Adds a posting list node to the posting list corresponding to this inverted index node
	docId - the docId for which the posting list node has to be added*/
	public void addPostingListNode(int docId) {
		PostingListNode newNode = new PostingListNode(docId, 1);
		postingList.add(newNode);
	}

	public int getTermId() {
		return termId;
	}

	public void setTermId(int termId) {
		this.termId = termId;
	}

	/*Update the posting list corresponding to this inverted index node
	If docId is already present in the posting list - increment the term frequency
	If docId is not present in the posting list - Add a posting list node to the posting list*/
	public void updatePostingList(int docId, Map<String, Integer> dfts, String currentTerm) {
		boolean found = false;
		for(int i = 0; i < postingList.size() && !found; i++) {
			if(postingList.get(i).getDocId() == docId) {
				postingList.get(i).incrementTermFreq();
				found = true;
			}
		}
		if(!found) {
			addPostingListNode(docId);
			dfts.replace(currentTerm, dfts.get(currentTerm) + 1);
		}
	}

	@Override
	public String toString() {
		return "InvertedIndexNode [termId=" + termId + ", postingList=" + postingList + "]";
	}
}
