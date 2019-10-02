<?xml version="1.0" encoding="UTF-8"?>
<tileset name="touch_of_death" tilewidth="32" tileheight="32" tilecount="30" columns="5">
 <image source="../../../textures/warcraft2/missiles/touch_of_death.png" width="160" height="192"/>
 <properties>
  <property name="ammoSize" value="10"/>
  <property name="ammoSpeed" value="10"/>
  <property name="animationTime" value="10"/>
 </properties>
 <tile id="0">
  <properties>
   <property name="tileName" value="weapon_UP"/>
  </properties>
  <animation>
   <frame tileid="0" duration="100"/>
  </animation>
 </tile>
 <tile id="1">
  <properties>
   <property name="tileName" value="weapon_UP_RIGHT"/>
  </properties>
  <animation>
   <frame tileid="1" duration="100"/>
  </animation>
 </tile>
 <tile id="2">
  <properties>
   <property name="tileName" value="weapon_RIGHT"/>
  </properties>
  <animation>
   <frame tileid="2" duration="100"/>
  </animation>
 </tile>
 <tile id="3">
  <properties>
   <property name="tileName" value="weapon_DOWN_RIGHT"/>
  </properties>
  <animation>
   <frame tileid="3" duration="100"/>
  </animation>
 </tile>
 <tile id="4">
  <properties>
   <property name="tileName" value="weapon_DOWN"/>
  </properties>
  <animation>
   <frame tileid="4" duration="100"/>
  </animation>
 </tile>
</tileset>
