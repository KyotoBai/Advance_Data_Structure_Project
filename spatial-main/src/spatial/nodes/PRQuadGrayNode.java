package spatial.nodes;

import spatial.exceptions.UnimplementedMethodException;
import spatial.kdpoint.KDPoint;
import spatial.knnutils.BoundedPriorityQueue;
import spatial.knnutils.NNData;
import spatial.trees.CentroidAccuracyException;
import spatial.trees.PRQuadTree;

import java.util.ArrayList;
import java.util.Collection;

/** <p>A {@link PRQuadGrayNode} is a gray (&quot;mixed&quot;) {@link PRQuadNode}. It
 * maintains the following invariants: </p>
 * <ul>
 *      <li>Its children pointer buffer is non-null and has a length of 4.</li>
 *      <li>If there is at least one black node child, the total number of {@link KDPoint}s stored
 *      by <b>all</b> of the children is greater than the bucketing parameter (because if it is equal to it
 *      or smaller, we can prune the node.</li>
 * </ul>
 *
 * <p><b>YOU ***** MUST ***** IMPLEMENT THIS CLASS!</b></p>
 *
 *  @author --- YOUR NAME HERE! ---
 */
public class PRQuadGrayNode extends PRQuadNode{


    /* ******************************************************************** */
    /* *************  PLACE ANY  PRIVATE FIELDS AND METHODS HERE: ************ */
    /* ********************************************************************** */
	
	private int height;
	private int numTotalPoint;
	private PRQuadNode NW, NE, SW, SE;

    /* *********************************************************************** */
    /* ***************  IMPLEMENT THE FOLLOWING PUBLIC METHODS:  ************ */
    /* *********************************************************************** */

    /**
     * Creates a {@link PRQuadGrayNode}  with the provided {@link KDPoint} as a centroid;
     * @param centroid A {@link KDPoint} that will act as the centroid of the space spanned by the current
     *                 node.
     * @param k The See {@link PRQuadTree#PRQuadTree(int, int)} for more information on how this parameter works.
     * @param bucketingParam The bucketing parameter fed to this by {@link PRQuadTree}.
     * @see PRQuadTree#PRQuadTree(int, int)
     */
    public PRQuadGrayNode(KDPoint centroid, int k, int bucketingParam){
        super(centroid, k, bucketingParam); // Call to the super class' protected constructor to properly initialize the object!
        
        this.NW = null;
        this.NE = null;
        this.SW = null;
        this.SE = null;
        
        this.numTotalPoint = 0;
        this.height = 1;
    }


    /**
     * <p>Insertion into a {@link PRQuadGrayNode} consists of navigating to the appropriate child
     * and recursively inserting elements into it. If the child is a white node, memory should be allocated for a
     * {@link PRQuadBlackNode} which will contain the provided {@link KDPoint} If it's a {@link PRQuadBlackNode},
     * refer to {@link PRQuadBlackNode#insert(KDPoint, int)} for details on how the insertion is performed. If it's a {@link PRQuadGrayNode},
     * the current method would be called recursively. Polymorphism will allow for the appropriate insert to be called
     * based on the child object's runtime object.</p>
     * @param p A {@link KDPoint} to insert into the subtree rooted at the current {@link PRQuadGrayNode}.
     * @param k The side length of the quadrant spanned by the <b>current</b> {@link PRQuadGrayNode}. It will need to be updated
     *          per recursive call to help guide the input {@link KDPoint}  to the appropriate subtree.
     * @return The subtree rooted at the current node, potentially adjusted after insertion.
     * @see PRQuadBlackNode#insert(KDPoint, int)
     */
    @Override
    public PRQuadNode insert(KDPoint p, int k) {
    	int splitedQuadrantLen = (int)Math.pow(2, k-1);
    	int newCentriodLen = splitedQuadrantLen / 2;
    	
    	if(splitedQuadrantLen == 0) {
    		throw new CentroidAccuracyException("Could not split");
    	}
    	
        if(p.coords[0] < this.centroid.coords[0]) {
        	// insert point is in the left of line X = centroid[x]
        	
        	if(p.coords[1] < this.centroid.coords[1]) {
        		// insert point is under the line Y = centroid[y]
        		// SW
        		if(this.SW != null) {
        			this.SW = this.SW.insert(p, k - 1);
        		}else {
        			KDPoint newCentroid = new KDPoint(this.centroid.coords[0] - newCentriodLen, this.centroid.coords[1] - newCentriodLen);
        			this.SW = new PRQuadBlackNode(newCentroid, k - 1, this.bucketingParam, p);
        		}
        	}else {
        		// NW
        		if(this.NW != null) {
        			this.NW = this.NW.insert(p, k - 1);
        		}else {
        			KDPoint newCentroid = new KDPoint(this.centroid.coords[0] - newCentriodLen, this.centroid.coords[1] + newCentriodLen);
        			this.NW = new PRQuadBlackNode(newCentroid, k - 1, this.bucketingParam, p);
        		}
        	}
        }else {
        	// insert point is in the right or on the line of X = centroid[x]s
        	
        	if(p.coords[1] < this.centroid.coords[1]) {
        		// insert point is under the line Y = centroid[y]
        		// SE
        		if(this.SE != null) {
        			this.SE = this.SE.insert(p, k - 1);
        		}else {
        			KDPoint newCentroid = new KDPoint(this.centroid.coords[0] + newCentriodLen, this.centroid.coords[1] - newCentriodLen);
        			this.SE = new PRQuadBlackNode(newCentroid, k - 1, this.bucketingParam, p);
        		}
        	}else {
        		// NE
        		if(this.NE != null) {
        			this.NE = this.NE.insert(p, k - 1);
        		}else {
        			KDPoint newCentroid = new KDPoint(this.centroid.coords[0] + newCentriodLen, this.centroid.coords[1] + newCentriodLen);
        			this.NE = new PRQuadBlackNode(newCentroid, k - 1, this.bucketingParam, p);
        		}
        	}
        }
        // determine the height
        int SW_Height, NW_Height, SE_Height, NE_Height;
        
        if(this.SW == null) {
        	SW_Height = -1;
        }else {
        	SW_Height = this.SW.height();
        }
        if(this.NW == null) {
        	NW_Height = -1;
        }else {
        	NW_Height = this.NW.height();
        }
        if(this.SE == null) {
        	SE_Height = -1;
        }else {
        	SE_Height = this.SE.height();
        }
        if(this.NE == null) {
        	NE_Height = -1;
        }else {
        	NE_Height = this.NE.height();
        }
        
        this.numTotalPoint++;
        this.height = Math.max(SW_Height, Math.max(NW_Height, Math.max(SE_Height, NE_Height))) + 1;
        
        return this;
    }

    /**
     * <p>Deleting a {@link KDPoint} from a {@link PRQuadGrayNode} consists of recursing to the appropriate
     * {@link PRQuadBlackNode} child to find the provided {@link KDPoint}. If no such child exists, the search has
     * <b>necessarily failed</b>; <b>no changes should then be made to the subtree rooted at the current node!</b></p>
     *
     * <p>Polymorphism will allow for the recursive call to be made into the appropriate delete method.
     * Importantly, after the recursive deletion call, it needs to be determined if the current {@link PRQuadGrayNode}
     * needs to be collapsed into a {@link PRQuadBlackNode}. This can only happen if it has no gray children, and one of the
     * following two conditions are satisfied:</p>
     *
     * <ol>
     *     <li>The deletion left it with a single black child. Then, there is no reason to further subdivide the quadrant,
     *     and we can replace this with a {@link PRQuadBlackNode} that contains the {@link KDPoint}s that the single
     *     black child contains.</li>
     *     <li>After the deletion, the <b>total</b> number of {@link KDPoint}s contained by <b>all</b> the black children
     *     is <b>equal to or smaller than</b> the bucketing parameter. We can then similarly replace this with a
     *     {@link PRQuadBlackNode} over the {@link KDPoint}s contained by the black children.</li>
     *  </ol>
     *
     * @param p A {@link KDPoint} to delete from the tree rooted at the current node.
     * @return The subtree rooted at the current node, potentially adjusted after deletion.
     */
    @Override
    public PRQuadNode delete(KDPoint p) {
        if(p.coords[0] < this.centroid.coords[0]) {
        	if(p.coords[1] < this.centroid.coords[1]) {
        		if(this.SW == null) {
        			return this;
        		}else {
        			this.SW = this.SW.delete(p);
        			this.numTotalPoint--;
        		}
        	}else {
        		if(this.NW == null) {
        			return this;
        		}else {
        			this.NW = this.NW.delete(p);
        			this.numTotalPoint--;
        		}
        	}
        }else {
        	if(p.coords[1] < this.centroid.coords[1]) {
        		if(this.SE == null) {
        			return this;
        		}else {
        			this.SE = this.SE.delete(p);
        			this.numTotalPoint--;
        		}
        	}else {
        		if(this.NE == null) {
        			return this;
        		}else {
        			this.NE = this.NE.delete(p);
        			this.numTotalPoint--;
        		}
        	}
        }
        
        ArrayList<PRQuadBlackNode> BlackNodeLst = new ArrayList<>();
        ArrayList<PRQuadGrayNode> GrayNodeLst = new ArrayList<>();
        
        if(this.SW != null) {
        	if(this.SW instanceof PRQuadBlackNode) {
        		BlackNodeLst.add((PRQuadBlackNode) this.SW);
        	}else if(this.SW instanceof PRQuadGrayNode) {
        		GrayNodeLst.add((PRQuadGrayNode) this.SW);
        	}
        }
        
        if(this.NW != null) {
        	if(this.NW instanceof PRQuadBlackNode) {
        		BlackNodeLst.add((PRQuadBlackNode) this.NW);
        	}else if(this.NW instanceof PRQuadGrayNode) {
        		GrayNodeLst.add((PRQuadGrayNode) this.NW);
        	}
        }
        
        if(this.SE != null) {
        	if(this.SE instanceof PRQuadBlackNode) {
        		BlackNodeLst.add((PRQuadBlackNode) this.SE);
        	}else if(this.SE instanceof PRQuadGrayNode) {
        		GrayNodeLst.add((PRQuadGrayNode) this.SE);
        	}
        }
        
        if(this.NE != null) {
        	if(this.NE instanceof PRQuadBlackNode) {
        		BlackNodeLst.add((PRQuadBlackNode) this.NE);
        	}else if(this.NE instanceof PRQuadGrayNode) {
        		GrayNodeLst.add((PRQuadGrayNode) this.NE);
        	}
        }
        
        if(this.numTotalPoint == 0) {
        	// should not happen
        	return null;
        }else if((BlackNodeLst.size() == 1 && GrayNodeLst.size() == 0) ||
        		(this.numTotalPoint <= this.bucketingParam && GrayNodeLst.size() == 0)) {
        	// if there is only one BlackNode child of GrayNode
        	// or there is only BlackNode child and WhiteNode child in GrayNode, and points in BlackNode could merge into one BlackNode
        	PRQuadBlackNode newNode = new PRQuadBlackNode(this.centroid, k, this.bucketingParam);
        	
        	for(int j = 0; j < BlackNodeLst.size(); j++) {
        		for(KDPoint pt: BlackNodeLst.get(j).getPoints()) {
        			newNode.insert(pt, k);
        		}
        	}
        	
        	this.height--;
        	return newNode;
        }else {
        	int SW_Height, NW_Height, SE_Height, NE_Height;
            
            if(this.SW == null) {
            	SW_Height = -1;
            }else {
            	SW_Height = this.SW.height();
            }
            if(this.NW == null) {
            	NW_Height = -1;
            }else {
            	NW_Height = this.NW.height();
            }
            if(this.SE == null) {
            	SE_Height = -1;
            }else {
            	SE_Height = this.SE.height();
            }
            if(this.NE == null) {
            	NE_Height = -1;
            }else {
            	NE_Height = this.NE.height();
            }
            
            this.height = Math.max(SW_Height, Math.max(NW_Height, Math.max(SE_Height, NE_Height))) + 1;
            return this;
        }
    }

    @Override
    public boolean search(KDPoint p){
    	if(p.coords[0] < this.centroid.coords[0]) {
        	if(p.coords[1] < this.centroid.coords[1]) {
        		if(this.SW == null) {
        			return false;
        		}else if(this.SW instanceof PRQuadBlackNode){
        			return ((PRQuadBlackNode)this.SW).search(p);
        		}else {
        			return this.SW.search(p);
        		}
        	}else {
        		if(this.NW == null) {
        			return false;
        		}else if(this.NW instanceof PRQuadBlackNode){
        			return ((PRQuadBlackNode)this.NW).search(p);
        		}else {
        			return this.NW.search(p);
        		}
        	}
        }else {
        	if(p.coords[1] < this.centroid.coords[1]) {
        		if(this.SE == null) {
        			return false;
        		}else if(this.SE instanceof PRQuadBlackNode){
        			return ((PRQuadBlackNode)this.SE).search(p);
        		}else {
        			return  this.SE.search(p);
        		}
        	}else {
        		if(this.NE == null) {
        			return false;
        		}else if(this.NE instanceof PRQuadBlackNode){
        			return ((PRQuadBlackNode)this.NE).search(p);
        		}else {
        			return this.NE.search(p);
        		}
        	}
        }
    }

    @Override
    public int height(){
        return this.height;
    }

    @Override
    public int count(){
        return this.numTotalPoint;
    }

    /**
     * Returns the children of the current node in the form of a Z-ordered 1-D array.
     * @return An array of references to the children of {@code this}. The order is Z (Morton), like so:
     * <ol>
     *     <li>0 is NW</li>
     *     <li>1 is NE</li>
     *     <li>2 is SW</li>
     *     <li>3 is SE</li>
     * </ol>
     */
    public PRQuadNode[] getChildren(){
    	PRQuadNode [] lst = new PRQuadNode[4];
    	
    	lst[0] = this.NW;
    	lst[1] = this.NE;
    	lst[2] = this.SW;
    	lst[3] = this.SE;
    	
    	return lst;
    }

    @Override
    public void range(KDPoint anchor, Collection<KDPoint> results,
                      double range) {
    	boolean visitNW = false, visitNE = false, visitSW = false, visitSE = false;
    	
    	if(anchor.coords[0] < this.centroid.coords[0]) {
        	if(anchor.coords[1] < this.centroid.coords[1]) {
        		visitSW = true;
        		
        		if(this.SW != null && this.SW instanceof PRQuadBlackNode) {
        			((PRQuadBlackNode)this.SW).range(anchor, results, range);
        		}else {
        			((PRQuadGrayNode)this.SW).range(anchor, results, range);
        		}
        	}else {
        		visitNW = true;
        		
        		if(this.NW != null && this.NW instanceof PRQuadBlackNode) {
        			((PRQuadBlackNode)this.NW).range(anchor, results, range);
        		}else {
        			((PRQuadGrayNode)this.NW).range(anchor, results, range);
        		}
        	}
        }else {
        	if(anchor.coords[1] < this.centroid.coords[1]) {
        		visitSE = true;
        		
        		if(this.SE != null && this.SE instanceof PRQuadBlackNode) {
        			((PRQuadBlackNode)this.SE).range(anchor, results, range);
        		}else {
        			((PRQuadGrayNode)this.SE).range(anchor, results, range);
        		}
        	}else {
        		visitNE = true;
        		
        		if(this.NE != null && this.NE instanceof PRQuadBlackNode) {
        			((PRQuadBlackNode)this.NE).range(anchor, results, range);
        		}else {
        			((PRQuadGrayNode)this.NE).range(anchor, results, range);
        		}
        	}
        }
    	
    	if(this.NW != null && visitNW != true && this.NW.doesQuadIntersectAnchorRange(anchor, range)) {
    		this.NW.range(anchor, results, range);
    	}
    	if(this.NE != null && visitNE != true && this.NE.doesQuadIntersectAnchorRange(anchor, range)) {
    		this.NE.range(anchor, results, range);
    	}
    	if(this.SW != null && visitSW != true && this.SW.doesQuadIntersectAnchorRange(anchor, range)) {
    		this.SW.range(anchor, results, range);
    	}
    	if(this.SE != null && visitSE != true && this.SE.doesQuadIntersectAnchorRange(anchor, range)) {
    		this.SE.range(anchor, results, range);
    	}
    }

    @Override
    public NNData<KDPoint> nearestNeighbor(KDPoint anchor, NNData<KDPoint> n)  {
        this.nearestNeighborHelper(anchor, n);
        return n;
    }
    
    private void nearestNeighborHelper(KDPoint anchor, NNData<KDPoint> n) {
    	boolean visitNW = false, visitNE = false, visitSW = false, visitSE = false;
    	
    	if(anchor.coords[0] < this.centroid.coords[0]) {
        	if(anchor.coords[1] < this.centroid.coords[1]) {
        		visitSW = true;
        		
        		if(this.SW != null) {
        			this.SW.nearestNeighbor(anchor, n);
        		}
        	}else {
        		visitNW = true;
        		
        		if(this.NW != null) {
        			this.NW.nearestNeighbor(anchor, n);
        		}
        	}
        }else {
        	if(anchor.coords[1] < this.centroid.coords[1]) {
        		visitSE = true;
        		
        		if(this.SE != null) {
        			this.SE.nearestNeighbor(anchor, n);
        		}
        	}else {
        		visitNE = true;
        		
        		if(this.NE != null ) {
        			this.NE.nearestNeighbor(anchor, n);
        		}
        	}
        }
    	
    	if(this.NW != null && visitNW != true) { 
    		if(n.getBestDist() == -1 || this.NW.doesQuadIntersectAnchorRange(anchor, n.getBestDist())) {
    			this.NW.nearestNeighbor(anchor, n);
    		}	
    	}
    	
    	if(this.NE != null && visitNE != true) {
    		if(n.getBestDist() == -1 || this.NE.doesQuadIntersectAnchorRange(anchor, n.getBestDist())) {
    			this.NE.nearestNeighbor(anchor, n);
    		}
    	}
    	
    	if(this.SW != null && visitSW != true) {
    		if(n.getBestDist() == -1 || this.SW.doesQuadIntersectAnchorRange(anchor, n.getBestDist())) {
    			this.SW.nearestNeighbor(anchor, n);
    		}
    	}
    	
    	if(this.SE != null && visitSE != true) {
    		if(n.getBestDist() == -1 || this.SE.doesQuadIntersectAnchorRange(anchor, n.getBestDist())) {
    			this.SE.nearestNeighbor(anchor, n);
    		}
    	}
    }

    @Override
    public void kNearestNeighbors(int k, KDPoint anchor, BoundedPriorityQueue<KDPoint> queue) {
    	//System.out.printf("anchor x = %d, y =%d\n",anchor.coords[0], anchor.coords[1]);
    	
    	boolean visitNW = false, visitNE = false, visitSW = false, visitSE = false;
    	
    	if(anchor.coords[0] < this.centroid.coords[0]) {
        	if(anchor.coords[1] < this.centroid.coords[1]) {
        		visitSW = true;
        		
        		if(this.SW != null) {
        			this.SW.kNearestNeighbors(k, anchor, queue);
        		}
        	}else {
        		visitNW = true;
        		
        		if(this.NW != null) {
        			this.NW.kNearestNeighbors(k, anchor, queue);
        		}
        	}
        }else {
        	if(anchor.coords[1] < this.centroid.coords[1]) {
        		visitSE = true;
        		
        		if(this.SE != null) {
        			this.SE.kNearestNeighbors(k, anchor, queue);
        		}
        	}else {
        		visitNE = true;
        		
        		if(this.NE != null ) {
        			this.NE.kNearestNeighbors(k, anchor, queue);
        		}
        	}
        }
    	
    	if(this.NW != null && visitNW != true) {
    		if(queue.size() != k || this.NW.doesQuadIntersectAnchorRange(anchor, queue.last().euclideanDistance(anchor))) {
    			this.NW.kNearestNeighbors(k, anchor, queue);
    		}
    	}
    	if(this.NE != null && visitNE != true) {
    		if(queue.size() != k || this.NE.doesQuadIntersectAnchorRange(anchor, queue.last().euclideanDistance(anchor))) {
    			this.NE.kNearestNeighbors(k, anchor, queue);
    		}
    	}
    	if(this.SW != null && visitSW != true) {
    		if(queue.size() != k || this.SW.doesQuadIntersectAnchorRange(anchor, queue.last().euclideanDistance(anchor))) {
    			this.SW.kNearestNeighbors(k, anchor, queue);
    		}
    	}
    	if(this.SE != null && visitSE != true ) {
    		if(queue.size() != k || this.SE.doesQuadIntersectAnchorRange(anchor, queue.last().euclideanDistance(anchor))) {
    			this.SE.kNearestNeighbors(k, anchor, queue);
    		}
    	}
    }
}

