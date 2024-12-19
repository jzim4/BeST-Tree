import edu.macalester.graphics.CanvasWindow;
import edu.macalester.graphics.GraphicsGroup;
import edu.macalester.graphics.GraphicsObject;
import edu.macalester.graphics.GraphicsText;
import edu.macalester.graphics.Line;
import edu.macalester.graphics.Point;
import edu.macalester.graphics.Rectangle;
import edu.macalester.graphics.ui.Button;

/**
 * Visualizes a binary search tree (BST) on a graphical canvas. The tree structure is drawn
 * interactively, with nodes represented as graphical objects, and parent-child relationships
 * displayed as lines.
 */
public class Visualizer {
    public static final int CANVAS_WIDTH = 1400;
    public static final int CANVAS_HEIGHT = 800;

    private final int initialTreeSize;

    private Tree tree;
    private CanvasWindow canvas;
    private Button resetButton;
    private Button newNodeButton;
    private Pivot pivot;
    private GraphicsGroup treeGroup;
    private GraphicsText explanation; 
    private GraphicsGroup lineGroup;
    public Visualizer(int size) {
        this.initialTreeSize = size;
        canvas = new CanvasWindow("The BeST Tree", CANVAS_WIDTH, CANVAS_HEIGHT);
        tree = new Tree(initialTreeSize);
        pivot = new Pivot();
        pivot.setRotateMethods(tree::rotateLeft, tree::rotateRight);
        treeGroup = new GraphicsGroup();
        explanation = new GraphicsText("The more yellow the node is, the more negative the AVL value is. \nThe more purple the node is, the more positive the AVL value is. \nIf the node is white, its AVL value is 1, -1, or 0");
        lineGroup = new GraphicsGroup();
        drawGraphics();
        setUpNodeClickListener();

        newTreeButton();
        newNodeButton();
        canvas.add(lineGroup);
        canvas.add(treeGroup);
        canvas.add(pivot);
        canvas.add(explanation);
        explanation.setCenter(1100,50);
        canvas.animate(this::update);
    }

    /**
     * Updates the canvas, recalculating the avl values of the nodes, 
     * animating the node positions, redrawing the lines between nodes,
     * and checking if the tree is balanced
     * 
     * @param dTime the time since the last update
     */
    private void update(double dTime) {
        lineGroup.removeAll();
        tree.updateNodes();
        LevelIterator iterator = new LevelIterator(tree.getRoot());
        boolean balanced = true; // assume the tree is balanced until proven otherwise
        while (iterator.hasNext()) {
            Node node = iterator.next();
            drawLine(node);
            node.animate(dTime);
            if (Math.abs(node.getAvlValue()) > 1) {
                balanced = false;
                newNodeButton.setPosition(-50, -50);
            }
        }
        if (balanced) {
            showWinMessage();
        }
    }

    private void showWinMessage() {
        GraphicsObject winRect = new Rectangle(0, 0, 300, 50);
        GraphicsText winText = new GraphicsText("The tree is balanced");
        winText.setCenter(90,15);
        newNodeButton.setPosition(150, 20);
        lineGroup.add(winRect);
        lineGroup.add(winText);
    }

    /**
     * draws a line between the node and its parent
     * @param node
     */
    private void drawLine(Node node) {
        if (node != null && node.getParent() != null) {
            Point pos1 = node.getCenter();
            Point pos2 = node.getParent().getCenter();

            GraphicsObject line = new Line(pos1, pos2);
            lineGroup.add(line);
        }
    }

    /**
     * Removes and redraws all nodes on the canvas
     */
    private void drawGraphics() {
        pivot.clearSelectedNode();
        treeGroup.removeAll();
        LevelIterator iterator = new LevelIterator(tree.getRoot());
        iterator.forEachRemaining(node -> {
            treeGroup.add(node, canvas.getWidth() / 2, -50);
        });
    }

    /**
     * Event listener on canvas to identify when nodes have been clicked
     */
    private void setUpNodeClickListener() {
        System.out.println("setting up node click listeners");
        canvas.onClick(e -> {
            GraphicsObject clicked = canvas.getElementAt(e.getPosition());
            if (clicked instanceof HitBox) {
                System.out.println("clicked node");
                HitBox hit = (HitBox) clicked;
                pivot.setSelectedNode(hit.getNode());
                System.out.println(pivot.getSelectedNode().toString());
            } else {
                pivot.clearSelectedNode();
            }
        });
    }

    /**
     * Adds the button to make a new tree to the canvas
     */
    public void newTreeButton() {
        resetButton = new Button("Make new tree");
        canvas.add(resetButton);
        resetButton.setPosition(20, 20);
        resetButton.onClick(() -> {
            tree = new Tree(initialTreeSize);
            drawGraphics();
            pivot.setRotateMethods(tree::rotateLeft, tree::rotateRight);
        });
    }

    public void newNodeButton() {
        newNodeButton = new Button("Add new node");
        canvas.add(newNodeButton);
        newNodeButton.setPosition(-50, -50);
        newNodeButton.onClick(() -> {
            Node newNode = tree.addRandomNode();
            treeGroup.add(newNode, canvas.getWidth() / 2, -50 );
            newNodeButton.setPosition(-50, -50);
        });
    }

    public static void main(String[] args) {
        Visualizer v = new Visualizer(15);
    }
}