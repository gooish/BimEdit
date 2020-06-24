/*
 *
 * Iivari Mansikka
 *
 * Laki 2 -harjoitustyö
 *
 * iivari.mansikka@tuni.fi
 *
 * 2019
 *
 */

import java.io.*;
import java.util.Scanner;
import java.io.PrintWriter;
/* Tämä ohjelma käsittelee binäärikuvaa. Se voi tehdä kuvalle morfologisia operaatioita,
 *  muuttaa sen sisältöä, ja kertoa siitä tietoa, sekä myös tulostaa sen */

class BimEdit {
    // asetetaan luokkavakioita
    public static final Scanner input = new Scanner(System.in);
    public static final String ERRARG = "Invalid command-line argument!";
    public static final String ERRIMG = "Invalid image file!";
    public static final String VIRHE = "Invalid command!";
    public static final String TULOSTA = "print";
    public static final String TIEDOT = "info";
    public static final String KAANNA = "invert";
    public static final String LOUHI = "erode";
    public static final String PAISU = "dilate";
    public static final String LATAA = "load";
    public static final String TIEDOSTOON ="fprint";
    public static final String POISTU = "quit";

    //asetetaan vakiot kuvan piirtämiseen
    public static final String YKS = "10 10 10    ";
    public static final String NOL = "0  0  0    ";

    /*
     * Main-ohjelma sisältää tarkistukset syötteelle ja kuvalle, joiden onnistuessa
     * ohjelma jatkaa pääsilmukkaan. Muussa tapauksessa pääsilmukka jätetään välistä
     * ja poistutaan ohjelmasta suoraan, virheilmoituksen kera.
     */
    public static void main(String[] args) {
        System.out.println("-----------------------");
        System.out.println("| Binary image editor |");
        System.out.println("-----------------------");

        // Luodaan muuttujia pää- ja silmukkametodeille
        int syöteTyyppi = 0;
        String tiedostoNimi = null;
        char[] merkit = new char[2];
        int[] pituudet = new int[2];
        boolean echo = false;
        String syote = "";
        boolean mainiin = true;
        char[][] kuvaTaulu = null;

        // tarkistetaan syötteen tyyppi
        if (args.length > 0 && args.length < 3) {
            syöteTyyppi = tarkistaSyöte(args);
            tiedostoNimi = args[0];
        } else {
            System.out.println(ERRARG);
            mainiin = false;
        }

        // tarkistetaan tarkistuksen tulos ja asetetaan lippuja
        if (mainiin) {
            if (syöteTyyppi == 0) {
                System.out.println(ERRARG);
                mainiin = false;
            } else if (syöteTyyppi == 2) {
                echo = true;
                tiedostoNimi = args[1];
            }
        }

        /*
         * koetetaan ladata tiedosto tauluun. jos tämä ei onnistu, otetaan exception
         * kiinni ja tulostetaan virheilmoitus. muussa tapauksessa jatketaan.
         */
        if (mainiin) {
            try {
                kuvaTaulu = lataaKuvaTaulukkoon(tiedostoNimi, merkit, pituudet);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid image file!");
                mainiin = false;
            }
        }

        // ajetaan taulukontarkistusfunktio
        if (mainiin) {
            if (kuvaTaulu == null || tarkistaTaulukko(kuvaTaulu, merkit, pituudet) == false) {
                System.out.println("Invalid image file!");
                mainiin = false;
            }
        }

        // mennään mainiin, jos sitä ei ole kielletty aikaisemmin
        if (mainiin) {
            mainLoop(kuvaTaulu, syote, merkit, pituudet, tiedostoNimi, echo);
        }

        // hyvästellään käyttäjä mainin jälkeen
        System.out.println("Bye, see you soon.");
    }

    /*
     * Pääsilmukka, sisältää switch-case -lauseen ja komentosyötteen käsittelyä.
     * Kaikki alifunktiot antavat null-arvoja tai muuta virheellistä saadessaan ulos
     * IllegalArgumentException -virheen, joka tarkistetaan.
     */
    public static void mainLoop(char[][] kuvaTaulu, String syote, char[] merkit, int[] pituudet, String tiedostoNimi,
            boolean echo) {
        boolean jatketaanko = true;

        while (jatketaanko) {
            // annetaan komennot, ja otetaan syote. syotteen jalkeen echo printtaa sen
            System.out.println("print/info/invert/dilate/erode/load/fprint/quit?");
            if (syote != POISTU) {
                syote = input.nextLine();
            }
            if (echo) {
                System.out.println(syote);
            }

            // tehdaan syotteesta array, ja tarkistetaan etta onko se joko yksimerkkinen tai
            // dilate
            String[] syoteArray = syote.split(" ");
            if (!(syoteArray.length == 1
                    || (syoteArray.length == 2 && (syoteArray[0].equals(PAISU) || syoteArray[0].equals(LOUHI))))) {
                System.out.println(VIRHE);
                continue;
            }

            if (syoteArray.length == 1 && (syoteArray[0].equals(PAISU) || syoteArray[0].equals(LOUHI))) {
                System.out.println(VIRHE);
                continue;
            }

            syote = syoteArray[0];

            // switch - case kaikille komennoille
            try {
                switch (syote) {
                    case TULOSTA:
                        tulosta2d(kuvaTaulu);
                        break;
                    case TIEDOT:
                        taulunTiedot(kuvaTaulu, merkit, pituudet);
                        break;
                    case KAANNA:
                        vaihdaMerkit(kuvaTaulu, merkit[0], merkit[1], merkit);
                        break;
                    case PAISU:
                        kuvaTaulu = dilaatio(kuvaTaulu, merkit, pituudet, syoteArray);
                        break;
                    case LATAA:
                        kuvaTaulu = lataaKuvaTaulukkoon(tiedostoNimi, merkit, pituudet);
                        break;
                    case POISTU:
                        jatketaanko = false;
                        break;
                    case LOUHI:
                        kuvaTaulu = eroosio(kuvaTaulu, merkit, pituudet, syoteArray);
                        break;
                    case TIEDOSTOON:
                        toImage(kuvaTaulu, merkit, pituudet);
                        break;
                    default:
                        System.out.println(VIRHE);
                        break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println(VIRHE);
            }
        }
    }

    // Metodi ensin luo työkalut tiedoston lukuun, sitten lukee tiedoston merkki
    // merkiltä.
    public static char[][] lataaKuvaTaulukkoon(String tiedNimi, char[] merkit, int[] pituudet)
            throws IllegalArgumentException {
        // null-tarkistus
        if (tiedNimi == null || merkit == null || pituudet == null) {
            throw new IllegalArgumentException();
        }

        // koetetaan lukea tiedostoa, virheen ilmetessä palautetaan null ja heitetään
        // exception.
        try {
            if (merkit != null && merkit.length == 2) {
                File tiedosto = new File(tiedNimi);
                Scanner tiedLukija = new Scanner(tiedosto);
                int laskuri = 0;

                // luetaan tiedostosta tarvittavat merkit ja pituudet
                pituudet[0] = Integer.parseInt(tiedLukija.nextLine());
                pituudet[1] = Integer.parseInt(tiedLukija.nextLine());
                merkit[0] = tiedLukija.nextLine().charAt(0);
                merkit[1] = tiedLukija.nextLine().charAt(0);

                // luodaan taulukko, ja täytetään se tiedostoa läpi loopaten
                char[][] paluuTaulu = new char[pituudet[0]][pituudet[1]];
                while (tiedLukija.hasNextLine()) {
                    String rivi = tiedLukija.nextLine();
                    for (int i = 0; i < rivi.length(); i++) {
                        paluuTaulu[laskuri][i] = rivi.charAt(i);
                    }
                    laskuri++;
                }

                // palautetaan taulu, jos kaikki on onnistunut
                tiedLukija.close();
                return paluuTaulu;
            } else {
                return null;
            }
        } catch (FileNotFoundException e) {
            return null;
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    // metodi käy taulukon läpi ja kopioi sen rivi riviltä
    public static char[][] kopioi2dTaulukko(char[][] alkuTaulu) throws IllegalArgumentException {
        char[][] paluuTaulu = new char[alkuTaulu.length][alkuTaulu[0].length];
        for (int x = 0; x < alkuTaulu.length; x++) {
            // kopioidaan silmukassa jokainen taulun osa uuteen tauluun
            for (int y = 0; y < alkuTaulu[x].length; y++) {
                paluuTaulu[x][y] = alkuTaulu[x][y];
            }
        }
        return paluuTaulu;
    }

    // käy taulukon läpi merkki merkiltä ja tulostaa joka merkin
    public static void tulosta2d(char[][] matr) {
        if (matr != null) {
            try {
                for (int x = 0; x < matr.length; x++) {
                    for (int y = 0; y < matr[x].length; y++) {
                        System.out.print(matr[x][y]);
                    }
                    System.out.println("");
                }
            } catch (NullPointerException e) {
            }
        }
    }

    // metodi käy merkit läpi, ja korvaa matchaavat merkit toisella merkillä
    public static boolean vaihdaMerkit(char[][] vaihdatne, char eka, char toka, char[] merkit)
            throws IllegalArgumentException {
        merkit[0] = toka;
        merkit[1] = eka;
        // kaydaan merkit lapi ja katsotaan jos matchaavia merkkeja tulee
        if (vaihdatne != null && vaihdatne.length > 0) {
            for (int x = 0; x < vaihdatne.length; x++) {
                for (int y = 0; y < vaihdatne[x].length; y++) {
                    if (vaihdatne[x][y] == eka) {
                        vaihdatne[x][y] = toka;
                    } else if (vaihdatne[x][y] == toka) {
                        vaihdatne[x][y] = eka;
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /*
     * metodi tulostaa taulukon tietoja, luottaen syötearvojen oikeellisuuteen mutta
     * silti varoen nulleja
     */
    public static void taulunTiedot(char[][] taulu, char[] merkkiTaulu, int[] pituusTaulu)
            throws IllegalArgumentException {
        // printataan vanhasta taulukko - oliosta pituudet, tiedetaan niiden olevan
        // oikein jo tassa kohtaa

        if (taulu == null || merkkiTaulu == null || pituusTaulu == null) {
            throw new IllegalArgumentException();
        }

        System.out.printf("%d x %d%n", pituusTaulu[0], pituusTaulu[1]);
        int[] merkkiMäärät = { 0, 0 };

        // loopataan merkit lapi, ja laskentaan yhteenkayvyydet
        for (int i = 0; i < pituusTaulu[0]; i++) {
            for (int j = 0; j < pituusTaulu[1]; j++) {
                if (taulu[i][j] == merkkiTaulu[0]) {
                    merkkiMäärät[0]++;
                }
                if (taulu[i][j] == merkkiTaulu[1]) {
                    merkkiMäärät[1]++;
                }
            }
        }

        // printataan
        System.out.printf("%s %d%n", merkkiTaulu[0], merkkiMäärät[0]);
        System.out.printf("%s %d%n", merkkiTaulu[1], merkkiMäärät[1]);
    }

    // katsotaan argumenttien muoto. 1 tarkoittaa tavallista, 2 echoa, 0 virhetta.
    public static int tarkistaSyöte(String[] ament) throws IllegalArgumentException {
        if (ament == null) {
            throw new IllegalArgumentException();
        }
        if (ament.length == 1 || ament.length == 2) {
            if (ament[0].endsWith(".txt") && ament.length == 1) {
                return 1;
            } else if (ament[0].equals("echo") && ament.length == 2 && ament[1].endsWith(".txt")) {
                return 2;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    // metodi morfologisesti eroosioi taulukon
    public static char[][] eroosio(char[][] kTaulu, char[] mTaulu, int[] pTaulu, String[] syoteArray)
            throws IllegalArgumentException {
        if (kTaulu == null || mTaulu == null || pTaulu == null || syoteArray == null) {
            throw new IllegalArgumentException();
        }
        int dilMaar = parseDilSyot(syoteArray, pTaulu);
        char[][] minneMenee = kopioi2dTaulukko(kTaulu);
        int dReuna = (dilMaar - 1) / 2;

        // ensimmaiset kaksi looppia kayvat alkuperaisen taulukon lapi, kaksi
        // jalkimmaista
        // toiminnon kokoisen alueen sen umparilta, lahtien miinus puolikkaasta alueesta
        // ja saapuen puolikkaaseen alueeseen

        for (int i = dReuna; i < pTaulu[0] - dReuna; i++) {
            for (int j = dReuna; j < pTaulu[1] - dReuna; j++) {
                for (int x = dReuna * -1; x <= dReuna; x++) {
                    for (int y = dReuna * -1; y <= dReuna; y++) {
                        if (kTaulu[x + i][y + j] == mTaulu[0]) {
                            minneMenee[i][j] = mTaulu[0];
                        }
                    }
                }
            }
        }
        return minneMenee;
    }

    // metodi on kuormitus eroosiometodille, kääntäen sen.
    public static char[][] dilaatio(char[][] kTaulu, char[] mTaulu, int[] pTaulu, String[] syoteArray)
            throws IllegalArgumentException {
        // kuormitus eroosiometodille
        char[] umt = { mTaulu[1], mTaulu[0] };
        return eroosio(kTaulu, umt, pTaulu, syoteArray);
    }

    /*
     * taulukontarkastaja. laskee rivit, sarakkeet, vertaa niita pituuksiin, ja
     * katsoo samalla etta kuvassa on vain hyvaksyttavia merkkeja
     */
    public static boolean tarkistaTaulukko(char[][] kuvaT, char[] merkkiT, int[] numeroT)
            throws IllegalArgumentException {
        if (kuvaT == null || merkkiT == null || numeroT == null) {
            throw new IllegalArgumentException();
        }
        int rivinPituus = merkkiT[1];
        int riviMaara = 0;
        for (int i = 0; i < kuvaT.length; i++) {
            rivinPituus = 0;
            riviMaara++;
            for (int j = 0; j < kuvaT[i].length; j++) {
                rivinPituus++;
                if (!(kuvaT[i][j] == (merkkiT[0]) || kuvaT[i][j] == merkkiT[1])) {
                    return false;
                }
            }
            if (rivinPituus != numeroT[1]) {
                return false;
            }
        }
        if (riviMaara != numeroT[0]) {
            return false;
        }
        return true;
    }

    // metodi käsittelee dilaatio- ja eroosiometodien syötteen
    public static int parseDilSyot(String[] syoteArray, int[] pTaulu) throws IllegalArgumentException {
        if (syoteArray == null || pTaulu == null) {
            throw new IllegalArgumentException();
        }
        int dilMaar = 0;
        try {
            dilMaar = Integer.parseInt(syoteArray[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException();
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException();
        }
        if (!(dilMaar > 2 && dilMaar < pTaulu[0] && dilMaar % 2 == 1)) {
            throw new IllegalArgumentException();
        }
        return dilMaar;
    }


    // tulostetaan kuva yksinkertaiseen ppm-muotoon.
    public static void toImage(char[][] kuvaTaulu, char[] merkkiTaulu, int[] pituusTaulu) throws IllegalArgumentException {
        try {

            // initialisoidaan tiedosto
            System.out.println("Syötä nimi tiedostolle:");
            String tiedostoNimi = "kissa.ppm";
            FileWriter fOut = new FileWriter(tiedostoNimi);

            // kirjoitetaan tiedostoheader
            fOut.append("P3\n");
            fOut.append((pituusTaulu[1]) + " " + (pituusTaulu[0] - 4 ) + "\n");
            fOut.append("10\n");

            /* Käydään läpi 2d array, ja kirjoitetaan joko 
            *  mustia tai valkoisia pikseleitä sen perusteella,
            *  onko kyseessä Etu- vai Takamerkki */

            for (int i = 0; i < kuvaTaulu.length; i++) {
                for (int j = 0; j < kuvaTaulu[i].length; j++) {
                    if (kuvaTaulu[i][j] == merkkiTaulu[0]) {
                        fOut.append(YKS);
                    }
                    else if (kuvaTaulu[i][j] == merkkiTaulu[1]) {
                        fOut.append(NOL);
                        
                    }
                    else if (kuvaTaulu[i][j] == '\n') {
                        fOut.append("\n");
                    }

                }
                
            }
            System.out.print(fOut);
            System.out.printf("Kirjoitettu tiedostoon %s\n", tiedostoNimi);

        } catch (IOException e) {
            System.out.println("Tapahtui virhe");
        }
    }
}
