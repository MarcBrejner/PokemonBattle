import java.util.List;

public class Pokemon {
    public static int HP;
    public static String name,ele;
    public static List<Ability> abilityList;
    public static boolean alive;

    public Pokemon(String name, String ele){
        HP = 80;
        this.name = name;
        this.ele= ele;
        alive = true;
    }

}
