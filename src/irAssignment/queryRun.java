package irAssignment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class queryRun {

	/*Takes a query from the user, runs this query for 100%, 50%, 25%, 12.5% and 10% document partitioning of the corpus.
	For parallel processing, threads are created*/
	public static void main(String[] args) throws NumberFormatException, IOException, InterruptedException {
		Map<Integer, String> documentMap = new HashMap<Integer, String>();
		Map<String, Long> timeMap = new HashMap<String, Long>();
    	populateDocumentMap(documentMap);
    	System.out.println("Enter the query: ");
    	Scanner in = new Scanner(System.in);
		String query = in.nextLine();
		String[] queryTerms = query.split(" ");

    	File directory = new File("index");

        File[] fList = directory.listFiles();
        for (File file : fList){
        	if(file.isDirectory()) {

        		String percent = file.getName();

        		int times = (int) Math.ceil((100.0 / Double.parseDouble(percent)));
        		Thread[] threads = new Thread[times];
        		ComputeScore[] computeScores = new ComputeScore[times];
        		long startTime = System.nanoTime();
        		for(int i = 0; i < times; i++) {
        			computeScores[i] = new ComputeScore(percent, i, queryTerms, documentMap.size());
        			threads[i] = new Thread(computeScores[i]);
        			threads[i].setName("Index"+percent+"Thread"+i);
        			threads[i].start();
        		}
        		for(int i = 0; i < times; i++) {
        			threads[i].join();
        		}
        		List<Score> scores = new ArrayList<Score>();
        		for(int i = 0; i < documentMap.size(); i++) {
        			scores.add(new Score(0, i));
        		}
        		for(int i = 0; i < times; i++) {
        			Map<Integer, Double> scoreMap = computeScores[i].getScoreMap();
        			for(Integer key : scoreMap.keySet()) {
        				scores.remove(key.intValue());
        				scores.add(key.intValue(), new Score(scoreMap.get(key), key.intValue()));
        			}
        		}
        		Collections.sort(scores);
        		File result = new File("result");
            	if(!result.exists()) {
            		result.mkdir();
            	}
            	long elapsedTime = System.nanoTime() - startTime;
            	timeMap.put(percent, new Long(elapsedTime));
        		writeScoresAndTimeToFile(scores, documentMap, percent, elapsedTime);
        		System.out.println("Execution time of " + percent + " is " + elapsedTime);
        	}


        }
        writeTrend(timeMap);
        in.close();
	}

	//Write the time taken by different percent partitions of the corpus into a file
	public static void writeTrend(Map<String, Long> timeMap) throws IOException {
		File trendFile = new File("result/trend.txt");
    	if(!trendFile.exists()) {
    		trendFile.createNewFile();
    	}
		FileWriter fw = new FileWriter(trendFile.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		for(String key : timeMap.keySet()) {
			long elapsedTime = timeMap.get(key).longValue();
			bw.write(key + " " + ((double)elapsedTime / 1000000000));
			bw.newLine();
		}
		bw.close();
	}

	//Write the document scores and the total time taken into a file
	public static void writeScoresAndTimeToFile(List<Score> scores, Map<Integer, String> documentMap, String percent, long elapsedTime) throws IOException {
		File scoreFile = new File("result/"+percent+".txt");
    	if(!scoreFile.exists()) {
    		scoreFile.createNewFile();
    	}
		FileWriter fw = new FileWriter(scoreFile.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write("Elapsed Time = " + ((double)elapsedTime / 1000000000) + "seconds");
		bw.newLine();
		bw.write("Documents               Scores");
		bw.newLine();
		for(int i = 0; i < scores.size(); i++) {
			String fileName = documentMap.get(scores.get(i).getDocId());
			bw.write(fileName + "            " + scores.get(i).getScore());
			bw.newLine();
		}
		bw.close();
	}

	//Populate the document map
	 public static void populateDocumentMap(Map<Integer, String> documentMap) throws NumberFormatException, IOException {
		 BufferedReader br = new BufferedReader(new FileReader("index/globalDocumentMap.txt"));
		String line;
			while ((line = br.readLine()) != null) {
				String[] words = line.split(" ");
				documentMap.put(Integer.parseInt(words[0]), words[1]);
			}
			br.close();
    }
}
