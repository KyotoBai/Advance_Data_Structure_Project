package pqueue.priorityqueues; // ******* <---  DO NOT ERASE THIS LINE!!!! *******


/* *****************************************************************************************
 * THE FOLLOWING IMPORTS WILL BE NEEDED BY YOUR CODE, BECAUSE WE REQUIRE THAT YOU USE
 * ANY ONE OF YOUR EXISTING MINHEAP IMPLEMENTATIONS TO IMPLEMENT THIS CLASS. TO ACCESS
 * YOUR MINHEAP'S METHODS YOU NEED THEIR SIGNATURES, WHICH ARE DECLARED IN THE MINHEAP
 * INTERFACE. ALSO, SINCE THE PRIORITYQUEUE INTERFACE THAT YOU EXTEND IS ITERABLE, THE IMPORT OF ITERATOR
 * IS NEEDED IN ORDER TO MAKE YOUR CODE COMPILABLE. THE IMPLEMENTATIONS OF CHECKED EXCEPTIONS
 * ARE ALSO MADE VISIBLE BY VIRTUE OF THESE IMPORTS.
 ** ********************************************************************************* */

import pqueue.exceptions.*;
import pqueue.heaps.ArrayMinHeap;
import pqueue.heaps.EmptyHeapException;
import pqueue.heaps.MinHeap;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
/**
 * <p>{@link MinHeapPriorityQueue} is a {@link PriorityQueue} implemented using a {@link MinHeap}.</p>
 *
 * <p>You  <b>must</b> implement the methods of this class! To receive <b>any credit</b> for the unit tests
 * related to this class, your implementation <b>must</b> use <b>whichever</b> {@link MinHeap} implementation
 * among the two that you should have implemented you choose!</p>
 *
 * @author  ---- Yuchen Liu ----
 *
 * @param <T> The Type held by the container.
 *
 * @see LinearPriorityQueue
 * @see MinHeap
 * @see PriorityQueue
 */
public class MinHeapPriorityQueue<T> implements PriorityQueue<T>{

	/* ***********************************************************************************
	 * Write any private data elements or private methods for MinHeapPriorityQueue here...*
	 * ***********************************************************************************/

	private ArrayMinHeap<Node> heap_p_queue;
	
	private int count_mod = 0;
	
	private class Node implements Comparable<Node>{
		T value;
		int data;
		
		public Node(T data, int priority) {
			this.value = data;
			this.data = priority;
		}
		
		@Override
		public int compareTo(Node o) {
			return this.data - o.data;
		}
	}

	/* *********************************************************************************************************
	 * Implement the following public methods. You should erase the throwings of UnimplementedMethodExceptions.*
	 ***********************************************************************************************************/
		/**
	 * Simple default constructor.
	 */
	public MinHeapPriorityQueue(){
		this.heap_p_queue = new ArrayMinHeap<Node>();
	}

	@Override
	public void enqueue(T element, int priority) throws InvalidPriorityException {	// DO *NOT* ERASE THE "THROWS" DECLARATION!
		if(priority > 0) {
			count_mod++;
			Node insert_node = new Node(element, priority);
			
			this.heap_p_queue.insert(insert_node);
		}else {
			throw new InvalidPriorityException("Priority invalid");
		}
	}

	@Override
	public T dequeue() throws EmptyPriorityQueueException{		// DO *NOT* ERASE THE "THROWS" DECLARATION!
		if(this.heap_p_queue.size() > 0) {
			count_mod++;
			T val;
			
			try {
				val = this.heap_p_queue.getMin().value;
				this.heap_p_queue.deleteMin();
			} catch (EmptyHeapException e) {
				throw new EmptyPriorityQueueException("Empty Queue");
			}
			return val;
		}else {
			throw new EmptyPriorityQueueException("Empty Queue");
		}
	}

	@Override
	public T getFirst() throws EmptyPriorityQueueException {	// DO *NOT* ERASE THE "THROWS" DECLARATION!
		T val;
		
		try{
			val = this.heap_p_queue.getMin().value;
		}catch(EmptyHeapException e){
			throw new EmptyPriorityQueueException("Empty Queuet");
		}
		return val;
	}

	@Override
	public int size() {
		return this.heap_p_queue.size();
	}

	@Override
	public boolean isEmpty() {
		return this.heap_p_queue.size() == 0;
	}


	@Override
	public Iterator<T> iterator() {
		return new MinHeapPriorityQueueIterator();
	}
	
	private class MinHeapPriorityQueueIterator implements Iterator<T>{
		ArrayMinHeap<Node> arr = heap_p_queue;
		Iterator<Node> iterator = arr.iterator();
		int mod = count_mod;
		
		@Override
		public boolean hasNext() {
			if(mod == count_mod) {
				return iterator.hasNext();
			}else {
				throw new ConcurrentModificationException();
			}
		}

		@Override
		public T next() {
			if(mod == count_mod) {
				return iterator.next().value;
			}else{
				throw new ConcurrentModificationException();
			}
		}
		
	}
}
