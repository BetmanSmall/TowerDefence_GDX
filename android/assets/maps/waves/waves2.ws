<?xml version="1.0" encoding="UTF-8"?>
<waves>
    <!-- delay - время между последним крипом из предыдущей пачки крипов и нашима первым + spawnInterval + interval -->
    <!-- а интервал - время между крипами одного типа + spawnInterval -->
    <!-- а spawnInterval - дополнительное или же одно единственное время между всеми крипами в этой волне. -->
    <!-- startToMove - ну тут все понятно. столько пройдет времени после нажатия пользователем кнопки пуска крипов -->
    <wave spawnPointX="3" spawnPointY="124" exitPointX="120" exitPointY="7" spawnInterval="0" startToMove="0">
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
</waves>
