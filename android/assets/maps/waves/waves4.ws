<?xml version="1.0" encoding="UTF-8"?>
<waves>
    <!-- delay - время между последним крипом из предыдущей пачки крипов и нашима первым + spawnInterval + interval -->
    <!-- а интервал - время между крипами одного типа + spawnInterval -->
    <!-- а spawnInterval - дополнительное или же одно единственное время между всеми крипами в этой волне. -->
    <!-- startToMove - ну тут все понятно. столько пройдет времени после нажатия пользователем кнопки пуска крипов -->
    <wave spawnPointX="4" spawnPointY="5" exitPointX="123" exitPointY="124" spawnInterval="0.5" startToMove="0">
        <unit delay="1" templateName="unit10_ballista" interval="1" amount="2"/>
        <unit delay="2" templateName="unit2_troll_axethrower" interval="2" amount="2"/>
        <unit delay="1" templateName="unit1_grunt" interval="3" amount="2"/>
        <unit delay="0.8" templateName="unit2_troll_axethrower" interval="2" amount="2"/>
    </wave>
    <wave spawnPointX="123" spawnPointY="124" exitPointX="4" exitPointY="5" spawnInterval="1.5" startToMove="0">
        <unit delay="1" templateName="unit1_grunt" amount="1"/>
        <unit delay="2" templateName="unit2_troll_axethrower" amount="1"/>
        <unit delay="1" templateName="unit1_grunt" amount="2"/>
        <unit delay="0" templateName="unit13_knight" amount="1"/>
        <unit delay="1" templateName="unit13_knight" amount="1"/>
        <unit delay="2" templateName="unit13_knight" amount="1"/>
    </wave>
    <wave spawnPointX="122" spawnPointY="5" exitPointX="3" exitPointY="124" spawnInterval="0" startToMove="0">
        <unit templateName="unit5_daemon" amount="5"/>
        <unit templateName="unit2_troll_axethrower" amount="1"/>
        <unit templateName="unit1_grunt" amount="2"/>
        <unit templateName="unit12_gryphon_rider" amount="2"/>
    </wave>
    <wave spawnPointX="3" spawnPointY="124" exitPointX="120" exitPointY="6" spawnInterval="5" startToMove="0">
        <unit templateName="unit1_grunt" interval="0" amount="3"/>
        <unit delay="0" templateName="unit2_troll_axethrower" interval="0" amount="2"/>
        <unit delay="0" templateName="unit4_catapult" interval="0" amount="1"/>
        <unit delay="0" templateName="unit5_daemon" interval="0" amount="2"/>
        <unit delay="0" templateName="unit6_death_knight" interval="0" amount="1"/>
        <unit delay="0" templateName="unit7_dragon" interval="0" amount="3"/>
        <unit delay="0" interval="0" templateName="unit8_ogre" amount="0"/>
        <unit delay="1" interval="1" templateName="unit9_peon" amount="2"/>
        <unit delay="1" interval="0" templateName="unit3_footman" amount="1"/>
        <unit delay="1" interval="0" templateName="unit10_ballista" amount="0"/>
        <unit delay="1" interval="2" templateName="unit11_elven_archer" amount="1"/>
        <unit delay="1" interval="0" templateName="unit12_gryphon_rider" amount="2"/>
        <unit delay="1" interval="3" templateName="unit13_knight" amount="1"/>
        <unit delay="1" interval="2" templateName="unit14_mage" amount="1"/>
        <unit delay="1" interval="1" templateName="unit15_peasant" amount="01"/>
        <unit delay="1" interval="0" templateName="unit15_peasant" amount="2"/>
        <unit delay="5" interval="5" templateName="unit4_catapult" amount="2"/>
    </wave>
    <wave spawnPointX="3" spawnPointY="124" exitPointX="123" exitPointY="124" spawnInterval="0.5" startToMove="0">
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
</waves>
