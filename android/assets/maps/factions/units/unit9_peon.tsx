<?xml version="1.0" encoding="UTF-8"?>
<tileset name="unit9_peon" tilewidth="72" tileheight="72" tilecount="100" columns="5">
 <properties>
  <property name="factionName" value="Orcs_Faction"/>
  <property name="name" value="Peon"/>
  <property name="healthPoints" value="100"/>
  <property name="bounty" value="0"/>
  <property name="cost" value="1"/>
  <property name="speed" value="0.6"/>
  <property name="type" value="unit"/>
  <property name="attackType" value="Melee"/>
  <property name="attackType_damage" value="8"/>
  <property name="attackType_range" value="1"/>
  <property name="attackType_attackSpeed" value="0.5"/>
  <property name="attackType_reload" value="0.7"/>
  <property name="attackType_stackInOneCell" value="true"/>
  <property name="attackType_stayToDie" value="true"/>
 </properties>
 <image source="../../textures/warcraft2/orc/units/peon.png" trans="ff00ff" width="360" height="1440"/>
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
 <tile id="25">
  <properties>
   <property name="actionAndDirection" value="attack_UP"/>
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
   <property name="actionAndDirection" value="attack_UP_RIGHT"/>
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
   <property name="actionAndDirection" value="attack_RIGHT"/>
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
   <property name="actionAndDirection" value="attack_DOWN_RIGHT"/>
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
   <property name="actionAndDirection" value="attack_DOWN"/>
  </properties>
  <animation>
   <frame tileid="29" duration="100"/>
   <frame tileid="34" duration="100"/>
   <frame tileid="39" duration="100"/>
   <frame tileid="44" duration="100"/>
   <frame tileid="49" duration="100"/>
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
