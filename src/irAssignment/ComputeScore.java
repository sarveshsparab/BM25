package irAssignment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ComputeScore implements Runnable {

	private String percent;
	private int indexNum;
	private String[] queryTerms;
	private int minDocId;
	private int numDocs;
	private int totalDocs;
	private int totalLength;
	private List<Double> scores;
	private Map<Integer, Double> scoreMap;
	private long elapsedTime;

	/*Creates a runnable instance which can be associated with a thread
	This runnable instance will handle a part of corpus as specified by percent
	It will then calculate the scores for each document it is handling using the BM25 ranking function*/
	public ComputeScore(String percent, int indexNum, String[] queryTerms, int totalDocs) {
		this.percent = percent;
		this.indexNum = indexNum;
		this.queryTerms = queryTerms;
		this.totalDocs = totalDocs;
		minDocId = 0;
		numDocs = 0;
		totalLength = 0;
		scoreMap = new HashMap<Integer, Double>();
	}

	public int getTotalLength() {
		return totalLength;
	}

	/*Calculates the scores for the documents this runnable instance is handling*/
	@Override
	public void run() {
		long startTime = System.nanoTime();
		//System.out.println(Thread.currentThread().getName() + " starts....");
		List<Integer> lengthDocs = new ArrayList<Integer>();
		InvertedIndex invertedIndex = null;
		try {
			invertedIndex = readInvertedIndex(percent, indexNum, lengthDocs);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println("Inverted Index:");
		//invertedIndex.printInvertedIndex();
		Map<String, Integer> dfts = new HashMap<String, Integer>();
		try {
			readDftsAndTotalLength(dfts);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println("Length of documents in " + Thread.currentThread().getName() + " " + lengthDocs);
		//System.out.println("Dfts = " + dfts);
		//System.out.println("Total length = " + totalLength);
		double avgl = (double) totalLength / totalDocs;
		double b = 0.75, k1 = 1.5;
		scores = new ArrayList<Double>();
		for(int i = 0; i < numDocs; i++) {
			scores.add(0.0);
		}

		for(int i = 0; i < queryTerms.length; i++) {
			if(invertedIndex.getTermMap().containsKey(queryTerms[i])) {
				List<PostingListNode> postings = invertedIndex.getInvertedIndex().get(invertedIndex.getTermId(queryTerms[i])).getPostingList();
				int dft = dfts.get(queryTerms[i]);
				//System.out.println(postings);
				for(int j = 0; j < postings.size(); j++) {
					int docId = postings.get(j).getDocId();
					int termFreq = postings.get(j).getTermFreq();
					//double idf = Math.log((numDocs - dft + 0.5) / (dft + 0.5));
					double idf = Math.log((double)totalDocs / dft);
					//System.out.println(minDocId + " " + lengthDocs.size() + "e");
					double score = idf * (termFreq * (k1 + 1)) / (termFreq + (k1 *(1 - b + b * (lengthDocs.get(docId - minDocId)/avgl))));
					double oldScore = scores.get(docId - minDocId);
					//System.out.println(idf + " " + score + " " + avgl + " " + dft);
					scores.remove(docId - minDocId);
					scores.add(docId - minDocId, oldScore + score);
				}
			}
		}
		//System.out.println(scores);
		for(int i = 0; i < scores.size(); i++) {
			scoreMap.put(i + minDocId, scores.get(i));
		}
		elapsedTime = System.nanoTime() - startTime;
		System.out.println(Thread.currentThread().getName() + " ends.... " + "Time = " + elapsedTime);
	}

	public Map<Integer, Double> getScoreMap() {
		return scoreMap;
	}

	//Reads the global term frequencies of the terms, this will thereby be used in calculating the scores using the BM25 function
	public void readDftsAndTotalLength(Map<String, Integer> dfts) throws NumberFormatException, IOException {
		BufferedReader br = new BufferedReader(new FileReader("index/"+percent+"/dftFile.txt"));
		String line;
		while ((line = br.readLine()) != null) {
			String[] words = line.split(" ");
			if(words[0].equals("=")) {
				this.totalLength = Integer.parseInt(words[1]);
			} else {
				dfts.put(words[0], Integer.parseInt(words[1]));
			}
		}
		br.close();
	}

	//Reads the inverted index for the documents
	public InvertedIndex readInvertedIndex(String percent, int indexNum, List<Integer> lengthDocs) throws IOException {
		InvertedIndex invertedIndex = new InvertedIndex();
		minDocId = new Integer(readDocumentFile(invertedIndex.getDocumentMap(), percent, indexNum));
		numDocs = invertedIndex.getDocumentMap().size();
		readIndexFile(invertedIndex, numDocs, percent, indexNum, minDocId, lengthDocs);
		readTermFile(invertedIndex.getTermMap(), percent, indexNum);
		return invertedIndex;
	}

	//Read the document map file
	public int readDocumentFile(Map<Integer, String> docMap, String percent, int indexNum) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("index/"+percent+"/documentMapFile"+indexNum+".txt"));
		String line;
		int minDocId = Integer.MAX_VALUE;
		while ((line = br.readLine()) != null) {
			String[] words = line.split(" ");
			docMap.put(Integer.parseInt(words[0]), words[1]);
			if(Integer.parseInt(words[0]) < minDocId) {
				minDocId = Integer.parseInt(words[0]);
			}
		}
		br.close();
		return minDocId;
	}

	//Read the term map file
	public void readTermFile(Map<String, Integer> termMap, String percent, int indexNum) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("index/"+percent+"/termMapFile"+indexNum+".txt"));
		String line;
		while ((line = br.readLine()) != null) {
			String[] words = line.split(" ");
			termMap.put(words[0], Integer.parseInt(words[1]));
		}
		br.close();
	}

	//Read the inverted index file
	public void readIndexFile(InvertedIndex invertedIndex, int numDocs, String percent, int indexNum, int minDocId, List<Integer> lengthDocs) throws NumberFormatException, IOException {
		BufferedReader br = new BufferedReader(new FileReader("index/"+percent+"/indexFile"+indexNum+".txt"));
		String line;
		for(int i = 0; i < numDocs; i++) {
			lengthDocs.add(0);
		}
		while ((line = br.readLine()) != null) {
			String[] words = line.split(" ");
			InvertedIndexNode iiNode = new InvertedIndexNode(Integer.parseInt(words[0]));
			for(int i=1;i<words.length;i++){
				String[] pNodes = words[i].split("->");
				PostingListNode plNode = new PostingListNode(Integer.parseInt(pNodes[0]), Integer.parseInt(pNodes[1]));
				//System.out.println((Integer.parseInt(pNodes[0]) - minDocId) + "IndexNum" + indexNum + "MinDocId" + minDocId);
				int oldLength = lengthDocs.get(Integer.parseInt(pNodes[0]) - minDocId);
				lengthDocs.remove(Integer.parseInt(pNodes[0]) - minDocId);
				lengthDocs.add(Integer.parseInt(pNodes[0]) - minDocId, oldLength + Integer.parseInt(pNodes[1]));
				iiNode.addIntoPostingList(plNode);
			}
			invertedIndex.addIntoInvertedIndex(iiNode);
		}
		br.close();
		//System.out.println("Length docs" + lengthDocs);
	}

}
