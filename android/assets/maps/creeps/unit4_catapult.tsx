<?xml version="1.0" encoding="UTF-8"?>
<tileset name="unit4_catapult" tilewidth="64" tileheight="64" tilecount="50" columns="5">
 <properties>
  <property name="bounty" value="7"/>
  <property name="factionName" value="Faction1"/>
  <property name="healthPoints" value="100"/>
  <property name="name" value="Catapult"/>
  <property name="speed" value="0.5"/>
  <property name="type" value="unit"/>
 </properties>
 <image source="../textures/warcraft2/orc/units/catapult.png" width="320" height="640"/>
 <tile id="0">
  <properties>
   <property name="actionAndDirection" value="walk3_UP"/>
  </properties>
  <animation>
   <frame tileid="0" duration="100"/>
   <frame tileid="5" duration="100"/>
   <frame tileid="0" duration="100"/>
   <frame tileid="5" duration="100"/>
   <frame tileid="0" duration="100"/>
   <frame tileid="5" duration="100"/>
  </animation>
 </tile>
 <tile id="1">
  <properties>
   <property name="actionAndDirection" value="walk3_UP_RIGHT"/>
  </properties>
  <animation>
   <frame tileid="1" duration="100"/>
   <frame tileid="6" duration="100"/>
   <frame tileid="1" duration="100"/>
   <frame tileid="6" duration="100"/>
   <frame tileid="1" duration="100"/>
   <frame tileid="6" duration="100"/>
  </animation>
 </tile>
 <tile id="2">
  <properties>
   <property name="actionAndDirection" value="walk3_RIGHT"/>
  </properties>
  <animation>
   <frame tileid="2" duration="100"/>
   <frame tileid="7" duration="100"/>
   <frame tileid="2" duration="100"/>
   <frame tileid="7" duration="100"/>
   <frame tileid="2" duration="100"/>
   <frame tileid="7" duration="100"/>
  </animation>
 </tile>
 <tile id="3">
  <properties>
   <property name="actionAndDirection" value="walk3_DOWN_RIGHT"/>
  </properties>
  <animation>
   <frame tileid="3" duration="100"/>
   <frame tileid="8" duration="100"/>
   <frame tileid="3" duration="100"/>
   <frame tileid="8" duration="100"/>
   <frame tileid="3" duration="100"/>
   <frame tileid="8" duration="100"/>
  </animation>
 </tile>
 <tile id="4">
  <properties>
   <property name="actionAndDirection" value="walk3_DOWN"/>
  </properties>
  <animation>
   <frame tileid="4" duration="100"/>
   <frame tileid="9" duration="100"/>
   <frame tileid="4" duration="100"/>
   <frame tileid="9" duration="100"/>
   <frame tileid="4" duration="100"/>
   <frame tileid="9" duration="100"/>
  </animation>
 </tile>
 <tile id="20">
  <properties>
   <property name="actionAndDirection" value="death1_UP"/>
  </properties>
  <animation>
   <frame tileid="20" duration="100"/>
   <frame tileid="25" duration="100"/>
   <frame tileid="30" duration="100"/>
   <frame tileid="35" duration="100"/>
   <frame tileid="40" duration="100"/>
   <frame tileid="45" duration="100"/>
  </animation>
 </tile>
 <tile id="21">
  <properties>
   <property name="actionAndDirection" value="death1_UP_RIGHT"/>
  </properties>
  <animation>
   <frame tileid="21" duration="100"/>
   <frame tileid="26" duration="100"/>
   <frame tileid="31" duration="100"/>
   <frame tileid="36" duration="100"/>
   <frame tileid="41" duration="100"/>
   <frame tileid="46" duration="100"/>
  </animation>
 </tile>
 <tile id="22">
  <properties>
   <property name="actionAndDirection" value="death1_RIGHT"/>
  </properties>
  <animation>
   <frame tileid="22" duration="100"/>
   <frame tileid="27" duration="100"/>
   <frame tileid="32" duration="100"/>
   <frame tileid="37" duration="100"/>
   <frame tileid="42" duration="100"/>
   <frame tileid="47" duration="100"/>
  </animation>
 </tile>
 <tile id="23">
  <properties>
   <property name="actionAndDirection" value="death1_DOWN_RIGHT"/>
  </properties>
  <animation>
   <frame tileid="23" duration="100"/>
   <frame tileid="28" duration="100"/>
   <frame tileid="33" duration="100"/>
   <frame tileid="38" duration="100"/>
   <frame tileid="43" duration="100"/>
   <frame tileid="48" duration="100"/>
  </animation>
 </tile>
 <tile id="24">
  <properties>
   <property name="actionAndDirection" value="death1_DOWN"/>
  </properties>
  <animation>
   <frame tileid="24" duration="100"/>
   <frame tileid="29" duration="100"/>
   <frame tileid="34" duration="100"/>
   <frame tileid="39" duration="100"/>
   <frame tileid="44" duration="100"/>
   <frame tileid="49" duration="100"/>
  </animation>
 </tile>
</tileset>
