<?xml version="1.0" encoding="UTF-8"?>
<tileset name="tower4_cannon" tilewidth="64" tileheight="64">
 <properties>
  <property name="ammoSpeed" value="0.2"/>
  <property name="cost" value="10"/>
  <property name="damage" value="25"/>
  <property name="factionName" value="Faction1"/>
  <property name="name" value="Cannon2"/>
  <property name="radius" value="2"/>
  <property name="reloadTime" value="0.2"/>
  <property name="size" value="2"/>
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