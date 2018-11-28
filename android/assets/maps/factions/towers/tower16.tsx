<?xml version="1.0" encoding="UTF-8"?>
<tileset name="tower16" tilewidth="64" tileheight="64" tilecount="6" columns="2">
 <properties>
  <property name="factionName" value="Faction1"/>
  <property name="name" value="tower_FreezeBall"/>
  <property name="radiusDetection" value="2"/>
  <property name="radiusFlyShell" value="350"/>
  <property name="damage" value="30"/>
  <property name="size" value="1"/>
  <property name="cost" value="100"/>
  <property name="ammoSize" value="15"/>
  <property name="ammoSpeed" value="10"/>
  <property name="reloadTime" value="3"/>
  <property name="towerAttackType" value="FireBall"/>
  <property name="towerShellType" value="MultipleTarget"/>
  <property name="towerShellEffect" value="FreezeEffect"/>
  <property name="shellEffectType_time" value="2"/>
  <!-- <property name="shellEffectType_damage" value="5"/> -->
  <property name="shellEffectType_speed" value="0.5"/>
 </properties>
 <image source="../../textures/warcraft2/tilesets/summer/orc/buildings/cannon_tower_test.png" trans="ff00ff" width="128" height="192"/>
 <terraintypes>
  <terrain name="idleTile" tile="0"/>
 </terraintypes>
 <tile id="0">
  <properties>
   <property name="tileName" value="idleTile"/>
  </properties>
 </tile>
</tileset>
