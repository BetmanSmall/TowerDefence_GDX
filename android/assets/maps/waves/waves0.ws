<?xml version="1.0" encoding="UTF-8"?>
<waves allTogether="false"> <!-- allTogether=true - волны буду спавнить крипов после startToMove все вместе. allTogether=false - волны буду ждать предыдущию и нажатаю кнопку от пользователя и когда нету крипов на игровом поле -->
    <!-- delay - время между последним крипом из предыдущей пачки крипов и нашима первым + spawnInterval + interval -->
    <!-- а interval - время между крипами одного типа + spawnInterval -->
    <!-- а spawnInterval - дополнительное или же одно единственное время между всеми крипами в этой волне. -->
    <!-- startToMove - ну тут все понятно. столько пройдет времени после нажатия пользователем кнопки пуска крипов -->
    <!-- startToMove -> delay-->
    <wave spawnPointX="15" spawnPointY="31" exitPointX="0" exitPointY="0" spawnInterval="0.5" startToMove="0">
        <unit templateName="unit1_grunt" amount="1"/>
        <unit templateName="unit2_troll_axethrower" amount="1"/>
        <unit templateName="unit3_footman" amount="1"/>
        <unit templateName="unit4_catapult" amount="1"/>
        <unit templateName="unit5_daemon" amount="1"/>
        <unit templateName="unit6_death_knight" amount="1"/>
        <unit templateName="unit7_dragon" amount="1"/>
        <unit templateName="unit8_ogre" amount="1"/>
        <unit templateName="unit9_peon" amount="1"/>
        <unit templateName="unit10_ballista" amount="1"/>
        <unit templateName="unit11_elven_archer" amount="1"/>
        <unit templateName="unit12_gryphon_rider" amount="1"/>
        <unit templateName="unit13_knight" amount="1"/>
        <unit templateName="unit14_mage" amount="1"/>
        <unit templateName="unit15_peasant" amount="1"/>
    </wave>
    <wave spawnPointX="15" spawnPointY="31" exitPointX="0" exitPointY="0" spawnInterval="0.5" startToMove="10">
        <unit delay="7" templateName="unit8_ogre" amount="3"/>
        <unit templateName="unit2_troll_axethrower" amount="10"/>
        <unit templateName="unit6_death_knight" amount="10"/>
        <unit templateName="unit3_footman" amount="30"/>
        <unit templateName="unit1_grunt" amount="30"/>
        <!-- <unit delay="0" templateName="unit4_catapult" amount="1"/> -->
        <!-- <unit delay="0" templateName="unit5_daemon" amount="1"/> -->
        <!-- <unit templateName="unit7_dragon" amount="1"/> -->
        <!-- <unit templateName="unit9_peon" amount="1"/> -->
        <!-- <unit templateName="unit10_ballista" amount="1"/> -->
        <!-- <unit templateName="unit11_elven_archer" amount="1"/> -->
        <!-- <unit templateName="unit12_gryphon_rider" amount="1"/> -->
        <!-- <unit templateName="unit13_knight" amount="1"/> -->
        <!-- <unit templateName="unit14_mage" amount="1"/> -->
        <!-- <unit templateName="unit15_peasant" amount="1"/> -->
    </wave>
    <wave spawnPointX="15" spawnPointY="31" exitPointX="0" exitPointY="0" spawnInterval="0.5" startToMove="15">
        <unit delay="7" templateName="unit2_troll_axethrower" amount="10"/>
        <unit templateName="unit3_footman" amount="20"/>
        <unit templateName="unit1_grunt" amount="20"/>
        <unit templateName="unit6_death_knight" amount="5"/>
        <!-- <unit templateName="unit4_catapult" amount="1"/> -->
        <!-- <unit templateName="unit5_daemon" amount="1"/> -->
        <!-- <unit templateName="unit7_dragon" amount="1"/> -->
        <!-- <unit delay="0" templateName="unit8_ogre" amount="1"/> -->
        <!-- <unit templateName="unit9_peon" amount="1"/> -->
        <!-- <unit templateName="unit10_ballista" amount="1"/> -->
        <!-- <unit templateName="unit11_elven_archer" amount="1"/> -->
        <!-- <unit templateName="unit12_gryphon_rider" amount="1"/> -->
        <!-- <unit templateName="unit13_knight" amount="1"/> -->
        <!-- <unit templateName="unit14_mage" amount="1"/> -->
        <!-- <unit templateName="unit15_peasant" amount="1"/> -->
    </wave>
    <wave spawnPointX="15" spawnPointY="31" exitPointX="0" exitPointY="0" spawnInterval="0.5" startToMove="20">
        <!-- <unit templateName="unitPs_heaveKnight" amount="2"/> -->
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
    <!-- <waveForUser spawnPointX="0" spawnPointY="31" exitPointX="15" exitPointY="0"/> -->
</waves>