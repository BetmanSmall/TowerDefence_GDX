<?xml version="1.0" encoding="UTF-8"?>
<tileset name="unit2_troll_axethrower" tilewidth="72" tileheight="72">
 <properties>
  <property name="faction_name" value="Faction1"/>
  <property name="health_point" value="90"/>
  <property name="name" value="Troll Axethrower"/>
  <property name="type" value="unit"/>
 </properties>
 <image source="../textures/warcraft2/orc/units/troll_axethrower.png" trans="ff00ff" width="360" height="864"/>
 <terraintypes>
  <terrain name="idle_up" tile="0"/>
  <terrain name="idle_up_right" tile="1"/>
  <terrain name="idle_right" tile="2"/>
  <terrain name="idle_down_right" tile="3"/>
  <terrain name="idle_down" tile="4"/>
  <terrain name="walk1_up" tile="10"/>
  <terrain name="walk2_up" tile="5"/>
  <terrain name="walk3_up" tile="10"/>
  <terrain name="walk4_up" tile="20"/>
  <terrain name="walk5_up" tile="15"/>
  <terrain name="walk6_up" tile="20"/>
  <terrain name="walk1_up_right" tile="11"/>
  <terrain name="walk2_up_right" tile="6"/>
  <terrain name="walk3_up_right" tile="11"/>
  <terrain name="walk4_up_right" tile="21"/>
  <terrain name="walk5_up_right" tile="16"/>
  <terrain name="walk6_up_right" tile="21"/>
  <terrain name="walk1_right" tile="12"/>
  <terrain name="walk2_right" tile="7"/>
  <terrain name="walk3_right" tile="12"/>
  <terrain name="walk4_right" tile="22"/>
  <terrain name="walk5_right" tile="17"/>
  <terrain name="walk6_right" tile="22"/>
  <terrain name="walk1_down_right" tile="13"/>
  <terrain name="walk2_down_right" tile="8"/>
  <terrain name="walk3_down_right" tile="13"/>
  <terrain name="walk4_down_right" tile="23"/>
  <terrain name="walk5_down_right" tile="18"/>
  <terrain name="walk6_down_right" tile="23"/>
  <terrain name="walk1_down" tile="14"/>
  <terrain name="walk2_down" tile="9"/>
  <terrain name="walk3_down" tile="14"/>
  <terrain name="walk4_down" tile="24"/>
  <terrain name="walk5_down" tile="19"/>
  <terrain name="walk6_down" tile="24"/>
  <terrain name="attack1_up" tile="25"/>
  <terrain name="attack2_up" tile="30"/>
  <terrain name="attack3_up" tile="35"/>
  <terrain name="attack4_up" tile="40"/>
  <terrain name="attack1_up_right" tile="26"/>
  <terrain name="attack2_up_right" tile="31"/>
  <terrain name="attack3_up_right" tile="36"/>
  <terrain name="attack4_up_right" tile="41"/>
  <terrain name="attack1_right" tile="27"/>
  <terrain name="attack2_right" tile="32"/>
  <terrain name="attack3_right" tile="37"/>
  <terrain name="attack4_right" tile="42"/>
  <terrain name="attack1_down_right" tile="28"/>
  <terrain name="attack2_down_right" tile="33"/>
  <terrain name="attack3_down_right" tile="38"/>
  <terrain name="attack4_down_right" tile="43"/>
  <terrain name="attack1_down" tile="29"/>
  <terrain name="attack2_down" tile="34"/>
  <terrain name="attack3_down" tile="39"/>
  <terrain name="attack4_down" tile="44"/>
  <terrain name="death1_up" tile="45"/>
  <terrain name="death2_up" tile="50"/>
  <terrain name="death3_up" tile="55"/>
  <terrain name="death1_up_right" tile="46"/>
  <terrain name="death2_up_right" tile="51"/>
  <terrain name="death3_up_right" tile="56"/>
  <terrain name="death1_right" tile="47"/>
  <terrain name="death2_right" tile="52"/>
  <terrain name="death3_right" tile="57"/>
  <terrain name="death1_down_right" tile="48"/>
  <terrain name="death2_down_right" tile="53"/>
  <terrain name="death3_down_right" tile="58"/>
  <terrain name="death1_down" tile="49"/>
  <terrain name="death2_down" tile="54"/>
  <terrain name="death3_down" tile="59"/>
 </terraintypes>
 <tile id="0">
  <properties>
   <property name="actionAndDirection" value="idle_up"/>
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
   <property name="actionAndDirection" value="idle_up_right"/>
  </properties>
 </tile>
 <tile id="2">
  <properties>
   <property name="actionAndDirection" value="idle_right"/>
  </properties>
 </tile>
 <tile id="3">
  <properties>
   <property name="actionAndDirection" value="idle_down_right"/>
  </properties>
 </tile>
 <tile id="4">
  <properties>
   <property name="actionAndDirection" value="idle_down"/>
  </properties>
 </tile>
 <tile id="5">
  <properties>
   <property name="actionAndDirection" value="walk2_up"/>
  </properties>
 </tile>
 <tile id="6">
  <properties>
   <property name="actionAndDirection" value="walk2_up_right"/>
  </properties>
 </tile>
 <tile id="7">
  <properties>
   <property name="actionAndDirection" value="walk2_right"/>
  </properties>
 </tile>
 <tile id="8">
  <properties>
   <property name="actionAndDirection" value="walk2_down_right"/>
  </properties>
 </tile>
 <tile id="9">
  <properties>
   <property name="actionAndDirection" value="walk2_down"/>
  </properties>
 </tile>
 <tile id="10">
  <properties>
   <property name="actionAndDirection" value="walk3_up"/>
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
   <property name="actionAndDirection" value="walk3_up_right"/>
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
   <property name="actionAndDirection" value="walk3_right"/>
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
   <property name="actionAndDirection" value="walk3_down_right"/>
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
   <property name="actionAndDirection" value="walk3_down"/>
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
   <property name="actionAndDirection" value="walk5_up"/>
  </properties>
 </tile>
 <tile id="16">
  <properties>
   <property name="actionAndDirection" value="walk5_up_right"/>
  </properties>
 </tile>
 <tile id="17">
  <properties>
   <property name="actionAndDirection" value="walk5_right"/>
  </properties>
 </tile>
 <tile id="18">
  <properties>
   <property name="actionAndDirection" value="walk5_down_right"/>
  </properties>
 </tile>
 <tile id="19">
  <properties>
   <property name="actionAndDirection" value="walk5_down"/>
  </properties>
 </tile>
 <tile id="20">
  <properties>
   <property name="actionAndDirection" value="walk6_up"/>
  </properties>
 </tile>
 <tile id="21">
  <properties>
   <property name="actionAndDirection" value="walk6_up_right"/>
  </properties>
 </tile>
 <tile id="22">
  <properties>
   <property name="actionAndDirection" value="walk6_right"/>
  </properties>
 </tile>
 <tile id="23">
  <properties>
   <property name="actionAndDirection" value="walk6_down_right"/>
  </properties>
 </tile>
 <tile id="24">
  <properties>
   <property name="actionAndDirection" value="walk6_down"/>
  </properties>
 </tile>
 <tile id="25">
  <properties>
   <property name="actionAndDirection" value="attack1_up"/>
  </properties>
 </tile>
 <tile id="26">
  <properties>
   <property name="actionAndDirection" value="attack1_up_right"/>
  </properties>
 </tile>
 <tile id="27">
  <properties>
   <property name="actionAndDirection" value="attack1_right"/>
  </properties>
 </tile>
 <tile id="28">
  <properties>
   <property name="actionAndDirection" value="attack1_down_right"/>
  </properties>
 </tile>
 <tile id="29">
  <properties>
   <property name="actionAndDirection" value="attack1_down"/>
  </properties>
 </tile>
 <tile id="30">
  <properties>
   <property name="actionAndDirection" value="attack2_up"/>
  </properties>
 </tile>
 <tile id="31">
  <properties>
   <property name="actionAndDirection" value="attack2_up_right"/>
  </properties>
 </tile>
 <tile id="32">
  <properties>
   <property name="actionAndDirection" value="attack2_right"/>
  </properties>
 </tile>
 <tile id="33">
  <properties>
   <property name="actionAndDirection" value="attack2_down_right"/>
  </properties>
 </tile>
 <tile id="34">
  <properties>
   <property name="actionAndDirection" value="attack2_down"/>
  </properties>
 </tile>
 <tile id="35">
  <properties>
   <property name="actionAndDirection" value="attack3_up"/>
  </properties>
 </tile>
 <tile id="36">
  <properties>
   <property name="actionAndDirection" value="attack3_up_right"/>
  </properties>
 </tile>
 <tile id="37">
  <properties>
   <property name="actionAndDirection" value="attack3_right"/>
  </properties>
 </tile>
 <tile id="38">
  <properties>
   <property name="actionAndDirection" value="attack3_down_right"/>
  </properties>
 </tile>
 <tile id="39">
  <properties>
   <property name="actionAndDirection" value="attack3_down"/>
  </properties>
 </tile>
 <tile id="40">
  <properties>
   <property name="actionAndDirection" value="attack4_up"/>
  </properties>
 </tile>
 <tile id="41">
  <properties>
   <property name="actionAndDirection" value="attack4_up_right"/>
  </properties>
 </tile>
 <tile id="42">
  <properties>
   <property name="actionAndDirection" value="attack4_right"/>
  </properties>
 </tile>
 <tile id="43">
  <properties>
   <property name="actionAndDirection" value="attack4_down_right"/>
  </properties>
 </tile>
 <tile id="44">
  <properties>
   <property name="actionAndDirection" value="attack4_down"/>
  </properties>
 </tile>
 <tile id="45">
  <properties>
   <property name="actionAndDirection" value="death1_up"/>
  </properties>
  <animation>
   <frame tileid="45" duration="100"/>
   <frame tileid="50" duration="100"/>
   <frame tileid="55" duration="100"/>
  </animation>
 </tile>
 <tile id="46">
  <properties>
   <property name="actionAndDirection" value="death1_up_right"/>
  </properties>
  <animation>
   <frame tileid="46" duration="100"/>
   <frame tileid="51" duration="100"/>
   <frame tileid="56" duration="100"/>
  </animation>
 </tile>
 <tile id="47">
  <properties>
   <property name="actionAndDirection" value="death1_right"/>
  </properties>
  <animation>
   <frame tileid="47" duration="100"/>
   <frame tileid="52" duration="100"/>
   <frame tileid="57" duration="100"/>
  </animation>
 </tile>
 <tile id="48">
  <properties>
   <property name="actionAndDirection" value="death1_down_right"/>
  </properties>
  <animation>
   <frame tileid="48" duration="100"/>
   <frame tileid="53" duration="100"/>
   <frame tileid="58" duration="100"/>
  </animation>
 </tile>
 <tile id="49">
  <properties>
   <property name="actionAndDirection" value="death1_down"/>
  </properties>
  <animation>
   <frame tileid="49" duration="100"/>
   <frame tileid="54" duration="100"/>
   <frame tileid="59" duration="100"/>
  </animation>
 </tile>
 <tile id="50">
  <properties>
   <property name="actionAndDirection" value="death2_up"/>
  </properties>
 </tile>
 <tile id="51">
  <properties>
   <property name="actionAndDirection" value="death2_up_right"/>
  </properties>
 </tile>
 <tile id="52">
  <properties>
   <property name="actionAndDirection" value="death2_right"/>
  </properties>
 </tile>
 <tile id="53">
  <properties>
   <property name="actionAndDirection" value="death2_down_right"/>
  </properties>
 </tile>
 <tile id="54">
  <properties>
   <property name="actionAndDirection" value="death2_down"/>
  </properties>
 </tile>
 <tile id="55">
  <properties>
   <property name="actionAndDirection" value="death3_up"/>
  </properties>
 </tile>
 <tile id="56">
  <properties>
   <property name="actionAndDirection" value="death3_up_right"/>
  </properties>
 </tile>
 <tile id="57">
  <properties>
   <property name="actionAndDirection" value="death3_right"/>
  </properties>
 </tile>
 <tile id="58">
  <properties>
   <property name="actionAndDirection" value="death3_down_right"/>
  </properties>
 </tile>
 <tile id="59">
  <properties>
   <property name="actionAndDirection" value="death3_down"/>
  </properties>
 </tile>
</tileset>
