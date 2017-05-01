<?xml version="1.0" encoding="UTF-8"?>
<tileset name="unit15_peasant" tilewidth="72" tileheight="72" tilecount="100" columns="5">
 <properties>
  <property name="factionName" value="Humans_Faction"/>
  <property name="name" value="Peasant"/>
  <property name="healthPoints" value="6000"/>
  <property name="bounty" value="40"/>
  <property name="cost" value="76"/>
  <property name="speed" value="1"/>
  <property name="type" value="unit"/>
 </properties>
 <image source="../../textures/warcraft2/human/units/peasant.png" trans="ff00ff" width="360" height="1440"/>
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
 <tile id="10">
  <properties>
   <property name="actionAndDirection" value="walk_UP"/>
  </properties>
  <animation>
   <frame tileid="10" duration="100"/>
   <frame tileid="5" duration="100"/>
   <frame tileid="10" duration="100"/>
   <frame tileid="20" duration="100"/>
   <frame tileid="15" duration="100"/>
   <frame tileid="20" duration="100"/>
  </animation>
 </tile>
 <tile id="11">
  <properties>
   <property name="actionAndDirection" value="walk_UP_RIGHT"/>
  </properties>
  <animation>
   <frame tileid="11" duration="100"/>
   <frame tileid="6" duration="100"/>
   <frame tileid="11" duration="100"/>
   <frame tileid="21" duration="100"/>
   <frame tileid="16" duration="100"/>
   <frame tileid="21" duration="100"/>
  </animation>
 </tile>
 <tile id="12">
  <properties>
   <property name="actionAndDirection" value="walk_RIGHT"/>
  </properties>
  <animation>
   <frame tileid="12" duration="100"/>
   <frame tileid="7" duration="100"/>
   <frame tileid="12" duration="100"/>
   <frame tileid="22" duration="100"/>
   <frame tileid="17" duration="100"/>
   <frame tileid="22" duration="100"/>
  </animation>
 </tile>
 <tile id="13">
  <properties>
   <property name="actionAndDirection" value="walk_DOWN_RIGHT"/>
  </properties>
  <animation>
   <frame tileid="13" duration="100"/>
   <frame tileid="8" duration="100"/>
   <frame tileid="13" duration="100"/>
   <frame tileid="23" duration="100"/>
   <frame tileid="18" duration="100"/>
   <frame tileid="23" duration="100"/>
  </animation>
 </tile>
 <tile id="14">
  <properties>
   <property name="actionAndDirection" value="walk_DOWN"/>
  </properties>
  <animation>
   <frame tileid="14" duration="100"/>
   <frame tileid="9" duration="100"/>
   <frame tileid="14" duration="100"/>
   <frame tileid="24" duration="100"/>
   <frame tileid="19" duration="100"/>
   <frame tileid="24" duration="100"/>
  </animation>
 </tile>
 <tile id="50">
  <properties>
   <property name="actionAndDirection" value="death_UP"/>
  </properties>
  <animation>
   <frame tileid="50" duration="100"/>
   <frame tileid="55" duration="100"/>
   <frame tileid="65" duration="100"/>
   <frame tileid="60" duration="100"/>
   <frame tileid="70" duration="100"/>
   <frame tileid="75" duration="100"/>
   <frame tileid="80" duration="100"/>
   <frame tileid="85" duration="100"/>
   <frame tileid="90" duration="100"/>
  </animation>
 </tile>
 <tile id="51">
  <properties>
   <property name="actionAndDirection" value="death_UP_RIGHT"/>
  </properties>
  <animation>
   <frame tileid="51" duration="100"/>
   <frame tileid="56" duration="100"/>
   <frame tileid="66" duration="100"/>
   <frame tileid="61" duration="100"/>
   <frame tileid="71" duration="100"/>
   <frame tileid="76" duration="100"/>
   <frame tileid="81" duration="100"/>
   <frame tileid="86" duration="100"/>
   <frame tileid="91" duration="100"/>
  </animation>
 </tile>
 <tile id="52">
  <properties>
   <property name="actionAndDirection" value="death_RIGHT"/>
  </properties>
  <animation>
   <frame tileid="52" duration="100"/>
   <frame tileid="57" duration="100"/>
   <frame tileid="67" duration="100"/>
   <frame tileid="62" duration="100"/>
   <frame tileid="72" duration="100"/>
   <frame tileid="77" duration="100"/>
   <frame tileid="82" duration="100"/>
   <frame tileid="87" duration="100"/>
   <frame tileid="92" duration="100"/>
  </animation>
 </tile>
 <tile id="53">
  <properties>
   <property name="actionAndDirection" value="death_DOWN_RIGHT"/>
  </properties>
  <animation>
   <frame tileid="53" duration="100"/>
   <frame tileid="58" duration="100"/>
   <frame tileid="68" duration="100"/>
   <frame tileid="63" duration="100"/>
   <frame tileid="73" duration="100"/>
   <frame tileid="78" duration="100"/>
   <frame tileid="83" duration="100"/>
   <frame tileid="88" duration="100"/>
   <frame tileid="93" duration="100"/>
  </animation>
 </tile>
 <tile id="54">
  <properties>
   <property name="actionAndDirection" value="death_DOWN"/>
  </properties>
  <animation>
   <frame tileid="54" duration="100"/>
   <frame tileid="59" duration="100"/>
   <frame tileid="69" duration="100"/>
   <frame tileid="64" duration="100"/>
   <frame tileid="74" duration="100"/>
   <frame tileid="79" duration="100"/>
   <frame tileid="84" duration="100"/>
   <frame tileid="89" duration="100"/>
   <frame tileid="94" duration="100"/>
  </animation>
 </tile>
</tileset>
