package phonebook.hashes;

import java.util.ArrayList;

import phonebook.exceptions.UnimplementedMethodException;
import phonebook.utils.KVPair;
import phonebook.utils.PrimeGenerator;
import phonebook.utils.Probes;

/**
 * <p>{@link LinearProbingHashTable} is an Openly Addressed {@link HashTable} implemented with <b>Linear Probing</b> as its
 * collision resolution strategy: every key collision is resolved by moving one address over. It is
 * the most famous collision resolution strategy, praised for its simplicity, theoretical properties
 * and cache locality. It <b>does</b>, however, suffer from the &quot; clustering &quot; problem:
 * collision resolutions tend to cluster collision chains locally, making it hard for new keys to be
 * inserted without collisions. {@link QuadraticProbingHashTable} is a {@link HashTable} that
 * tries to avoid this problem, albeit sacrificing cache locality.</p>
 *
 * @author YOUR NAME HERE!
 *
 * @see HashTable
 * @see SeparateChainingHashTable
 * @see OrderedLinearProbingHashTable
 * @see QuadraticProbingHashTable
 * @see CollisionResolver
 */
public class LinearProbingHashTable extends OpenAddressingHashTable {

    /* ********************************************************************/
    /* ** INSERT ANY PRIVATE METHODS OR FIELDS YOU WANT TO USE HERE: ******/
    /* ********************************************************************/
	private int occupied_count;
    /* ******************************************/
    /*  IMPLEMENT THE FOLLOWING PUBLIC METHODS: */
    /* **************************************** */

    /**
     * Constructor with soft deletion option. Initializes the internal storage with a size equal to the starting value of  {@link PrimeGenerator}.
     *
     * @param soft A boolean indicator of whether we want to use soft deletion or not. {@code true} if and only if
     *             we want soft deletion, {@code false} otherwise.
     */
    public LinearProbingHashTable(boolean soft) {
    	this.softFlag = soft;
    	this.count = 0;
    	this.occupied_count = 0;
    	this.primeGenerator = new PrimeGenerator();
    	this.table = new KVPair[this.primeGenerator.getCurrPrime()];
    }

    /**
     * Inserts the pair &lt;key, value&gt; into this. The container should <b>not</b> allow for {@code null}
     * keys and values, and we <b>will</b> test if you are throwing a {@link IllegalArgumentException} from your code
     * if this method is given {@code null} arguments! It is important that we establish that no {@code null} entries
     * can exist in our database because the semantics of {@link #get(String)} and {@link #remove(String)} are that they
     * return {@code null} if, and only if, their key parameter is {@code null}. This method is expected to run in <em>amortized
     * constant time</em>.
     * <p>
     * Instances of {@link LinearProbingHashTable} will follow the writeup's guidelines about how to internally resize
     * the hash table when the capacity exceeds 50&#37;
     *
     * @param key   The record's key.
     * @param value The record's value.
     * @return The {@link phonebook.utils.Probes} with the value added and the number of probes it makes.
     * @throws IllegalArgumentException if either argument is {@code null}.
     */
    @Override
    public Probes put(String key, String value) {
        if(key == null || value == null) {
        	throw new IllegalArgumentException();
        }
        
        int probe_count = 1;
        
        /*resize if more than 50% of current bucket is full*/
        if(this.occupied_count >= 0.5*this.primeGenerator.getCurrPrime()) {
        	ArrayList<KVPair> old_table = new ArrayList<>();
        	for(int i = 0; i < this.table.length; i++) {
        		if(this.table[i] != null && this.table[i] != TOMBSTONE) {
        			old_table.add(this.table[i]);
        		}
        		probe_count++; //loop every bucket in the old table to find if there is value stored
        	}
        	
        	this.table = new KVPair[this.primeGenerator.getNextPrime()];
        	this.count = 0;
        	this.occupied_count = 0;
        	
        	for(int i = 0; i < old_table.size(); i++) {
        		probe_count += (this.put(old_table.get(i).getKey(), old_table.get(i).getValue())).getProbes();
        	}
        }
        
        int k_val = this.hash(key);
        while(this.table[k_val] != null || this.table[k_val] == TOMBSTONE) {
        	k_val = (k_val + 1) % this.capacity();
        	probe_count++;
        }
        this.table[k_val] = new KVPair(key, value);
        this.count++;
        this.occupied_count++;
        
        Probes return_val = new Probes(value, probe_count);
        
        return return_val;
    }

    @Override
    public Probes get(String key) {
    	int k_val = this.hash(key);
    	int probe_count = 1;
    	
    	while(this.table[k_val] != null) {
    		if(this.table[k_val].getKey().equals(key) && this.table[k_val] != TOMBSTONE) {
    			return new Probes(this.table[k_val].getValue(), probe_count);
    		}
    		probe_count++;
    		k_val = (k_val + 1) % this.capacity();
    	}
    	
    	return new Probes(null, probe_count);
    }


    /**
     * <b>Return</b> the value associated with key in the {@link HashTable}, and <b>remove</b> the {@link phonebook.utils.KVPair} from the table.
     * If key does not exist in the database
     * or if key = {@code null}, this method returns {@code null}. This method is expected to run in <em>amortized constant time</em>.
     *
     * @param key The key to search for.
     * @return The {@link phonebook.utils.Probes} with associated value and the number of probe used. If the key is {@code null}, return value {@code null}
     * and 0 as number of probes; if the key doesn't exist in the database, return {@code null} and the number of probes used.
     */
    @Override
    public Probes remove(String key) {
    	int probe_count = 1;
    	int k_val = this.hash(key);
    	
    	if(this.softFlag == true) {
    		while(this.table[k_val] != null) {
    			if(this.table[k_val].getKey().equals(key) && this.table[k_val] != TOMBSTONE) {
    				String remove_val = this.table[k_val].getValue();
    				this.table[k_val] = TOMBSTONE;
    				this.count--;
    				
    				return new Probes(remove_val, probe_count);
    			}
    			probe_count++;
    			k_val = (k_val + 1) % this.capacity();
    		}
    	}else {
    		while(this.table[k_val] != null) {
    			if(this.table[k_val].getKey().equals(key)) {
    				String remove_val = this.table[k_val].getValue();
    				this.table[k_val] = null;
    				this.count--;
    				this.occupied_count--;
    				
    				probe_count++;  //check one more bucket after find target
    				k_val = (k_val + 1)%this.capacity();
    				
    				ArrayList<KVPair> cluster = new ArrayList<>();
    				if(this.table[k_val] != null) {    //if there is a cluster
    					while(this.table[k_val] != null) {    //loop to find end of cluster
	    					cluster.add(this.table[k_val]);
	    					this.table[k_val] = null;
	    					this.count--;
	    					this.occupied_count--;
	    					k_val = (k_val + 1)%this.capacity();
	    					probe_count++;
    					}
    					
    					for(int i = 0; i < cluster.size(); i++) {//reinsert
        					probe_count += (this.put(cluster.get(i).getKey(), cluster.get(i).getValue())).getProbes();
        				}
    				}
    				
    				return new Probes(remove_val, probe_count);
    			}
    			probe_count++;
    			k_val = (k_val + 1) % this.capacity();
    		}
    	}
		return new Probes(null, probe_count);
    }

    @Override
    public boolean containsKey(String key) {
    	if (this.get(key).getValue() != null) {
    		return true;
    	}
    	return false;
    }

    @Override
    public boolean containsValue(String value) {
        for(int i = 0; i < this.table.length; i++) {
        	if(this.table[i].getValue().equals(value) && this.table[i] != TOMBSTONE) {
        		return true;
        	}
        }
        return false;
    }

    @Override
    public int size() {
        return this.count;
    }

    @Override
    public int capacity() {
        return this.primeGenerator.getCurrPrime();
    }
}
