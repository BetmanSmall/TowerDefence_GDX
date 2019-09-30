<?xml version="1.0" encoding="UTF-8"?>
<tileset name="unit14_mage" tilewidth="72" tileheight="72" tilecount="80" columns="5">
 <properties>
  <property name="factionName" value="Humans_Faction"/>
  <property name="name" value="Mage"/>
  <property name="healthPoints" value="600"/>
  <property name="bounty" value="18"/>
  <property name="cost" value="32"/>
  <property name="speed" value="0.65"/>
  <property name="type" value="healer"/>
  <property name="attackType" value="Range"/>
  <property name="attackType_damage" value="10"/>
  <property name="attackType_range" value="160"/>
  <property name="attackType_attackSpeed" value="0.5"/>
  <property name="attackType_reload" value="0.6"/>
  <property name="attackType_stackInOneCell" value="true"/>
  <property name="attackType_stayToDie" value="false"/>
 </properties>
 <image source="../../textures/warcraft2/human/units/mage.png" trans="ff00ff" width="360" height="1152"/>
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
  </animation>
 </tile>
 <tile id="45">
  <properties>
   <property name="actionAndDirection" value="death_UP"/>
  </properties>
  <animation>
   <frame tileid="45" duration="100"/>
   <frame tileid="50" duration="100"/>
   <frame tileid="60" duration="100"/>
   <frame tileid="65" duration="100"/>
   <frame tileid="70" duration="100"/>
   <frame tileid="75" duration="100"/>
  </animation>
 </tile>
 <tile id="46">
  <properties>
   <property name="actionAndDirection" value="death_UP_RIGHT"/>
  </properties>
  <animation>
   <frame tileid="46" duration="100"/>
   <frame tileid="51" duration="100"/>
   <frame tileid="61" duration="100"/>
   <frame tileid="66" duration="100"/>
   <frame tileid="71" duration="100"/>
   <frame tileid="76" duration="100"/>
  </animation>
 </tile>
 <tile id="47">
  <properties>
   <property name="actionAndDirection" value="death_RIGHT"/>
  </properties>
  <animation>
   <frame tileid="47" duration="100"/>
   <frame tileid="52" duration="100"/>
   <frame tileid="62" duration="100"/>
   <frame tileid="67" duration="100"/>
   <frame tileid="72" duration="100"/>
   <frame tileid="77" duration="100"/>
  </animation>
 </tile>
 <tile id="48">
  <properties>
   <property name="actionAndDirection" value="death_DOWN_RIGHT"/>
  </properties>
  <animation>
   <frame tileid="48" duration="100"/>
   <frame tileid="53" duration="100"/>
   <frame tileid="63" duration="100"/>
   <frame tileid="68" duration="100"/>
   <frame tileid="73" duration="100"/>
   <frame tileid="78" duration="100"/>
  </animation>
 </tile>
 <tile id="49">
  <properties>
   <property name="actionAndDirection" value="death_DOWN"/>
  </properties>
  <animation>
   <frame tileid="49" duration="100"/>
   <frame tileid="54" duration="100"/>
   <frame tileid="64" duration="100"/>
   <frame tileid="69" duration="100"/>
   <frame tileid="74" duration="100"/>
   <frame tileid="79" duration="100"/>
  </animation>
 </tile>
</tileset>
