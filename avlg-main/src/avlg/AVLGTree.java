package avlg;

import avlg.exceptions.UnimplementedMethodException;
import avlg.exceptions.EmptyTreeException;
import avlg.exceptions.InvalidBalanceException;

/** <p>{@link AVLGTree}  is a class representing an <a href="https://en.wikipedia.org/wiki/AVL_tree">AVL Tree</a> with
 * a relaxed balance condition. Its constructor receives a strictly  positive parameter which controls the <b>maximum</b>
 * imbalance allowed on any subtree of the tree which it creates. So, for example:</p>
 *  <ul>
 *      <li>An AVL-1 tree is a classic AVL tree, which only allows for perfectly balanced binary
 *      subtrees (imbalance of 0 everywhere), or subtrees with a maximum imbalance of 1 (somewhere). </li>
 *      <li>An AVL-2 tree relaxes the criteria of AVL-1 trees, by also allowing for subtrees
 *      that have an imbalance of 2.</li>
 *      <li>AVL-3 trees allow an imbalance of 3.</li>
 *      <li>...</li>
 *  </ul>
 *
 *  <p>The idea behind AVL-G trees is that rotations cost time, so maybe we would be willing to
 *  accept bad search performance now and then if it would mean less rotations. On the other hand, increasing
 *  the balance parameter also means that we will be making <b>insertions</b> faster.</p>
 *
 * @author Yuchen Liu
 *
 * @see EmptyTreeException
 * @see InvalidBalanceException
 * @see StudentTests
 */
public class AVLGTree<T extends Comparable<T>> {

    /* ********************************************************* *
     * Write any private data elements or private methods here...*
     * ********************************************************* */
	private class Node{
		private Node l_Child;
		private Node r_Child;
		private Node parent;
		private T data;
		
		private Node(T val) {
			l_Child = null;
			r_Child = null;
			parent = null;
			data = val;
		}
		
		private Node(Node other) {
			this.l_Child = other.l_Child;
			this.r_Child = other.r_Child;
			this.parent = other.parent;
			this.data = other.data;
		}
	}
	
	private Node root;
	private int size;
	private int max_Imbalance;
	
	private void r_Rotation(Node rotate_center) {
		Node center_p = rotate_center.parent;   //the parent of the Rotate Node
		Node center_l = rotate_center.l_Child;     //the node that will be parent after rotation
		Node center_l_r = rotate_center.l_Child.r_Child;   //the right child of the left Subtree of Rotate Node (this node will be move to be rotate_center's l_child)
		
		//connect new parent after rotation to the old parent
		if(center_p != null) {
			if(center_p.l_Child == rotate_center) {
				center_p.l_Child = rotate_center.l_Child;
			}else {
				center_p.r_Child = rotate_center.l_Child;
			}
		}
		
		//connect new parent after rotation to the old parent
		center_l.parent = center_p;
		
		//rotate center node
		center_l.r_Child = rotate_center;
		rotate_center.parent = center_l;
		
		//connect the right child of the subtree to center node
		rotate_center.l_Child = center_l_r;
		if(center_l_r != null) {
			center_l_r.parent = rotate_center;
		}
		
		if(center_l.parent == null) {
			this.root = center_l;
		}
	}
	
	private void l_Rotation(Node rotate_center) {
		Node center_p = rotate_center.parent;
		Node center_r = rotate_center.r_Child;
		Node center_r_l = rotate_center.r_Child.l_Child;
		
		if(center_p != null) {
			if(center_p.l_Child == rotate_center) {
				center_p.l_Child = rotate_center.r_Child;
			}else {
				center_p.r_Child = rotate_center.r_Child;
			}
		}
		
		center_r.parent = center_p;
		
		center_r.l_Child = rotate_center;
		rotate_center.parent = center_r;
		
		rotate_center.r_Child = center_r_l;
		if(center_r_l != null) {
			center_r_l.parent = rotate_center;
		}
			
		if(center_r.parent == null) {
			this.root = center_r;
		}
	}
	
	private void r_l_Rotation(Node rotate_center) {
		r_Rotation(rotate_center.r_Child);
		l_Rotation(rotate_center);
	}
	
	private void l_r_Rotation(Node rotate_center) {
		l_Rotation(rotate_center.l_Child);
		r_Rotation(rotate_center);
	}
	
	private int getNodeHeight(Node curr) {
    	if(curr == null) {
    		return -1;
    	}else if(curr.l_Child == null && curr.r_Child == null) {
    		return 0;
    	}else {
    		return Math.max(getNodeHeight(curr.l_Child), getNodeHeight(curr.r_Child)) + 1;
    	}
    }
	
    /* ******************************************************** *
     * ************************ PUBLIC METHODS **************** *
     * ******************************************************** */

    /**
     * The class constructor provides the tree with the maximum imbalance allowed.
     * @param maxImbalance The maximum imbalance allowed by the AVL-G Tree.
     * @throws InvalidBalanceException if maxImbalance is a value smaller than 1.
     */
    public AVLGTree(int maxImbalance) throws InvalidBalanceException {
        if (maxImbalance < 1) {
        	throw new InvalidBalanceException("The balance is smaller than 1!");
        }else {
        	this.root = null;
        	this.size = 0;
        	this.max_Imbalance = maxImbalance;
        }
    }

    /**
     * Insert key in the tree. You will <b>not</b> be tested on
     * duplicates! This means that in a deletion test, any key that has been
     * inserted and subsequently deleted should <b>not</b> be found in the tree!
     * s
     * @param key The key to insert in the tree.
     */
    public void insert(T key) {
        Node insert_node = new Node(key);
        
        if(this.size == 0) {
        	this.root = insert_node;
        }else {
        	insert_helper(this.root, insert_node);
        }
        this.size++;
    }
    
    private void insert_helper(Node curr, Node insert_node) {
    	Node curr_parent = null;
    	int l_or_r = -1;
    	
    	while(curr != null) {
    		if(curr.data.compareTo(insert_node.data) > 0) {
    			curr_parent = curr;
    			curr = curr.l_Child;
    			l_or_r = 0;
    		}else {
    			curr_parent = curr;
    			curr = curr.r_Child;
    			l_or_r = 1;
    		}
    	}
    	
    	if(l_or_r == 0) {
    		curr_parent.l_Child = insert_node;
    		insert_node.parent = curr_parent;
    	}else if(l_or_r == 1) {
    		curr_parent.r_Child = insert_node;
    		insert_node.parent = curr_parent;
    	}
    	
    	balanceTree(curr_parent);
    }
    
    private void balanceTree(Node curr) {
    	
    	while(curr != null) {
    		if(getNodeHeight(curr.l_Child) - getNodeHeight(curr.r_Child) > this.max_Imbalance) {  //left heavier
    			if(getNodeHeight(curr.l_Child.l_Child) - getNodeHeight(curr.l_Child.r_Child) >= 0) {   //Balance(Left_subtree_of_ROTATE NODE) > 0
    				r_Rotation(curr);
    			}else if(getNodeHeight(curr.l_Child.l_Child) - getNodeHeight(curr.l_Child.r_Child) < 0) {   //Balance(Left_subtree_of_ROTATE NODE) < 0
    				l_r_Rotation(curr);
    			}
    			
    			curr = curr.parent.parent; 
    		}else if(Math.abs(getNodeHeight(curr.l_Child) - getNodeHeight(curr.r_Child)) > this.max_Imbalance) {   //right heavier 
    			if(getNodeHeight(curr.r_Child.l_Child) - getNodeHeight(curr.r_Child.r_Child) > 0) {    //Balance(Right_subtree_of_ROTATE NODE) > 0
    				r_l_Rotation(curr);
    			}else if(getNodeHeight(curr.r_Child.l_Child) - getNodeHeight(curr.r_Child.r_Child) <= 0) {   //Balance(Right_subtree_of_ROTATE NODE) < 0
    				l_Rotation(curr);
    			}
    			
    			curr = curr.parent.parent;
    		}else {
    			curr = curr.parent;
    		}
    	}
    }

    /**
     * Delete the key from the data structure and return it to the caller.
     * @param key The key to delete from the structure.
     * @return The key that was removed, or {@code null} if the key was not found.
     * @throws EmptyTreeException if the tree is empty.
     */
    public T delete(T key) throws EmptyTreeException {
    	Node curr = this.root;
    	T val_return;
    	
        if(this.size == 0) {
        	throw new EmptyTreeException("This tree is empty");
        }
        
        //find key
        while(curr != null) {
        	if(curr.data.compareTo(key) == 0) {
        		break;  //have found key
        	}else if(curr.data.compareTo(key) < 0) {
        		curr = curr.r_Child;   // key is greater than current node
        	}else {
        		curr = curr.l_Child;   // key is less than current node
        	}
        }
        
        //key value not find
        if(curr == null) {
        	return null;
        }
        val_return = curr.data;
        
        delete_helper(curr);
        
        this.size--;
        
        return val_return;
    }
    
    private void delete_helper(Node delete_node) {
    	Node delete_node_p = delete_node.parent;
    	
    	if(delete_node.l_Child == null && delete_node.r_Child == null) {//test if delete_node has no child (root / leaf)
    		if(delete_node == this.root) {   //delete_node is root
    			delete_node.parent = null;
    			this.root = null;
    		}else if(delete_node_p.l_Child == delete_node){   //delete_node is leaf
    			delete_node_p.l_Child = null;
    			delete_node.parent = null;
    			
    			balanceTree(delete_node_p);
    		}else {
    			delete_node_p.r_Child = null;
    			delete_node.parent = null;
    			
    			balanceTree(delete_node_p);
    		}
    	}else if(delete_node.l_Child != null && delete_node.r_Child == null) {//if delete node has just one L_child (no R_child) 
    		Node delete_node_l = delete_node.l_Child;
    		
    		if(delete_node_p != null) {
    			if(delete_node_p.l_Child == delete_node) {
    				delete_node_p.l_Child = delete_node_l;
    				delete_node.parent = null;
    			}else {
    				delete_node_p.r_Child = delete_node_l;
    				delete_node.parent = null;
    			}
    		}else {
    			this.root = delete_node_l;
    		}
    		
    		delete_node.l_Child = null;
    		delete_node_l.parent = delete_node_p;
    		
    		balanceTree(delete_node_p);
    	}else if(delete_node.l_Child == null && delete_node.r_Child != null) {//if delete node has just one R_child (no L_child)
    		Node delete_node_r = delete_node.r_Child;
    		
    		if(delete_node_p != null) {
    			if(delete_node_p.l_Child == delete_node) {
    				delete_node_p.l_Child = delete_node_r;
    				delete_node.parent = null;
    			}else {
    				delete_node_p.r_Child = delete_node_r;
    				delete_node.parent = null;
    			}
    		}else {
    			this.root = delete_node_r;
    		}
    		
    		delete_node.r_Child = null;
    		delete_node_r.parent = delete_node_p;
    		
    		balanceTree(delete_node_p);
    	}else if(delete_node.l_Child != null && delete_node.r_Child != null) {//if delete node has just two Children
    		Node inorder_succ = find_inorderSuccessor(delete_node);
    		
    		if(delete_node.r_Child == inorder_succ) {   //in-order Successor is delete_node.right
    			delete_node.data = inorder_succ.data;
    			
    			delete_node.r_Child = inorder_succ.r_Child;
    			
    			if(inorder_succ.r_Child != null) {     //in-order Successor has no right subtree
    				inorder_succ.r_Child.parent = delete_node;
    			}
    			
    			inorder_succ.r_Child = null;
    			inorder_succ.parent = null;
    			
    			balanceTree(delete_node);
    		}else {   //in-order Successor is delete_node.right.left......left
    			Node inorder_succ_p = inorder_succ.parent;
    			delete_node.data = inorder_succ.data;
    			
    			if (inorder_succ.r_Child != null) {  //if in-order Successor has right subtree
    				inorder_succ.r_Child.parent = inorder_succ_p;
    				inorder_succ_p.l_Child = inorder_succ.r_Child;
    			}else {
    				inorder_succ_p.l_Child = null;
    			}
    			
    			inorder_succ.parent = null;
    			
    			balanceTree(inorder_succ_p);
    		}
    	}
    }
    
    private Node find_inorderSuccessor(Node curr) {
    	Node curr_p = null;
    	
    	curr = curr.r_Child;  // go one step to curr.right
    	
    	while(curr != null){   //while curr.left have node
    		curr_p = curr;
    		curr = curr.l_Child;
    	}
    	
    	return curr_p;
    }

    /**
     * <p>Search for key in the tree. Return a reference to it if it's in there,
     * or {@code null} otherwise.</p>
     * @param key The key to search for.
     * @return key if key is in the tree, or {@code null} otherwise.
     * @throws EmptyTreeException if the tree is empty.
     */
    public T search(T key) throws EmptyTreeException {
        if(this.size == 0) {
        	throw new EmptyTreeException("This is empty");
        }
        T val = null;
        Node curr = this.root;
        
        while(curr != null) {
        	if(curr.data.compareTo(key) == 0) {
        		val = curr.data;
        		break;  //have found key
        	}else if(curr.data.compareTo(key) < 0) {
        		curr = curr.r_Child;   // key is greater than current node
        	}else {
        		curr = curr.l_Child;   // key is less than current node
        	}
        }
        
        return val;
    }

    /**
     * Retrieves the maximum imbalance parameter.
     * @return The maximum imbalance parameter provided as a constructor parameter.
     */
    public int getMaxImbalance(){
        return this.max_Imbalance;
    }


    /**
     * <p>Return the height of the tree. The height of the tree is defined as the length of the
     * longest path between the root and the leaf level. By definition of path length, a
     * stub tree has a height of 0, and we define an empty tree to have a height of -1.</p>
     * @return The height of the tree. If the tree is empty, returns -1.
     */
    public int getHeight() {
        if(this.size == 0) {
        	return -1;
        }else {
        	return getNodeHeight(this.root);
        }
    }

    /**
     * Query the tree for emptiness. A tree is empty iff it has zero keys stored.
     * @return {@code true} if the tree is empty, {@code false} otherwise.
     */
    public boolean isEmpty() {
        return this.size == 0;
    }

    /**
     * Return the key at the tree's root node.
     * @return The key at the tree's root node.
     * @throws  EmptyTreeException if the tree is empty.
     */
    public T getRoot() throws EmptyTreeException{
    	if(this.size == 0) {
    		throw new EmptyTreeException("Empty Tree");
    	}
        return this.root.data;
    }


    /**
     * <p>Establishes whether the AVL-G tree <em>globally</em> satisfies the BST condition. This method is
     * <b>terrifically useful for testing!</b></p>
     * @return {@code true} if the tree satisfies the Binary Search Tree property,
     * {@code false} otherwise.
     */
    public boolean isBST() {
        return checkBST(this.root);
    }

    private boolean checkBST(Node curr) {
    	if(curr == null) {
    		return true;
    	}else {
    		if(curr.l_Child == null && curr.r_Child == null) {
    			return true;
    		}else if(curr.l_Child != null && curr.r_Child != null &&
    				curr.l_Child.data.compareTo(curr.data) < 0 &&  curr.r_Child.data.compareTo(curr.data) > 0) {
    			return checkBST(curr.l_Child) && checkBST(curr.r_Child);
    		}else if(curr.l_Child != null && curr.r_Child == null &&
    				curr.l_Child.data.compareTo(curr.data) < 0) {
    			return checkBST(curr.l_Child);
    		}else if(curr.l_Child == null && curr.r_Child != null &&
    				curr.r_Child.data.compareTo(curr.data) > 0) {
    			return checkBST(curr.r_Child);
    		}
    		return false;
    	}
    }
    
    /**
     * <p>Establishes whether the AVL-G tree <em>globally</em> satisfies the AVL-G condition. This method is
     * <b>terrifically useful for testing!</b></p>
     * @return {@code true} if the tree satisfies the balance requirements of an AVLG tree, {@code false}
     * otherwise.
     */
    public boolean isAVLGBalanced() {
    	return checkAVL(this.root);
    }
    
    private boolean checkAVL(Node curr){
        if (curr == null){
            return true;
        }else{
            if (Math.abs(getNodeHeight(curr.l_Child) - getNodeHeight(curr.r_Child)) > this.max_Imbalance){
                return false; 
            }else{
                return checkAVL(curr.l_Child) && checkAVL(curr.r_Child);
            }
        }
    }

    
    /**
     * <p>Empties the AVL-G Tree of all its elements. After a call to this method, the
     * tree should have <b>0</b> elements.</p>
     */
    public void clear(){
        this.root = null;
        this.size = 0;
    }


    /**
     * <p>Return the number of elements in the tree.</p>
     * @return  The number of elements in the tree.
     */
    public int getCount(){
        return this.size;
    }
    
    //TEST print node
    public void printTree(Node curr){
        if (curr == null){
            return;
        }
        if (curr.l_Child != null && curr.r_Child == null){
            System.out.println(curr.data + " =>" +" Left : " + curr.l_Child.data + " Right : Null" + " Balance :" + getNodeHeight(curr));
        }else if(curr.l_Child == null && curr.r_Child != null){
            System.out.println(curr.data + " =>" + " Left : Null " + " Right : " + curr.r_Child.data + " Balance :" + getNodeHeight(curr));
        }else if(curr.l_Child == null && curr.r_Child == null){
            System.out.println(curr.data + " =>" +" Left : NULL " + " Right : NULL" + " Balance :" + getNodeHeight(curr));
        }else{
            System.out.println(curr.data + " =>" + " Left : " + curr.l_Child.data + " Right : " + curr.r_Child.data + " Balance :" + getNodeHeight(curr));         
        }
        printTree(curr.l_Child);
        printTree(curr.r_Child);
    }
    
    //TEST return root node
    public Node getR() {
    	return this.root;
    }
    
    public int getS() {
    	return this.size;
    }
}
