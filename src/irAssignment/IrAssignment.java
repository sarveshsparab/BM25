package irAssignment;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class IrAssignment {

    public static void main(String args[]) throws IOException {
    	Map<Integer, String> documentMap = new HashMap<Integer, String>();
    	populateDocumentMap(documentMap);
    	Double[] percents = {100.0, 50.0, 25.0, 12.5, 10.0};
    	for(int i = 0; i < percents.length; i++) {
    		createIndexForPercent(percents[i], documentMap);
    	}
		writeDocumentMap(documentMap);
    }

    //Writes the document map to a file
    public static void writeDocumentMap(Map<Integer, String> documentMap) throws IOException {
    	File documentMapFile = new File("index/globalDocumentMap.txt");
    	if(!documentMapFile.exists()) {
    		documentMapFile.createNewFile();
    	}
		FileWriter fw = new FileWriter(documentMapFile.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		for(Integer key: documentMap.keySet()) {
			String value = documentMap.get(key);
			bw.write(key.toString() + " " + value);
			bw.newLine();
		}
		bw.close();
    }

    /*Creates inverted index for the specified percent of the corpus
    percent - the percent of corpus for which the document partitioning has to be done
    documentMap - the map of the documents present in the corpus*/
    public static void createIndexForPercent(double percent, Map<Integer, String> documentMap) throws IOException {
    	System.out.println("Creating index for " + percent + " ....");
    	int numDocs = documentMap.size();
    	Map<String, Integer> dfts = new HashMap<String, Integer>();
    	dfts.put("=", 0);
		int docsPerIndex = (int) Math.floor(numDocs / (100.0/percent));
		int times = (int) Math.floor((double)(numDocs) / docsPerIndex);
		int currentDocId = 0;
		int i;
		File index = new File("index");
    	if(!index.exists()) {
    		index.mkdir();
    	}
		for(i = 0; i < times - 1; i++) {
			InvertedIndex invertedIndex = createIndexForDocs(currentDocId, currentDocId + docsPerIndex - 1, documentMap, dfts);
			currentDocId += docsPerIndex;
			writeInvertedIndexToFile(invertedIndex, percent, i);
		}
		InvertedIndex invertedIndex = createIndexForDocs(currentDocId, numDocs - 1, documentMap, dfts);
		writeInvertedIndexToFile(invertedIndex, percent, i);
		writeDftsIntoFile(dfts, percent);
		System.out.println("Index creation done for " + percent);
    }

    //Writes the term frequencies of each term into a file
    public static void writeDftsIntoFile(Map<String, Integer> dfts, double percent) throws IOException {
    	File dftFile = new File("index/"+percent+"/dftFile.txt");
    	if(!dftFile.exists()) {
    		dftFile.createNewFile();
    	}
		FileWriter fw = new FileWriter(dftFile.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		for(String key: dfts.keySet()) {
			Integer value = dfts.get(key);
			bw.write(key + " " + value);
			bw.newLine();
		}
		bw.close();
    }

    //Writes the inverted index to a file
    public static void writeInvertedIndexToFile(InvertedIndex invertedIndex, double percent, int indexNum) throws IOException {
    	File index = new File("index/"+percent);
    	if(!index.exists()) {
    		index.mkdir();
    	}
    	File indexFile = new File("index/"+percent+"/indexFile"+indexNum+".txt");
    	if(!indexFile.exists()) {
    		indexFile.createNewFile();
    	}
		FileWriter fw = new FileWriter(indexFile.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		invertedIndex.writeToFile(bw);
		//System.out.println(i + "Here");
		//invertedIndex.printInvertedIndex();
		bw.close();

		File termMapFile = new File("index/"+percent+"/termMapFile"+indexNum+".txt");
    	if(!termMapFile.exists()) {
    		termMapFile.createNewFile();
    	}
		fw = new FileWriter(termMapFile.getAbsoluteFile());
		bw = new BufferedWriter(fw);
		for(String key: invertedIndex.getTermMap().keySet()) {
			String value = Integer.toString(invertedIndex.getTermId(key));
			bw.write(key + " " + value);
			bw.newLine();
		}
		bw.close();

		File documentMapFile = new File("index/"+percent+"/documentMapFile"+indexNum+".txt");
    	if(!documentMapFile.exists()) {
    		documentMapFile.createNewFile();
    	}
		fw = new FileWriter(documentMapFile.getAbsoluteFile());
		bw = new BufferedWriter(fw);
		for(Integer key: invertedIndex.getDocumentMap().keySet()) {
			String value = invertedIndex.getDocumentName(key);
			bw.write(key.toString() + " " + value);
			bw.newLine();
		}
		bw.close();
    }

    //Populates the document map by associating a document id for each document
    public static void populateDocumentMap(Map<Integer, String> documentMap) {
    	 File directory = new File("corpus");

         File[] fList = directory.listFiles();
         int numDocuments = 0;
         for (File file : fList){

             //System.out.println(file.getName());
         	documentMap.put(numDocuments, file.getName());
         	numDocuments++;
         }
    }

    /*Creates inverted index for the number of documents specified*/
    public static InvertedIndex createIndexForDocs(int docIdStart, int docIdEnd, Map<Integer, String> documentMap, Map<String, Integer> dfts) {
    	InvertedIndex invertedIndex = new InvertedIndex();
    	for(int i = docIdStart; i <= docIdEnd; i++) {
        	createIndexForFile(invertedIndex, documentMap.get(i), i, dfts);
        	invertedIndex.addDocument(i, documentMap.get(i));
    	}
    	return invertedIndex;
    }

    //Creates index for the given file
    public static void createIndexForFile(InvertedIndex invertedIndex, String fileName, int docId, Map<String, Integer> dfts) {
    	BufferedReader br = null;
    	//System.out.println(fileName);
		try {

			String sCurrentLine;

			br = new BufferedReader(new FileReader("corpus/" + fileName));

			while ((sCurrentLine = br.readLine()) != null) {
				String[] currentTerms = sCurrentLine.split(" ");
				//System.out.println(sCurrentLine);
				insertTermsIntoIndex(invertedIndex, currentTerms, docId, dfts);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
    }

    //Checks if a word is a stop word
    public static boolean stopList(String str){
		String stopStr = "a about above after again against all am an and any are aren't as at be because been before being below between both but by can't cannot could couldn't did didn't do does doesn't doing don't down during each few for from further had hadn't has hasn't have haven't having he he'd he'll he's her here here's hers herself him himself his how how's i i'd i'll i'm i've if in into is isn't it it's its itself let's me more  most mustn't my myself no nor not of off on once only or other ought our  ours ourselves out over own same shan't she she'd she'll she's should shouldn't so some such than that that's the their theirs them themselves then there there's these they they'd they'll they're they've this those through to too under until up very was wasn't we we'd we'll we're we've were weren't what what's when when's where where's which while who who's whom why why's with won't would wouldn't you you'd you'll you're you've your yours yourself yourselves";
		if (stopStr.toLowerCase().indexOf(str.toLowerCase()) != -1 )
			return true;
		else
			return false;
	}

    //Removes punctuation and case
    private static String remPuncAndCase(String str){
		//return str.toLowerCase();
    	return str.replaceAll("([a-z]+)[?:!.,;)\"']*", "$1").toLowerCase();
		//return str.replaceAll("^\\p{Punct}+|\\p{Punct}+$", "").toLowerCase();
	}

    //Inserts the given term to the corresponding inverted index
    public static void insertTermsIntoIndex(InvertedIndex invertedIndex, String[] terms, int docId, Map<String, Integer> dfts) {
    	for(int i = 0; i < terms.length; i++) {
    		String currentTerm = remPuncAndCase(terms[i]);
    		if(!stopList(currentTerm)) {
    		if(!invertedIndex.containsTerm(currentTerm)) {
    			if(dfts.containsKey(currentTerm)) {
    				dfts.replace(currentTerm, dfts.get(currentTerm) + 1);
    			} else {
    				dfts.put(currentTerm, 1);
    			}
    			invertedIndex.addNode(invertedIndex.getNumTerms(), docId);
    			invertedIndex.addTerm(currentTerm, invertedIndex.getNumTerms());
    			//System.out.println(invertedIndex.getNextTermId() + " " + currentTerm);
    		} else {
    			//System.out.println(currentTerm + "Hey" + invertedIndex);
    			//System.out.println(documentMap);
    			//System.out.println(termMap);
    			invertedIndex.updateNode(invertedIndex.getTermId(currentTerm), docId, dfts, currentTerm);
    		}
    		dfts.replace("=", dfts.get("=") + 1);
    		}
    	}
    }
}
