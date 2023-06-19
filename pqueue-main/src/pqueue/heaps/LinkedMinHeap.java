package pqueue.heaps; // ******* <---  DO NOT ERASE THIS LINE!!!! *******


/* *****************************************************************************************
 * THE FOLLOWING IMPORT IS NECESSARY FOR THE ITERATOR() METHOD'S SIGNATURE. FOR THIS
 * REASON, YOU SHOULD NOT ERASE IT! YOUR CODE WILL BE UNCOMPILABLE IF YOU DO!
 * ********************************************************************************** */

import java.util.Iterator;
import java.util.NoSuchElementException;

import pqueue.exceptions.UnimplementedMethodException;

import java.util.ArrayList;
import java.lang.Math;
import java.util.ConcurrentModificationException;

/**
 * <p>A {@link LinkedMinHeap} is a tree (specifically, a <b>complete</b> binary tree) where every node is
 * smaller than or equal to its descendants (as defined by the {@link Comparable#compareTo(Object)} overridings of the type T).
 * Percolation is employed when the root is deleted, and insertions guarantee maintenance of the heap property in logarithmic time. </p>
 *
 * <p>You <b>must</b> edit this class! To receive <b>any</b> credit for the unit tests related to this class,
 * your implementation <b>must</b> be a &quot;linked&quot;, <b>non-contiguous storage</b> implementation based on a
 * binary tree of nodes and references. Use the skeleton code we have provided to your advantage, but always remember
 * that the only functionality our tests can test is {@code public} functionality.</p>
 * 
 * @author --- Yuchen Liu ---
 *
 * @param <T> The {@link Comparable} type of object held by {@code this}.
 *
 * @see MinHeap
 * @see ArrayMinHeap
 */
public class LinkedMinHeap<T extends Comparable<T>> implements MinHeap<T> {

	/* ***********************************************************************
	 * An inner class representing a minheap's node. YOU *SHOULD* BUILD YOUR *
	 * IMPLEMENTATION ON TOP OF THIS CLASS!                                  *
 	 * ********************************************************************* */
	private class MinHeapNode {
		private T data;
		private MinHeapNode lChild, rChild;

        /* *******************************************************************
         * Write any further data elements or methods for MinHeapNode here...*
         ********************************************************************* */
		private MinHeapNode parent;
	}

	/* *********************************
	  * Root of your tree: DO NOT ERASE!
	  * *********************************
	 */
	private MinHeapNode root;
	
    /* *********************************************************************************** *
     * Write any further private data elements or private methods for LinkedMinHeap here...*
     * *************************************************************************************/
	private int size;
	private int count_mod = 0;
	
	private MinHeapNode cpy_construct(MinHeapNode curr, MinHeapNode cpy_node) {
		curr = new MinHeapNode();
		curr.data = cpy_node.data;
		curr.lChild = cpy_construct(curr.lChild, cpy_node.lChild);
		curr.rChild = cpy_construct(curr.rChild, cpy_node.rChild);
		curr.parent = cpy_node.parent;

		return curr;
	}
	
	private ArrayList<Integer> find_node_path(int index){
		ArrayList<Integer> arr = new ArrayList<>();
		
		while(index >= 1) {
			if(index % 2 == 1) {
				arr.add(0, 1);
				
				index = (index - 1)/2;
			}else {
				arr.add(0, 0);
				
				index = index /2;
			}
		}
		return arr;
	}
	
	private MinHeapNode get_node(MinHeapNode curr, ArrayList<Integer> arr) {
		arr.remove(0);
		while(!arr.isEmpty()) {
			if (arr.get(0) == 0) {
				curr = curr.lChild;
			}else {
				curr = curr.rChild;
			}
			
			arr.remove(0);
		}
		return curr;
	}
	
	private int minheapify(int parent_index, MinHeapNode parent_node) {
		MinHeapNode lChild, rChild;
		lChild = parent_node.lChild;
		rChild = parent_node.rChild;
		
		if (lChild != null && rChild == null) {
			if (lChild.data.compareTo(parent_node.data) < 0) {
				MinHeapNode lChild_l, lChild_r, parent_p;
				lChild_l = lChild.lChild;
				lChild_r = lChild.rChild;
				parent_p = parent_node.parent;
				
				if (parent_p != null) {
					if (parent_p.lChild == parent_node) {
						parent_p.lChild = lChild;
					} else {
						parent_p.rChild = lChild;
					}
				}
				
				lChild.parent = parent_node.parent;
				lChild.lChild = parent_node;
				lChild.rChild = parent_node.rChild;
				
				if (parent_p == null) {
					this.root = lChild;
				}
				
				parent_node.parent = lChild;
				parent_node.lChild = lChild_l;
				parent_node.rChild = lChild_r;
				
				return 1;
			}else {
				return 0;
			}
		} else if(lChild == null && rChild == null) {
			return 0;
		} else {
			if(lChild.data.compareTo(parent_node.data) < 0
					&& lChild.data.compareTo(rChild.data) < 0) {
				MinHeapNode lChild_l, lChild_r, parent_p;
				lChild_l = lChild.lChild;
				lChild_r = lChild.rChild;
				parent_p = parent_node.parent;
				
				if(lChild_l != null) {
					lChild_l.parent = parent_node;
				}
				if(lChild_r != null) {
					lChild_r.parent = parent_node;
				}
				
				if (parent_p != null) {
					if (parent_p.lChild == parent_node) {
						parent_p.lChild = lChild;
					} else {
						parent_p.rChild = lChild;
					}
				}
				rChild.parent = lChild;
				
				lChild.parent = parent_node.parent;
				lChild.lChild = parent_node;
				lChild.rChild = parent_node.rChild;
				
				if (parent_p == null) {
					this.root = lChild;
				}
				
				parent_node.parent = lChild;
				parent_node.lChild = lChild_l;
				parent_node.rChild = lChild_r;
				
				return 1;
			}else if(rChild.data.compareTo(parent_node.data) < 0
						&& rChild.data.compareTo(lChild.data) < 0) {
				MinHeapNode rChild_l, rChild_r, parent_p;
				rChild_l = rChild.lChild;
				rChild_r = rChild.rChild;
				parent_p = parent_node.parent;
				
				if(rChild_l != null) {
					rChild_l.parent = parent_node;
				}
				if(rChild_r != null) {
					rChild_r.parent = parent_node;
				}
			
				if (parent_p != null) {
					if (parent_p.lChild == parent_node) {
						parent_p.lChild = rChild;
					} else {
						parent_p.rChild = rChild;
					}
				}
				lChild.parent = rChild;
				
				rChild.parent = parent_node.parent;
				rChild.rChild = parent_node;
				rChild.lChild = parent_node.lChild;
				
				if (parent_p == null) {
					this.root = rChild;
				}
				
				parent_node.parent = rChild;
				parent_node.lChild = rChild_l;
				parent_node.rChild = rChild_r;
				
				return 2;
			}
		}
		return 0;
	}
	

	private void minheapify_after_insert(int parent_node_index, MinHeapNode parent_node) {
		int is_over = -1; // 0 over, 1 not over
		
		while(parent_node_index >= 0 && parent_node != null) {
			is_over = minheapify(parent_node_index, parent_node);
			
			if (is_over == 0) {
				break;
			}else {
				parent_node_index = (int) Math.floor(parent_node_index / 2);
				if (parent_node_index == 0) {
					break;
				}
				parent_node = get_node(this.root, find_node_path(parent_node_index));
			}
		}
	}
	

	private void minheapify_from_top(int max_index, MinHeapNode parent_node, MinHeapNode root) {
		int is_over = -1;
		int index = 1;
		
		while(index <= max_index) {
			is_over = minheapify(index, parent_node);
			
			if (is_over == 0) {
				break;
			}else {
				if(is_over == 1) {
					index = index * 2;
				}
				
				if(is_over == 2) {
					index = index * 2 + 1;
				}
				parent_node = get_node(this.root, find_node_path(index));
			}
		}
	}


    /* *********************************************************************************************************
     * Implement the following public methods. You should erase the throwings of UnimplementedMethodExceptions.*
     ***********************************************************************************************************/

	/**
	 * Default constructor.
	 */
	public LinkedMinHeap() {
		this.root = null;
		this.size = 0;
	}

	/**
	 * Second constructor initializes {@code this} with the provided element.
	 *
	 * @param rootElement the data to create the root with.
	 */
	public LinkedMinHeap(T rootElement) {
		this.root = new MinHeapNode();
		this.root.data = rootElement;
		this.root.lChild = null;
		this.root.rChild = null;
		this.root.parent = null;
		this.size = 1;
	}

	/**
	 * Copy constructor initializes {@code this} as a carbon
	 * copy of the parameter, which is of the general type {@link MinHeap}!
	 * Since {@link MinHeap} is an {@link Iterable} type, we can access all
	 * of its elements in proper order and insert them into {@code this}.
	 *
	 * @param other The {@link MinHeap} to copy the elements from.
	 */
	public LinkedMinHeap(MinHeap<T> other) {
		LinkedMinHeap<T> temp_h = (LinkedMinHeap<T>) other;
		
		if (temp_h.root == null) {
			this.root = null;
			this.size = 0;
		}else {
			this.root = cpy_construct(this.root, temp_h.root);
			this.size = temp_h.size;
		}
	}


    /**
     * Standard {@code equals} method. We provide this for you. DO NOT EDIT!
     * You should notice how the existence of an {@link Iterator} for {@link MinHeap}
     * allows us to access the elements of the argument reference. This should give you ideas
     * for {@link #LinkedMinHeap(MinHeap)}.
     * @return {@code true} If the parameter {@code Object} and the current MinHeap
     * are identical Objects.
     *
     * @see Object#equals(Object)
     * @see #LinkedMinHeap(MinHeap)
     */
	/**
	 * Standard equals() method.
	 *
	 * @return {@code true} If the parameter Object and the current MinHeap
	 * are identical Objects.
	 */
	public void print_heap(MinHeapNode c) {
		if(c != null) {
			System.out.print(c.data);
		print_heap(c.lChild);
		print_heap(c.rChild);
		}
		System.out.println();
		
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof MinHeap))
			return false;
		Iterator itThis = iterator();
		Iterator itOther = ((MinHeap) other).iterator();
		while (itThis.hasNext())
			if (!itThis.next().equals(itOther.next()))
				return false;
		return !itOther.hasNext();
	}

	@Override
	public boolean isEmpty() {
		return this.root == null;
	}

	@Override
	public int size() {
		return this.size;
	}

	@Override
	public void insert(T element) {
		count_mod++;
		
		MinHeapNode insert_node = new MinHeapNode();
		insert_node.data = element;
		insert_node.lChild = null;
		insert_node.rChild = null;
		
		if (this.root == null){
			insert_node.parent = null;
			this.root = insert_node;
			this.size++;
		}else {
			MinHeapNode insert_node_parent;
			ArrayList<Integer> binary;
			int l_or_r_child;
			
			this.size++;
			binary = find_node_path(this.size);
			
			l_or_r_child = binary.get(binary.size() - 1);
			binary.remove(binary.size() - 1);
			
			if (binary.size() == 0) {
				insert_node_parent = this.root;
			}else {
				insert_node_parent = get_node(this.root, binary);
			}
			
			if(l_or_r_child == 0) {
				insert_node_parent.lChild = insert_node;
			}else {
				insert_node_parent.rChild = insert_node;
			}
			insert_node.parent = insert_node_parent;
			
			minheapify_after_insert((int)Math.floor(this.size / 2), insert_node_parent);
		}
	}

	@Override
	public T getMin() throws EmptyHeapException {		// DO *NOT* ERASE THE "THROWS" DECLARATION!
		if (isEmpty()) {
			throw new EmptyHeapException("No Heap, thus no min");
		}else {
			return this.root.data;
		}
	}
	
	@Override
	public T deleteMin() throws EmptyHeapException { // DO *NOT* ERASE THE "THROWS" DECLARATION!
		if (this.size == 0) {
			throw new EmptyHeapException("Empty Heap");
		} else {
			count_mod++;

			T delete_data = this.root.data;

			if (this.size == 1) {
				this.root = null;
				this.size--;
			} else {
				MinHeapNode last_node = get_node(root, find_node_path(this.size));

				T last_index_data = last_node.data;

				if (this.size % 2 == 0) {
					last_node.parent.lChild = null;
				} else {
					last_node.parent.rChild = null;
				}
				last_node.parent = null;

				this.root.data = last_index_data;
				this.size--;

				minheapify_from_top(this.size, this.root, this.root);
			}
			return delete_data;
		}
	}

	@Override
	public Iterator<T> iterator() {
		 return new LinkedMinHeapIterator(this);
	}
	
	private class LinkedMinHeapIterator implements Iterator<T>{
		private LinkedMinHeap<T> heap;
		private int heap_mod = count_mod;
		
		public LinkedMinHeapIterator(LinkedMinHeap<T> lmh) {
			this.heap = lmh;
		}

		@Override
		public boolean hasNext() {
			return this.heap.size() > 0;
		}

		@Override
		public T next() throws ConcurrentModificationException {
			if (heap_mod != count_mod) {
				throw new ConcurrentModificationException();
			} else {
				if(this.heap.size == 0) {
					throw new NoSuchElementException();
				}
				T val = this.heap.root.data;

				if (this.heap.size == 1) {
					this.heap = new LinkedMinHeap<>();

					return val;
				} else {
					MinHeapNode last_node = get_node(this.heap.root, find_node_path(this.heap.size));

					T last_index_data = last_node.data;

					if (this.heap.size % 2 == 0) {
						last_node.parent.lChild = null;
					} else {
						last_node.parent.rChild = null;
					}
					last_node.parent = null;

					this.heap.root.data = last_index_data;
					this.heap.size--;

					minheapify_from_top(this.heap.size, this.heap.root, this.heap.root);
					return val;
				}
			}
		}
	}
}
