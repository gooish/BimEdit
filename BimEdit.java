import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
class BimEdit {
    public static void main(String[] args) {
        System.out.println("-----------------------");
        System.out.println("| Binary image editor |");
        System.out.println("-----------------------");
        lueTiedosto(args[0]);
    }
    public static void lueTiedosto (String nimi) {
        try {
            File tiedosto = new File (nimi);
            Scanner lueTied = new Scanner(tiedosto);
            while (lueTied.hasNextLine()) {
                System.out.println(lueTied);
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("Error saatana");

        }
    }

    public static String[][] tiedostonKäsittelyArray (String[][] raaka, int x, int y) {
        String[][] ulos = new String[x][y];
        for (int hor = 0; hor < x; hor++) {
            for (int vert = 0; vert < y; vert++) {
                ulos[hor][vert] = raaka[hor][vert+4];
            }
        }
        return ulos;
    }
}