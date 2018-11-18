
public class Tree23
{
	private Node23 root = new Node23();
	
	//find needs no modification from the 2-3-4 tree
	public int find(long key)
	{
		Node23 curNode = root;
		int childNumber;
		while(true)
		{
			if( (childNumber=curNode.findItem(key)) != -1)
				return childNumber;			//found it
			else if( curNode.isLeaf() )
				return -1;					//couldn't find it
			else
				curNode = getNextChild(curNode, key);
		} //end while
	}
	
	//dives down to leaf and inserts the new DataItem if it's not full.
	//otherwise, splits upwards through all full nodes and modifies the tree.
	public void insert(long dValue)
	{
		Node23 curNode = root;
		DataItem tempItem = new DataItem(dValue);
		
		//Find point of insertion
		while(true)
		{
			if (curNode.isLeaf() )	//if node is leaf,
				break;					//go insert
			else
				curNode = getNextChild(curNode, dValue);
		}//end while
		
		if(curNode.isFull())	//if leaf is full, we have to split it
		{						//and all full parents
			split(curNode, tempItem);
		}
		else					//we lucked out, just put it in the leaf
			curNode.insertItem(tempItem);	//insert new DataItem
	}
		
	//split a full node, insert the middle value into the parent
	public Node23 split(Node23 thisNode, DataItem newItem)
	{
		Node23 parent = thisNode.getParent();
		
		//sort the two values from the node as well as the new item
		//ugly sort, I know, but it's easy and shouldn't hinder performance
		DataItem[] sortedItems = new DataItem[3];
		if(newItem.dData <= thisNode.getItem(0).dData)
		{
			sortedItems[0] = newItem;
			sortedItems[1] = thisNode.getItem(0);
			sortedItems[2] = thisNode.getItem(1);
		}
		else if(newItem.dData <= thisNode.getItem(1).dData)
		{
			sortedItems[0] = thisNode.getItem(0);
			sortedItems[1] = newItem;
			sortedItems[2] = thisNode.getItem(1);
		}
		else
		{
			sortedItems[0] = thisNode.getItem(0);
			sortedItems[1] = thisNode.getItem(1);
			sortedItems[2] = newItem;
		}
		
		//empty out the current node and put the lowest value as only data item
		thisNode.removeItem();
		thisNode.removeItem();
		thisNode.insertItem(sortedItems[0]);
		
		//make a new right node for the largest value
		Node23 newRightNode = new Node23();
		newRightNode.insertItem(sortedItems[2]);
		
		//if this node is the root, make a new root
		if(thisNode == this.root)
		{
			Node23 newRoot = new Node23();
			newRoot.insertItem(sortedItems[1]);
			newRoot.connectChild(0, thisNode);
			newRoot.connectChild(1, newRightNode);
			this.root = newRoot;
			return newRightNode;
		}
		//if the parent is full, call split again
		//then readjust child relationships
		else if(parent.isFull())
		{
			Node23 sibling = split(parent, sortedItems[1]);
			
			//case 1: split node is leftmost child of parent
			if(thisNode.equals(parent.getChild(0)))
			{
				Node23 childB = parent.disconnectChild(1);
				Node23 childC = parent.disconnectChild(2);
				parent.connectChild(1, newRightNode);
				sibling.connectChild(0, childB);
				sibling.connectChild(1, childC);
			}
			else if(thisNode.equals(parent.getChild(1)))
			{
				Node23 childC = parent.disconnectChild(2);
				sibling.connectChild(0, newRightNode);
				sibling.connectChild(1, childC);
			}
			else
			{
				parent.disconnectChild(2);
				sibling.connectChild(0, thisNode);
				sibling.connectChild(1, newRightNode);
			}
				
			return newRightNode;
		}
		//otherwise, add the middle value to the parent
		else
		{
			parent.insertItem(sortedItems[1]);
		
			//--then connect the newRightNode to parent--//
			
			//if the new Right node is made from the leftmost child of parent...
			if(thisNode.equals(parent.getChild(0)))
			{
				//shift current middle child to the right
				parent.connectChild(2, parent.disconnectChild(1));
				//and put the newRightNode as middle child
				parent.connectChild(1, newRightNode);
			}
			//otherwise just attach new Right node as rightmost child of parent
			else
				parent.connectChild(2, newRightNode);
		}
		
		return newRightNode;
	}//end split()

	public Node23 getNextChild(Node23 theNode, long theValue)
	{
		int j;
		//assumes node is not empty, not full, not a leaf
		int numItems = theNode.getNumItems();
		for(j=0; j<numItems; j++)		//for each item in node
		{								//are we less?
			if( theValue < theNode.getItem(j).dData)
				return theNode.getChild(j); //return left child
		} //end for						//we're greater, so
		return theNode.getChild(j);		//return right child
	}
	
	public void displayTree()
	{
		recDisplayTree(root, 0, 0);
	}
	
	private void recDisplayTree(Node23 thisNode, int level, int childNumber)
	{
		System.out.print("level="+level+" child="+childNumber+" ");
		thisNode.displayNode();
		
		//call ourselves for each child of this node
		int numItems = thisNode.getNumItems();
		for(int j = 0; j < numItems+1; j++)
		{
			Node23 nextNode = thisNode.getChild(j);
			if(nextNode != null)
				recDisplayTree(nextNode, level+1, j);
			else
				return;
		}
	}
} //end class tree23