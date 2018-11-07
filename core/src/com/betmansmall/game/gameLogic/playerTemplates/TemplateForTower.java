package com.betmansmall.game.gameLogic.playerTemplates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.StringBuilder;

import com.betmansmall.game.gameLogic.mapLoader.AnimatedTile;
import com.betmansmall.game.gameLogic.mapLoader.StaticTile;
import com.betmansmall.game.gameLogic.mapLoader.Tile;

/**
 * Created by betmansmall on 22.02.2016.
 */
public class TemplateForTower extends Template {
//    private Faction faction;
    public String   factionName;
    public String   name;
    public Float    radiusDetection;
    public Float    radiusFlyShell;
    public Integer  damage;
    public Integer  size;
    public Integer  cost;
    public Float    ammoSize;
    public Float    ammoSpeed;
    public Float    reloadTime;
//    public String   type;
    public TowerAttackType towerAttackType;
    public ShellAttackType shellAttackType;
    public ShellEffectType shellEffectType;
    public Integer capacity;
    public Tile idleTile;
    public ObjectMap<String, Tile> ammunitionPictures;

    public TemplateForTower(FileHandle templateFile) throws Exception {
        try {
            this.radiusDetection = 0.0f;
//            this.reloadTime = 3000;
            ammunitionPictures = new ObjectMap<String, Tile>();
            loadBasicTemplate(templateFile);
            specificLoad();
            validate();
        } catch (Exception exp) {
            Gdx.app.log("TemplateForTower::TemplateForTower()", "-- Could not load TemplateForTower from " + templateFile.path() + " Exp:" + exp);
            throw new Exception("TemplateForTower::TemplateForTower() -- Could not load TemplateForTower from " + templateFile.path() + " Exp:" + exp);
        }
    }

    void loadFireBall(SimpleTemplate fireBall) {
        if (fireBall != null) {
            for (AnimatedTile animatedTile : fireBall.animatedTiles.values()) {
                String tileName = animatedTile.getProperties().get("tileName", null);
                if (tileName != null) {
                    if(tileName.contains("fireball_")) {
                        setAmmoTiles(tileName.replace("fireball_", "ammo_"), animatedTile);
                    }
                }
            }
        }
    }

    void specificLoad() {
        for (Tile tile : tiles.values()) {
            String tileName = tile.getProperties().get("tileName", null);
            if (tileName != null) {
                if(tileName.equals("idleTile")) {
                    idleTile = tile;
                } else if(tileName.contains("ammo_")) {
                    setAmmoTiles(tileName, tile);
                }
            }
        }
    }

    private void setAmmoTiles(String tileName, Tile tile) {
        if(tile != null) {
            if(tileName.equals("ammo_" + Direction.UP)) {
                ammunitionPictures.put("ammo_" + Direction.UP, tile);
            } else if(tileName.equals("ammo_" + Direction.UP_RIGHT)) {
                ammunitionPictures.put("ammo_" + Direction.UP_RIGHT, tile);
                ammunitionPictures.put("ammo_" + Direction.UP_LEFT, flipTile(tile));
            } else if(tileName.equals("ammo_" + Direction.RIGHT)) {
                ammunitionPictures.put("ammo_" + Direction.RIGHT, tile);
                ammunitionPictures.put("ammo_" + Direction.LEFT, flipTile(tile));
            } else if(tileName.equals("ammo_" + Direction.DOWN_RIGHT)) {
                ammunitionPictures.put("ammo_" + Direction.DOWN_RIGHT, tile);
                ammunitionPictures.put("ammo_" + Direction.DOWN_LEFT, flipTile(tile));
            } else if(tileName.equals("ammo_" + Direction.DOWN)) {
                ammunitionPictures.put("ammo_" + Direction.DOWN, tile);
            }
        }
    }

    private Tile flipTile(Tile Tile) {
        TextureRegion textureRegion = new TextureRegion(Tile.getTextureRegion());
        textureRegion.flip(true, false);
        return new StaticTile(textureRegion);
    }

    private void validate() {
        basicValidate();
        // Need check range values
        if (!properties.containsKey("factionName")) {
            Gdx.app.log("TemplateForTower::validate()", "-- NotFound: factionName");
        } else {
            factionName = properties.get("factionName");
        }
        if (!properties.containsKey("name")) {
            Gdx.app.log("TemplateForTower::validate()", "-- NotFound: name");
        } else {
            name = properties.get("name");
        }
        if (!properties.containsKey("radiusDetection")) {
            Gdx.app.log("TemplateForTower::validate()", "-- NotFound: radiusDetection");
        } else {
            radiusDetection = Float.parseFloat(properties.get("radiusDetection"));
        }
        if (!properties.containsKey("radiusFlyShell")) {
            Gdx.app.log("TemplateForTower::validate()", "-- NotFound: radiusFlyShell");
        } else {
            radiusFlyShell = Float.parseFloat(properties.get("radiusFlyShell"));
        }
        if (!properties.containsKey("damage")) {
            Gdx.app.log("TemplateForTower::validate()", "-- NotFound: damage");
        } else {
            damage = Integer.parseInt(properties.get("damage"));
        }
        if (!properties.containsKey("size")) {
            Gdx.app.log("TemplateForTower::validate()", "-- NotFound: size");
        } else {
            size = Integer.parseInt(properties.get("size"));
        }
        if (!properties.containsKey("cost")) {
            Gdx.app.log("TemplateForTower::validate()", "-- NotFound: cost");
        } else {
            cost = Integer.parseInt(properties.get("cost"));
        }
        if (!properties.containsKey("ammoSize")) {
            Gdx.app.log("TemplateForTower::validate()", "-- NotFound: ammoSize");
        } else {
            ammoSize = Float.parseFloat(properties.get("ammoSize"));
        }
        if (!properties.containsKey("ammoSpeed")) {
            Gdx.app.log("TemplateForTower::validate()", "-- NotFound: ammoSpeed");
        } else {
            ammoSpeed = Float.parseFloat(properties.get("ammoSpeed"));
        }
        if (!properties.containsKey("reloadTime")) {
            Gdx.app.log("TemplateForTower::validate()", "-- NotFound: reloadTime");
        } else {
            reloadTime = Float.parseFloat(properties.get("reloadTime"));
        }
        if (!properties.containsKey("towerAttackType")) {
            Gdx.app.log("TemplateForTower::validate()", "-- NotFound: towerAttackType");
        } else {
            towerAttackType = TowerAttackType.getType(properties.get("towerAttackType"));
        }
        if (!properties.containsKey("shellAttackType") && towerAttackType != TowerAttackType.Pit) {
            Gdx.app.log("TemplateForTower::validate()", "-- NotFound: shellAttackType");
        } else {
            shellAttackType = ShellAttackType.getType(properties.get("shellAttackType"));
        }
        if (!properties.containsKey("shellEffectType")) {
            Gdx.app.log("TemplateForTower::validate()", "-- NotFound: shellEffectType");
        } else {
            shellEffectType = new ShellEffectType(ShellEffectType.ShellEffectEnum.getType(properties.get("shellEffectType")));
        }
        if (towerAttackType == TowerAttackType.Pit && properties.containsKey("capacity")) {
            capacity = Integer.parseInt(properties.get("capacity"));
        } else if (towerAttackType == TowerAttackType.Pit) {
            Gdx.app.log("TemplateForTower::validate()", "-- NotFound: capacity! When towerAttackType==Pit");
        }
        if(this.radiusDetection == null && this.towerAttackType != TowerAttackType.Pit) {
            Gdx.app.log("TemplateForTower::validate()", "-- NotFound: radiusDetection");
        }
        if(this.radiusFlyShell == null && this.shellAttackType != ShellAttackType.FirstTarget) {
            Gdx.app.log("TemplateForTower::validate()", "-- NotFound: radiusFlyShell");
            this.radiusFlyShell = 0f;
        }

        if(idleTile == null)
            Gdx.app.log("TemplateForTower::validate()", "-- NotFound: idleTile");
        else if(ammunitionPictures.size == 0)
            Gdx.app.log("TemplateForTower::validate()", "-- NotFound: ammo");
//    foreach (QString key, ammunitionPictures.keys()) {
//        Gdx.app.log("TemplateForTower::validate()", "-- Dir:" << key << " properties:" << ammunitionPictures.value(key)->properties;
//    }
//        Gdx.app.log("TemplateForTower::validate()", "-- " + toString(true));
    }

    public String toString() {
        return toString(false);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("TemplateForTower[");
        sb.append(toStringBasicParam());
        if(full) {
            sb.append("," + "factionName:" + factionName);
            sb.append("," + "name:" + name);
            sb.append("," + "radiusDetection:" + radiusDetection);
            sb.append("," + "radiusFlyShell:" + radiusFlyShell);
            sb.append("," + "damage:" + damage);
            sb.append("," + "size:" + size);
            sb.append("," + "cost:" + cost);
            sb.append("," + "ammoSize:" + ammoSize);
            sb.append("," + "ammoSpeed:" + ammoSpeed);
            sb.append("," + "reloadTime:" + reloadTime);
            sb.append("," + "towerAttackType:" + towerAttackType);
            sb.append("," + "shellAttackType:" + shellAttackType);
            sb.append("," + "shellEffectEnum:" + shellEffectType);
            sb.append("," + "capacity:" + capacity);
            sb.append("," + "idleTile:" + (idleTile != null) );
            sb.append("," + "ammunitionPictures.size:" + ammunitionPictures.size);
        }
        sb.append("]");
        return sb.toString();
    }
}
