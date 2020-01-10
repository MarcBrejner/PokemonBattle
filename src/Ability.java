public class Ability{
    private boolean onEnemy, onOwn;
    private int cost;
    private String name, ele;

    public Ability(String name){
        this.name = name;
        switch(name){
            case "Bolt":
                bolt();
                break;

            case "Recover":
                recover();
                break;

            case "Slap":
                slap();
                break;
        }
    }

    public void bolt(){
        onEnemy = true;
        onOwn = false;
        cost = 2;
        ele = "Electric";
    }

    public void slap(){
        onEnemy = true;
        onOwn = false;
        cost = 1;
        ele = "Normal";
    }

    public void recover(){
        onEnemy = false;
        onOwn = true;
        cost = 3;
        ele = "Normal";

    }

}
