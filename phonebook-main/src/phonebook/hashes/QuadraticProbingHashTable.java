package phonebook.hashes;

import java.util.ArrayList;

import phonebook.exceptions.UnimplementedMethodException;
import phonebook.utils.KVPair;
import phonebook.utils.PrimeGenerator;
import phonebook.utils.Probes;

/**
 * <p>{@link QuadraticProbingHashTable} is an Openly Addressed {@link HashTable} which uses <b>Quadratic
 * Probing</b> as its collision resolution strategy. Quadratic Probing differs from <b>Linear</b> Probing
 * in that collisions are resolved by taking &quot; jumps &quot; on the hash table, the length of which
 * determined by an increasing polynomial factor. For example, during a key insertion which generates
 * several collisions, the first collision will be resolved by moving 1^2 + 1 = 2 positions over from
 * the originally hashed address (like Linear Probing), the second one will be resolved by moving
 * 2^2 + 2= 6 positions over from our hashed address, the third one by moving 3^2 + 3 = 12 positions over, etc.
 * </p>
 *
 * <p>By using this collision resolution technique, {@link QuadraticProbingHashTable} aims to get rid of the
 * &quot;key clustering &quot; problem that {@link LinearProbingHashTable} suffers from. Leaving more
 * space in between memory probes allows other keys to be inserted without many collisions. The tradeoff
 * is that, in doing so, {@link QuadraticProbingHashTable} sacrifices <em>cache locality</em>.</p>
 *
 * @author YOUR NAME HERE!
 *
 * @see HashTable
 * @see SeparateChainingHashTable
 * @see OrderedLinearProbingHashTable
 * @see LinearProbingHashTable
 * @see CollisionResolver
 */
public class QuadraticProbingHashTable extends OpenAddressingHashTable {

    /* ********************************************************************/
    /* ** INSERT ANY PRIVATE METHODS OR FIELDS YOU WANT TO USE HERE: ******/
    /* ********************************************************************/
	
	private int occupied_count;
	
    /* ******************************************/
    /*  IMPLEMENT THE FOLLOWING PUBLIC METHODS: */
    /* **************************************** */

    /**
     * Constructor with soft deletion option. Initializes the internal storage with a size equal to the starting value of  {@link PrimeGenerator}.
     * @param soft A boolean indicator of whether we want to use soft deletion or not. {@code true} if and only if
     *               we want soft deletion, {@code false} otherwise.
     */
    public QuadraticProbingHashTable(boolean soft) {
    	this.softFlag = soft;
    	this.count = 0;
    	this.occupied_count = 0;
    	this.primeGenerator = new PrimeGenerator();
    	this.table = new KVPair[this.primeGenerator.getCurrPrime()];
    }

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
        int i = 1;
        while(this.table[k_val] != null) {
        	i += 1;
        	k_val = (this.hash(key) + (i - 1) + (i - 1)*(i - 1)) % this.capacity();
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
    	int i = 1;
    	
    	while(this.table[k_val] != null) {
    		//loop for a cycle and did not find
    		if(k_val == this.hash(key) && i != 1) {
    			return new Probes(null, probe_count);
    		}
    		//did find
    		if(this.table[k_val].getKey().equals(key) && this.table[k_val] != TOMBSTONE) {
    			return new Probes(this.table[k_val].getValue(), probe_count);
    		}
    		i += 1;
    		probe_count++;
    		k_val = (this.hash(key) + (i - 1) + (i - 1)*(i - 1)) % this.capacity();
    	}
    	return new Probes(null, probe_count);
    }

    @Override
    public Probes remove(String key) {
    	int probe_count = 1;
    	int k_val = this.hash(key);
    	int i = 1;
    	
    	if(this.softFlag == true) {
    		while(this.table[k_val] != null) {
    			if(k_val == this.hash(key) && i != 1) {
        			return new Probes(null, probe_count);
        		}
    			
    			if(this.table[k_val].getKey().equals(key) && this.table[k_val] != TOMBSTONE) {
    				String remove_val = this.table[k_val].getValue();
    				this.table[k_val] = TOMBSTONE;
    				this.count--;
    				
    				return new Probes(remove_val, probe_count);
    			}
    			i += 1;
    			probe_count++;
    			k_val = (this.hash(key) + (i - 1) + (i - 1)*(i - 1)) % this.capacity();
    		}
    	}else {
    		while(this.table[k_val] != null) {
    			if(this.table[k_val].getKey().equals(key)) {
    				String remove_val = this.table[k_val].getValue();
    				this.table[k_val] = null;
    				
    				//take out all the value inside of old table
    				ArrayList<KVPair> old_table = new ArrayList<>();
    				for(int j = 0; j < this.capacity(); j++) {
    					if(this.table[j] != null) {
    						old_table.add(this.table[j]);
    					}
    					probe_count++;
    				}
    				
    				//reset table
    				this.table = new KVPair[this.primeGenerator.getCurrPrime()];
    				this.count = 0;
    				this.occupied_count = 0;
    				
    				for(int j = 0; j < old_table.size(); j++) {
    					probe_count += (this.put(old_table.get(j).getKey(), old_table.get(j).getValue())).getProbes();
    				}
    				return new Probes(remove_val, probe_count);
    			}
	    		i += 1;
				probe_count++;
				k_val = (this.hash(key) + (i - 1) + (i - 1)*(i - 1)) % this.capacity();
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
    public int size(){
    	return this.count;
    }

    @Override
    public int capacity() {
    	return this.primeGenerator.getCurrPrime();
    }

}