import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

class TextBox {
    int x, y, w, h;
    String label;

    public TextBox(int x, int y, int h, String label){
        this.x = x;
        this.y = y;

        this.h = h;
        this.label = label;
    }

    public void draw(Group root){

        int th = 3;

        int tx = 0; int ty = 0;
        Text text = new Text(tx, ty, label);
        text.setFont(Font.font("Courier New",18));
        text.setFill(Color.BLACK);
        int tw = (int) text.getLayoutBounds().getWidth();

        int w = tw + 30;
        Rectangle rect = new Rectangle(x, y, w, h);
        rect.setFill(Color.DARKGREY);
        Rectangle rect2 = new Rectangle(x+th, y+th, w-(2*th), h-(2*th));
        rect2.setFill(Color.LIGHTGRAY);

        tx = x+(w/2)-(tw/2);
        ty = y+(h/2)+4;
        text.setX(tx);
        text.setY(ty);

        root.getChildren().addAll(rect, rect2, text);
    }



}