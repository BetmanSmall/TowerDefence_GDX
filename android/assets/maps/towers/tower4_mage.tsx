<?xml version="1.0" encoding="UTF-8"?>
<tileset name="tower2_mage" tilewidth="96" tileheight="96">
 <properties>
  <property name="attackSpeed" value="0.2"/>
  <property name="damage" value="25"/>
  <property name="factionName" value="Faction1"/>
  <property name="name" value="Mage"/>
  <property name="radius" value="2"/>
  <property name="size" value="3"/>
  <property name="type" value="tower"/>
 </properties>
 <image source="../textures/warcraft2/tilesets/winter/human/buildings/mage_tower.png" trans="ff00ff" width="96" height="192"/>
 <terraintypes>
  <terrain name="idle_up" tile="0"/>
  <terrain name="idle_down" tile="1"/>
 </terraintypes>
 <tile id="0">
  <properties>
   <property name="tileName" value="idleTile"/>
  </properties>
 </tile>
</tileset>
