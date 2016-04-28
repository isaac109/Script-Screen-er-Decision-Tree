package tree;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Main {

	public static void main(String[] args) {
		/*These are the 13 best words that were found to have the greatest entropy gain in determining what genere a movie script belonged to. 
		 * Later in the script is a large block of commented out code. This was used to find these 13 words. It sifted through all the known scripts
		 * and gathered a matrix of all the uses of every word in all the scripts in total, while creating matrixies for the individual genres.
		 * Afterwards it calculated the entropy gain for a genre based on the keywords. This is what came of them. The reason why this is kept
		 * seperate and not run with the rest of the program every time is because it took 4 hours. Run that at your own risk/time.
		*/
		String [] variables = {" gun", " body", " blast", "laser", "ship", " light", " wind", "sky", " scream", " space", " shoot", " fire", " creature"};
		
		File [] dir = new File [7]; //all directories of known scripts
		dir[0] = new File("KnownAction");
		dir[1] = new File("KnownComedy");
		dir[2] = new File("KnownDrama");
		dir[3] = new File("KnownHorror");
		dir[4] = new File("KnownMusical");
		dir[5] = new File("KnownMystery");
		dir[6] = new File("KnownSciFi");
		
		File unknownScriptsDir = new File("UnknownScripts");
		
		int testCases = 0;
		int [] genreTestCases = new int [dir.length];
		for(int i = 0; i < dir.length; i++){
			testCases += dir[i].listFiles().length; //total number of known files files
			genreTestCases[i] = dir[i].listFiles().length; //number of known files by genre
		}
		
		int targetEnumerations = dir.length; //value for length of directories
		int [] targetMatrix = new int [testCases]; //a matrix for the known files
		String [] scripts = new String [testCases]; // file paths for all known scripts
		String [] unknownScripts = new String [unknownScriptsDir.listFiles().length]; // file paths for all unknown scripts
		
		int counter = 0;
		for(int i = 0; i < dir.length; i++){
			for(int j = 0; j < dir[i].listFiles().length; j++){
				scripts[counter] = dir[i].listFiles()[j].toString();
				targetMatrix[counter] = i;
				counter++;
			}
		}
		
		for(int i = 0; i < unknownScripts.length; i++){
			unknownScripts[i] = unknownScriptsDir.listFiles()[i].toString();
		}
		
		int wordCount = 0;
		int frequency = 0;
		double percent = 0;
		int rhymeCount = 0;
		int frequencyDiscrete = 0;
		int rhymeDiscrete = 0;
		
		
		//the following information is gathered on known scripts to act as a base knowledge for the decision trees
		int [] [] dataMatrix = new int [testCases][variables.length + 1]; //data matrix for known files by keyword leaving a space for rhyme discrete
		for(int i = 0; i < scripts.length; i++){
			rhymeCount = getNumRhymes(scripts[i]);
			wordCount = getWordCount(scripts[i]);
			percent = (rhymeCount * 10000.0)/wordCount;
			
			rhymeDiscrete = 0; //This was a way to add weight to a script if it has rhymes. A 0 meant a negligible number of rhymes and a 1 meant a non-negligible number of rhymes 
			if(percent >= 9){
				rhymeDiscrete = 1;
			}
			dataMatrix[i][0] = rhymeDiscrete;
		}
		
		
		for(int i = 0; i < scripts.length; i++)
		{
			wordCount = getWordCount(scripts[i]);
			
			for(int j = 0; j < variables.length; j++)
			{
				frequency = scriptSearch(scripts[i], variables[j]); //This determined the frequency of the use of a keyword and ranked it on a basis of 1-4
				percent = (frequency * 10000.0) /wordCount;

				frequencyDiscrete = 0;
				
				if(percent >= .75 && percent < 1.5)
					frequencyDiscrete = 1;
				else if(percent >= 1.5 && percent < 4)
					frequencyDiscrete = 2;
				else if(percent >= 4)
					frequencyDiscrete = 3;

				dataMatrix[i][j + 1] = frequencyDiscrete;	
			}
		}
		
		//The following matrix is generated as the information breakdown of our unknowns
		int [] [] unknownDataMatrix = new int [unknownScripts.length] [variables.length + 1];
		for(int i = 0; i < unknownScripts.length; i++){
			rhymeCount = getNumRhymes(unknownScripts[i]);
			wordCount = getWordCount(unknownScripts[i]);
			percent = (rhymeCount * 10000.0)/wordCount;
			
			rhymeDiscrete = 0;
			if(percent >= 9){
				rhymeDiscrete = 1;
			}
			unknownDataMatrix[i][0] = rhymeDiscrete;
		}	
		
		for(int i = 0; i < unknownScripts.length; i++){
			wordCount = getWordCount(unknownScripts[i]);
			
			for(int j = 0; j < variables.length; j++)
			{
				frequency = scriptSearch(unknownScripts[i], variables[j]);
				percent = (frequency * 10000.0) /wordCount;	
				
				frequencyDiscrete = 0;
				
				if(percent >= .75 && percent < 1.5)
					frequencyDiscrete = 1;
				else if(percent >= 1.5 && percent < 4)
					frequencyDiscrete = 2;
				else if(percent >= 4)
					frequencyDiscrete = 3;
					
				unknownDataMatrix[i][j + 1] = frequencyDiscrete;	
			}
		}
		
		//values that may need to be changed!!!
		//They act as holders for the number of states each variable can possess;
		int [] enumerationMatrix = new int [variables.length + 1];
		enumerationMatrix[0] = 2;
		for(int i = 0; i < variables.length; i++){
			enumerationMatrix[i + 1] = 4;
		}
		
		int [] results = DataPoints.evaluateDecisionTree(testCases, variables.length, targetEnumerations, 
								enumerationMatrix, dataMatrix, targetMatrix, unknownDataMatrix, unknownScripts.length);
		

		System.out.println("\n\nRESULTS: ");
		
		String [] genreResults = new String [results.length];
		for(int i = 0; i < genreResults.length; i++){
			switch(results[i]){
			case(0):
				genreResults[i] = "Action";
				break;
			case(1):
				genreResults[i] = "Comedy";
				break;
			case(2):
				genreResults[i] = "Drama";
				break;
			case(3):
				genreResults[i] = "Horror";
				break;
			case(4):
				genreResults[i] = "Musical";
				break;
			case(5):
				genreResults[i] = "Mystery";
				break;
			case(6):
				genreResults[i] = "Sci-Fi";
				break;
			default:
				genreResults[i] = "ERROR!!!";
			}
		}
		for(int i = 0; i < unknownScripts.length; i++){
			System.out.println(unknownScripts[i] + "   " + genreResults[i]);
		}
		
		
		
		/*
		//start calculating entropy
		int [] [] subNodes = new int [variableEnumerations] [targetEnumerations];
		
		for(int j = 0; j < variableEnumerations; j++)
		{
			for(int k = 0; k < targetEnumerations; k++)
			{
				subNodes[j][k] = 0;
			}
		}
		
		//fill in subNodes array
		for(int j = 0; j < testCases; j++)
		{
			
			int valueOfVariable = enumerationMatrix[j][0];
			int valueOfTarget = targetMatrix[j];
			
			subNodes[valueOfVariable][valueOfTarget]++;
		}
		double entropyWeighted = 0;
		for(int j = 0; j < variableEnumerations; j++) 
		{
			double size = 0;
			double entropy = 0;
			double probability = 0;
			for(int k = 0; k < targetEnumerations; k++)
			{
				size += subNodes[j][k];
			}
			//if there are no values for this enumeration of variable i, then proceed to next enumeration
			if(size == 0){
				continue;
			}
			for(int k = 0; k < targetEnumerations; k++)
			{
				probability = (subNodes[j][k])/size;
				
				if(probability != 0){
				entropy -= (probability) * Math.log(probability);
				}
			}
			
			System.out.println("Entropy: " + entropy);
			entropyWeighted += size*entropy/testCases;
		}
		System.out.println("\nWeightedEntropy: " + entropyWeighted);
		
		
		
		
		Map<String, Double> wordSet = new HashMap<String, Double>();
		Map<String, Double> wordSetThatMatters = new HashMap<String, Double>();
		
		double baselineEntropy = 1.8217857 - 0.2;

		BufferedReader stream = null;
		for (int i = 0; i < scripts.length; ++i)
		{
			try {
				stream = new BufferedReader(new FileReader(scripts[i]));
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				System.out.println("Problem reading file.");
			}
			String line;
			try {
				while((line = stream.readLine()) != null){
					int lastSpacePos = 0;
					for (int j = 0; j < line.length(); ++j)
					{
						if (line.charAt(j) == ' ' || line.charAt(j) == '\t' || line.charAt(j) == '"' || line.charAt(j) == '\'' || line.charAt(j) == ';' || line.charAt(j) == '\n'|| line.charAt(j) == '/'|| line.charAt(j) == '_'|| line.charAt(j) == '!'|| line.charAt(j) == '?'|| line.charAt(j) == ','|| line.charAt(j) == '-'|| line.charAt(j) == '.')
						{
							if (lastSpacePos >= 0 && lastSpacePos < line.length() && lastSpacePos < j)
							{
								//System.out.println(lastSpacePos + "," + j);
								String newWord = line.substring(lastSpacePos, j).toLowerCase();
								newWord = " " + newWord.substring(1);
								System.out.println(newWord);
								if (newWord.length() > 0)
								{
									wordSet.put(newWord, 0.0);
								}
							}
							lastSpacePos = j;
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Problem reading file.");
			}
			
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println(wordSet.size());
		
		
		int [] [] enumerationMatrix = new int [testCases][1];
		int wordIndex = 0;
		Iterator<Entry<String, Double>> iter = wordSet.entrySet().iterator();
		while(iter.hasNext())
		{
			System.out.println("Word number " + wordIndex++);
			Entry<String, Double> pair = iter.next();
			String searchString = pair.getKey();
			//System.out.println("Searching on |" + searchString + "|");
			for(int i = 0; i < scripts.length; i++){
				
				int frequency = scriptSearch(scripts[i], searchString);
				int wordCount = getWordCount(scripts[i]);
				double percent = (frequency * 10000.0) /wordCount;
				//int rhymeCount = getNumRhymes(scripts[i]);
				
				
				int frequencyDiscrete = 0;
				int rhymeDiscrete = 0;
				
				if(percent >= .75 && percent < 1.5)
					frequencyDiscrete = 1;
				else if(percent >= 1.5 && percent < 4)
					frequencyDiscrete = 2;
				else if(percent >= 4)
					frequencyDiscrete = 3;
				
				if(rhymeCount >= 20 && rhymeCount < 50)
					rhymeDiscrete = 1;
				else if(rhymeCount >= 50)
					rhymeDiscrete = 2;
				
				enumerationMatrix[i][0] = frequencyDiscrete;
				
			}
			
			//values that may need to be changed!!!
			int variableEnumerations = 4;
			
			//start calculating entropy
			int [] [] subNodes = new int [variableEnumerations] [targetEnumerations];
			
			for(int j = 0; j < variableEnumerations; j++)
			{
				for(int k = 0; k < targetEnumerations; k++)
				{
					subNodes[j][k] = 0;
				}
			}
			
			//fill in subNodes array
			for(int j = 0; j < testCases; j++)
			{
				
				int valueOfVariable = enumerationMatrix[j][0];
				int valueOfTarget = targetMatrix[j];
				
				subNodes[valueOfVariable][valueOfTarget]++;
			}
			double entropyWeighted = 0;
			for(int j = 0; j < variableEnumerations; j++) 
			{
				double size = 0;
				double entropy = 0;
				double probability = 0;
				for(int k = 0; k < targetEnumerations; k++)
				{
					size += subNodes[j][k];
				}
				//if there are no values for this enumeration of variable i, then proceed to next enumeration
				if(size == 0){
					continue;
				}
				for(int k = 0; k < targetEnumerations; k++)
				{
					probability = (subNodes[j][k])/size;
					
					if(probability != 0){
					entropy -= (probability) * Math.log(probability);
					}
				}
				
				//System.out.println("Entropy: " + entropy);
				entropyWeighted += size*entropy/testCases;
			}
			// Compare weighted entropy to base entropy with no decisions made yet.
			if (entropyWeighted < baselineEntropy)
			{
				wordSetThatMatters.put(searchString, entropyWeighted);
				System.out.println("MATTERS: " + searchString);
			}
			//System.out.println("\nWeightedEntropy: " + entropyWeighted);
			//end calculating entropy
		}
		System.out.println("Generation complete");
		
		Iterator<Entry<String, Double>> iter2 = wordSetThatMatters.entrySet().iterator();
		wordIndex = 0;
		while(iter2.hasNext())
		{
			Entry<String, Double> pair = iter2.next();
			System.out.println("Word number " + (wordIndex++) + ", " + pair.getKey() + ", entropy " + pair.getValue());
		}
		*/
	}

	//This searched the script for the number of times an item was used
	public static int scriptSearch(String script, String arg){
		int sum = 0;
		BufferedReader stream = null;
		
		try {
			stream = new BufferedReader(new FileReader(script));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			System.out.println("Problem reading file.");
			return 0;
		}
		String line;
		try {
			while((line = stream.readLine()) != null){
				sum += wordSearch(line, arg);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Problem reading file.");
			return 0;
		}
		
		try {
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return sum;
	}
	
	//returned the number of times a specific word was used in a line
	public static int wordSearch(String line, String arg){
		arg = arg.toLowerCase();
		line = line.toLowerCase();
		
		int sum = 0;
		
		for(int i = 0; i < (line.length() - arg.length()); i++){
			for(int j = 0; j < arg.length(); j++){
				if(line.charAt(i + j) != arg.charAt(j))
					break;
				else if(j == (arg.length() - 1))
					sum++;		
			}
		}
		
		return sum;
	}
	//this returned the total number of words in a script.
	public static int getWordCount(String script)
	{
		int sum = 0;
		BufferedReader stream = null;
		
		try {
			stream = new BufferedReader(new FileReader(script));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			System.out.println("Problem reading file.");
			return 0;
		}
		String line;
		try {
			while((line = stream.readLine()) != null){
				sum++;
				sum += wordSearch(line, " ");
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Problem reading file.");
			return 0;
		}
		
		try {
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return sum;
	}	
	
	//returns the number of rhymes in the script. This was a method used to help classify if a script was a musical
	//a rhyme is determined by having a matching last 3 characters excluding punctuation
	//we compared words together that contained punctuation as this is generally where rhymes are.
	public static int getNumRhymes(String script)
	{
		// 33-47, 58-63 = ASCII (decimal) values of punctuation chars.
		int numRhymes = 0;
		BufferedReader stream = null;
		
		try {
			stream = new BufferedReader(new FileReader(script));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			System.out.println("Problem reading file.");
			return 0;
		}

		String line;
		String prevRhyme = "";
		String prevWord = "";
		char[] puncChars = {'.', ',', ';', '?', '!'};
		try {
			while((line = stream.readLine()) != null){
				int lastSpace = 0;
				for (int i = 3; i < line.length(); ++i)
				{
					boolean isPunc = false;
					if(line.charAt(i) == ' ')
						lastSpace = i;
					for (int j = 0; j < 5; ++j)
					{
						if (puncChars[j] == line.charAt(i))
						{
							isPunc = true;
						}
					}
					if (isPunc)
					{
						String thisRhymeEnding = "!@#";
						String thisWord = line.substring(lastSpace, i);
						thisWord = thisWord.replaceAll("[^A-Za-z]+", "");
						if(thisWord.length() >= 3){
							thisRhymeEnding =  thisWord.substring(thisWord.length() - 3, thisWord.length());
							if (prevRhyme.length() > 0 && prevWord.length() > 0)
							{
								if (prevRhyme.compareTo(thisRhymeEnding) == 0 && !prevWord.equals(thisWord))
								{
									numRhymes++;
								}
							}
							prevRhyme = thisRhymeEnding;
							prevWord = thisWord;
						}
						
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Problem reading file.");
			return 0;
		}
		
		try {
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return numRhymes;
	}
}