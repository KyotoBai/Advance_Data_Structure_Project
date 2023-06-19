package pqueue.heaps; // ******* <---  DO NOT ERASE THIS LINE!!!! *******

/* *****************************************************************************************
 * THE FOLLOWING IMPORT IS NECESSARY FOR THE ITERATOR() METHOD'S SIGNATURE. FOR THIS
 * REASON, YOU SHOULD NOT ERASE IT! YOUR CODE WILL BE UNCOMPILABLE IF YOU DO!
 * ********************************************************************************** */

import pqueue.exceptions.UnimplementedMethodException;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.lang.Math;

import java.util.Iterator;


/**
 * <p>{@link ArrayMinHeap} is a {@link MinHeap} implemented using an internal array. Since heaps are <b>complete</b>
 * binary trees, using contiguous storage to store them is an excellent idea, since with such storage we avoid
 * wasting bytes per {@code null} pointer in a linked implementation.</p>
 *
 * <p>You <b>must</b> edit this class! To receive <b>any</b> credit for the unit tests related to this class,
 * your implementation <b>must</b> be a <b>contiguous storage</b> implementation based on a linear {@link java.util.Collection}
 * like an {@link java.util.ArrayList} or a {@link java.util.Vector} (but *not* a {@link java.util.LinkedList} because it's *not*
 * contiguous storage!). or a raw Java array. We provide an array for you to start with, but if you prefer, you can switch it to a
 * {@link java.util.Collection} as mentioned above. </p>
 *
 * @author -- Yuchen Liu ---
 *
 * @see MinHeap
 * @see LinkedMinHeap
 * @see demos.GenericArrays
 */

public class ArrayMinHeap<T extends Comparable<T>> implements MinHeap<T> {

	/* *****************************************************************************************************************
	 * This array will store your data. You may replace it with a linear Collection if you wish, but
	 * consult this class' 	 * JavaDocs before you do so. We allow you this option because if you aren't
	 * careful, you can end up having ClassCastExceptions thrown at you if you work with a raw array of Objects.
	 * See, the type T that this class contains needs to be Comparable with other types T, but Objects are at the top
	 * of the class hierarchy; they can't be Comparable, Iterable, Clonable, Serializable, etc. See GenericArrays.java
	 * under the package demos* for more information.
	 * *****************************************************************************************************************/
	private Object[] data;
	private long[] time_lst;

	/* *********************************************************************************** *
	 * Write any further private data elements or private methods for LinkedMinHeap here...*
	 * *************************************************************************************/
	
	private int count_mod = 0;

	private long start_time;
	/* *********************************************************************************************************
	 * Implement the following public methods. You should erase the throwings of UnimplementedMethodExceptions.*
	 ***********************************************************************************************************/

	/**
	 * Default constructor initializes the data structure with some default
	 * capacity you can choose.
	 */
	public ArrayMinHeap(){
		this.data = new Object[0];
		this.time_lst = new long[0];
		this.start_time = System.nanoTime();
	}

	/**
	 *  Second, non-default constructor which provides the element with which to initialize the heap's root.
	 *  @param rootElement the element to create the root with.
	 */
	public ArrayMinHeap(T rootElement){
		this.data = new Object[1];
		this.time_lst = new long[1];
		this.time_lst[0] = System.nanoTime();
		this.data[0] = rootElement;
	}

	/**
	 * Copy constructor initializes {@code this} as a carbon copy of the {@link MinHeap} parameter.
	 *
	 * @param other The MinHeap object to base construction of the current object on.
	 */
	public ArrayMinHeap(MinHeap<T> other){
		ArrayMinHeap<T> temp_h = (ArrayMinHeap<T>) other;
		
		this.start_time = temp_h.start_time;
		
		this.data = new Object[temp_h.data.length];
		this.time_lst = new long[temp_h.data.length];
		for (int i = 0; i < temp_h.data.length; i++) {
			this.data[i] = temp_h.data[i];
			this.time_lst[i] = temp_h.time_lst[i];
		}
	}

	/**
	 * Standard {@code equals()} method. We provide it for you: DO NOT ERASE! Consider its implementation when implementing
	 * {@link #ArrayMinHeap(MinHeap)}.
	 * @return {@code true} if the current object and the parameter object
	 * are equal, with the code providing the equality contract.
	 * @see #ArrayMinHeap(MinHeap)
	 */
	/*
	public void print_heap() {
		int p_size = this.data.length;
		for (int i = 0; i < p_size; i++) {
			System.out.print(this.data[i]);
		}
		System.out.println();
	}*/
	
	@Override
	public boolean equals(Object other){
		if(other == null || !(other instanceof MinHeap))
			return false;
		Iterator itThis = iterator();
		Iterator itOther = ((MinHeap) other).iterator();
		while(itThis.hasNext())
			if(!itThis.next().equals(itOther.next()))
				return false;
		return !itOther.hasNext();
	}


	@Override
	public void insert(T element) {
		count_mod++;
		
		Object[] new_data = new Object[this.data.length + 1];
		long[] new_time_lst = new long[this.data.length + 1];
		
		for (int i = 0; i < this.data.length; i++) {
			new_data[i] = this.data[i];
			new_time_lst[i] = this.time_lst[i];
		}
		new_data[this.data.length] = element;
		new_time_lst[this.data.length] = System.nanoTime();
		
		int arr_size = new_data.length;
		int insert_parent_index = new_data.length;
		int insert_index = arr_size;
		
		this.data = new_data;
		this.time_lst = new_time_lst;
		
		if (insert_index % 2 == 0){
			insert_parent_index = insert_index / 2;
		}else {
			insert_parent_index = (insert_index - 1) / 2;
		}
		
		while(insert_parent_index > 0) {
			if ((((T) this.data[insert_index - 1]).compareTo((T) this.data[insert_parent_index - 1])) < 0) {
				T ele = (T) this.data[insert_index - 1];
				this.data[insert_index - 1] = this.data[insert_parent_index - 1];
				this.data[insert_parent_index - 1] = ele;
				
				long l = this.time_lst[insert_index - 1];
				this.time_lst[insert_index - 1] = this.time_lst[insert_parent_index - 1];
				this.time_lst[insert_parent_index - 1] = l;
				
				insert_index = insert_parent_index;
				
				if(insert_index == 0) {
					break;
				}else if (insert_index % 2 == 0){ 
					insert_parent_index = insert_index / 2;
				}else {
					insert_parent_index = (insert_index - 1) / 2;
				}
			}else {
				break;
			}
		}
		//this.print_heap();
	}

	@Override
	public T deleteMin() throws EmptyHeapException { // DO *NOT* ERASE THE "THROWS" DECLARATION!
		if (this.data.length > 0) {
			count_mod++;
			
			T delete_data = (T)this.data[0];
			
			if(this.data.length == 1) {
				this.data = new Object[0];
				return delete_data;
			}else {
				Object[] new_data = new Object[this.data.length - 1];
				long[] new_time_lst = new long[this.data.length + 1];
				
				new_data[0] = this.data[this.data.length - 1];
				new_time_lst[0] = this.time_lst[this.data.length - 1];
				for (int i = 1; i < this.data.length - 1; i++) {
					new_data[i] = this.data[i];
					new_time_lst[i] = this.time_lst[i];
				}
				
				this.data = new_data;
				this.time_lst = new_time_lst;
				
				int start_index = 1;
				int lChild = 2;
				int rChild = 3;
				while(start_index <= (Math.floor(this.data.length/2))) {
					if (rChild > this.data.length
							&& (((T) this.data[lChild - 1]).compareTo((T) this.data[start_index - 1])) <= 0) {
						if((((T) this.data[lChild - 1]).compareTo((T) this.data[start_index - 1])) == 0
								&& this.time_lst[lChild - 1] < this.time_lst[start_index - 1]) {
							break;
						}
						
						T ele = (T)this.data[start_index - 1];
						this.data[start_index - 1] = this.data[lChild - 1];
						this.data[lChild - 1] = ele;
						
						long l = this.time_lst[start_index - 1];
						this.time_lst[start_index - 1] = this.time_lst[lChild - 1];
						this.time_lst[lChild - 1] = l;
						
						break;
					}else if (rChild > this.data.length){
						break;
					}
					
					if ((((T) this.data[lChild - 1]).compareTo((T) this.data[start_index - 1])) <= 0
							&& (((T) this.data[lChild - 1]).compareTo((T) this.data[rChild - 1])) < 0) {
						if((((T) this.data[lChild - 1]).compareTo((T) this.data[start_index - 1])) == 0
								&& this.time_lst[lChild - 1] < this.time_lst[start_index - 1]) {
							break;
						}
						
						T ele = (T)this.data[start_index - 1];
						this.data[start_index - 1] = this.data[lChild - 1];
						this.data[lChild - 1] = ele;
						
						long l = this.time_lst[start_index - 1];
						this.time_lst[start_index - 1] = this.time_lst[lChild - 1];
						this.time_lst[lChild - 1] = l;
						
						start_index = lChild;
						lChild = start_index * 2;
						rChild = (start_index * 2) + 1;
					}else if((((T) this.data[rChild - 1]).compareTo((T) this.data[start_index - 1])) <= 0
							&& (((T) this.data[rChild - 1]).compareTo((T) this.data[lChild - 1])) < 0) {
						if((((T) this.data[rChild - 1]).compareTo((T) this.data[start_index - 1])) == 0
								&& this.time_lst[rChild - 1] < this.time_lst[start_index - 1]) {
							break;
						}
						
						T ele = (T)this.data[start_index - 1];
						this.data[start_index - 1] = this.data[rChild - 1];
						this.data[rChild - 1] = ele;
						
						long l = this.time_lst[start_index - 1];
						this.time_lst[start_index - 1] = this.time_lst[rChild - 1];
						this.time_lst[rChild - 1] = l;
						
						start_index = rChild;
						lChild = start_index * 2;
						rChild = (start_index * 2) + 1;
					}else {
						break;
					}
				}
				//this.print_heap();
				return delete_data;
			}
			
			
		}else {
			throw new EmptyHeapException("Empty Heap");
		}
	}

		@Override
	public T getMin() throws EmptyHeapException {	// DO *NOT* ERASE THE "THROWS" DECLARATION!
		if (this.data.length > 0) {
			return (T) this.data[0];
		}else {
			throw new EmptyHeapException("No Heap, thus no min");
		}
			
	}

	@Override
	public int size() {
		return this.data.length;
	}

	@Override
	public boolean isEmpty() {
		return this.data.length == 0;
	}

	/**
	 * Standard equals() method.
	 * @return {@code true} if the current object and the parameter object
	 * are equal, with the code providing the equality contract.
	 */


	@Override
	public Iterator<T> iterator() {
		return new ArrayMinHeapIterator<T>(this.data, this.time_lst);
	}
	
	private class ArrayMinHeapIterator<T extends Comparable<T>> implements Iterator<T>{
		private ArrayList<Object> arr_data;
		private ArrayList<Long> arr_time_lst;
		private int mod = count_mod;
		private int start_time;

		public ArrayMinHeapIterator(Object[] data, long[] t_lst) {
			arr_data = new ArrayList<>();
			arr_time_lst = new ArrayList<>();
			
			start_time = this.start_time;
			
			for (int i = 0; i < data.length; i++) {
				arr_data.add(i, data[i]);
				arr_time_lst.add(i, t_lst[i]);
			}
		}

		@Override
		public boolean hasNext() {
			if (mod != count_mod){
				throw new ConcurrentModificationException();
			}else{
				return this.arr_data.size() > 0;
			}
		}

		@Override
		public T next() {
			if (mod != count_mod) {
				throw new ConcurrentModificationException();
			}else {
				T val = (T) arr_data.get(0);
				
				if (arr_data.size() == 1) {
					arr_data = new ArrayList<>();
					
					return val;
				}else {
				    Object last_data = arr_data.get(arr_data.size() - 1);
				    
				    arr_data.remove(arr_data.size() - 1);
				    arr_data.remove(0);
				    
				    arr_data.add(0, last_data);
				    
				    // minheapify
				    int start_index = 1;
					int lChild = 2;
					int rChild = 3;
					while(start_index <= (Math.floor(arr_data.size()/2))) {
						if(rChild > arr_data.size() 
								&& (((T) arr_data.get(lChild - 1)).compareTo((T) arr_data.get(start_index - 1))) <= 0) {
							T ele = (T)arr_data.get(start_index - 1);
							arr_data.set(start_index - 1, arr_data.get(lChild - 1));
							arr_data.set(lChild - 1, ele);
							
							break;
						}else if (rChild > arr_data.size()){
							break;
						}
						
						if ( ((T) arr_data.get(lChild - 1)).compareTo((T) arr_data.get(start_index - 1)) == 0 ) {
							T ele = (T)arr_data.get(start_index - 1);
							arr_data.set(start_index - 1, arr_data.get(lChild - 1));
							arr_data.set(lChild - 1, ele);
						}
						
						if ( ((T) arr_data.get(rChild - 1)).compareTo((T) arr_data.get(start_index - 1)) == 0 ) {
							T ele = (T)arr_data.get(start_index - 1);
							arr_data.set(start_index - 1, arr_data.get(rChild - 1));
							arr_data.set(rChild - 1, ele);
						}
						
						if ((((T) arr_data.get(lChild - 1)).compareTo((T) arr_data.get(start_index - 1))) < 0 
								&& (((T) arr_data.get(lChild - 1)).compareTo((T) arr_data.get(rChild - 1))) < 0) {
							T ele = (T)arr_data.get(start_index - 1);
							arr_data.set(start_index - 1, arr_data.get(lChild - 1));
							arr_data.set(lChild - 1, ele);
							
							start_index = lChild;
							lChild = start_index * 2;
							rChild = (start_index * 2) + 1;
						}else if((((T) arr_data.get(rChild - 1)).compareTo((T) arr_data.get(start_index - 1))) < 0
								&& (((T) arr_data.get(rChild - 1)).compareTo((T) arr_data.get(lChild - 1))) < 0) {
							T ele = (T)arr_data.get(start_index - 1);
							arr_data.set(start_index - 1, arr_data.get(rChild - 1));
							arr_data.set(rChild - 1, ele);
							
							start_index = rChild;
							lChild = start_index * 2;
							rChild = (start_index * 2) + 1;
						}else {
							break;
						}
					}
					return val;
				}
			}
		}
		
	}

}
