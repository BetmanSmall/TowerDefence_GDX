package com.betmansmall.maps;

import com.badlogic.gdx.maps.MapProperties;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Arrays;

@XmlRootElement
public class Tileset {
    private String version;
    private String tiledversion;
    private String name;
    private String tilewidth;
    private String tileheight;
    private String spacing;
    private String margin;
    private String tilecount;
    private String columns;

    private Image image;
    private Terraintypes terraintypes;
    private Tile[] tile;

    public MapProperties getProperties() {
        MapProperties mapProperties = new MapProperties();
        mapProperties.put("version", version);
        mapProperties.put("tiledversion", tiledversion);
        mapProperties.put("name", name);
        mapProperties.put("tilewidth", tilewidth);
        mapProperties.put("tileheight", tileheight);
        mapProperties.put("spacing", spacing);
        mapProperties.put("margin", margin);
        mapProperties.put("tilecount", tilecount);
        mapProperties.put("columns", columns);
        return mapProperties;
    }

    @XmlAttribute
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    @XmlAttribute
    public String getTiledversion() {
        return tiledversion;
    }
    public void setTiledversion(String tiledversion) {
        this.tiledversion = tiledversion;
    }
    @XmlAttribute
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    @XmlAttribute
    public String getTilewidth() {
        return tilewidth;
    }
    public void setTilewidth(String tilewidth) {
        this.tilewidth = tilewidth;
    }
    @XmlAttribute
    public String getTileheight() {
        return tileheight;
    }
    public void setTileheight(String tileheight) {
        this.tileheight = tileheight;
    }
    @XmlAttribute
    public String getSpacing() {
        return spacing;
    }
    public void setSpacing(String spacing) {
        this.spacing = spacing;
    }
    @XmlAttribute
    public String getMargin() {
        return margin;
    }
    public void setMargin(String margin) {
        this.margin = margin;
    }
    @XmlAttribute
    public String getTilecount() {
        return tilecount;
    }
    public void setTilecount(String tilecount) {
        this.tilecount = tilecount;
    }
    @XmlAttribute
    public String getColumns() {
        return columns;
    }
    public void setColumns(String columns) {
        this.columns = columns;
    }
    public Image getImage() {
        return image;
    }
    public void setImage(Image image) {
        this.image = image;
    }
    public Terraintypes getTerraintypes() {
        return terraintypes;
    }
    public void setTerraintypes(Terraintypes terraintypes) {
        this.terraintypes = terraintypes;
    }
    public Tile[] getTile() {
        return tile;
    }
    public void setTile(Tile[] tile) {
        this.tile = tile;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Tileset{");
        sb.append("version='").append(version).append('\'');
        sb.append(", tiledversion='").append(tiledversion).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", tilewidth='").append(tilewidth).append('\'');
        sb.append(", tileheight='").append(tileheight).append('\'');
        sb.append(", spacing='").append(spacing).append('\'');
        sb.append(", margin='").append(margin).append('\'');
        sb.append(", tilecount='").append(tilecount).append('\'');
        sb.append(", columns='").append(columns).append('\'');
        sb.append(", image=").append(image);
        sb.append(", terraintypes=").append(terraintypes);
        sb.append(", tile=").append(Arrays.toString(tile));
        sb.append('}');
        return sb.toString();
    }
}
