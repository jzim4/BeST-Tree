import java.util.function.Consumer;

import edu.macalester.graphics.GraphicsGroup;
import edu.macalester.graphics.ui.Button;

/**
 * Represents the selected node with buttons that allow the user to rotate the selected node left or
 * right. This class extends GraphicsGroup, so make sure to add it to the canvas, but do not change
 * its position.
 */
public class Pivot extends GraphicsGroup {
    private Node selectedNode;
    private Button left;
    private Button right;

    /**
     * Constructs a new Pivot object with left and right buttons that are initially hidden.
     */
    public Pivot() {
        left = new Button("Rotate Left");
        right = new Button("Rotate Right");
        left.setPosition(-50, -50);
        right.setPosition(-50, -50);
        add(left);
        add(right);
    }

    /**
     * Sets the functions to be called when the left and right buttons are clicked. Call this method
     * whenever the tree is recreated.
     * 
     * @param rotateLeft  The function to rotate the selected node left.
     * @param rotateRight The function to rotate the selected node right.
     */
    public void setRotateMethods(Consumer<Node> rotateLeft, Consumer<Node> rotateRight) {
        left.onClick(() -> {
            if (selectedNode != null) {
                rotateLeft.accept(selectedNode);
            }
            clearSelectedNode();
        });
        right.onClick(() -> {
            if (selectedNode != null) {
                rotateRight.accept(selectedNode);
            }
            clearSelectedNode();
        });
    }


    public void setSelectedNode(Node node) {
        if (selectedNode != null) { // unselect the previous node
            selectedNode.unselectNode();
            hideRotateButtons();
        }
        selectedNode = node;
        selectedNode.selectNode();
        showRotateButtons();
    }

    public Node getSelectedNode() {
        return selectedNode;
    }

    public void clearSelectedNode() {
        hideRotateButtons();
        if (selectedNode != null) {
            selectedNode.unselectNode();
            selectedNode = null;
        }
    }

    /**
     * Moves the left and right buttons to appear around the selected node, Used after a node is
     * selected.
     */
    private void showRotateButtons() {
        if (selectedNode.getLeft() != null) {
            right.setPosition(selectedNode.getX() + Node.DIAMETER, selectedNode.getY());
        }
        if (selectedNode.getRight() != null) {
            left.setPosition(selectedNode.getX() - 110, selectedNode.getY());
        }
    }

    private void hideRotateButtons() {
        left.setPosition(-50, -50);
        right.setPosition(-50, -50);
    }
}
