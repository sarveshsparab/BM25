package irAssignment;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvertedIndex {

	private List<InvertedIndexNode> invertedIndex;
	private Map<Integer, String> documentMap;
	private Map<String, Integer> termMap;
	private int numTerms;

	/*Creates am inverted index
	invertedIndex - list of inverted index nodes. Each inverted index nodes represents a term
	documentMap - Map of document name to document Id (for the documents this inverted index is handling)
	termMap - Map of term to termId (for whichever terms this inverted index is handling)*/
	public InvertedIndex() {
		invertedIndex = new ArrayList<InvertedIndexNode>();
		documentMap = new HashMap<Integer, String>();
    	termMap = new HashMap<String, Integer>();
    	numTerms = 0;
	}

	/*Add document to document map
	docName - the name of the document to be added
	docId - the id to be assigned to this document
	*/
	public void addDocument(Integer docId, String docName) {
		documentMap.put(docId, docName);
	}

	//Get the id of the next term to be inserted in the term map
	public int getNextTermId() {
		return termMap.size();
	}

	//check if the termMap contains the given term
	public boolean containsTerm(String term) {
		return termMap.containsKey(term);
	}

	//get the total number of terms handled by this inverted index
	public int getNumTerms() {
		return numTerms;
	}

	/*add a term to the termMap
	term - the term to be added
	termId - the id to be assigned to the term*/
	public void addTerm(String term, Integer termId) {
		termMap.put(term, termId);
		numTerms++;
	}

	public Map<String, Integer> getTermMap() {
		return termMap;
	}

	public Map<Integer, String> getDocumentMap() {
		return documentMap;
	}

	public String getDocumentName(Integer docId) {
		return documentMap.get(docId);
	}

	public int getTermId(String term) {
		return termMap.get(term);
	}

	//Prints the inverted index
	public void printInvertedIndex() {
		for(int i = 0; i < invertedIndex.size(); i++) {
			invertedIndex.get(i).printInvertedIndexNode();
		}
	}

	public List<InvertedIndexNode> getInvertedIndex() {
		return invertedIndex;
	}

	/*Writes the inverted index to a file
	bw - the pointer to the buffer where the output contents to the file are being written*/
	public void writeToFile(BufferedWriter bw) throws IOException {
		for(int i = 0; i < invertedIndex.size(); i++) {
			invertedIndex.get(i).writeInvertedIndexNode(bw);

		}
	}

	/*Add a node to the inverted index
	termId - the termId for which the inverted index node has to be created
	docId -  the docId of the document which has to be inserted into the posting list of the corresponding term*/
	public void addNode(int termId, int docId) {
		InvertedIndexNode newNode = new InvertedIndexNode(termId);
		newNode.addPostingListNode(docId);
		invertedIndex.add(newNode);
	}

	/*Update the inverted index node*/
	public void updateNode(int termId, int docId, Map<String, Integer> dfts, String currentTerm) {
		int pos = findTermPosition(termId);
		InvertedIndexNode tobeUpdated = invertedIndex.get(pos);
		tobeUpdated.updatePostingList(docId, dfts, currentTerm);
	}

	//Find the term position in the list of inverted index nodes
	public int findTermPosition(int termId) {
		boolean found =  false;
		int i = 0, retVal = -1;
		while(!found) {
			if(invertedIndex.get(i).getTermId() == termId) {
				retVal = i;
				found = true;
			} else {
				i++;
			}
		}
		return retVal;
	}

	public void addIntoInvertedIndex(InvertedIndexNode iin) {
		invertedIndex.add(iin);
	}

	@Override
	public String toString() {
		return "InvertedIndex [invertedIndex=" + invertedIndex + "]";
	}

}
