package spatial.nodes;

import spatial.exceptions.UnimplementedMethodException;
import spatial.kdpoint.KDPoint;
import spatial.knnutils.BoundedPriorityQueue;
import spatial.knnutils.NNData;

import java.util.Collection;

/**
 * <p>{@link KDTreeNode} is an abstraction over nodes of a KD-Tree. It is used extensively by
 * {@link spatial.trees.KDTree} to implement its functionality.</p>
 *
 * <p><b>YOU ***** MUST ***** IMPLEMENT THIS CLASS!</b></p>
 *
 * @author  ---- YOUR NAME HERE! -----
 *
 * @see spatial.trees.KDTree
 */
public class KDTreeNode {


    /* *************************************************************************** */
    /* ************* WE PROVIDE THESE FIELDS TO GET YOU STARTED.  **************** */
    /* ************************************************************************** */
    private KDPoint p;
    private int height;
    private KDTreeNode left, right;

    /* *************************************************************************************** */
    /* *************  PLACE ANY OTHER PRIVATE FIELDS AND YOUR PRIVATE METHODS HERE: ************ */
    /* ************************************************************************************* */


    /* *********************************************************************** */
    /* ***************  IMPLEMENT THE FOLLOWING PUBLIC METHODS:  ************ */
    /* *********************************************************************** */


    /**
     * 1-arg constructor. Stores the provided {@link KDPoint} inside the freshly created node.
     * @param p The {@link KDPoint} to store inside this. Just a reminder: {@link KDPoint}s are
     *          <b>mutable!!!</b>.
     */
    public KDTreeNode(KDPoint p){
        this.p = new KDPoint(p);
        this.height = 0;
        this.left = null;
        this.right = null;
    }

    /**
     * <p>Inserts the provided {@link KDPoint} in the tree rooted at this. To select which subtree to recurse to,
     * the KD-Tree acts as a Binary Search Tree on currDim; it will examine the value of the provided {@link KDPoint}
     * at currDim and determine whether it is larger than or equal to the contained {@link KDPoint}'s relevant dimension
     * value. If so, we recurse right, like a regular BST, otherwise left.</p>
     * @param currDim The current dimension to consider
     * @param dims The total number of dimensions that the space considers.
     * @param pIn The {@link KDPoint} to insert into the node.
     * @see #delete(KDPoint, int, int)
     */
    public void insert(KDPoint pIn, int currDim, int dims){
    	int nextDim;
    	
        if(this.p.coords[currDim] > pIn.coords[currDim]) {
        	if(this.left == null) {
        		this.left = new KDTreeNode(pIn);
        		
        		if(this.right == null) {
        			this.height++;
        		}
        	}else {
        		if((currDim + 1) == dims) {
        			nextDim = 0;
        		}else {
        			nextDim = currDim + 1;
        		}
        		this.left.insert(pIn, nextDim, dims);
        		
        		this.height = this.height();
        	}
        }else {
        	if(this.right == null) {
        		this.right = new KDTreeNode(pIn);
        		
        		if(this.left == null) {
        			this.height++;
        		}
        	}else {
        		if((currDim + 1) == dims) {
        			nextDim = 0;
        		}else {
        			nextDim = currDim + 1;
        		}
        		this.right.insert(pIn, nextDim, dims);
        		
        		this.height = this.height();
        	}
        }
    }

    /**
     * <p>Deletes the provided {@link KDPoint} from the tree rooted at this. To select which subtree to recurse to,
     * the KD-Tree acts as a Binary Search Tree on currDim; it will examine the value of the provided {@link KDPoint}
     * at currDim and determine whether it is larger than or equal to the contained {@link KDPoint}'s relevant dimension
     * value. If so, we recurse right, like a regular BST, otherwise left. There exist two special cases of deletion,
     * depending on whether we are deleting a {@link KDPoint} from a node who either:</p>
     *
     * <ul>
     *      <li>Has a NON-null subtree as a right child.</li>
     *      <li>Has a NULL subtree as a right child.</li>
     * </ul>
     *
     * <p>You should consult the class slides, your notes, and the textbook about what you need to do in those two
     * special cases.</p>
     * @param currDim The current dimension to consider.
     * @param dims The total number of dimensions that the space considers.
     * @param pIn The {@link KDPoint} to insert into the node.
     * @see #insert(KDPoint, int, int)
     * @return A reference to this after the deletion takes place.
     */
    public KDTreeNode delete(KDPoint pIn, int currDim, int dims){
    	int nextDim;
		
		// find next dimension
		if((currDim + 1) == dims) {
			nextDim = 0;
		}else {
			nextDim = currDim + 1;
		}
    	
        if(this.p.equals(pIn)) {
        	if(this.left == null && this.right == null) {
        		return null;
        	}else if(this.right != null) {
        		
        		// case curr node has a right subtree
    			// use findMin to find inorderSuccessor Node
        		KDTreeNode inorderSuccessor = findMin(this.right, currDim, nextDim, dims);
        		// deep copy of the KDPoint from inorderSuccessor to currNode
        		this.p = new KDPoint(inorderSuccessor.p);
        		// go into right subtree to delete that inorderSuccessor
        		this.right = this.right.delete(inorderSuccessor.p, nextDim, dims);
        		
        		this.height = this.height();
        		
        		return this;
        	}else if(this.left != null) {
        		// use findMin to find inorderSuccessor Node
    			KDTreeNode inorderSuccessor = findMin(this.left, currDim, nextDim, dims);
    			// deep copy of the KDPoint from inorderSuccessor to currNode
    			this.p = new KDPoint(inorderSuccessor.p);
    			
    			// flip the left subtree of currNode to the right child of currNode
    			this.right = this.left;
    			this.left = null;
    			
    			// go into right subtree to delete that inorderSuccessor
    			this.right = this.right.delete(inorderSuccessor.p, nextDim, dims);
    			
    			this.height = this.height();
    			
    			return this;
        	}
        }else if(this.p.coords[currDim] > pIn.coords[currDim]) {
        	// should go into left subtree to find the node
        	this.left = this.left.delete(pIn, nextDim, dims);
        	this.height = this.height();
        	return this;
        }else {
        	this.right = this.right.delete(pIn, nextDim, dims);
        	this.height = this.height();
        	return this;
        }
		return null;
    }
    
    private KDTreeNode findMin(KDTreeNode currNode, int targetDim, int currDim, int numDims) {
    	if(currNode == null) {
    		return null;
    	}else if(currNode.left == null && currNode.right == null) {
    		return currNode;
    	}else if(targetDim == currDim) {
    		if(currNode.left == null) {
    			return currNode;
    		}else {
    			int nextDim;
    			if((currDim + 1) == numDims) {
    				nextDim = 0;
    			}else {
    				nextDim = currDim + 1;
    			}
    			
    			return findMin(currNode.left, targetDim, nextDim, numDims);
    		}
    	}
    	
    	int nextDim;
    	if((currDim + 1) == numDims) {
			nextDim = 0;
		}else {
			nextDim = currDim + 1;
		}
    	KDTreeNode lMin = findMin(currNode.left, targetDim, nextDim, numDims);
    	KDTreeNode rMin = findMin(currNode.right, targetDim, nextDim, numDims);
    	
    	return min3(lMin, rMin, currNode, targetDim);
    }
    
    private KDTreeNode min3(KDTreeNode lMin, KDTreeNode rMin, KDTreeNode currNode, int targetDim) {
    	if(currNode != null && lMin == null && rMin == null) {
    		return currNode;
    	}else if(currNode != null && lMin != null && rMin == null) {
    		if(lMin.p.coords[targetDim] < currNode.p.coords[targetDim]) {
    			return lMin;
    		}else {
    			return currNode;
    		}
    	}else if(currNode != null && lMin == null && rMin != null) {
    		if(rMin.p.coords[targetDim] < currNode.p.coords[targetDim]) {
    			return rMin;
    		}else {
    			return currNode;
    		}
    	}else{
    		KDTreeNode minChild;
    		if(lMin.p.coords[targetDim] < rMin.p.coords[targetDim]) {
    			minChild = lMin;
    		}else {
    			minChild = rMin;
    		}
    		
    		if(minChild.p.coords[targetDim] < currNode.p.coords[targetDim]) {
    			return minChild;
    		}else {
    			return currNode;
    		}
    	}
    }

    /**
     * Searches the subtree rooted at the current node for the provided {@link KDPoint}.
     * @param pIn The {@link KDPoint} to search for.
     * @param currDim The current dimension considered.
     * @param dims The total number of dimensions considered.
     * @return true iff pIn was found in the subtree rooted at this, false otherwise.
     */
    public boolean search(KDPoint pIn, int currDim, int dims){
    	int nextDim;
    	if((currDim + 1) == dims) {
			nextDim = 0;
		}else {
			nextDim = currDim + 1;
		}
    	
        if(this.p.equals(pIn)) {
        	return true;
        }else if(this.p.coords[currDim] > pIn.coords[currDim]) {
        	if(this.left == null) {
        		return false;
        	}else {
        		return this.left.search(pIn, nextDim, dims);
        	}
        }else {
        	if(this.right == null) {
        		return false;
        	}else {
        		return this.right.search(pIn, nextDim, dims);
        	}
        }
    }

    /**
     * <p>Executes a range query in the given {@link KDTreeNode}. Given an &quot;anchor&quot; {@link KDPoint},
     * all {@link KDPoint}s that have a {@link KDPoint#euclideanDistance(KDPoint) euclideanDistance} of <b>at most</b> range
     * <b>INCLUSIVE</b> from the anchor point <b>except</b> for the anchor itself should be inserted into the {@link Collection}
     * that is passed.</p>
     *
     * <p>Remember: range queries behave <em>greedily</em> as we go down (approaching the anchor as &quot;fast&quot;
     * as our currDim allows and <em>prune subtrees</em> that we <b>don't</b> have to visit as we backtrack. Consult
     * all of our resources if you need a reminder of how these should work.</p>
     *
     * @param anchor The centroid of the hypersphere that the range query implicitly creates.
     * @param results A {@link Collection} that accumulates all the {@link }
     * @param currDim The current dimension examined by the {@link KDTreeNode}.
     * @param dims The total number of dimensions of our {@link KDPoint}s.
     * @param range The <b>INCLUSIVE</b> range from the &quot;anchor&quot; {@link KDPoint}, within which all the
     *              {@link KDPoint}s that satisfy our query will fall. The euclideanDistance metric used} is defined by
     *              {@link KDPoint#euclideanDistance(KDPoint)}.
     */
    public void range(KDPoint anchor, Collection<KDPoint> results,
                      double range, int currDim , int dims){
    	int nextDim;
    	if((currDim + 1) == dims) {
    		nextDim = 0;
    	}else {
    		nextDim = currDim + 1;
    	}
    	
    	if(anchor.coords[currDim] < this.p.coords[currDim]) {
    		// we should visit left subtree first, because it is getting closer to anchor point
    		
    		// first check if currNode is in range
    		if(isInRange(anchor, this.p, range) && (this.p.equals(anchor) == false)) {
    			results.add(this.p);
    		}
    		
    		// check if there is a left subtree for us to visit
    		// left subtree is the point where leftChild[currDim] is less than currNode[currDim]
    		// which means closer to the line that anchor[currDim]
    		// thus there is a possibility that left subtree has a point in range
    		if(this.left != null) {
	    		this.left.range(anchor, results, range, nextDim, dims);
    		}
    		
    		// check if there is a right subtree for us to visit
    		if(this.right != null) {
    			// the point in right subtree is farther away from anchor point compare to left subtree
    			// check to see if right subtree is reasonable for us to visit
    			// i.e. if the range from currNode[currDim] to anchor[currDim] is larger than radius
    			//      it means that there could not be any point fit in radius
	    		if(Math.abs(this.p.coords[currDim] - anchor.coords[currDim]) <= range) {
	    			this.right.range(anchor, results, range, nextDim, dims);
	    		}
    		}
    	}else {
    		// we should visit right subtree first
    		
    		if(isInRange(anchor, this.p, range) && (this.p.equals(anchor) == false)) {
    			results.add(this.p);
    		}
    		
    		if(this.right != null) {
    			this.right.range(anchor, results, range, nextDim, dims);
    		}
    		
    		if(this.left != null) {
	    		if(Math.abs(this.p.coords[currDim] - anchor.coords[currDim]) <= range) {
	    			this.left.range(anchor, results, range, nextDim, dims);
	    		}
    		}
    	}
    }

    /**
     * check to see if the distance from a point to anchor point is less or equal to radius
     * 
     * @param pt1: anchor point
     * @param pt2: the point (currNode)
     * @param radius
     * @return true if point is in range; false if not
     */
    private boolean isInRange(KDPoint pt1, KDPoint pt2, double radius) {
    	return pt1.euclideanDistance(pt2) <= radius;
    }
    
    /**
     * <p>Executes a nearest neighbor query, which returns the nearest neighbor, in terms of
     * {@link KDPoint#euclideanDistance(KDPoint)}, from the &quot;anchor&quot; point.</p>
     *
     * <p>Recall that, in the descending phase, a NN query behaves <em>greedily</em>, approaching our
     * &quot;anchor&quot; point as fast as currDim allows. While doing so, it implicitly
     * <b>bounds</b> the acceptable solutions under the current <b>best solution</b>, which is passed as
     * an argument. This approach is known in Computer Science as &quot;branch-and-bound&quot; and it helps us solve an
     * otherwise exponential complexity problem (nearest neighbors) efficiently. Remember that when we want to determine
     * if we need to recurse to a different subtree, it is <b>necessary</b> to compare the euclideanDistance reported by
     * {@link KDPoint#euclideanDistance(KDPoint)} and coordinate differences! Those are comparable with each other because they
     * are the same data type ({@link Double}).</p>
     *
     * @return An object of type {@link NNData}, which exposes the pair (distance_of_NN_from_anchor, NN),
     * where NN is the nearest {@link KDPoint} to the anchor {@link KDPoint} that we found.
     *
     * @param anchor The &quot;ancor&quot; {@link KDPoint}of the nearest neighbor query.
     * @param currDim The current dimension considered.
     * @param dims The total number of dimensions considered.
     * @param n An object of type {@link NNData}, which will define a nearest neighbor as a pair (distance_of_NN_from_anchor, NN),
     *      * where NN is the nearest neighbor found.
     *
     * @see NNData
     * @see #kNearestNeighbors(int, KDPoint, BoundedPriorityQueue, int, int)
     */
    public NNData<KDPoint> nearestNeighbor(KDPoint anchor, int currDim,
                                            NNData<KDPoint> n, int dims){
    	this.nearestNeighborHelper(anchor, n, currDim, dims);
    	return n;
    }
    
    private void nearestNeighborHelper(KDPoint anchor, NNData<KDPoint> n, int currDim, int dims) {
    	int nextDim;
    	if((currDim + 1) == dims) {
    		nextDim = 0;
    	}else {
    		nextDim = currDim + 1;
    	}
    	
    	if(anchor.coords[currDim] < this.p.coords[currDim]) {
    		// we should visit left subtree first, because it is getting closer to anchor point
    		
    		// first check if currNode is in range
    		if(n.getBestDist() == -1 || this.p.euclideanDistance(anchor) <= n.getBestDist()) {
    			if (this.p.equals(anchor) == false) {
    				n.update(this.p, this.p.euclideanDistance(anchor));
    			}
    		}
    		
    		if(this.left != null) {
	    		this.left.nearestNeighborHelper(anchor, n, nextDim, dims);
    		}
    		
    		// check if there is a right subtree for us to visit
    		if(this.right != null) {
	    		if(this.right.p.euclideanDistance(anchor) <= n.getBestDist()) {
	    			this.right.nearestNeighborHelper(anchor, n, nextDim, dims);
	    		}
    		}
    	}else {
    		// we should visit right subtree first
    		
    		if(n.getBestDist() == -1 || this.p.euclideanDistance(anchor) <= n.getBestDist()) {
    			if (this.p.equals(anchor) == false) {
    				n.update(this.p, this.p.euclideanDistance(anchor));
    			}
    		}
    		
    		if(this.right != null) {
    			this.right.nearestNeighborHelper(anchor, n, nextDim, dims);
    		}
    		
    		if(this.left != null) {
	    		if(this.left.p.euclideanDistance(anchor) <= n.getBestDist()) {
	    			this.left.nearestNeighborHelper(anchor, n, nextDim, dims);
	    		}
    		}
    	}
    }

    /**
     * <p>Executes a nearest neighbor query, which returns the nearest neighbor, in terms of
     * {@link KDPoint#euclideanDistance(KDPoint)}, from the &quot;anchor&quot; point.</p>
     *
     * <p>Recall that, in the descending phase, a NN query behaves <em>greedily</em>, approaching our
     * &quot;anchor&quot; point as fast as currDim allows. While doing so, it implicitly
     * <b>bounds</b> the acceptable solutions under the current <b>worst solution</b>, which is maintained as the
     * last element of the provided {@link BoundedPriorityQueue}. This is another instance of &quot;branch-and-bound&quot;
     * Remember that when we want to determine if we need to recurse to a different subtree, it is <b>necessary</b>
     * to compare the euclideanDistance reported by* {@link KDPoint#euclideanDistance(KDPoint)} and coordinate differences!
     * Those are comparable with each other because they are the same data type ({@link Double}).</p>
     *
     * <p>The main difference of the implementation of this method and the implementation of
     * {@link #nearestNeighbor(KDPoint, int, NNData, int)} is the necessity of using the class
     * {@link BoundedPriorityQueue} effectively. Consult your various resources
     * to understand how you should be using this class.</p>
     *
     * @param k The total number of neighbors to retrieve. It is better if this quantity is an odd number, to
     *          avoid ties in Binary Classification tasks.
     * @param anchor The &quot;anchor&quot; {@link KDPoint} of the nearest neighbor query.
     * @param currDim The current dimension considered.
     * @param dims The total number of dimensions considered.
     * @param queue A {@link BoundedPriorityQueue} that will maintain at most k nearest neighbors of
     *              the anchor point at all times, sorted by euclideanDistance to the point.
     *
     * @see BoundedPriorityQueue
     */
    public void kNearestNeighbors(int k, KDPoint anchor, BoundedPriorityQueue<KDPoint> queue, int currDim, int dims){
    	int nextDim;
    	if((currDim + 1) == dims) {
    		nextDim = 0;
    	}else {
    		nextDim = currDim + 1;
    	}
    	
    	if(anchor.coords[currDim] < this.p.coords[currDim]) {
    		// we should visit left subtree first, because it is getting closer to anchor point
    		
    		// first check if currNode is in range
    		if(this.p.equals(anchor) == false) {
    			queue.enqueue(this.p, this.p.euclideanDistance(anchor));
    		}
    		
    		if(this.left != null) {
	    		this.left.kNearestNeighbors(k, anchor, queue, nextDim, dims);
    		}
    		
    		// check if there is a right subtree for us to visit
    		if(this.right != null) {
	    		if(Math.abs(this.p.coords[currDim] - anchor.coords[currDim]) <= queue.last().euclideanDistance(anchor)) {
	    			this.right.kNearestNeighbors(k, anchor, queue, nextDim, dims);
	    		}
    		}
    	}else {
    		// we should visit right subtree first
    		
    		if(this.p.equals(anchor) == false) {
    			queue.enqueue(this.p, this.p.euclideanDistance(anchor));
    		}
    		
    		if(this.right != null) {
    			this.right.kNearestNeighbors(k, anchor, queue, nextDim, dims);
    		}
    		
    		if(this.left != null) {
	    		if(Math.abs(this.p.coords[currDim] - anchor.coords[currDim]) <= queue.last().euclideanDistance(anchor)) {
	    			this.left.kNearestNeighbors(k, anchor, queue, nextDim, dims);
	    		}
    		}
    	}
    }

    /**
     * Returns the height of the subtree rooted at the current node. Recall our definition of height for binary trees:
     * <ol>
     *     <li>A null tree has a height of -1.</li>
     *     <li>A non-null tree has a height equal to max(height(left_subtree), height(right_subtree))+1</li>
     * </ol>
     * @return the height of the subtree rooted at the current node.
     */
    public int height(){
        return getHeight(this);
    }
    
    private int getHeight(KDTreeNode currNode) {
    	if(currNode == null) {
    		return -1;
    	}else if(currNode.left == null && currNode.right == null) {
    		return 0;
    	}else {
    		return Math.max(getHeight(currNode.left), getHeight(currNode.right)) + 1;
    	}
    }

    /**
     * A simple getter for the {@link KDPoint} held by the current node. Remember: {@link KDPoint}s ARE
     * MUTABLE, SO WE NEED TO DO DEEP COPIES!!!
     * @return The {@link KDPoint} held inside this.
     */
    public KDPoint getPoint(){
        KDPoint val = new KDPoint(this.p);
        return val;
    }

    public KDTreeNode getLeft(){
    	return this.left;
    }

    public KDTreeNode getRight(){
        return this.right;
    }
}
