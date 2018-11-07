<?xml version="1.0" encoding="UTF-8"?>
<tileset name="tower_FireBall" tilewidth="64" tileheight="64" tilecount="6" columns="2">
 <properties>
  <property name="factionName" value="Faction1"/>
  <property name="name" value="tower_FireBall"/>
  <property name="radiusDetection" value="15"/>
  <property name="radiusFlyShell" value="150"/>
  <property name="damage" value="12"/>
  <property name="size" value="1"/>
  <property name="cost" value="130"/>
  <property name="ammoSize" value="50"/>
  <property name="ammoSpeed" value="10"/>
  <property name="reloadTime" value="2.5"/>
  <property name="towerAttackType" value="FireBall"/>
  <!-- <property name="shellAttackType" value="MultipleTarget"/> -->
  <!-- <property name="shellEffectType" value="None"/> -->
 </properties>
 <image source="../../textures/warcraft2/tilesets/winter/human/buildings/mage_tower.png" trans="ff00ff" width="128" height="192"/>
 <terraintypes>
  <terrain name="idleTile" tile="0"/>
 </terraintypes>
 <tile id="0">
  <properties>
   <property name="tileName" value="idleTile"/>
  </properties>
 </tile>
</tileset>
