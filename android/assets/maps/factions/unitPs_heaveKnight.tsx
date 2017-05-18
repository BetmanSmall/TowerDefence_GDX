<?xml version="1.0" encoding="UTF-8"?>
<tileset name="unitPs_heaveKnight" tilewidth="22" tileheight="51" tilecount="60" columns="12">
 <properties>
  <property name="factionName" value="Orcs_Faction"/>
  <property name="name" value="Heave Knight"/>
  <property name="healthPoints" value="500"/>
  <property name="bounty" value="40"/>
  <property name="cost" value="40"/>
  <property name="speed" value="0.8"/>
  <property name="type" value="unit"/>
 </properties>
 <image source="../textures/people.png" width="264" height="255"/>
 <tile id="51">
  <properties>
   <property name="actionAndDirection" value="walk_UP"/>
  </properties>
  <animation>
   <frame tileid="51" duration="100"/>
   <frame tileid="52" duration="100"/>
   <frame tileid="53" duration="100"/>
   <frame tileid="54" duration="100"/>
   <frame tileid="51" duration="100"/>
   <frame tileid="52" duration="100"/>
   <frame tileid="53" duration="100"/>
   <frame tileid="54" duration="100"/>
  </animation>
 </tile>
 <tile id="55">
  <properties>
   <property name="actionAndDirection" value="walk_DOWN"/>
  </properties>
  <animation>
   <frame tileid="55" duration="100"/>
   <frame tileid="56" duration="100"/>
   <frame tileid="57" duration="100"/>
   <frame tileid="58" duration="100"/>
   <frame tileid="55" duration="100"/>
   <frame tileid="56" duration="100"/>
   <frame tileid="57" duration="100"/>
   <frame tileid="58" duration="100"/>
  </animation>
 </tile>
</tileset>
