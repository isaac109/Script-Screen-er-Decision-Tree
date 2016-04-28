package tree;

import java.util.LinkedList;

public class Node {
	private int value;
	private LinkedList<Node> children;
	private LinkedList<Integer> parentSplitValues;
	
	public Node(int value){
		this.value = value;
		children = new LinkedList<Node>();
		parentSplitValues = new LinkedList<Integer>();
	}
	
	public void addChild(Node child, int splitValue){
		children.add(child);
		parentSplitValues.add(new Integer(splitValue));
	}
	
	public int getValue(){
		return this.value;
	}
	
	public Node getSpecifiedChild(int i){
		return this.children.get(i);
	}
	
	public void printTree(){
		
		System.out.println(this.value);
		Node currentNode;
		LinkedList<Node> queue = new LinkedList<Node>();
		queue.add(this);
		queue.add(null);
		while(true){
			if(queue.size() == 0){
				break;
			}
			
			currentNode = queue.removeFirst();
			if(currentNode == null){
				if(queue.size() > 0){
					queue.add(null);
				}
				System.out.println();
				continue;
			}
			
			for(int i = 0; i < currentNode.children.size(); i++)
			{
				if(currentNode.children.get(i) != null){
					System.out.print(currentNode.children.get(i).value + "," + currentNode.parentSplitValues.get(i) + " ; ");
					queue.add(currentNode.children.get(i));
				}
				else{
					System.out.print("C," + currentNode.parentSplitValues.get(i) + " ; ");
				}
			}
		}
	}


}