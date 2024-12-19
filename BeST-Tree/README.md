# The BeST TREE

## Creators
Jonah Zimmer, Royce Johnson, Rūta Rupeikytė

## Inspiration
While learning about trees in COMP 128: Data Structures, we realized that tree rotations are integral to the understanding of binary search trees. We struggled to find interactive visualizations to help our understanding. This is why we have decided to create our own. 

## The Project
Our program randomly creates a binary search tree for the user that is unbalanced. The nodes are colored according to their AVL values: nodes with values greater than 1 are purple, and nodes with avl values less than -1 are yellow. The user can click on nodes to set pivots and then rotate either left or right. When the tree is balanced, they will receive a notification as such, at which point they can either create a new tree or unbalance the same one. 

## Class Logic

### Visualizer Class
This class puts all of the components on the canvas and controls user interaction. It is the main class, as it has the main method that runs the simulation.

### Node Class
This class stores the node's parent and children, data, avl information, and id. In a complete tree, a top down iteration would count the ids in order. Thus, the id of the node indicates where on the canvas the node is positioned.
<img src="/nodeIDScreenshot.png" width=100%>
### HitBox Class
Because Kilt Graphics detects GraphicsObjects, not GraphicsGroups, and Node is a GraphicsGroup, this helper class allows us to identify if the user has clicked on a Node. A HitBox has two components: a transparent ellipse on top of the node and a map from the ellipse to the Node that it covers.

### Tree Class
This class stores all of the Nodes. It handles inserting Nodes as well as the rotate logic.

### Pivot Class
This class handles the selected Node and the buttons that rotate the tree.

### Level Iterator Class
This is a custom iterator that traverses the tree in top-down level order. By using a queue and visiting the parent, then left child, then right child, it is a breadth-first traversal starting at the root.
