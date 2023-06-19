package phonebook.hashes;

import java.util.ArrayList;
import java.util.Iterator;

import phonebook.exceptions.UnimplementedMethodException;
import phonebook.utils.KVPair;
import phonebook.utils.KVPairList;
import phonebook.utils.PrimeGenerator;
import phonebook.utils.Probes;

/**<p>{@link SeparateChainingHashTable} is a {@link HashTable} that implements <b>Separate Chaining</b>
 * as its collision resolution strategy, i.e the collision chains are implemented as actual
 * Linked Lists. These Linked Lists are <b>not assumed ordered</b>. It is the easiest and most &quot; natural &quot; way to
 * implement a hash table and is useful for estimating hash function quality. In practice, it would
 * <b>not</b> be the best way to implement a hash table, because of the wasted space for the heads of the lists.
 * Open Addressing methods, like those implemented in {@link LinearProbingHashTable} and {@link QuadraticProbingHashTable}
 * are more desirable in practice, since they use the original space of the table for the collision chains themselves.</p>
 *
 * @author Yuchen Liu
 * @see HashTable
 * @see SeparateChainingHashTable
 * @see LinearProbingHashTable
 * @see OrderedLinearProbingHashTable
 * @see CollisionResolver
 */
public class SeparateChainingHashTable implements HashTable{

    /* ****************************************************************** */
    /* ***** PRIVATE FIELDS / METHODS PROVIDED TO YOU: DO NOT EDIT! ***** */
    /* ****************************************************************** */

    private KVPairList[] table;
    private int count;
    private PrimeGenerator primeGenerator;

    // We mask the top bit of the default hashCode() to filter away negative values.
    // Have to copy over the implementation from OpenAddressingHashTable; no biggie.
    private int hash(String key){
        return (key.hashCode() & 0x7fffffff) % table.length;
    }

    /* **************************************** */
    /*  IMPLEMENT THE FOLLOWING PUBLIC METHODS:  */
    /* **************************************** */
    /**
     *  Default constructor. Initializes the internal storage with a size equal to the default of {@link PrimeGenerator}.
     */
    public SeparateChainingHashTable(){
        count = 0;
        primeGenerator = new PrimeGenerator();
        table = new KVPairList[primeGenerator.getCurrPrime()];
        for(int i = 0; i < table.length; i++) {
        	table[i] = new KVPairList();
        }
    }

    @Override
    public Probes put(String key, String value) {
    	int k_val =	this.hash(key);
    	table[k_val].addBack(key, value);
    	count++;
    	Probes put_p = new Probes(value, 1);
    	return put_p;
    }

    @Override
    public Probes get(String key) {
        return table[this.hash(key)].getValue(key);
    }

    @Override
    public Probes remove(String key) {
    	Probes remove_p = table[this.hash(key)].removeByKey(key);
    	if(remove_p.getValue() != null) {
    		count--;
    	}
        return remove_p;
    }

    @Override
    public boolean containsKey(String key) {
        return table[this.hash(key)].containsKey(key);
    }

    @Override
    public boolean containsValue(String value) {
    	return table[this.hash(value)].containsValue(value);
    }

    @Override
    public int size() {
        return this.count;
    }

    @Override
    public int capacity() {
        return table.length; // Or the value of the current prime.
    }

    /**
     * Enlarges this hash table. At the very minimum, this method should increase the <b>capacity</b> of the hash table and ensure
     * that the new size is prime. The class {@link PrimeGenerator} implements the enlargement heuristic that
     * we have talked about in class and can be used as a black box if you wish.
     * @see PrimeGenerator#getNextPrime()
     */
    public void enlarge() {
    	ArrayList<KVPair> old_table = new ArrayList<>();
    	for(int i = 0; i < this.capacity(); i++) {
    		Iterator<KVPair> iter = this.table[i].iterator();
    		while(iter.hasNext()) {
    			old_table.add(iter.next());
    		}
    	}
    	
    	KVPairList[] new_table = new KVPairList[this.primeGenerator.getNextPrime()];
    	for(int j = 0; j < this.primeGenerator.getCurrPrime(); j++) {
    		new_table[j] = new KVPairList();
    	}
    	this.table = new_table;
    	this.count= 0;
    	
    	for(int j = 0; j < old_table.size(); j++) {
    		this.put(old_table.get(j).getKey(), old_table.get(j).getValue());
    	}
    	
    }

    /**
     * Shrinks this hash table. At the very minimum, this method should decrease the size of the hash table and ensure
     * that the new size is prime. The class {@link PrimeGenerator} implements the shrinking heuristic that
     * we have talked about in class and can be used as a black box if you wish.
     *
     * @see PrimeGenerator#getPreviousPrime()
     */
    public void shrink(){
    	ArrayList<KVPair> temp = new ArrayList<>();
    	for(int i = 0; i < this.capacity(); i++) {
    		Iterator<KVPair> iter = this.table[i].iterator();
    		while(iter.hasNext()) {
    			temp.add(iter.next());
    		}
    	}
    	
    	KVPairList[] new_table = new KVPairList[this.primeGenerator.getPreviousPrime()];
    	for(int j = 0; j < this.primeGenerator.getCurrPrime(); j++) {
    		new_table[j] = new KVPairList();
    	}
    	this.table = new_table;
    	this.count= 0;
    	
    	
    	for(int j = 0; j < temp.size(); j++) {
    		this.put(temp.get(j).getKey(), temp.get(j).getValue());
    	}
    }
}
