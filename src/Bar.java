import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;

class Bar {
    Group root;
    int x;
    int y;
    int width;
    double startContent, content;
    Color color;
    Rectangle innerRect;
    Timeline timeline;
    
    public Bar (Group root, int x, int y, int width, int content, Color color) {
        this.root = root;
        this.x = x;
        this.y = y;
        this.width = width;
        this.content = (double) content;
        this.startContent = this.content;
        this.color = color;
    }
    
    public void changeContent(int newValue) {
        System.out.println("newValue: "+newValue);
        content = (double) newValue;
        System.out.println("content: "+content);
        double widthDouble = (double) width;
        KeyValue wValue  = new KeyValue(innerRect.widthProperty(), widthDouble*(content/startContent)); 
        KeyFrame keyFrame  = new KeyFrame(Duration.millis(500), wValue);
        timeline  = new Timeline(); 
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }
    
    public void draw() {
        int border = 2;
        int height = 4;
        
        Rectangle outerRect = new Rectangle();
        outerRect.setFill(color.DARKGREY);
        outerRect.setX(x-border);
        outerRect.setY(y-border);
        outerRect.setWidth(width+border*2);
        outerRect.setHeight(height+border*2);
        root.getChildren().add(outerRect);
        
        innerRect = new Rectangle();
        innerRect.setFill(color);
        innerRect.setX(x);
        innerRect.setY(y);
        double widthDouble = (double) width;
        innerRect.setWidth(widthDouble*(content/startContent));
        innerRect.setHeight(height);
        root.getChildren().add(innerRect);
    }
}