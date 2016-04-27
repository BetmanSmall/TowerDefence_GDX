<?xml version="1.0" encoding="UTF-8"?>
<tileset name="tower3_cannon" tilewidth="64" tileheight="64" tilecount="2" columns="1">
 <properties>
  <property name="ammoDistance" type="int" value="10"/>
  <property name="ammoSpeed" value="0.1"/>
  <property name="cost" value="10"/>
  <property name="damage" value="7"/>
  <property name="factionName" value="Faction1"/>
  <property name="name" value="Cannon"/>
  <property name="radius" value="1"/>
  <property name="reloadTime" value="0.4"/>
  <property name="size" value="1"/>
  <property name="type" value="tower"/>
 </properties>
 <image source="../textures/warcraft2/tilesets/summer/orc/buildings/cannon_tower.png" trans="ff00ff" width="64" height="128"/>
 <terraintypes>
  <terrain name="idle_UP" tile="0"/>
  <terrain name="idle_DOWN" tile="1"/>
 </terraintypes>
 <tile id="0">
  <properties>
   <property name="tileName" value="idleTile"/>
  </properties>
 </tile>
</tileset>