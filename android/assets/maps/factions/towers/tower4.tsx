<?xml version="1.0" encoding="UTF-8"?>
<tileset name="tower4" tilewidth="64" tileheight="64" tilecount="1" columns="1">
 <properties>
  <property name="factionName" value="Faction1"/>
  <property name="name" value="Melee2"/>
  <property name="radiusDetection" value="1"/>
  <property name="damage" value="1"/>
  <property name="size" value="1"/>
  <property name="cost" value="50"/>
  <property name="ammoSize" value="1"/>
  <property name="ammoSpeed" value="1"/>
  <property name="reloadTime" value="1"/>
  <property name="towerAttackType" value="Melee"/>
  <property name="towerShellType" value="MultipleTarget"/>
  <!-- <property name="towerShellEffect" value="None"/> -->
 </properties>
 <image source="../../textures/tower_melee.png" trans="ff00ff" width="64" height="64"/>
 <terraintypes>
  <terrain name="idleTile" tile="0"/>
  <terrain name="ammo_UP" tile="1"/>
  <terrain name="ammo_UP_RIGHT" tile="2"/>
  <terrain name="ammo_RIGHT" tile="3"/>
  <terrain name="ammo_DOWN_RIGHT" tile="4"/>
  <terrain name="ammo_DOWN" tile="5"/>
 </terraintypes>
 <tile id="0">
  <properties>
   <property name="tileName" value="idleTile"/>
  </properties>
 </tile>
 <tile id="1">
  <properties>
   <property name="tileName" value="ammo_UP"/>
  </properties>
 </tile>
 <tile id="2">
  <properties>
   <property name="tileName" value="ammo_UP_RIGHT"/>
  </properties>
 </tile>
 <tile id="3">
  <properties>
   <property name="tileName" value="ammo_RIGHT"/>
  </properties>
 </tile>
 <tile id="4">
  <properties>
   <property name="tileName" value="ammo_DOWN_RIGHT"/>
  </properties>
 </tile>
 <tile id="5">
  <properties>
   <property name="tileName" value="ammo_DOWN"/>
  </properties>
 </tile>
</tileset>
