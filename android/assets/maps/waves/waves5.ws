<?xml version="1.0" encoding="UTF-8"?>
<waves>
	<!-- delay - время между последним крипом из предыдущей пачки крипов и нашима первым + spawnInterval + interval -->
	<!-- а интервал - время между крипами одного типа + spawnInterval -->
	<!-- а spawnInterval - дополнительное или же одно единственное время между всеми крипами в этой волне. WaveInterval | to be continue... -->
	<wave spawntPointX="29" spawntPointY="34" exitPointX="1" exitPointY="32" spawnInterval="0.5" startToMove="0">
	    <unit templateName="unit1_grunt" amount="2" delay="1" interval="1"/>
	    <unit templateName="unit2_troll_axethrower" amount="3"/>
	    <unit templateName="unit3_footman" amount="2"/>
	    <unit templateName="unit4_catapult" amount="3"/>
	    <unit templateName="unit5_daemon" amount="4"/>
	    <unit templateName="unit6_death_knight" amount="3"/>
	    <unit templateName="unit7_dragon" amount="2"/>
	    <unit templateName="unit8_ogre" amount="3"/>
	    <unit templateName="unit9_peon" amount="4"/>
	    <unit templateName="unit10_ballista" amount="5"/>
	    <unit templateName="unit11_elven_archer" amount="4"/>
	    <unit templateName="unit12_gryphon_rider" amount="3"/>
	    <unit templateName="unit13_knight" amount="2"/>
	    <unit templateName="unit14_mage" amount="3"/>
	    <unit templateName="unit15_peasant" amount="4"/>
	</wave>
	<wave spawntPointX="29" spawntPointY="29" exitPointX="1" exitPointY="31" spawnInterval="0.5" startToMove="0">
		<unit delay="1" templateName="unit1_grunt" interval="0.1" amount="3"/>
		<unit delay="2" templateName="unit2_troll_axethrower" interval="0.2" amount="2"/>
		<unit delay="3" templateName="unit4_catapult" interval="0.3" amount="1"/>
		<unit delay="1" templateName="unit5_daemon" interval="0.4" amount="2"/>
		<unit delay="2" templateName="unit6_death_knight" interval="0.5" amount="3"/>
		<unit delay="3" templateName="unit7_dragon" interval="0.6" amount="4"/>
		<unit delay="1" templateName="unit8_ogre" interval="0.7" amount="3"/>
		<unit delay="2" templateName="unit9_peon" interval="0.8" amount="2"/>
		<unit delay="3" templateName="unit3_footman" interval="0.9" amount="1"/>
		<unit delay="4" templateName="unit10_ballista" interval="1" amount="2"/>
		<unit delay="1" templateName="unit11_elven_archer" interval="2" amount="3"/>
		<unit delay="2" templateName="unit12_gryphon_rider" interval="3" amount="4"/>
		<unit delay="3" templateName="unit13_knight" interval="4" amount="5"/>
		<unit delay="4" templateName="unit14_mage" interval="1" amount="1"/>
		<unit delay="1" templateName="unit15_peasant" interval="2" amount="01"/>
		<unit delay="2" templateName="unit15_peasant" interval="3" amount="02"/>
		<unit delay="3" templateName="unit4_catapult" interval="0" amount="03"/>
	</wave>
	<!-- <wave spawntPointX="34" spawntPointY="33" exitPointX="62" exitPointY="32" spawnInterval="0.5" startToMove="0"> -->
		<!-- <unit delay="1" templateName="unit1_grunt" interval="0.1" amount="3"/> -->
	<!-- </wave> -->
	<!-- <wave spawntPointX="34" spawntPointY="29" exitPointX="62" exitPointY="31" spawnInterval="0.5" startToMove="0"> -->
		<!-- <unit delay="1" templateName="unit1_grunt" interval="0.1" amount="3"/> -->
	<!-- </wave> -->
</waves>
