package com.stucom.planets.model;

import com.google.gson.annotations.SerializedName;

public class Planet {
    @SerializedName("Id")
    private int id;
    @SerializedName("Name")
    private String name;
    @SerializedName("Image")
    private String image;
    @SerializedName("Dwarf")
    private int dwarf;      // API gives 1 or 0, not boolean.

    public Planet(int id, String name, String image, int dwarf) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.dwarf = dwarf;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public boolean isDwarf() { return (dwarf == 1); }
    public void setDwarf(boolean dwarf) { this.dwarf = dwarf ? 1 : 0; }
}
