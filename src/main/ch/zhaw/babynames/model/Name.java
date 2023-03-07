package ch.zhaw.babynames.model;

public class Name {
    private String name;
    private String geschlecht;
    private int anzahl;

    public Name(String name, String geschlecht, int anzahl) {
        this.name = name;
        this.geschlecht = geschlecht;
        this.anzahl = anzahl;
    }

    public int getAnzahl() {
        return anzahl;
    }

    public String getName() {
        return name;
    }

    public String getGeschlecht() {
        return geschlecht;
    }

    @Override
    public String toString() {
        return "Name [name=" + name + ", geschlecht=" + geschlecht + ", anzahl=" + anzahl + "]";
    }
}
