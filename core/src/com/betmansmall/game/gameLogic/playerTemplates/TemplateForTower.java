package com.betmansmall.game.gameLogic.playerTemplates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.StringBuilder;

import com.betmansmall.game.gameLogic.mapLoader.AnimatedTiledMapTile;
import com.betmansmall.game.gameLogic.mapLoader.StaticTiledMapTile;
import com.betmansmall.game.gameLogic.mapLoader.TiledMapTile;

/**
 * Created by betmansmall on 22.02.2016.
 */
public class TemplateForTower extends Template {
//    private Faction faction;
    public String   factionName;
    public String   name;
    public Float    healthPoints;

    public Float    radiusDetection = null;
    public Float    radiusFlyShell = null;
    public Integer  damage;
    public Integer  size;
    public Integer  cost;
    public Float    ammoSize;
    public Float    ammoSpeed;
    public Float    reloadTime;
//    public String   type;
    public TowerAttackType towerAttackType;
    public TowerShellType towerShellType;
    public TowerShellEffect towerShellEffect;
    public Integer capacity;

//    public AnimatedTiledMapTile idleTile;
    public TiledMapTile idleTile;
    public ObjectMap<String, AnimatedTiledMapTile> animations;

    public TemplateForTower(FileHandle templateFile) throws Exception {
        try {
//            this.radiusDetection = 0.0f;
//            this.reloadTime = 3000;
            animations = new ObjectMap<String, AnimatedTiledMapTile>();
            loadBasicTemplate(templateFile);
            specificLoad();
            validate();
//            this.size = 1;
        } catch (Exception exp) {
            Gdx.app.log("TemplateForTower::TemplateForTower()", "-- Could not load TemplateForTower from " + templateFile.path() + " Exp:" + exp);
            throw new Exception("TemplateForTower::TemplateForTower() -- Could not load TemplateForTower from " + templateFile.path() + " Exp:" + exp);
        }
    }

    void loadExplosion(SimpleTemplate explosion) {
        if (explosion != null) {
            for (AnimatedTiledMapTile animatedTile : explosion.animatedTiles.values()) {
                String tileName = animatedTile.getProperties().get("tileName", null);
                if (tileName != null) {
                    if(tileName.equals("explosion_")) {
                        animations.put("explosion_", animatedTile);
                    }
                }
            }
        }
    }

    void loadFireBall(SimpleTemplate fireBall) {
        if (fireBall != null) {
            for (AnimatedTiledMapTile animatedTile : fireBall.animatedTiles.values()) {
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
        for (TiledMapTile tiledMapTile : tiles.values()) {
            String tileName = tiledMapTile.getProperties().get("tileName", null);
            if (tileName != null) {
                if(tileName.equals("idleTile")) {
                    idleTile = tiledMapTile;
//                } else if(tileName.contains("ammo_")) {
//                    setAmmoTiles(tileName, tiledMapTile);
//                } else {
//                    Gdx.app.log("TemplateForTower::specificLoad()", "-- Not found idleTile");
                }
            }
        }
    }

    private void setAmmoTiles(String tileName, AnimatedTiledMapTile tile) {
        if(tile != null) {
            if(tileName.equals("ammo_" + Direction.UP)) {
                animations.put("ammo_" + Direction.UP, tile);
            } else if(tileName.equals("ammo_" + Direction.UP_RIGHT)) {
                animations.put("ammo_" + Direction.UP_RIGHT, tile);
                animations.put("ammo_" + Direction.UP_LEFT, flipAnimatedTiledMapTile(tile));
            } else if(tileName.equals("ammo_" + Direction.RIGHT)) {
                animations.put("ammo_" + Direction.RIGHT, tile);
                animations.put("ammo_" + Direction.LEFT, flipAnimatedTiledMapTile(tile));
            } else if(tileName.equals("ammo_" + Direction.DOWN_RIGHT)) {
                animations.put("ammo_" + Direction.DOWN_RIGHT, tile);
                animations.put("ammo_" + Direction.DOWN_LEFT, flipAnimatedTiledMapTile(tile));
            } else if(tileName.equals("ammo_" + Direction.DOWN)) {
                animations.put("ammo_" + Direction.DOWN, tile);
            }
        }
    }

//    private TiledMapTile flipTile(TiledMapTile TiledMapTile) {
//        TextureRegion textureRegion = new TextureRegion(TiledMapTile.getTextureRegion());
//        textureRegion.flip(true, false);
//        return new StaticTiledMapTile(textureRegion);
//    }

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
        if (!properties.containsKey("healthPoints")) {
            Gdx.app.log("TemplateForTower::validate()", "-- NotFound: healthPoints");
        } else {
            healthPoints = Float.parseFloat(properties.get("healthPoints"));
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
        if (!properties.containsKey("towerShellType") && towerAttackType != TowerAttackType.Pit) {
            Gdx.app.log("TemplateForTower::validate()", "-- NotFound: towerShellType");
        } else {
            towerShellType = TowerShellType.getType(properties.get("towerShellType"));
        }
        if (!properties.containsKey("towerShellEffect")) {
            Gdx.app.log("TemplateForTower::validate()", "-- NotFound: towerShellEffect");
        } else {
            towerShellEffect = new TowerShellEffect(TowerShellEffect.ShellEffectEnum.getType(properties.get("towerShellEffect")));
            if (!properties.containsKey("shellEffectType_time")) {
                Gdx.app.log("TemplateForTower::validate()", "-- NotFound: shellEffectType_time");
            } else {
                towerShellEffect.time = Float.parseFloat(properties.get("shellEffectType_time"));
            }
            if (!properties.containsKey("shellEffectType_damage")) {
                Gdx.app.log("TemplateForTower::validate()", "-- NotFound: shellEffectType_damage");
            } else {
                towerShellEffect.damage = Float.parseFloat(properties.get("shellEffectType_damage"));
            }
            if (!properties.containsKey("shellEffectType_speed")) {
                Gdx.app.log("TemplateForTower::validate()", "-- NotFound: shellEffectType_speed");
            } else {
                towerShellEffect.speed = Float.parseFloat(properties.get("shellEffectType_speed"));
            }
        }
        if (towerAttackType == TowerAttackType.Pit && properties.containsKey("capacity")) {
            capacity = Integer.parseInt(properties.get("capacity"));
        } else if (towerAttackType == TowerAttackType.Pit) {
            Gdx.app.log("TemplateForTower::validate()", "-- NotFound: capacity! When towerAttackType==Pit");
        }
        if(this.radiusDetection == null && this.towerAttackType != TowerAttackType.Pit) {
            Gdx.app.log("TemplateForTower::validate()", "-- NotFound: radiusDetection");
        }
        if(this.radiusFlyShell == null && this.towerShellType != towerShellType.FirstTarget) {
            Gdx.app.log("TemplateForTower::validate()", "-- NotFound: radiusFlyShell");
//            this.radiusFlyShell = 0f;
        }

        if(idleTile == null)
            Gdx.app.log("TemplateForTower::validate()", "-- NotFound: idleTile");
        else if(animations.size == 0)
            Gdx.app.log("TemplateForTower::validate()", "-- NotFound: animations");

//        for (String key : animations.keys()) {
//            Gdx.app.log("TemplateForTower::validate()", "-- Dir:" + key + " length:" + animations.get(key).getFrameTiles().length);
//        }
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
//            sb.append(toStringProperties());
            sb.append(",factionName:" + factionName);
            sb.append(",name:" + name);
            sb.append(",radiusDetection:" + radiusDetection);
            sb.append(",radiusFlyShell:" + radiusFlyShell);
            sb.append(",damage:" + damage);
            sb.append(",size:" + size);
            sb.append(",cost:" + cost);
            sb.append(",ammoSize:" + ammoSize);
            sb.append(",ammoSpeed:" + ammoSpeed);
            sb.append(",reloadTime:" + reloadTime);
            sb.append(",towerAttackType:" + towerAttackType);
            sb.append(",towerShellType:" + towerShellType);
            sb.append(",shellEffectEnum:" + towerShellEffect);
            sb.append(",capacity:" + capacity);
            sb.append(",idleTile:" + (idleTile != null) );
            sb.append(",animations.size:" + animations.size);
        }
        sb.append("]");
        return sb.toString();
    }
}
