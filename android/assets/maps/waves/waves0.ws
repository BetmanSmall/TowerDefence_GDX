<?xml version="1.0" encoding="UTF-8"?>
<waves>
    <!-- delay - время между последним крипом из предыдущей пачки крипов и нашима первым + spawnInterval + interval -->
    <!-- а интервал - время между крипами одного типа + spawnInterval -->
    <!-- а spawnInterval - дополнительное или же одно единственное время между всеми крипами в этой волне. -->
    <!-- startToMove - ну тут все понятно. столько пройдет времени после нажатия пользователем кнопки пуска крипов -->
    <wave spawnPointX="15" spawnPointY="31" exitPointX="0" exitPointY="0" spawnInterval="0.7" startToMove="0">
        <unit templateName="unit3_footman" amount="40"/>
        <unit templateName="unit1_grunt" amount="15"/>
        <!-- <unit templateName="unit2_troll_axethrower" amount="1"/> -->
        <!-- <unit templateName="unit4_catapult" amount="1"/> -->
        <!-- <unit templateName="unit5_daemon" amount="1"/> -->
        <!-- <unit templateName="unit6_death_knight" amount="1"/> -->
        <!-- <unit templateName="unit7_dragon" amount="1"/> -->
        <!-- <unit templateName="unit8_ogre" amount="1"/> -->
        <!-- <unit templateName="unit9_peon" amount="1"/> -->
        <!-- <unit templateName="unit10_ballista" amount="1"/> -->
        <!-- <unit templateName="unit11_elven_archer" amount="1"/> -->
        <!-- <unit templateName="unit12_gryphon_rider" amount="1"/> -->
        <!-- <unit templateName="unit13_knight" amount="1"/> -->
        <!-- <unit templateName="unit14_mage" amount="1"/> -->
        <!-- <unit templateName="unit15_peasant" amount="1"/> -->
    </wave>
    <waveForUser spawnPointX="0" spawnPointY="31" exitPointX="15" exitPointY="0"/>
</waves>