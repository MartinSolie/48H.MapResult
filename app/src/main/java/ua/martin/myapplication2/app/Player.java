package ua.martin.myapplication2.app;

/**
 * Created by admin on 29.06.2014.
 */
public class Player {
    static private Player player = null;
    public String name;

    public static Player getPlayer(String name){
        if (Player.player == null){
            Player.player = new Player(name);
        }
        return Player.player;
    }

    private Player (){
        name = "NONAME";
    }
    private Player (String n){
        //TODO: login activity
        this.name = n;
        Player.player = this;
    }
}
