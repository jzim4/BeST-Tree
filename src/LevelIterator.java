import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

public class LevelIterator implements Iterator<Node> {
    private Queue<Node> queue;
    private Node current;

    /**
     * An iterator that traverses the tree in level order.
     * @param root
     */
    public LevelIterator(Node root) {
        queue = new ArrayDeque<Node>();
        queue.add(root);
    }

    @Override
    public boolean hasNext() {
        return !queue.isEmpty();
    }

    @Override
    public Node next() {
        current = queue.poll();
        // add the children of the current node to the queue so they can be visited later.
        if (current.getLeft() != null) {
            queue.add(current.getLeft());
        }
        if (current.getRight() != null) {
            queue.add(current.getRight());
        }
        return current;
    }

}