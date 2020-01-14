import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

class Button {
    int x, y;
    String label,state;
    GraphicsContext gc;
    boolean selected;
    public Button(GraphicsContext gc, int x, int y, String label, String state, boolean selected) {
        this.x = x;
        this.y = y;
        this.label = label;
        this.state = state;
        this.gc = gc;
        this.selected = selected;
    }
    
    public void draw() {
        gc.setFont(Font.font("Courier New",18));
        gc.setFill(Color.BLACK);
        gc.fillText(label, x, y);
        if (selected) {
            gc.fillRect(x-8, y-7, 6, 6);
        }	
    }
}