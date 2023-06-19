package bpt;

import bpt.UnimplementedMethodException;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p>{@code BinaryPatriciaTrie} is a Patricia Trie over the binary alphabet &#123;	 0, 1 &#125;. By restricting themselves
 * to this small but terrifically useful alphabet, Binary Patricia Tries combine all the positive
 * aspects of Patricia Tries while shedding the storage cost typically associated with tries that
 * deal with huge alphabets.</p>
 *
 * @author YOUR NAME HERE!
 */
public class BinaryPatriciaTrie {

    /* We are giving you this class as an example of what your inner node might look like.
     * If you would prefer to use a size-2 array or hold other things in your nodes, please feel free
     * to do so. We can *guarantee* that a *correct* implementation exists with *exactly* this data
     * stored in the nodes.
     */
    private static class TrieNode {
        private TrieNode left, right;
        private String str;
        private boolean isKey;

        // Default constructor for your inner nodes.
        TrieNode() {
            this("", false);
        }

        // Non-default constructor.
        TrieNode(String str, boolean isKey) {
            left = right = null;
            this.str = str;
            this.isKey = isKey;
        }
    }

    private TrieNode root;
    private int key_size;

    /**
     * Simple constructor that will initialize the internals of {@code this}.
     */
    public BinaryPatriciaTrie() {
        this.root = new TrieNode();
        this.key_size = 0;
    }

    /**
     * Searches the trie for a given key.
     *
     * @param key The input {@link String} key.
     * @return {@code true} if and only if key is in the trie, {@code false} otherwise.
     */
    public boolean search(String key) {
        if (key == "") {
        	return false;
        }
        	
        TrieNode curr = this.root;
        String key_prefix = "";
        
        while(curr != null) {
        	// if key has '0' on string index[0]
        	if(key.charAt(0) == '0') {
        		// search in left child
        		curr = curr.left;
        	}else {
        		// if key has '1' on string index[0], search in right child
        		curr = curr.right;
        	}
        	
        	// if child is not null
        	if(curr != null) {
        		// key length is bigger than str len stored in curr node
        		if(key.length() >= curr.str.length()) {
        			// get the first [len of curr.str] char of the key
        			key_prefix = key.substring(0, curr.str.length());
        		}else {
        			// no match, search fail
        			return false;
        		}
        		// if key has the prefix same as curr.str, means curr node is the node we could stay
        		if(key_prefix.compareTo(curr.str) != 0) {
        			// the prefix is not same as curr.str, we could not go down, search fail
        			return false;
        		}
        		
        		// key minus the prefix(curr.str)
        		key = key.substring(curr.str.length());
        		// if key is empty, means this is the node we want find
        		if(key.isEmpty() == true && curr.isKey == true) {
        			// if this is the key, we found the target, search success
        			return true;
        		}
        		if(key.isEmpty() == true && curr.isKey == false) {
        			return false;
        		}
        	}
        }
        return false;
    }
   
    
    /**
     * Inserts key into the trie.
     *
     * @param key The input {@link String}  key.
     * @return {@code true} if and only if the key was not already in the trie, {@code false} otherwise.
     */
    public boolean insert(String key) {
        if (this.search(key) == true) {
        	return false;
        }
        
        TrieNode curr = this.root, parent = null;
        String key_prefix = "";
        
        while(curr != null) {
        	parent = curr;
        	if(key.charAt(0) == '0') {
        		curr = curr.left;
        	}else {
        		curr = curr.right;
        	}
        	
        	// we should just insert a new node at the end of trie
        	if(curr == null) {
        		TrieNode insert = new TrieNode(key, true);
        		
        		if(key.charAt(0) == '0') {
        			parent.left = insert;
        		}else {
        			parent.right = insert;
        		}
        		
        		this.key_size++;
        		
        		return true;
        	}else {
        		boolean need_check = false;
        		
        		// if we could call substring
        		if(key.length() >= curr.str.length()) {
        			// call substring to get a piece of insert string
        			key_prefix = key.substring(0, curr.str.length());
        			// prefix of key is match with curr node, means we could stay in this node
        			if(key_prefix.compareTo(curr.str) == 0) {
	        			// if key is empty after minus the prefix, just set isKey from false to true
	        			if(key_prefix.length() == key.length()) {
	        				curr.isKey = true;
	        				
	        				this.key_size++;
	        				
	        				return true;
	        			}else {
	        				// keep moving to next node
	        				key = key.substring(curr.str.length());
	        			}
	        		}else {
	        			// if line151[if] fail, means string we insert not match with curr node, need to be check if add parent or merge
	        			need_check = true;
	        		}
        		}else {
        			// if curr node have string longer than our insert string, means we could not go into curr node
        			need_check = true;
        		}
        		
        		// here is for insert a parent AND merge node
        		if(need_check == true) {
        			// if the whole key is a prefix of curr node, we add a parent of curr node to store our insert string
	        		if(curr.str.indexOf(key) == 0) {
	        			TrieNode insert = new TrieNode(key, true);
	        			
	        			if(key.charAt(0) == '0') {
	            			parent.left = insert;
	            		}else {
	            			parent.right = insert;
	            		}
	        			
	        			this.key_size++;
	        			
	        			// curr str minus insert node str
	        			curr.str = curr.str.substring(key.length());
	        			
	        			// which child curr is in insert node
	        			if(curr.str.charAt(0) == '0') {
	            			insert.left = curr;
	            		}else {
	            			insert.right = curr;
	            		}
	        			
	        			return true;
	        		}else{
	        			int start_merge;
	        			// there is a substring in key which is a prefix of curr node
	        			for(int i = 1; i < key.length(); i++) {
	        				start_merge = curr.str.indexOf(key.substring(0, key.length() - i));
	        				if(start_merge == 0) {
	        					TrieNode insert = new TrieNode(key.substring(0, key.length() - i), false);
	        					TrieNode insert_child = new TrieNode(key.substring(key.length() - i), true);
	        					curr.str = curr.str.substring(key.length() - i);
	        					
	        					if(key.charAt(0) == '0') {
	    	            			parent.left = insert;
	    	            		}else {
	    	            			parent.right = insert;
	    	            		}
	        					
	        					if(insert_child.str.charAt(0) == '0') {
	        						insert.left = insert_child;
	        						insert.right = curr;
	        					}else {
	        						insert.left = curr;
	        						insert.right = insert_child;
	        					}
	        					
	        					this.key_size++;
	        					
	        					return true;
	        				}
	        			}
	        		}
        		}
        	}
        }
        return false;
    }


    /**
     * Deletes key from the trie.
     *
     * @param key The {@link String}  key to be deleted.
     * @return {@code true} if and only if key was contained by the trie before we attempted deletion, {@code false} otherwise.
     */
    public boolean delete(String key) {
        if(this.search(key) == false) {
        	return false;
        }
        
        TrieNode curr = this.root, parent = null;
        String key_prefix = "";
        
        while(curr != null) {
        	parent = curr;
        	if(key.charAt(0) == '0') {
        		curr = curr.left;
        	}else {
        		curr = curr.right;
        	}
        	
        	key = key.substring(curr.str.length());
        	
        	if(key.isEmpty()) {
        		break;
        	}
        }
        
        if(curr.left != null && curr.right != null) {
        	curr.isKey = false;
        	
        	this.key_size--;
        	
        }else if(curr.left == null && curr.right == null) {
        	if(curr.str.charAt(0) == '0') {
        		parent.left = null;
        	}else {
        		parent.right = null;
        	}
        	
        	TrieNode temp;
        	if(parent.isKey == false && parent != this.root) {
        		if(parent.left != null) {
        			temp = parent.left;
        			parent.str = parent.str.concat(temp.str);
        			parent.isKey = temp.isKey;
        		}else {
        			temp = parent.right;
        			parent.str = parent.str.concat(temp.str);
        			parent.isKey = temp.isKey;
        		}
        		
        		parent.left = temp.left;
        		parent.right = temp.right;
        	}
        	
        	this.key_size --;
        	
        }else {
        	TrieNode temp;
        	if(curr.left != null) {
        		temp = curr.left;
        		curr.str = curr.str.concat(temp.str);
        		curr.isKey = temp.isKey;
        	}else {
        		temp = curr.right;
        		curr.str = curr.str.concat(temp.str);
        		curr.isKey = temp.isKey;
        	}
        	
        	curr.left = temp.left;
        	curr.right = temp.right;
        	
        	this.key_size--;
        	
        }
        
        
    	return true;
    }

    /**
     * Queries the trie for emptiness.
     *
     * @return {@code true} if and only if {@link #getSize()} == 0, {@code false} otherwise.
     */
    public boolean isEmpty() {
        return this.key_size == 0;
    }

    /**
     * Returns the number of keys in the tree.
     *
     * @return The number of keys in the tree.
     */
    public int getSize() {
        return this.key_size;
    }

    /**
     * <p>Performs an <i>inorder (symmetric) traversal</i> of the Binary Patricia Trie. Remember from lecture that inorder
     * traversal in tries is NOT sorted traversal, unless all the stored keys have the same length. This
     * is of course not required by your implementation, so you should make sure that in your tests you
     * are not expecting this method to return keys in lexicographic order. We put this method in the
     * interface because it helps us test your submission thoroughly and it helps you debug your code! </p>
     *
     * <p>We <b>neither require nor test </b> whether the {@link Iterator} returned by this method is fail-safe or fail-fast.
     * This means that you  do <b>not</b> need to test for thrown {@link java.util.ConcurrentModificationException}s and we do
     * <b>not</b> test your code for the possible occurrence of concurrent modifications.</p>
     *
     * <p>We also assume that the {@link Iterator} is <em>immutable</em>, i,e we do <b>not</b> test for the behavior
     * of {@link Iterator#remove()}. You can handle it any way you want for your own application, yet <b>we</b> will
     * <b>not</b> test for it.</p>
     *
     * @return An {@link Iterator} over the {@link String} keys stored in the trie, exposing the elements in <i>symmetric
     * order</i>.
     */
    public Iterator<String> inorderTraversal() {
        return new inorderTraversalIterator(this.root);
    }
    
    private class inorderTraversalIterator implements Iterator<String>{
    	ArrayList<String> str_lst;
    	TrieNode curr;
    	
    	public inorderTraversalIterator(TrieNode input) {
    		str_lst = new ArrayList<String>();
    		curr = input;
    		inorderRec(curr, "");
    	}
    	
    	private void inorderRec(TrieNode curr, String prefix) {
    		if(curr.left != null) {
    			inorderRec(curr.left, prefix.concat(curr.left.str));
    		}
    		if(curr.isKey) {
    			this.str_lst.add(prefix);
    		}
    		if(curr.right != null) {
    			inorderRec(curr.right, prefix.concat(curr.right.str));
    		}
    	}
    	
		@Override
		public boolean hasNext() {
			if(str_lst.isEmpty()) {
				return false;
			}
			return true;
		}

		@Override
		public String next() {
			String val = str_lst.get(0);
			str_lst.remove(0);
			return val;
		}
    }
    /**
     * Finds the longest {@link String} stored in the Binary Patricia Trie.
     * @return <p>The longest {@link String} stored in this. If the trie is empty, the empty string &quot;&quot; should be
     * returned. Careful: the empty string &quot;&quot;is <b>not</b> the same string as &quot; &quot;; the latter is a string
     * consisting of a single <b>space character</b>! It is also <b>not the same as the</b> null <b>reference</b>!</p>
     *
     * <p>Ties should be broken in terms of <b>value</b> of the bit string. For example, if our trie contained
     * only the binary strings 01 and 11, <b>11</b> would be the longest string. If our trie contained
     * only 001 and 010, <b>010</b> would be the longest string.</p>
     */
    public String getLongest() {
        Iterator<String> iter = this.inorderTraversal();
        
        String val = "", temp = "";
        
        while(iter.hasNext()) {
        	temp = iter.next();
        	if(temp.length() > val.length()) {
        		val = temp;
        	}else if(temp.length() == val.length()) {
        		if(temp.compareTo(val) > 0) {
        			val = temp;
        		}
        	}else {
        	}
        }
        return val;
    }

    /**
     * Makes sure that your trie doesn't have splitter nodes with a single child. In a Patricia trie, those nodes should
     * be pruned.
     * @return {@code true} iff all nodes in the trie either denote stored strings or split into two subtrees, {@code false} otherwise.
     */
    public boolean isJunkFree(){
        return isEmpty() || (isJunkFree(root.left) && isJunkFree(root.right));
    }

    private boolean isJunkFree(TrieNode n){
        if(n == null){   // Null subtrees trivially junk-free
            return true;
        }
        if(!n.isKey){   // Non-key nodes need to be strict splitter nodes
            return ( (n.left != null) && (n.right != null) && isJunkFree(n.left) && isJunkFree(n.right) );
        } else {
            return ( isJunkFree(n.left) && isJunkFree(n.right) ); // But key-containing nodes need not.
        }
    }
    
}
