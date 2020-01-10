public class Ability{
    private boolean onEnemy, onOwn;
    private int cost;
    private int damage;
    private String name, ele, appliesStatus;

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

    private void bolt(){
        this.damage = -30;
        this.onEnemy = true;
        this.onOwn = false;
        this.cost = 2;
        this.ele = "Electric";
        this.appliesStatus = "Paralyzed";
    }

    private void slap(){
        this.damage = -30;
        this.onEnemy = true;
        this.onOwn = false;
        this.cost = 1;
        this.ele = "Normal";
        this.appliesStatus = "None";
    }

    private void recover(){
        this.damage = 30;
        this.onEnemy = false;
        this.onOwn = true;
        this.cost = 3;
        this.ele = "Normal";
        this.appliesStatus = "None";
    }

    private void sleep(){
        this.damage = 0;
        this.onEnemy = true;
        this.onOwn = false;
        this.cost = 2;
        this.ele = "Grass";
        this.appliesStatus = "Sleepy";
    }

    public boolean isOnEnemy() {
        return onEnemy;
    }

    public boolean isOnOwn() {
        return onOwn;
    }

    public int getCost() {
        return cost;
    }

    public int getDamage() {
        return damage;
    }

    public String getName() {
        return name;
    }

    public String getEle() {
        return ele;
    }

    public String getAppliesStatus() {
        return appliesStatus;
    }

}
