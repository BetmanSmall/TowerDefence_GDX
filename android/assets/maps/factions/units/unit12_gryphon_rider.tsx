<?xml version="1.0" encoding="UTF-8"?>
<tileset name="unit12_gryphon_rider" tilewidth="80" tileheight="80" tilecount="65" columns="5">
 <properties>
  <property name="factionName" value="Humans_Faction"/>
  <property name="name" value="Gryphon Rider"/>
  <property name="healthPoints" value="326"/>
  <property name="bounty" value="10"/>
  <property name="cost" value="28"/>
  <property name="speed" value="0.3"/>
  <property name="type" value="fly"/>
 </properties>
 <image source="../../textures/warcraft2/human/units/gryphon_rider.png" trans="ff00ff" width="400" height="1040"/>
 <tile id="0">
  <properties>
   <property name="actionAndDirection" value="idle_UP"/>
  </properties>
  <animation>
   <frame tileid="20" duration="100"/>
   <frame tileid="21" duration="100"/>
   <frame tileid="22" duration="100"/>
   <frame tileid="23" duration="100"/>
   <frame tileid="24" duration="100"/>
  </animation>
 </tile>
 <tile id="1">
  <properties>
   <property name="actionAndDirection" value="idle_UP_RIGHT"/>
  </properties>
 </tile>
 <tile id="2">
  <properties>
   <property name="actionAndDirection" value="idle_RIGHT"/>
  </properties>
 </tile>
 <tile id="3">
  <properties>
   <property name="actionAndDirection" value="idle_DOWN_RIGHT"/>
  </properties>
 </tile>
 <tile id="4">
  <properties>
   <property name="actionAndDirection" value="idle_DOWN"/>
  </properties>
 </tile>
 <tile id="5">
  <properties>
   <property name="actionAndDirection" value="walk_UP"/>
  </properties>
  <animation>
   <frame tileid="15" duration="100"/>
   <frame tileid="0" duration="100"/>
   <frame tileid="5" duration="100"/>
   <frame tileid="10" duration="100"/>
  </animation>
 </tile>
 <tile id="6">
  <properties>
   <property name="actionAndDirection" value="walk_UP_RIGHT"/>
  </properties>
  <animation>
   <frame tileid="16" duration="100"/>
   <frame tileid="1" duration="100"/>
   <frame tileid="6" duration="100"/>
   <frame tileid="11" duration="100"/>
  </animation>
 </tile>
 <tile id="7">
  <properties>
   <property name="actionAndDirection" value="walk_RIGHT"/>
  </properties>
  <animation>
   <frame tileid="17" duration="100"/>
   <frame tileid="2" duration="100"/>
   <frame tileid="7" duration="100"/>
   <frame tileid="12" duration="100"/>
  </animation>
 </tile>
 <tile id="8">
  <properties>
   <property name="actionAndDirection" value="walk_DOWN_RIGHT"/>
  </properties>
  <animation>
   <frame tileid="18" duration="100"/>
   <frame tileid="3" duration="100"/>
   <frame tileid="8" duration="100"/>
   <frame tileid="13" duration="100"/>
  </animation>
 </tile>
 <tile id="9">
  <properties>
   <property name="actionAndDirection" value="walk_DOWN"/>
  </properties>
  <animation>
   <frame tileid="19" duration="100"/>
   <frame tileid="4" duration="100"/>
   <frame tileid="9" duration="100"/>
   <frame tileid="14" duration="100"/>
  </animation>
 </tile>
 <tile id="35">
  <properties>
   <property name="actionAndDirection" value="death_UP"/>
  </properties>
  <animation>
   <frame tileid="35" duration="100"/>
   <frame tileid="40" duration="100"/>
   <frame tileid="45" duration="100"/>
   <frame tileid="50" duration="100"/>
   <frame tileid="55" duration="100"/>
   <frame tileid="60" duration="100"/>
  </animation>
 </tile>
 <tile id="36">
  <properties>
   <property name="actionAndDirection" value="death_UP_RIGHT"/>
  </properties>
  <animation>
   <frame tileid="36" duration="100"/>
   <frame tileid="41" duration="100"/>
   <frame tileid="46" duration="100"/>
   <frame tileid="51" duration="100"/>
   <frame tileid="56" duration="100"/>
   <frame tileid="61" duration="100"/>
  </animation>
 </tile>
 <tile id="37">
  <properties>
   <property name="actionAndDirection" value="death_RIGHT"/>
  </properties>
  <animation>
   <frame tileid="37" duration="100"/>
   <frame tileid="42" duration="100"/>
   <frame tileid="47" duration="100"/>
   <frame tileid="52" duration="100"/>
   <frame tileid="57" duration="100"/>
   <frame tileid="62" duration="100"/>
  </animation>
 </tile>
 <tile id="38">
  <properties>
   <property name="actionAndDirection" value="death_DOWN_RIGHT"/>
  </properties>
  <animation>
   <frame tileid="38" duration="100"/>
   <frame tileid="43" duration="100"/>
   <frame tileid="48" duration="100"/>
   <frame tileid="53" duration="100"/>
   <frame tileid="58" duration="100"/>
   <frame tileid="63" duration="100"/>
  </animation>
 </tile>
 <tile id="39">
  <properties>
   <property name="actionAndDirection" value="death_DOWN"/>
  </properties>
  <animation>
   <frame tileid="39" duration="100"/>
   <frame tileid="44" duration="100"/>
   <frame tileid="49" duration="100"/>
   <frame tileid="54" duration="100"/>
   <frame tileid="59" duration="100"/>
   <frame tileid="64" duration="100"/>
  </animation>
 </tile>
</tileset>
