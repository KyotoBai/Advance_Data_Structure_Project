package pqueue.priorityqueues; // ******* <---  DO NOT ERASE THIS LINE!!!! *******

/* *****************************************************************************************
 * THE FOLLOWING IMPORTS ARE HERE ONLY TO MAKE THE JAVADOC AND iterator() METHOD SIGNATURE
 * "SEE" THE RELEVANT CLASSES. SOME OF THOSE IMPORTS MIGHT *NOT* BE NEEDED BY YOUR OWN
 * IMPLEMENTATION, AND IT IS COMPLETELY FINE TO ERASE THEM. THE CHOICE IS YOURS.
 * ********************************************************************************** */

import demos.GenericArrays;
import pqueue.exceptions.*;
import pqueue.fifoqueues.FIFOQueue;
import pqueue.heaps.ArrayMinHeap;
import java.util.ConcurrentModificationException;

import java.util.*;
/**
 * <p>{@link LinearPriorityQueue} is a {@link PriorityQueue} implemented as a linear {@link java.util.Collection}
 * of common {@link FIFOQueue}s, where the {@link FIFOQueue}s themselves hold objects
 * with the same priority (in the order they were inserted).</p>
 *
 * <p>You  <b>must</b> implement the methods in this file! To receive <b>any credit</b> for the unit tests related to
 * this class, your implementation <b>must</b>  use <b>whichever</b> linear {@link Collection} you want (e.g
 * {@link ArrayList}, {@link LinkedList}, {@link java.util.Queue}), or even the various {@link List} and {@link FIFOQueue}
 * implementations that we provide for you. You can also use <b>raw</b> arrays, but take a look at {@link GenericArrays}
 * if you intend to do so. Note that, unlike {@link ArrayMinHeap}, we do not insist that you use a contiguous storage
 * {@link Collection}, but any one available (including {@link LinkedList}) </p>
 *
 * @param <T> The type held by the container.
 *
 * @author  ---- Yuchen Liu ----
 *
 * @see MinHeapPriorityQueue
 * @see PriorityQueue
 * @see GenericArrays
 */
public class LinearPriorityQueue<T> implements PriorityQueue<T> {

	/* ***********************************************************************************
	 * Write any private data elements or private methods for LinearPriorityQueue here...*
	 * ***********************************************************************************/

	private ArrayList<Node> p_queue;
	private int count_mod = 0;
	private int max_size;
	
	private class Node{
		T data;
		int priority;
		
		public Node(T data, int priority) {
			this.data = data;
			this.priority = priority;
		}
	}

	/* *********************************************************************************************************
	 * Implement the following public methods. You should erase the throwings of UnimplementedMethodExceptions.*
	 ***********************************************************************************************************/

	/**
	 * Default constructor initializes the element structure with
	 * a default capacity. This default capacity will be the default capacity of the
	 * underlying element structure that you will choose to use to implement this class.
	 */
	public LinearPriorityQueue(){
		this.p_queue = new ArrayList<Node>();
		this.max_size = 15;
	}

	/**
	 * Non-default constructor initializes the element structure with
	 * the provided capacity. This provided capacity will need to be passed to the default capacity
	 * of the underlying element structure that you will choose to use to implement this class.
	 * @see #LinearPriorityQueue()
	 * @param capacity The initial capacity to endow your inner implementation with.
	 * @throws InvalidCapacityException if the capacity provided is less than 1.
	 */
	public LinearPriorityQueue(int capacity) throws InvalidCapacityException{	// DO *NOT* ERASE THE "THROWS" DECLARATION!
		if (capacity >= 0) {
			this.p_queue = new ArrayList<Node>();
			this.max_size = capacity;
		}else {
			throw new InvalidCapacityException("Capacity invalid");
		}
	}

	@Override
	public void enqueue(T element, int priority) throws InvalidPriorityException { // DO *NOT* ERASE THE "THROWS"
																					// DECLARATION!
		if (priority > 0) {
			if ((this.p_queue.size() + 1) <= this.max_size) {
				count_mod++;

				Node insert_node = new Node(element, priority);

				if (this.p_queue.size() == 0) {
					this.p_queue.add(insert_node);
				} else {
					int i = (this.p_queue.size() - 1);
					while (i >= 0) {
						if (this.p_queue.get(i).priority <= priority) {
							this.p_queue.add(i + 1, insert_node);

							break;
						}
						--i;
					}
					
					if(i == -1 && this.p_queue.get(0).priority > priority) {
						this.p_queue.add(i + 1, insert_node);
					}
				}
			}

		} else {
			throw new InvalidPriorityException("Priority invalid");
		}
	}

	@Override
	public T dequeue() throws EmptyPriorityQueueException { 	// DO *NOT* ERASE THE "THROWS" DECLARATION!
		if(this.p_queue.size() > 0) {
			count_mod++;
			T val = this.p_queue.get(0).data;
			this.p_queue.remove(0);
			
			return val;
		}else {
			throw new EmptyPriorityQueueException("Nothing in queue");
		}
	}

	@Override
	public T getFirst() throws EmptyPriorityQueueException {	// DO *NOT* ERASE THE "THROWS" DECLARATION!
		if (this.p_queue.size() > 0) {
			return this.p_queue.get(0).data;
		}else {
			throw new EmptyPriorityQueueException("Nothing in queue");
		}
	}

	@Override
	public int size() {
		return this.p_queue.size();
	}

	@Override
	public boolean isEmpty() {
		return this.p_queue.size() == 0;
	}


	@Override
	public Iterator<T> iterator() {
		return new LinearPriorityQueueIterator();
	}
	
	private class LinearPriorityQueueIterator implements Iterator<T>{
		private ArrayList<Node> arr = p_queue;
		int index = 0;
		int mod = count_mod;
		
		@Override
		public boolean hasNext() {
			return (index < arr.size());
		}

		@Override
		public T next() throws ConcurrentModificationException{
			if (mod == count_mod) {
				T val = arr.get(index).data;
				index++;
				return val;
			}else {
				throw new ConcurrentModificationException();
			}
		}
		
	}

}