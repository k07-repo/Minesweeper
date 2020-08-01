package net.k07.minesweeper;

import java.io.*;

/**
 * Serializable class for saving the options to a file so that they persist upon closing and reopening the game.
 */
public class Options implements Serializable {

    public int rows;
    public int columns;
    public int mines;
    public int lifelines;
    public boolean soundEnabled;
    public boolean colorEnabled;


    private String filename = "options";

    public void saveToFile() throws IOException {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));
            out.writeObject(this);
            out.close();
        }
        catch(IOException e) {
            //could not save to file!
        }
    }

    public boolean loadFromFile() {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
            Options temp = (Options)in.readObject();
            in.close();

            this.rows = temp.rows;
            this.columns = temp.columns;
            this.mines = temp.mines;
            this.lifelines = temp.lifelines;
            this.soundEnabled = temp.soundEnabled;
            this.colorEnabled = temp.colorEnabled;
            return true;
        }
        catch(IOException e) {
            //could not read file!
        }
        catch(ClassNotFoundException e) {
            //also shouldn't happen lol
        }
        catch(ClassCastException e) {
            //also shouldn't happen lol
        }

        return false;
    }
}
