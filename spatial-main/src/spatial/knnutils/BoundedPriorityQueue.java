package spatial.knnutils;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import spatial.exceptions.UnimplementedMethodException;


/**
 * <p>{@link BoundedPriorityQueue} is a priority queue whose number of elements
 * is bounded. Insertions are such that if the queue's provided capacity is surpassed,
 * its length is not expanded, but rather the maximum priority element is ejected
 * (which could be the element just attempted to be enqueued).</p>
 *
 * <p><b>YOU ***** MUST ***** IMPLEMENT THIS CLASS!</b></p>
 *
 * @author  <a href = "https://github.com/jasonfillipou/">Jason Filippou</a>
 *
 * @see PriorityQueue
 * @see PriorityQueueNode
 */
public class BoundedPriorityQueue<T> implements PriorityQueue<T>{

	/* *********************************************************************** */
	/* *************  PLACE YOUR PRIVATE FIELDS AND METHODS HERE: ************ */
	/* *********************************************************************** */

	private int size;
	private int orderInserted;
	private ArrayList<PriorityQueueNode<T>> queue;

	/* *********************************************************************** */
	/* ***************  IMPLEMENT THE FOLLOWING PUBLIC METHODS:  ************ */
	/* *********************************************************************** */

	public int mod_count = 0;
	
	/**
	 * Constructor that specifies the size of our queue.
	 * @param size The static size of the {@link BoundedPriorityQueue}. Has to be a positive integer.
	 * @throws IllegalArgumentException if size is not a strictly positive integer.
	 */
	public BoundedPriorityQueue(int size) throws IllegalArgumentException{
		if(size <= 0) {
			throw new IllegalArgumentException("Size is not right");
		}
		
		this.size = size;
		this.orderInserted = 0;
		this.queue = new ArrayList<>();
	}

	/**
	 * <p>Enqueueing elements for BoundedPriorityQueues works a little bit differently from general case
	 * PriorityQueues. If the queue is not at capacity, the element is inserted at its
	 * appropriate location in the sequence. On the other hand, if the object is at capacity, the element is
	 * inserted in its appropriate spot in the sequence (if such a spot exists, based on its priority) and
	 * the maximum priority element is ejected from the structure.</p>
	 * 
	 * @param element The element to insert in the queue.
	 * @param priority The priority of the element to insert in the queue.
	 */
	@Override
	public void enqueue(T element, double priority) {
		this.orderInserted += 1;
		PriorityQueueNode<T> curr = new PriorityQueueNode<>(element, priority, this.orderInserted);
		
		this.mod_count++;
		// nothing in queue, thus add a node in queue
		if(this.queue.isEmpty()) {
			this.queue.add(0, curr);
		}else {
			int index;
			boolean add_last = true;
			
			// loop through to find where to insert
			for(index = 0; index < this.queue.size(); index++) {
				// if compareTo return -1
				// means curr.priority < [index].priority   (should add curr before [index])
				// or curr.priority = [index].priority && curr.orderInserted < [index].orderInserted (should add curr before [index])
				if (curr.compareTo(this.queue.get(index)) < 0) {
					this.queue.add(index, curr);
					add_last = false;
					break;
				}
			}
			
			if(add_last) {
				this.queue.add(this.queue.size(), curr);
			}
			
			// check to see if queue has insert sth and bigger than it's size
			if(this.queue.size() > this.size) {
				// remove the last thing in queue
				this.queue.remove(this.queue.size() - 1);
			}
		}
	}

	@Override
	public T dequeue() {
		if(this.queue.isEmpty() == false) {
			this.mod_count++;
			T val = this.queue.get(0).getData();
			this.queue.remove(0);
			return val;
		}else {
			return null;
		}
	}

	@Override
	public T first() {
		if(this.queue.isEmpty() == false) {
			T val = this.queue.get(0).getData();
			return val;
		}else {
			return null;
		}
	}
	
	/**
	 * Returns the last element in the queue. Useful for cases where we want to 
	 * compare the priorities of a given quantity with the maximum priority of 
	 * our stored quantities. In a minheap-based implementation of any {@link PriorityQueue},
	 * this operation would scan O(n) nodes and O(nlogn) links. In an array-based implementation,
	 * it takes constant time.
	 * @return The maximum priority element in our queue, or null if the queue is empty.
	 */
	public T last() {
		if(this.queue.isEmpty() == false) {
			T val = this.queue.get(this.queue.size() - 1).getData();
			return val;
		}else {
			return null;
		}
	}

	/**
	 * Inspects whether a given element is in the queue. O(N) complexity.
	 * @param element The element to search for.
	 * @return {@code true} iff {@code element} is in {@code this}, {@code false} otherwise.
	 */
	public boolean contains(T element){
		for(int i = 0; i < this.queue.size(); i++) {
			if(this.queue.get(i).getData().equals(element)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int size() {
		return this.queue.size();
	}

	@Override
	public boolean isEmpty() {
		return this.queue.isEmpty();
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<>() {
			int index = 0;
			int iterMod = mod_count;
			@Override
			public boolean hasNext() {
				if(iterMod == mod_count) {
					return index < queue.size();
				}else {
					throw new ConcurrentModificationException();
				}
			}

			@Override
			public T next() {
				if(iterMod == mod_count) {
					index++;
					return queue.get(index - 1).getData();
				}else {
					throw new ConcurrentModificationException();
				}
			}
		};
	}
}
