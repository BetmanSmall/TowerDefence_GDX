<?xml version="1.0" encoding="UTF-8"?>
<tileset name="catapult_rock" tilewidth="32" tileheight="32" tilecount="15" columns="5">
 <image source="../textures/warcraft2/missiles/catapult_rock.png" width="160" height="96"/>
 <properties>
  <property name="ammoSize" value="15"/>
  <property name="ammoSpeed" value="10"/>
  <property name="animationTime" value="10"/>
 </properties>
 <tile id="10">
  <properties>
   <property name="tileName" value="weapon_UP"/>
  </properties>
  <animation>
   <frame tileid="0" duration="100"/>
   <frame tileid="5" duration="100"/>
   <frame tileid="10" duration="100"/>
   <frame tileid="5" duration="100"/>
  </animation>
 </tile>
 <tile id="11">
  <properties>
   <property name="tileName" value="weapon_UP_RIGHT"/>
  </properties>
  <animation>
   <frame tileid="1" duration="100"/>
   <frame tileid="6" duration="100"/>
   <frame tileid="11" duration="100"/>
   <frame tileid="6" duration="100"/>
  </animation>
 </tile>
 <tile id="12">
  <properties>
   <property name="tileName" value="weapon_RIGHT"/>
  </properties>
  <animation>
   <frame tileid="2" duration="100"/>
   <frame tileid="7" duration="100"/>
   <frame tileid="12" duration="100"/>
   <frame tileid="7" duration="100"/>
  </animation>
 </tile>
 <tile id="13">
  <properties>
   <property name="tileName" value="weapon_DOWN_RIGHT"/>
  </properties>
  <animation>
   <frame tileid="3" duration="100"/>
   <frame tileid="8" duration="100"/>
   <frame tileid="13" duration="100"/>
   <frame tileid="8" duration="100"/>
  </animation>
 </tile>
 <tile id="14">
  <properties>
   <property name="tileName" value="weapon_DOWN"/>
  </properties>
  <animation>
   <frame tileid="4" duration="100"/>
   <frame tileid="9" duration="100"/>
   <frame tileid="14" duration="100"/>
   <frame tileid="9" duration="100"/>
  </animation>
 </tile>
</tileset>
