package tree;

public class DataPoints {

	public static int [] evaluateDecisionTree(int testCases, int variables, int targetEnumerations, int [] enumerationMatrix, 
			int [] [] dataMatrix, int [] targetMatrix, int [][] unknownDataMatrix, int numOfUnknownCases){
		
		int [] ignoreVariables = new int [variables]; //a way for us to determine if a variable will be ignored
		int [] ignoreCases = new int [testCases]; //a way to determine if a case is to be ignored
		
		//in both instances, a 0 meant do not ignore, and a 1 meant ignore. It is an int in case more cases are added later.
		
		for(int i = 0; i < variables; i++){
			ignoreVariables[i] = 0;
		}
		
		for(int i = 0; i < testCases; i++){
			ignoreCases[i] = 0;
		}
		
		Node root = null;
		root = Tree.createTree(testCases, variables, targetEnumerations, enumerationMatrix, dataMatrix, targetMatrix,
				ignoreVariables, ignoreCases, 0);
		root.printTree();
		
		int [] results = applyTestedTree(numOfUnknownCases, unknownDataMatrix, root);

		return results;
	}
	
	
	//Takes the new tree based on the known data matrix and runs the unknown scripts through it
	private static int [] applyTestedTree(int testCases, int [] [] dataMatrix, Node root){
		int [] targetMatrix = new int [testCases];
		int value = 0;
		int variableValue = 0;
		
		for(int i = 0; i < testCases; i++)
		{
			Node currentNode = root;
			while(true)
			{
				value = currentNode.getValue();
				if(value < 0){
					targetMatrix[i] = -1 * (value + 1);
					break;
				}
				
				variableValue = dataMatrix[i][value];
				currentNode = currentNode.getSpecifiedChild(variableValue);
			}
		}
		
		return targetMatrix;
	}
	
}
