import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;


/**
 * Represents a binary search tree that stores unique random integers. Provides methods for tree
 * construction, node insertion, and AVL tree rotations.
 */
public class Tree {

    private Node root;
    private Set<Integer> allValues;
    private boolean needsUpdate = true;

    /**
     * Constructs a Tree with the specified size. Initializes the tree with unique random integers.
     *
     * @param size The number of nodes to include in the tree.
     */
    public Tree(int size) {
        allValues = new HashSet<>();
        root = makeNewNode();
        root.setId(0);
        makeTree(size);
    }

    /**
     * Builds the tree by adding new nodes until the desired size is reached.
     *
     * @param size The desired number of nodes in the tree.
     */
    private void makeTree(int size) {
        while (allValues.size() < size) {
            Node newNode = makeNewNode();
            insertNode(root, newNode);
        }
        needsUpdate = true;
        updateNodes();
    }

    public Node addRandomNode() {
        Node newNode = makeNewNode();
        insertNode(root, newNode);
        needsUpdate = true;
        updateNodes();
        return newNode;
    }

    /**
     * Updates the AVL properties of each node in the tree. This method should be called after any
     * tree modification to ensure the AVL values are accurate.
     */
    public void updateNodes() {
        if (needsUpdate) {
            LevelIterator iterator = new LevelIterator(root);
            Deque<Node> reverseQueue = new ArrayDeque<Node>();
            root.setId(0);
            while (iterator.hasNext()) { // traverse the tree in level order, updating the ID of each node as we go.
                Node node = iterator.next();
                if (node.getLeft() != null) {
                    node.getLeft().setId(node.getId() * 2 + 1);
                }
                if (node.getRight() != null) {
                    node.getRight().setId(node.getId() * 2 + 2);
                }
                reverseQueue.push(node); // add the node to the reverseQueue so we can iterate them in reverse order.
            }
            System.out.println(reverseQueue.peek().toString());
            while (!reverseQueue.isEmpty()) {
                Node node = reverseQueue.pop();
                node.updateAVLProperties();
                updateNodeColor(node);
            }
            needsUpdate = false;
        }
    }

    private void updateNodeColor(Node node) {
        double avlValue = node.getAvlValue();
        double maxValue = allValues.size() / 2;
        if (avlValue == 1 || avlValue == -1) {
            node.updateColor(0);
        }
        else {
            node.updateColor(avlValue / maxValue);
        }
    }

    /**
     * Inserts a node into the tree following binary search tree rules.
     *
     * @param localRoot The current root where the node is being inserted.
     * @param node      The new node to insert into the tree.
     */
    private void insertNode(Node localRoot, Node node) {
        if (node.getData() > localRoot.getData()) {
            if (localRoot.getRight() == null) {
                localRoot.setRight(node);
                node.setParent(localRoot);
                return;
            }
            insertNode(localRoot.getRight(), node);
        } else if (node.getData() < localRoot.getData()) {
            if (localRoot.getLeft() == null) {
                localRoot.setLeft(node);
                node.setParent(localRoot);
                return;
            }
            insertNode(localRoot.getLeft(), node);
        }
    }

    /**
     * Creates a new node with a unique random integer value.
     *
     * @return A new Node with a unique random value.
     */
    private Node makeNewNode() {
        Random rand = new Random();
        int val = rand.nextInt(100);
        while (allValues.contains(val)) { // cycle until we find a unique value
            val = rand.nextInt(100);
        }
        allValues.add(val);
        return new Node(val);
    }

    /**
     * Performs a left rotation on a given pivot node.
     *
     * @param pivot The pivot node to rotate.
     * @return The new root of the subtree after rotation.
     */
    public Node rotateLeft(Node pivot) {
        if (pivot.getRight() == null) {
            return null;
        }
        Node x = pivot.getRight();
        pivot.setRight(x.getLeft());
        if (pivot.getRight() != null) {
            pivot.getRight().setParent(pivot);
        }
        if (pivot.getParent() == null) {
            root = x;
            x.setParent(null);
        } else {
            if (pivot.getParent().getLeft() == pivot) {
                pivot.getParent().setLeft(x);
            } else {
                pivot.getParent().setRight(x);
            }
            x.setParent(pivot.getParent());
        }
        x.setLeft(pivot);
        pivot.setParent(x);
        this.needsUpdate = true;
        return x;
    }

    /**
     * Performs a right rotation on a given pivot node.
     *
     * @param pivot The pivot node to rotate.
     * @return The new root of the subtree after rotation.
     */
    public Node rotateRight(Node pivot) {
        if (pivot.getLeft() == null) {
            return null;
        }
        Node x = pivot.getLeft();
        pivot.setLeft(x.getRight());
        if (pivot.getLeft() != null) {
            pivot.getLeft().setParent(pivot);
        }
        if (pivot.getParent() == null) {
            root = x;
            x.setParent(null);
        } else {
            if (pivot.getParent().getLeft() == pivot) {
                pivot.getParent().setLeft(x);
            } else {
                pivot.getParent().setRight(x);
            }
            x.setParent(pivot.getParent());
        }
        x.setRight(pivot);
        pivot.setParent(x);
        this.needsUpdate = true;
        return x;
    }

    /**
     * Updates the ID of each node in the tree. Call this method only on the root node as it will update all of the children.
     * @param localRoot
     */
    public void updateId(Node localRoot) {
        if (localRoot == null) return;
        if (localRoot.getParent() == null) {
            localRoot.setId(0);
        } else {
            if (localRoot.getParent().getLeft() == localRoot) {
                localRoot.setId(localRoot.getParent().getId() * 2 + 1);
            } else {
                localRoot.setId(localRoot.getParent().getId() * 2 + 2);
            }
        }
        updateId(localRoot.getLeft());
        updateId(localRoot.getRight());
    }

    /**
     * Returns a set containing all values stored in the tree.
     *
     * @return A set of all unique integers in the tree.
     */
    public Set<Integer> getAllValues() {
        return allValues;
    }

    /**
     * Returns the root node of the tree.
     *
     * @return The root node of the tree.
     */
    public Node getRoot() {
        return root;
    }

    @Override 
    public String toString() {
        return toStringHelper(root);
    }
    private String toStringHelper(Node node) {
        if (node == null) {
            return "null ";
        }

        return 
        "" + "[" + node.getData() + "-" + node.getId() + " " +
        "P: " + (node.getParent() == null ? "null " : node.getParent().getData() + "-" + node.getParent().getId() + " ") +
        "L: " + toStringHelper(node.getLeft()) +
        "R: " + toStringHelper(node.getRight()) + "]";
    }
}