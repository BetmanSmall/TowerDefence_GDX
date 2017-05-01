<?xml version="1.0" encoding="UTF-8"?>
<tileset name="unit13_knight" tilewidth="72" tileheight="72" tilecount="70" columns="5">
 <properties>
  <property name="factionName" value="Humans_Faction"/>
  <property name="name" value="Knight"/>
  <property name="healthPoints" value="350"/>
  <property name="bounty" value="5"/>
  <property name="cost" value="12"/>
  <property name="speed" value="0.4"/>
  <property name="type" value="unit"/>
 </properties>
 <image source="../../textures/warcraft2/human/units/knight.png" trans="ff00ff" width="360" height="1008"/>
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
   <property name="actionAndDirection" value="walk2_UP"/>
  </properties>
 </tile>
 <tile id="6">
  <properties>
   <property name="actionAndDirection" value="walk2_UP_RIGHT"/>
  </properties>
 </tile>
 <tile id="7">
  <properties>
   <property name="actionAndDirection" value="walk2_RIGHT"/>
  </properties>
 </tile>
 <tile id="8">
  <properties>
   <property name="actionAndDirection" value="walk2_DOWN_RIGHT"/>
  </properties>
 </tile>
 <tile id="9">
  <properties>
   <property name="actionAndDirection" value="walk2_DOWN"/>
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
 <tile id="15">
  <properties>
   <property name="actionAndDirection" value="walk5_UP"/>
  </properties>
 </tile>
 <tile id="16">
  <properties>
   <property name="actionAndDirection" value="walk5_UP_RIGHT"/>
  </properties>
 </tile>
 <tile id="17">
  <properties>
   <property name="actionAndDirection" value="walk5_RIGHT"/>
  </properties>
 </tile>
 <tile id="18">
  <properties>
   <property name="actionAndDirection" value="walk5_DOWN_RIGHT"/>
  </properties>
 </tile>
 <tile id="19">
  <properties>
   <property name="actionAndDirection" value="walk5_DOWN"/>
  </properties>
 </tile>
 <tile id="20">
  <properties>
   <property name="actionAndDirection" value="walk6_UP"/>
  </properties>
 </tile>
 <tile id="21">
  <properties>
   <property name="actionAndDirection" value="walk6_UP_RIGHT"/>
  </properties>
 </tile>
 <tile id="22">
  <properties>
   <property name="actionAndDirection" value="walk6_RIGHT"/>
  </properties>
 </tile>
 <tile id="23">
  <properties>
   <property name="actionAndDirection" value="walk6_DOWN_RIGHT"/>
  </properties>
 </tile>
 <tile id="24">
  <properties>
   <property name="actionAndDirection" value="walk6_DOWN"/>
  </properties>
 </tile>
 <tile id="25">
  <properties>
   <property name="actionAndDirection" value="attack1_UP"/>
  </properties>
 </tile>
 <tile id="26">
  <properties>
   <property name="actionAndDirection" value="attack1_UP_RIGHT"/>
  </properties>
 </tile>
 <tile id="27">
  <properties>
   <property name="actionAndDirection" value="attack1_RIGHT"/>
  </properties>
 </tile>
 <tile id="28">
  <properties>
   <property name="actionAndDirection" value="attack1_DOWN_RIGHT"/>
  </properties>
 </tile>
 <tile id="29">
  <properties>
   <property name="actionAndDirection" value="attack1_DOWN"/>
  </properties>
 </tile>
 <tile id="30">
  <properties>
   <property name="actionAndDirection" value="attack2_UP"/>
  </properties>
 </tile>
 <tile id="31">
  <properties>
   <property name="actionAndDirection" value="attack2_UP_RIGHT"/>
  </properties>
 </tile>
 <tile id="32">
  <properties>
   <property name="actionAndDirection" value="attack2_RIGHT"/>
  </properties>
 </tile>
 <tile id="33">
  <properties>
   <property name="actionAndDirection" value="attack2_DOWN_RIGHT"/>
  </properties>
 </tile>
 <tile id="34">
  <properties>
   <property name="actionAndDirection" value="attack2_DOWN"/>
  </properties>
 </tile>
 <tile id="35">
  <properties>
   <property name="actionAndDirection" value="attack3_UP"/>
  </properties>
 </tile>
 <tile id="36">
  <properties>
   <property name="actionAndDirection" value="attack3_UP_RIGHT"/>
  </properties>
 </tile>
 <tile id="37">
  <properties>
   <property name="actionAndDirection" value="attack3_RIGHT"/>
  </properties>
 </tile>
 <tile id="38">
  <properties>
   <property name="actionAndDirection" value="attack3_DOWN_RIGHT"/>
  </properties>
 </tile>
 <tile id="39">
  <properties>
   <property name="actionAndDirection" value="attack3_DOWN"/>
  </properties>
 </tile>
 <tile id="40">
  <properties>
   <property name="actionAndDirection" value="attack4_UP"/>
  </properties>
 </tile>
 <tile id="41">
  <properties>
   <property name="actionAndDirection" value="attack4_UP_RIGHT"/>
  </properties>
 </tile>
 <tile id="42">
  <properties>
   <property name="actionAndDirection" value="attack4_RIGHT"/>
  </properties>
 </tile>
 <tile id="43">
  <properties>
   <property name="actionAndDirection" value="attack4_DOWN_RIGHT"/>
  </properties>
 </tile>
 <tile id="44">
  <properties>
   <property name="actionAndDirection" value="attack4_DOWN"/>
  </properties>
 </tile>
 <tile id="45">
  <properties>
   <property name="actionAndDirection" value="death_UP"/>
  </properties>
  <animation>
   <frame tileid="45" duration="100"/>
   <frame tileid="50" duration="100"/>
   <frame tileid="55" duration="100"/>
   <frame tileid="60" duration="100"/>
   <frame tileid="65" duration="100"/>
  </animation>
 </tile>
 <tile id="46">
  <properties>
   <property name="actionAndDirection" value="death_UP_RIGHT"/>
  </properties>
  <animation>
   <frame tileid="46" duration="100"/>
   <frame tileid="51" duration="100"/>
   <frame tileid="56" duration="100"/>
   <frame tileid="61" duration="100"/>
   <frame tileid="66" duration="100"/>
  </animation>
 </tile>
 <tile id="47">
  <properties>
   <property name="actionAndDirection" value="death_RIGHT"/>
  </properties>
  <animation>
   <frame tileid="47" duration="100"/>
   <frame tileid="52" duration="100"/>
   <frame tileid="57" duration="100"/>
   <frame tileid="62" duration="100"/>
   <frame tileid="67" duration="100"/>
  </animation>
 </tile>
 <tile id="48">
  <properties>
   <property name="actionAndDirection" value="death_DOWN_RIGHT"/>
  </properties>
  <animation>
   <frame tileid="48" duration="100"/>
   <frame tileid="53" duration="100"/>
   <frame tileid="58" duration="100"/>
   <frame tileid="63" duration="100"/>
   <frame tileid="68" duration="100"/>
  </animation>
 </tile>
 <tile id="49">
  <properties>
   <property name="actionAndDirection" value="death_DOWN"/>
  </properties>
  <animation>
   <frame tileid="49" duration="100"/>
   <frame tileid="54" duration="100"/>
   <frame tileid="59" duration="100"/>
   <frame tileid="64" duration="100"/>
   <frame tileid="69" duration="100"/>
  </animation>
 </tile>
</tileset>
