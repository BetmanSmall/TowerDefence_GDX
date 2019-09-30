<?xml version="1.0" encoding="UTF-8"?>
<tileset name="unit7_dragon" tilewidth="88" tileheight="80" tilecount="50" columns="5">
 <properties>
  <property name="factionName" value="Orcs_Faction"/>
  <property name="name" value="Dragon"/>
  <property name="healthPoints" value="650"/>
  <property name="bounty" value="60"/>
  <property name="cost" value="60"/>
  <property name="speed" value="0.9"/>
  <property name="type" value="fly"/>
  <property name="attackType" value="Range"/>
  <property name="attackType_damage" value="50"/>
  <property name="attackType_range" value="200"/>
  <property name="attackType_attackSpeed" value="1.0"/>
  <property name="attackType_reload" value="1.0"/>
  <property name="attackType_stackInOneCell" value="false"/>
  <property name="attackType_stayToDie" value="true"/>
 </properties>
 <image source="../../textures/warcraft2/orc/units/dragon.png" trans="ff00ff" width="440" height="800"/>
 <tile id="0">
  <properties>
   <property name="actionAndDirection" value="walk_UP"/>
  </properties>
  <animation>
   <frame tileid="0" duration="100"/>
   <frame tileid="5" duration="100"/>
   <frame tileid="0" duration="100"/>
   <frame tileid="10" duration="100"/>
   <frame tileid="15" duration="100"/>
   <frame tileid="10" duration="100"/>
  </animation>
 </tile>
 <tile id="1">
  <properties>
   <property name="actionAndDirection" value="walk_UP_RIGHT"/>
  </properties>
  <animation>
   <frame tileid="1" duration="100"/>
   <frame tileid="6" duration="100"/>
   <frame tileid="1" duration="100"/>
   <frame tileid="11" duration="100"/>
   <frame tileid="16" duration="100"/>
   <frame tileid="11" duration="100"/>
  </animation>
 </tile>
 <tile id="2">
  <properties>
   <property name="actionAndDirection" value="walk_RIGHT"/>
  </properties>
  <animation>
   <frame tileid="2" duration="100"/>
   <frame tileid="7" duration="100"/>
   <frame tileid="2" duration="100"/>
   <frame tileid="12" duration="100"/>
   <frame tileid="17" duration="100"/>
   <frame tileid="12" duration="100"/>
  </animation>
 </tile>
 <tile id="3">
  <properties>
   <property name="actionAndDirection" value="walk_DOWN_RIGHT"/>
  </properties>
  <animation>
   <frame tileid="3" duration="100"/>
   <frame tileid="8" duration="100"/>
   <frame tileid="3" duration="100"/>
   <frame tileid="13" duration="100"/>
   <frame tileid="18" duration="100"/>
   <frame tileid="13" duration="100"/>
  </animation>
 </tile>
 <tile id="4">
  <properties>
   <property name="actionAndDirection" value="walk_DOWN"/>
  </properties>
  <animation>
   <frame tileid="4" duration="100"/>
   <frame tileid="9" duration="100"/>
   <frame tileid="4" duration="100"/>
   <frame tileid="14" duration="100"/>
   <frame tileid="19" duration="100"/>
   <frame tileid="14" duration="100"/>
  </animation>
 </tile>
 <tile id="10">
  <properties>
   <property name="actionAndDirection" value="idle_UP"/>
  </properties>
  <animation>
   <frame tileid="10" duration="100"/>
   <frame tileid="11" duration="100"/>
   <frame tileid="12" duration="100"/>
   <frame tileid="13" duration="100"/>
   <frame tileid="14" duration="100"/>
  </animation>
 </tile>
 <tile id="20">
  <properties>
   <property name="actionAndDirection" value="attack_UP"/>
  </properties>
  <animation>
   <frame tileid="20" duration="100"/>
   <frame tileid="15" duration="100"/>
  </animation>
 </tile>
 <tile id="21">
  <properties>
   <property name="actionAndDirection" value="attack_UP_RIGHT"/>
  </properties>
  <animation>
   <frame tileid="21" duration="1000"/>
   <frame tileid="16" duration="1000"/>
  </animation>
 </tile>
 <tile id="22">
  <properties>
   <property name="actionAndDirection" value="attack_RIGHT"/>
  </properties>
  <animation>
   <frame tileid="22" duration="1000"/>
   <frame tileid="7" duration="1000"/>
  </animation>
 </tile>
 <tile id="23">
  <properties>
   <property name="actionAndDirection" value="attack_DOWN_RIGHT"/>
  </properties>
  <animation>
   <frame tileid="23" duration="100"/>
   <frame tileid="18" duration="100"/>
  </animation>
 </tile>
 <tile id="24">
  <properties>
   <property name="actionAndDirection" value="attack_DOWN"/>
  </properties>
  <animation>
   <frame tileid="24" duration="100"/>
   <frame tileid="19" duration="100"/>
  </animation>
 </tile>
 <tile id="25">
  <properties>
   <property name="actionAndDirection" value="death_UP"/>
  </properties>
  <animation>
   <frame tileid="25" duration="100"/>
   <frame tileid="30" duration="100"/>
   <frame tileid="35" duration="100"/>
   <frame tileid="40" duration="100"/>
   <frame tileid="45" duration="100"/>
  </animation>
 </tile>
 <tile id="26">
  <properties>
   <property name="actionAndDirection" value="death_UP_RIGHT"/>
  </properties>
  <animation>
   <frame tileid="26" duration="100"/>
   <frame tileid="31" duration="100"/>
   <frame tileid="36" duration="100"/>
   <frame tileid="41" duration="100"/>
   <frame tileid="46" duration="100"/>
  </animation>
 </tile>
 <tile id="27">
  <properties>
   <property name="actionAndDirection" value="death_RIGHT"/>
  </properties>
  <animation>
   <frame tileid="27" duration="100"/>
   <frame tileid="32" duration="100"/>
   <frame tileid="37" duration="100"/>
   <frame tileid="42" duration="100"/>
   <frame tileid="47" duration="100"/>
  </animation>
 </tile>
 <tile id="28">
  <properties>
   <property name="actionAndDirection" value="death_DOWN_RIGHT"/>
  </properties>
  <animation>
   <frame tileid="28" duration="100"/>
   <frame tileid="33" duration="100"/>
   <frame tileid="38" duration="100"/>
   <frame tileid="43" duration="100"/>
   <frame tileid="48" duration="100"/>
  </animation>
 </tile>
 <tile id="29">
  <properties>
   <property name="actionAndDirection" value="death_DOWN"/>
  </properties>
  <animation>
   <frame tileid="29" duration="100"/>
   <frame tileid="34" duration="100"/>
   <frame tileid="39" duration="100"/>
   <frame tileid="44" duration="100"/>
   <frame tileid="49" duration="100"/>
  </animation>
 </tile>
</tileset>
