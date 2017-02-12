<?xml version="1.0" encoding="UTF-8"?>
<tileset name="tower2_melee" tilewidth="128" tileheight="128" tilecount="6" columns="2">
 <properties>
  <property name="factionName" value="Faction1"/>
  <property name="name" value="Melee1"/>
  <property name="radiusDetection" value="1"/>
  <property name="damage" value="0"/>
  <property name="size" value="1"/>
  <property name="cost" value="10"/>
  <property name="ammoSize" value="20"/>
  <property name="ammoSpeed" value="10"/>
  <property name="reloadTime" value="0.8"/>
  <property name="towerAttackType" value="Melee"/>
  <property name="shellAttackType" value="SingleTarget"/>
  <!-- <property name="shellEffectType" value="None"/> -->
 </properties>
 <image source="../../textures/tower_melee.png" trans="ff00ff" width="128" height="128"/>
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
