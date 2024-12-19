import java.awt.Color;
import edu.macalester.graphics.*;

/**
 * Represents a node in a binary tree that stores integers. This class extends {@code GraphicsGroup}
 * for easy canvas interaction but does not rely on its hashcode as its position can change
 * dynamically. Each node maintains references to its left child, right child, parent, and several
 * properties like its data, height from the leaf, AVL value, and a unique ID.
 */
public class Node extends GraphicsGroup implements Comparable<Node> {
    public static final int DIAMETER = 40;

    private Node left;
    private Node right;
    private Node parent;
    private final int data;
    private int heightFromLeaf;
    private int avlValue;
    private int id;

    private Ellipse bubble;

    private HitBox hitbox;
    /**
     * Constructs a new Node with the specified integer value. Initializes the node with default child
     * and parent references, sets the height from the leaf, and prepares graphical representation.
     * 
     * @param value The integer value to store in the node.
     */
    public Node(int value) {
        this.left = null;
        this.right = null;
        this.parent = null;
        this.data = value;
        this.heightFromLeaf = 1;
        this.bubble = new Ellipse(0, 0, DIAMETER, DIAMETER);
        this.id = -1;
        initializeGraphics();
        updateAVLProperties();
    }

    /**
     * Compares this node with another node based on their integer values.
     * 
     * @param o The node to compare with.
     * @return A negative integer, zero, or a positive integer as this node's value is less than, equal
     *         to, or greater than the specified node's value.
     */
    @Override
    public int compareTo(Node o) {
        return this.data - o.getData();
    }

    /**
     * Finds the position of the node based on its unique ID according to top-down order for graphical
     * placement.
     * 
     * @param id The unique ID of the node based on top-down order
     * @return The Point representing the position of the node.
     */
    private Point findPos(int id) {

        // find depth of node by rounding down the log of the id
        double y = Math.floor(log2(id + 1)) + 1.0;
        // find position from left-most possible node in row, then divide by number of total possible nodes
        double x = (id + 2.0 - Math.pow(2, y - 1)) / (Math.pow(2, y - 1) + 1) + 0.01;
        // scale depth and x-position according to canvas width and height
        return new Point(x, y).scale(getCanvas().getWidth() * 0.98, getCanvas().getHeight() / 10);
    }

    /**
     * Calculates the base-2 logarithm of an integer.
     * 
     * @param N The integer value.
     * @return The base-2 logarithm of N.
     */
    private static double log2(int N) {
        return (Math.log(N) / Math.log(2));
    }

    /**
     * Animates the node's movement to its new position.
     * 
     * @param dTime The time since the last frame in seconds.
     */
    public void animate(double dTime) {
        Point targetPos = findPos(this.id);

        // adjust the target position to avoid overlap with neighbors
        if (this.id >= 31 && this.getCenter().distance(targetPos) < 80 * DIAMETER) {
            Node leftNeighbor = findLeftNeighbor();
            Node rightNeighbor = findRightNeighbor();

            if (leftNeighbor != null && rightNeighbor != null) { // node has both neighbors
                // average the x position of the neighbors
                double newX = leftNeighbor.getCenter().getX() + ((rightNeighbor.getCenter().getX() - leftNeighbor.getCenter().getX()) / 2);
                targetPos = targetPos.withX(newX);

            } else if (leftNeighbor != null) { // node has a left neighbor
                double newX = Math.max(leftNeighbor.getCenter().getX() + 48, DIAMETER / 2 + 2);
                targetPos = targetPos.withX(Math.max(newX, targetPos.getX()));
            } else if (rightNeighbor != null) { // node has a right neighbor
                double newX = Math.min(rightNeighbor.getCenter().getX() - 48, this.getCanvas().getWidth() - DIAMETER / 2 - 2);
                targetPos = targetPos.withX(Math.min(newX, targetPos.getX()));
            }
        }
        this.setCenter(Point.interpolate(this.getCenter(), targetPos, 0.05));
    }

    /**
     * Finds the left neighbor of the node on the canvas based on its position.
     * Returns null if no neighbor is found, or if this node is not drawn on a canvas.
     * @return
     */
    private Node findLeftNeighbor() {
        CanvasWindow canvas = this.getCanvas();
        if (canvas == null) {
            return null;
        }
        Point[] testPoints = new Point[] {
            this.getCenter().add(new Point(-28, 0)),
            this.getCenter().add(new Point(-28, 0).rotate(7 * Math.PI / 4)),
            this.getCenter().add(new Point(-28, 0).rotate(Math.PI / 4)),
        };
        for (Point testPoint : testPoints) {
            GraphicsObject hitLeft = canvas.getElementAt(testPoint);
            if (hitLeft instanceof HitBox) {
                Node leftNeighbor = ((HitBox) hitLeft).getNode();
                if (leftNeighbor.getData() < this.getData()) {
                    return leftNeighbor;
                } 
            }
        }
        return null;
    }

    /**
     * Finds the right neighbor of the node on the canvas based on its position.
     * Returns null if no neighbor is found, or if this node is not drawn on a canvas.
     * @return
     */
    private Node findRightNeighbor() {
        CanvasWindow canvas = this.getCanvas();
        if (canvas == null) {
            return null;
        }
        Point[] testPoints = new Point[] {
            this.getCenter().add(new Point(28, 0)),
            this.getCenter().add(new Point(28, 0).rotate(7 * Math.PI / 4)),
            this.getCenter().add(new Point(28, 0).rotate(Math.PI / 4))
        };

        for (Point testPoint : testPoints) {
            GraphicsObject hitRight = canvas.getElementAt(testPoint);
            if (hitRight instanceof HitBox) {
                Node rightNeighbor = ((HitBox) hitRight).getNode();
                if (rightNeighbor.getData() > this.getData()) {
                    return rightNeighbor;
                }
            }
        }
        return null;
    }

    /**
     * styles the bubble to show that it has been selected
     */
    public void selectNode() {
        this.bubble.setStrokeWidth(6);
    }

    /**
     * styles the bubble to show that it is not selected
     */
    public void unselectNode() {
        this.bubble.setStrokeWidth(1);
    }

    /**
     * 
     * @param balance value between 1 and -1, where -1 is strong left and 1 is strong right
     */
    public void updateColor(double balance) {
        float purpleHue = 263f / 360;
        float yellowHue = 55f / 360;

        balance = Math.clamp(balance, -1, 1);

        if (balance > 0) {
            bubble.setFillColor(Color.getHSBColor(purpleHue, (float) balance, 1f));
        } else if (balance < 0) {
            bubble.setFillColor(Color.getHSBColor(yellowHue, (float) balance * -1, 1f));
        } else {
            bubble.setFillColor(Color.white);
        }
    }


    /**
     * Returns a string representation of the node and its children, using preorder traversal.
     */
    @Override
    public String toString() {
        return "Data: " + Integer.toString(data) + "ID: " + Integer.toString(id);
    }

    public void setHeightFromLeaf(int height) {
        this.heightFromLeaf = height;
    }

    public int getHeightFromLeaf() {
        return this.heightFromLeaf;
    }

    /**
     * Updates the AVL value, height from leaf, using the properties of its children. This method should
     * be called on children before the parent.
     */
    public void updateAVLProperties() {
        int leftHeight = 0;
        int rightHeight = 0;
        if (this.left != null) {
            leftHeight = left.getHeightFromLeaf();
        }
        if (this.right != null) {
            rightHeight = right.getHeightFromLeaf();
        }
        // Height of a node is 1 + max height of its children
        int height = 1 + Math.max(leftHeight, rightHeight);
        int avlValue = rightHeight - leftHeight;
        this.heightFromLeaf = height;
        this.avlValue = avlValue;
    }

    public void setAVLValue(int value) {
        this.avlValue = value;
    }

    /**
     * Assigns a unique ID to this node and updates its graphical position.
     * 
     * @param id The unique ID to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the unique ID of this node.
     * 
     * @return The node's unique ID.
     */
    public int getId() {
        return id;
    }

    public int getAvlValue() {
        return avlValue;
    }

    /**
     * Returns the left child of this node.
     * 
     * @return The left child node.
     */
    public Node getLeft() {
        return left;
    }

    /**
     * Sets the left child of this node.
     * 
     * @param newLeft The node to set as the left child.
     */
    public void setLeft(Node newLeft) {
        this.left = newLeft;
    }

    /**
     * Returns the right child of this node.
     * 
     * @return The right child node.
     */
    public Node getRight() {
        return right;
    }

    /**
     * Sets the right child of this node.
     * 
     * @param newRight The node to set as the right child.
     */
    public void setRight(Node newRight) {
        this.right = newRight;
    }

    public Node getParent() {
        return parent;
    }
    public void setParent(Node newParent) {
        this.parent = newParent;
    }

    /**
     * Returns the integer data stored in this node.
     * 
     * @return The data stored in the node.
     */
    public int getData() {
        return data;
    }

    // Called once by the constructor.
    /**
     * Initializes the graphical representation of the node with a circle and its data label.
     */
    private void initializeGraphics() {
        bubble.setFillColor(null);
        bubble.setStrokeColor(Color.BLACK);
        this.add(bubble);
        GraphicsText text = new GraphicsText(Integer.toString(data), DIAMETER / 2, DIAMETER / 2 + 4);
        text.setAlignment(TextAlignment.CENTER);
        this.add(text);
        hitbox = new HitBox(this);
        this.add(hitbox);
    }
}
