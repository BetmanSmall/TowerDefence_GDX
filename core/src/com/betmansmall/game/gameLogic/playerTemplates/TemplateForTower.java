package com.betmansmall.game.gameLogic.playerTemplates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.XmlReader;

import java.util.StringTokenizer;

/**
 * Created by betmansmall on 22.02.2016.
 */
public class TemplateForTower {
    private Faction faction;
    private String templateName;

    public String   factionName;
    public String   name;
    public Integer  radiusDetection;
    public Float    radiusFlyShell;
    public Integer  damage;
    public Integer  size;
    public Integer  cost;
    public Float    ammoSize;
    public Float    ammoSpeed;
    public Float    reloadTime;
    public TowerAttackType towerAttackType;
    public ShellAttackType shellAttackType;
    public ShellEffectType shellEffectType;
    public Integer capacity;
//    public Integer ammoDistance;

    public TiledMapTile idleTile;
    public ObjectMap<String, TiledMapTile> ammunitionPictures;

    public TemplateForTower(FileHandle templateFile) throws Exception {
        try {
            XmlReader xmlReader = new XmlReader();
            XmlReader.Element templateORtileset = xmlReader.parse(templateFile);
            this.templateName = templateORtileset.getAttribute("name");

            int firstgid = templateORtileset.getIntAttribute("firstgid", 0);
            int tilewidth = templateORtileset.getIntAttribute("tilewidth", 0);
            int tileheight = templateORtileset.getIntAttribute("tileheight", 0);
            int spacing = templateORtileset.getIntAttribute("spacing", 0);
            int margin = templateORtileset.getIntAttribute("margin", 0);

            XmlReader.Element properties = templateORtileset.getChildByName("properties");
            if (properties != null) {
                for (XmlReader.Element property : properties.getChildrenByName("property")) {
                    String key = property.getAttribute("name");
                    String value = property.getAttribute("value");
                    if (key.equals("factionName")) {
                        this.factionName = value;
                    } else if (key.equals("name")) {
                        this.name = value;
                    } else if (key.equals("radiusDetection")) {
                        this.radiusDetection = Integer.parseInt(value);
                    } else if (key.equals("radiusFlyShell")) {
                        this.radiusFlyShell = Float.parseFloat(value);
                    } else if (key.equals("damage")) {
                        this.damage = Integer.parseInt(value);
                    } else if (key.equals("size")) {
                        this.size = Integer.parseInt(value);
                    } else if (key.equals("cost")) {
                        this.cost = Integer.parseInt(value);
                    } else if (key.equals("ammoSize")) {
                        this.ammoSize = Float.parseFloat(value);
                    } else if (key.equals("ammoSpeed")) {
                        this.ammoSpeed = Float.parseFloat(value);
                    } else if (key.equals("reloadTime")) {
                        this.reloadTime = Float.parseFloat(value);
                    } else if (key.equals("towerAttackType")) {
                        this.towerAttackType = TowerAttackType.getType(value);
                    } else if (key.equals("shellAttackType")) {
                        this.shellAttackType = ShellAttackType.getType(value);
                    } else if (key.equals("shellEffectType")) {
                        ShellEffectType.ShellEffectEnum shellEffectEnum = ShellEffectType.ShellEffectEnum.getType(value);
                        this.shellEffectType = new ShellEffectType(shellEffectEnum);
                    } else if (key.equals("shellEffectType_time")) {
                        if(shellEffectType != null) {
                            shellEffectType.time = Float.parseFloat(value);
                        }
                    } else if (key.equals("shellEffectType_damage")) {
                        if(shellEffectType != null) {
                            shellEffectType.damage = Float.parseFloat(value);
                        }
                    } else if (key.equals("shellEffectType_speed")) {
                        if(shellEffectType != null) {
                            shellEffectType.speed = Float.parseFloat(value);
                        }
                    }
                }
            }
            XmlReader.Element imageElement = templateORtileset.getChildByName("image");
            String source = imageElement.getAttribute("source");
            FileHandle textureFile = getRelativeFileHandle(templateFile, source);
            Texture texture = new Texture(Gdx.files.internal(textureFile.path()));

            int stopWidth = texture.getWidth() - tilewidth;
            int stopHeight = texture.getHeight() - tileheight;
            int id = firstgid;

            ObjectMap<Integer, TiledMapTile> tiles = new ObjectMap<Integer, TiledMapTile>();
            for (int y = margin; y <= stopHeight; y += tileheight + spacing) {
                for (int x = margin; x <= stopWidth; x += tilewidth + spacing) {
                    TextureRegion tileRegion = new TextureRegion(texture, x, y, tilewidth, tileheight);
                    TiledMapTile tile = new StaticTiledMapTile(tileRegion);
                    tile.setId(id);
                    tiles.put(id++, tile);
                }
            }

            Array<XmlReader.Element> tileElements = templateORtileset.getChildrenByName("tile");

            ammunitionPictures = new ObjectMap<String, TiledMapTile>();
            Array<AnimatedTiledMapTile> animatedTiles = new Array<AnimatedTiledMapTile>();

            for (XmlReader.Element tileElement : tileElements) {
                int localtid = tileElement.getIntAttribute("id", 0);
                TiledMapTile tile = tiles.get(localtid);
                if (tile != null) {
                    XmlReader.Element propertiesElement = tileElement.getChildByName("properties");
                    if (propertiesElement != null) {
                        for (XmlReader.Element property : propertiesElement.getChildrenByName("property")) {
                            String name = property.getAttribute("name", null);
                            String value = property.getAttribute("value", null);
                            if (value == null) {
                                value = property.getText();
                            }
                            tile.getProperties().put(name, value);
                        }
                    }

                    XmlReader.Element animationElement = tileElement.getChildByName("animation");
                    if (animationElement != null) {
                        Array<StaticTiledMapTile> staticTiles = new Array<StaticTiledMapTile>();
                        IntArray intervals = new IntArray();
                        for (XmlReader.Element frameElement : animationElement.getChildrenByName("frame")) {
                            staticTiles.add((StaticTiledMapTile) tiles.get(frameElement.getIntAttribute("tileid")));
                            intervals.add(frameElement.getIntAttribute("duration"));
                        }

                        AnimatedTiledMapTile animatedTile = new AnimatedTiledMapTile(intervals, staticTiles);
                        animatedTile.setId(tile.getId());
                        animatedTile.getProperties().putAll(tile.getProperties());
                        animatedTiles.add(animatedTile);
                        tile = animatedTile;
                    }

                    String tileName = tile.getProperties().get("tileName", String.class);
                    if (tileName != null) {
                        if(tileName.equals("idleTile")) {
                            idleTile = tile;
                        } else if(tileName.contains("ammo_")) {
                            setAmmoTiles(tileName, tile);
                        }
                    }
                }
            }

            validate();
        } catch (Exception exp) {
            Gdx.app.log("TemplateForTower::TemplateForTower()", " -- Could not load TemplateForTower from " + templateFile.path() + " Exp:" + exp);
            throw new Exception("TemplateForTower::TemplateForTower() -- Could not load TemplateForTower from " + templateFile.path() + " Exp:" + exp);
        }
    }

//    public TemplateForTower(TiledMapTileSet tileSet) {
//        try {
//            this.factionName =  tileSet.getProperties().get("factionName", String.class);
//            this.name =         tileSet.getProperties().get("name", String.class);
//            this.radiusDetection =       Integer.parseInt(tileSet.getProperties().get("radiusDetection", String.class));
//            this.damage =       Integer.parseInt(tileSet.getProperties().get("damage", String.class));
//            this.size =         Integer.parseInt(tileSet.getProperties().get("size", String.class));
//            this.cost =         Integer.parseInt(tileSet.getProperties().get("cost", String.class));
//            this.ammoSize =     Float.parseFloat(tileSet.getProperties().get("ammoSize", String.class));
//            this.ammoSpeed =    Float.parseFloat(tileSet.getProperties().get("ammoSpeed", String.class));
//            this.reloadTime =   Float.parseFloat(tileSet.getProperties().get("reloadTime", String.class));
////            this.type =         tileSet.getProperties().get("type", String.class);
//            this.capacity =     Integer.parseInt(tileSet.getProperties().get("capacity", String.class));
////            this.ammoDistance = Integer.parseInt(tileSet.getProperties().get("ammoDistance", String.class));
//        } catch(Exception exp) {
//            Gdx.app.error("TemplateForTower::TemplateForTower()", " -- Exp: " + exp + " Cheak the file!");
//        }
//
//        this.ammunitionPictures = new ObjectMap<String, TiledMapTile>();
//
//        setTiledMapTiles(tileSet);
//        validate();
//    }

    private void setTiledMapTiles(TiledMapTileSet tileSet) {
        for(TiledMapTile tile: tileSet) {
            String tileName = tile.getProperties().get("tileName", String.class);
            if (tileName != null) {
                if(tileName.equals("idleTile")) {
                    idleTile = tile;
                } else if(tileName.contains("ammo_")) {
                    setAmmoTiles(tileName, tile);
                }
            }
        }
    }

    private void setAmmoTiles(String tileName, TiledMapTile tile) {
        if(tile != null) {
            if(tileName.equals("ammo_" + Direction.UP)) {
                ammunitionPictures.put("ammo_" + Direction.UP, tile);
            } else if(tileName.equals("ammo_" + Direction.UP_RIGHT)) {
                ammunitionPictures.put("ammo_" + Direction.UP_RIGHT, tile);
                ammunitionPictures.put("ammo_" + Direction.UP_LEFT, flipTiledMapTile(tile));
            } else if(tileName.equals("ammo_" + Direction.RIGHT)) {
                ammunitionPictures.put("ammo_" + Direction.RIGHT, tile);
                ammunitionPictures.put("ammo_" + Direction.LEFT, flipTiledMapTile(tile));
            } else if(tileName.equals("ammo_" + Direction.DOWN_RIGHT)) {
                ammunitionPictures.put("ammo_" + Direction.DOWN_RIGHT, tile);
                ammunitionPictures.put("ammo_" + Direction.DOWN_LEFT, flipTiledMapTile(tile));
            } else if(tileName.equals("ammo_" + Direction.DOWN)) {
                ammunitionPictures.put("ammo_" + Direction.DOWN, tile);
            }
        }
    }

    private TiledMapTile flipTiledMapTile(TiledMapTile tiledMapTile) {
        TextureRegion textureRegion = new TextureRegion(tiledMapTile.getTextureRegion());
        textureRegion.flip(true, false);
        return new StaticTiledMapTile(textureRegion);
    }

    private void validate() {
        // Need cheak range values
        if(this.factionName == null)
            Gdx.app.error("TemplateForTower::validate()", "-- Can't get 'factionName'! Check the file");
        else if(this.name == null)
            Gdx.app.error("TemplateForTower::validate()", "-- Can't get 'name'! Check the file");
        else if(this.radiusDetection == null && this.towerAttackType != TowerAttackType.Pit)
            Gdx.app.error("TemplateForTower::validate()", "-- Can't get 'radiusDetection'! Check the file");
        else if(this.radiusFlyShell == null)
            this.radiusFlyShell = 0f;
        else if(this.damage == null)
            Gdx.app.error("TemplateForTower::validate()", "-- Can't get 'damage'! Check the file");
        else if(this.size == null)
            Gdx.app.error("TemplateForTower::validate()", "-- Can't get 'size'! Check the file");
        else if(this.cost == null)
            Gdx.app.error("TemplateForTower::validate()", "-- Can't get 'cost'! Check the file");
        else if(this.ammoSize == null)
            Gdx.app.error("TemplateForTower::validate()", "-- Can't get 'ammoSize'! Check the file");
        else if(this.ammoSpeed == null)
            Gdx.app.error("TemplateForTower::validate()", "-- Can't get 'ammoSpeed'! Check the file");
        else if(this.reloadTime == null)
            Gdx.app.error("TemplateForTower::validate()", "-- Can't get 'reloadTime'! Check the file");
        else if(this.towerAttackType == null)
            Gdx.app.error("TemplateForTower::validate()", "-- Can't get 'towerAttackType'! Check the file");
        else if(this.shellAttackType == null && this.towerAttackType != TowerAttackType.Pit)
            Gdx.app.error("TemplateForTower::validate()", "-- Can't get 'shellAttackType'! Check the file");
//        else if(this.shellEffectType == null)
//            Gdx.app.error("TemplateForTower::validate()", "-- Can't get 'shellEffectEnum'! Check the file");
        else if(this.towerAttackType == TowerAttackType.Pit && this.capacity == null)
            Gdx.app.error("TemplateForTower::validate()", "-- Can't get 'capacity'! When towerAttackType==Pit");

        if(idleTile == null)
            Gdx.app.error("TemplateForTower::validate()", "-- Can't get 'idleTile'! Check the file");
        else if(ammunitionPictures.size == 0)
            Gdx.app.error("TemplateForTower::validate()", "-- Can't get 'ammo'! Check the file");
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TemplateForTower[");
        sb.append("factionName:" + factionName + ",");
        sb.append("name:" + name + ",");
        sb.append("radiusDetection:" + radiusDetection + ",");
        sb.append("radiusFlyShell:" + radiusFlyShell + ",");
        sb.append("damage:" + damage + ",");
        sb.append("size:" + size + ",");
        sb.append("cost:" + cost + ",");
        sb.append("ammoSize:" + ammoSize + ",");
        sb.append("ammoSpeed:" + ammoSpeed + ",");
        sb.append("reloadTime:" + reloadTime + ",");
        sb.append("towerAttackType:" + towerAttackType + ",");
        sb.append("shellAttackType:" + shellAttackType + ",");
        sb.append("shellEffectEnum:" + shellEffectType + ",");
        sb.append("capacity:" + capacity + ",");
        sb.append("]");
        return sb.toString();
    }

    public void setFaction(Faction faction) {
        this.faction = faction;
    }
    public String getFactionName() {
        return factionName;
    }

    protected static FileHandle getRelativeFileHandle(FileHandle file, String path) {
        StringTokenizer tokenizer = new StringTokenizer(path, "\\/");
        FileHandle result = file.parent();
        while (tokenizer.hasMoreElements()) {
            String token = tokenizer.nextToken();
            if (token.equals(".."))
                result = result.parent();
            else {
                result = result.child(token);
            }
        }
        return result;
    }
}
