<?xml version="1.0" encoding="UTF-8"?>
<tileset name="unit16_diablo" tilewidth="512" tileheight="512" tilecount="1" columns="1">
 <properties>
  <property name="bounty" value="20"/>
  <property name="factionName" value="Orcs_Faction"/>
  <property name="healthPoints" value="400"/>
  <property name="name" value="Diablo"/>
  <property name="speed" value="0.4"/>
  <property name="type" value="unit"/>
 </properties>
 <image source="../../textures/diablo.png" trans="ff00ff" width="512" height="512"/>
 <terraintypes>
  <terrain name="idle_UP" tile="0"/>
  <terrain name="idle_UP_RIGHT" tile="1"/>
  <terrain name="idle_RIGHT" tile="2"/>
  <terrain name="idle_DOWN_RIGHT" tile="3"/>
  <terrain name="idle_DOWN" tile="4"/>
  <terrain name="walk1_UP" tile="10"/>
  <terrain name="walk2_UP" tile="5"/>
  <terrain name="walk_UP" tile="10"/>
  <terrain name="walk4_UP" tile="20"/>
  <terrain name="walk5_UP" tile="15"/>
  <terrain name="walk6_UP" tile="20"/>
  <terrain name="walk1_UP_RIGHT" tile="11"/>
  <terrain name="walk2_UP_RIGHT" tile="6"/>
  <terrain name="walk_UP_RIGHT" tile="11"/>
  <terrain name="walk4_UP_RIGHT" tile="21"/>
  <terrain name="walk5_UP_RIGHT" tile="16"/>
  <terrain name="walk6_UP_RIGHT" tile="21"/>
  <terrain name="walk1_RIGHT" tile="12"/>
  <terrain name="walk2_RIGHT" tile="7"/>
  <terrain name="walk_RIGHT" tile="12"/>
  <terrain name="walk4_RIGHT" tile="22"/>
  <terrain name="walk5_RIGHT" tile="17"/>
  <terrain name="walk6_RIGHT" tile="22"/>
  <terrain name="walk1_DOWN_RIGHT" tile="13"/>
  <terrain name="walk2_DOWN_RIGHT" tile="8"/>
  <terrain name="walk_DOWN_RIGHT" tile="13"/>
  <terrain name="walk4_DOWN_RIGHT" tile="23"/>
  <terrain name="walk5_DOWN_RIGHT" tile="18"/>
  <terrain name="walk6_DOWN_RIGHT" tile="23"/>
  <terrain name="walk1_DOWN" tile="14"/>
  <terrain name="walk2_DOWN" tile="9"/>
  <terrain name="walk_DOWN" tile="14"/>
  <terrain name="walk4_DOWN" tile="24"/>
  <terrain name="walk5_DOWN" tile="19"/>
  <terrain name="walk6_DOWN" tile="24"/>
  <terrain name="attack1_UP" tile="25"/>
  <terrain name="attack2_UP" tile="30"/>
  <terrain name="attack3_UP" tile="35"/>
  <terrain name="attack4_UP" tile="40"/>
  <terrain name="attack1_UP_RIGHT" tile="26"/>
  <terrain name="attack2_UP_RIGHT" tile="31"/>
  <terrain name="attack3_UP_RIGHT" tile="36"/>
  <terrain name="attack4_UP_RIGHT" tile="41"/>
  <terrain name="attack1_RIGHT" tile="27"/>
  <terrain name="attack2_RIGHT" tile="32"/>
  <terrain name="attack3_RIGHT" tile="37"/>
  <terrain name="attack4_RIGHT" tile="42"/>
  <terrain name="attack1_DOWN_RIGHT" tile="28"/>
  <terrain name="attack2_DOWN_RIGHT" tile="33"/>
  <terrain name="attack3_DOWN_RIGHT" tile="38"/>
  <terrain name="attack4_DOWN_RIGHT" tile="43"/>
  <terrain name="attack1_DOWN" tile="29"/>
  <terrain name="attack2_DOWN" tile="34"/>
  <terrain name="attack3_DOWN" tile="39"/>
  <terrain name="attack4_DOWN" tile="44"/>
  <terrain name="death_UP" tile="45"/>
  <terrain name="death2_UP" tile="50"/>
  <terrain name="death3_UP" tile="55"/>
  <terrain name="death_UP_RIGHT" tile="46"/>
  <terrain name="death2_UP_RIGHT" tile="51"/>
  <terrain name="death3_UP_RIGHT" tile="56"/>
  <terrain name="death_RIGHT" tile="47"/>
  <terrain name="death2_RIGHT" tile="52"/>
  <terrain name="death3_RIGHT" tile="57"/>
  <terrain name="death_DOWN_RIGHT" tile="48"/>
  <terrain name="death2_DOWN_RIGHT" tile="53"/>
  <terrain name="death3_DOWN_RIGHT" tile="58"/>
  <terrain name="death_DOWN" tile="49"/>
  <terrain name="death2_DOWN" tile="54"/>
  <terrain name="death3_DOWN" tile="59"/>
 </terraintypes>
 <tile id="0">
  <properties>
   <property name="actionAndDirection" value="idle_UP"/>
  </properties>
  <animation>
   <frame tileid="0" duration="100"/>
   <frame tileid="0" duration="100"/>
   <frame tileid="0" duration="100"/>
   <frame tileid="0" duration="100"/>
   <frame tileid="0" duration="100"/>
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
   <frame tileid="65" duration="100"/>
   <frame tileid="60" duration="100"/>
   <frame tileid="70" duration="100"/>
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
   <frame tileid="66" duration="100"/>
   <frame tileid="61" duration="100"/>
   <frame tileid="71" duration="100"/>
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
   <frame tileid="67" duration="100"/>
   <frame tileid="62" duration="100"/>
   <frame tileid="72" duration="100"/>
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
   <frame tileid="68" duration="100"/>
   <frame tileid="63" duration="100"/>
   <frame tileid="73" duration="100"/>
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
   <frame tileid="69" duration="100"/>
   <frame tileid="64" duration="100"/>
   <frame tileid="74" duration="100"/>
  </animation>
 </tile>
 <tile id="50">
  <properties>
   <property name="actionAndDirection" value="death2_UP"/>
  </properties>
 </tile>
 <tile id="51">
  <properties>
   <property name="actionAndDirection" value="death2_UP_RIGHT"/>
  </properties>
 </tile>
 <tile id="52">
  <properties>
   <property name="actionAndDirection" value="death2_RIGHT"/>
  </properties>
 </tile>
 <tile id="53">
  <properties>
   <property name="actionAndDirection" value="death2_DOWN_RIGHT"/>
  </properties>
 </tile>
 <tile id="54">
  <properties>
   <property name="actionAndDirection" value="death2_DOWN"/>
  </properties>
 </tile>
 <tile id="55">
  <properties>
   <property name="actionAndDirection" value="death3_UP"/>
  </properties>
 </tile>
 <tile id="56">
  <properties>
   <property name="actionAndDirection" value="death3_UP_RIGHT"/>
  </properties>
 </tile>
 <tile id="57">
  <properties>
   <property name="actionAndDirection" value="death3_RIGHT"/>
  </properties>
 </tile>
 <tile id="58">
  <properties>
   <property name="actionAndDirection" value="death3_DOWN_RIGHT"/>
  </properties>
 </tile>
 <tile id="59">
  <properties>
   <property name="actionAndDirection" value="death3_DOWN"/>
  </properties>
 </tile>
</tileset>
