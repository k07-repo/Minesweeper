package net.k07.minesweeper;

import java.io.*;

public class Options implements Serializable {

    public int rows;
    public int columns;
    public int mines;
    public String filename = "options";

    public Options() {
    }

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

            System.out.println(temp.rows + " " + temp.columns + " " + temp.mines);
            System.out.println(this.rows + " " + this.columns + " " + this.mines);

            this.rows = temp.rows;
            this.columns = temp.columns;
            this.mines = temp.mines;

            System.out.println(temp.rows + " " + temp.columns + " " + temp.mines);
            System.out.println(this.rows + " " + this.columns + " " + this.mines);
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
