package tree;

public class Tree {
	
	//builds the decision tree per node. is recursive
	public static Node createTree(int testCases, int variables, int targetEnumerations, int [] enumerationMatrix, 
			int [] [] dataMatrix, int [] targetMatrix, int [] ignoreVariables, int [] ignoreCases, int mostCommonLastGen){
		
		int mostCommonThisGen = mostCommonTarget(targetMatrix, targetEnumerations, ignoreCases);
		int validVariables = 0; //tells us the number of variables still to apply this generation
		int validCases = 0; // tells us the number of cases still to apply this generation
		
		for(int i = 0; i < variables; i++){
			if(ignoreVariables[i] != 1)
				validVariables++;
		}
		for(int i = 0; i < testCases; i++){
			if(ignoreCases[i] != 1)
				validCases++;
		}
		
		// if the valid variables or cases has run out, we are at a leaf. Setting the value to a negative number helps see the tree when it is 
		// constructed and has no bearing on the actual value.
		if(validVariables == 0){
			int targetValue = mostCommonTarget(targetMatrix, targetEnumerations, ignoreCases);
			Node child = new Node((-1*targetValue) - 1);
			
			/* check if leaf nodes are well ordered
			if(validCases != 0){
				double currentEntropy = sumEntropy(targetMatrix, targetEnumerations, ignoreCases, validCases);
				System.out.println(currentEntropy + "    " + targetValue);
			}
			*/
			
			return child;
		}
		
		if(validCases == 0){
			Node child = new Node((-1*mostCommonLastGen) - 1);
			return child;
		}
		
		double currentEntropy = sumEntropy(targetMatrix, targetEnumerations, ignoreCases, validCases);
		if(currentEntropy == 0){ //if there is no entropy gain, make a leaf
			int targetValue = mostCommonTarget(targetMatrix, targetEnumerations, ignoreCases);
			Node child = new Node((-1*targetValue) - 1);
			return child;
		}
		
		//initialize variables
		double gain;
		double bestGain = -1;
		int bestVariable = -1;
		
		int enumerations;
		int [] variableArray = new int [testCases];

		//find the best variable to split tree with
		for(int i = 0; i < variables; i++)
		{
			//do not consider variables which have already been used to split in a previous node
			if(ignoreVariables[i] == 1){
				continue;
			}
			//find number of enumerations for current variable
			enumerations = enumerationMatrix[i];
			
			//collects value of variable i for each valid test case
			for(int j = 0; j < testCases; j++){
				if(ignoreCases[j] == 1){
					variableArray[j] = -1;
				}
				else{
					variableArray[j] = dataMatrix[j] [i];
				}
			}
			
			//array will determine how variable i will split the tree
			int [] [] subNodes = new int [enumerations] [targetEnumerations];
			
			//initialize array to 0 to be safe
			for(int j = 0; j < enumerations; j++)
			{
				for(int k = 0; k < targetEnumerations; k++)
				{
					subNodes[j][k] = 0;
				}
			}
			
			//fill in subNodes array
			for(int j = 0; j < testCases; j++)
			{
				if(ignoreCases[j] == 1){
					continue;
				}
				int valueOfVariable = variableArray[j];
				int valueOfTarget = targetMatrix[j];
				
				subNodes[valueOfVariable][valueOfTarget]++;
			}
			
			//finds entropy of each subNode, calculates total gain of splitting with variable i
			gain = currentEntropy;
			for(int j = 0; j < enumerations; j++)
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
				
				gain -= (size/validCases) * entropy;
			}
			
			//finds best gain out of all valid variables
			if(bestVariable == -1)
			{
				bestGain = gain;
				bestVariable = i;
			}
			else
			{
				if(gain > bestGain){
					bestGain = gain;
					bestVariable = i;
				}
			}
		}
		
		//create an updated copy of ignoreVariables array for recursive use
		int [] ignoreVariablesCopy = new int [variables];
		
		for(int i = 0; i < variables; i++){
			ignoreVariablesCopy [i] = ignoreVariables [i];
		}
		ignoreVariablesCopy[bestVariable] = 1;
		
		//creates parent Node which contains the bestVariable
		Node parent = new Node(bestVariable);
		
		//runs createTree algorithm on each of the children nodes generated by the best variable, recursively creates the tree
		for(int i = 0; i < enumerationMatrix[bestVariable]; i++)
		{	
			//creates an updated copy of ignoreCases array (different for each subNode) for recursive use
			int [] ignoreCasesCopy = new int [testCases];
			for(int j = 0; j < testCases; j++)
			{
				ignoreCasesCopy [j] = ignoreCases [j];
				if(dataMatrix [j][bestVariable] != i){
					ignoreCasesCopy [j] = 1;
				}
			}
			
			Node child = null;
			child = createTree(testCases, variables, targetEnumerations, enumerationMatrix, dataMatrix, targetMatrix,
					ignoreVariablesCopy, ignoreCasesCopy, mostCommonThisGen);
			//if(child != null){
				parent.addChild(child, i);
			//}
		}
		return parent;
	}

	//returns the total entropy given the current valid information in a node
	private static double sumEntropy(int [] array, int enumerations, int [] ignoreCases, int validCases)
	{	
		double entropy = 0;
		for(int i = 0; i < enumerations; i++)
		{
			double instances = instancesOf(array, i, ignoreCases);
			if(instances != 0){
				entropy -= (instances/validCases) * Math.log(instances/validCases);
			}
		}
		return entropy;
	}

	//determines the most common target case
	private static int mostCommonTarget(int [] array, int enumerations, int [] ignoreCases){
		int mostCommonTarget = -1;
		int appearancesOfMostCommon = -1;
		for(int i = 0; i < enumerations; i++)
		{
			int instances = instancesOf(array, i, ignoreCases);
			if(instances > appearancesOfMostCommon){
				appearancesOfMostCommon = instances;
				mostCommonTarget = i;
			}
		}
		
		return mostCommonTarget;
		
	}

	private static int instancesOf(int [] array, int variable, int [] ignoreCases)
	{
		int counter = 0;
		for(int i = 0; i < array.length; i++)
		{
			if(array[i] == variable && ignoreCases [i] != 1)
				counter++;
		}
		return counter;
	}
	
	
}