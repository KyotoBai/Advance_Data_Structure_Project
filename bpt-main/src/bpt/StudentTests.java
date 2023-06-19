package bpt;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Iterator;

/**
 * A jUnit test suite for {@link BinaryPatriciaTrie}.
 *
 * @author --- YOUR NAME HERE! ----.
 */
public class StudentTests {


    @Test public void testEmptyTrie() {
        BinaryPatriciaTrie trie = new BinaryPatriciaTrie();

        assertTrue("Trie should be empty",trie.isEmpty());
        assertEquals("Trie size should be 0", 0, trie.getSize());

        assertFalse("No string inserted so search should fail", trie.search("0101"));

    }

    @Test public void testFewInsertionsWithSearch() {
        BinaryPatriciaTrie trie = new BinaryPatriciaTrie();

        trie.insert("001");
        trie.insert("00100");
        trie.insert("11");
        trie.insert("1101");
        trie.insert("1111000");
        trie.insert("111100110001");
        
        //System.out.println(trie.getLongest());
    }
    
    
    	
    @Test public void testInsertionsWithSearch() {
        BinaryPatriciaTrie trie = new BinaryPatriciaTrie();

        assertTrue("String should be inserted successfully",trie.insert("00000"));
        assertTrue("String should be inserted successfully",trie.insert("00011"));
        assertFalse("Search should fail as string does not exist",trie.search("000"));

    }


    //testing isEmpty function
    @Test public void testFewInsertionsWithDeletion() {
        BinaryPatriciaTrie trie = new BinaryPatriciaTrie();

        trie.insert("000");
        trie.insert("001");
        trie.insert("011");
        trie.insert("1001");
        trie.insert("1");

        assertFalse("After inserting five strings, the trie should not be considered empty!", trie.isEmpty());
        assertEquals("After inserting five strings, the trie should report five strings stored.", 5, trie.getSize());

        trie.delete("0"); // Failed deletion; should affect exactly nothing.
        assertEquals("After inserting five strings and requesting the deletion of one not in the trie, the trie " +
                "should report five strings stored.", 5, trie.getSize());
        assertTrue("After inserting five strings and requesting the deletion of one not in the trie, the trie had some junk in it!",
                trie.isJunkFree());

        trie.delete("011"); // Successful deletion
        assertEquals("After inserting five strings and deleting one of them, the trie should report 4 strings.", 4, trie.getSize());
        assertTrue("After inserting five strings and deleting one of them, the trie had some junk in it!",
                trie.isJunkFree());
    }
    
    @Test public void testIter() {
    	BinaryPatriciaTrie trie = new BinaryPatriciaTrie();
    	
    	trie.insert("0");
    	trie.insert("1");
    	trie.insert("00");
    	trie.insert("111");
    	trie.insert("1011");
    	trie.insert("0001");
    	trie.insert("10101");
    	trie.insert("000");
    	trie.insert("001");
    	trie.insert("11");
    	
    	Iterator<String> iter = trie.inorderTraversal();
    	
    	
    	while(iter.hasNext()) {
    		System.out.println(iter.next());
    	}
    }
    
}