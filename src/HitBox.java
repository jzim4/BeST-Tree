import edu.macalester.graphics.Ellipse;

public class HitBox extends Ellipse{
    
    private Node node;

    public HitBox(Node node){
        super(0, 0, Node.DIAMETER + 8, Node.DIAMETER + 8);
        this.node=node;
        this.setStroked(false);
        this.setCenter(Node.DIAMETER/2, Node.DIAMETER/2);
    }

    public Node getNode() {
        return node;
    }

}
