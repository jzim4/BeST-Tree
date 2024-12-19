import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A test class that looks into different cases in which our app should be tested.
 * A lot of the testing for the program was also done visually but this is to
 * accompany that.
 */
public class TreeTest {
    private Tree tree;
    
    @BeforeEach
    public void setUp() {
        tree = new Tree(10); //create a new tree before each test
    }
    
    @Test 
    public void testTreeSize() {
        assertEquals(10, tree.getAllValues().size());
    }

    @Test
    public void testTreeNotEmpty() {
        assertNotNull(tree.getRoot());
    }

    @Test
    /** Make sure all children nodes follow the BST property */
    public void checkBST() {
        Node root = tree.getRoot();
        checkBSTHelper(root);
    }

    @Test
    public void testRotationMaintainsBSTProperty() {
        Node root = tree.getRoot();
        
        //perform a left rotation and check BST property
        if (root.getRight() != null) {
            tree.rotateLeft(root);
            checkBST();
        }

        //get new root and perform a right rotation and check BST property
        root = tree.getRoot();
        if (root.getLeft() != null) {
            tree.rotateRight(root);
            checkBST();
        }
    }

    @Test
    public void testAddRandomNodeMaintainsBSTProperty() {
        checkBST();
        //add a random node to the existing tree and check BST property
        tree.addRandomNode();
        checkBST();
    }

    @Test
    public void testNodeProperties() {
        Node root = tree.getRoot();
        //test root node properties
        assertEquals(0, root.getId());
        assertNull(root.getParent());
        assertTrue(root.getHeightFromLeaf() >= 1);
    }

    @Test
    public void testUniqueValues() {
        //make sure all values in the tree are unique
        assertEquals(tree.getAllValues().size(), 
            tree.getAllValues().stream().distinct().count());
    }

    @Test
    public void testValueRange() {
        //test that all values are within the expected range (0-99)
        assertTrue(tree.getAllValues().stream()
            .allMatch(value -> value >= 0 && value < 100));
    }

    @Test
    public void testRotationConnections() {
        Node root = tree.getRoot();
        if (root.getRight() != null) {
            Node oldRoot = root;
            Node newRoot = tree.rotateLeft(root);
            
            //test parent-child relationships after rotation
            assertNull(newRoot.getParent());
            assertEquals(oldRoot.getParent(), newRoot);
            assertEquals(tree.getRoot(), newRoot);
        }
    }

    @Test
    public void testAVLProperties() {
        tree.updateNodes();
        traverseAndCheckAVL(tree.getRoot());
    }

    private void traverseAndCheckAVL(Node node) {
        if (node == null) return;
        
        //check if AVL value is correctly calculated
        int leftHeight = (node.getLeft() == null) ? 0 : node.getLeft().getHeightFromLeaf();
        int rightHeight = (node.getRight() == null) ? 0 : node.getRight().getHeightFromLeaf();
        assertEquals(rightHeight - leftHeight, node.getAvlValue());
        
        traverseAndCheckAVL(node.getLeft());
        traverseAndCheckAVL(node.getRight());
    }

    private void checkBSTHelper(Node node) {
        if (node.getLeft() != null) {
            assertTrue(0 < node.compareTo(node.getLeft()));
            checkBSTHelper(node.getLeft());
        }
        if (node.getRight() != null) {
            assertTrue(0 > node.compareTo(node.getRight()));
            checkBSTHelper(node.getRight());
        }
    }
}
