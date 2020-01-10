import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;
import java.io.Serializable;
import java.util.List;

public class Pokemon {
    private int HP;
    private String name,ele,status,frontImage,backImage;
    private List<Ability> abilityList;
    private boolean alive;

    public Pokemon(String name){
        switch(name){
            case "Pikachu":
                pikachu();
                this.name = name;
                break;

            case "Charmander":
                charmander();
                break;
        }
    }

    private void pikachu(){
        this.HP = 80;
        this.ele = "Electric";
        this.alive = true;
    }

    private void charmander(){
        HP = 90;
        ele = "Fire";
        abilityList.add(new Ability("Slap"));
        alive = true;
    }

    public int getHP() {
        return HP;
    }

    public String getName() {
        return name;
    }

    public String getEle() {
        return ele;
    }

    public List<Ability> getAbilityList() {
        return abilityList;
    }

    public boolean isAlive() {
        return alive;
    }

    public static String toJson(Pokemon pokemon){
        Gson gson = new Gson();
        String json = gson.toJson(pokemon);
        return json;
    }

    public static Pokemon fromJson(String json){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Pokemon gsonPokemon = gson.fromJson(json, Pokemon.class);
        return gsonPokemon;
    }

}
