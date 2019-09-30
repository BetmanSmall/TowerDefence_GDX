<?xml version="1.0" encoding="UTF-8"?>
<tileset name="arrow" tilewidth="40" tileheight="40" tilecount="5" columns="5">
 <image source="../../textures/warcraft2/missiles/arrow.png" width="200" height="40"/>
 <properties>
  <property name="ammoSize" value="10"/>
  <property name="ammoSpeed" value="13"/>
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
