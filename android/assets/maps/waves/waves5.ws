<?xml version="1.0" encoding="UTF-8"?>
<waves>
    <!-- delay - время между последним крипом из предыдущей пачки крипов и нашима первым + spawnInterval + interval -->
    <!-- а интервал - время между крипами одного типа + spawnInterval -->
    <!-- а spawnInterval - дополнительное или же одно единственное время между всеми крипами в этой волне. -->
    <!-- startToMove - ну тут все понятно. столько пройдет времени после нажатия пользователем кнопки пуска крипов -->
    <wave spawnPointX="63" spawnPointY="31" exitPointX="0" exitPointY="32" spawnInterval="0.5" startToMove="0">
        <unit delay="1" templateName="unit10_ballista" interval="1" amount="2"/>
        <unit delay="2" templateName="unit2_troll_axethrower" interval="2" amount="2"/>
        <unit delay="1" templateName="unit1_grunt" interval="3" amount="2"/>
        <unit delay="0.8" templateName="unit2_troll_axethrower" interval="2" amount="2"/>
    </wave>
    <wave spawnPointX="4" spawnPointY="3" exitPointX="61" exitPointY="62" spawnInterval="1.5" startToMove="0">
        <unit delay="1" templateName="unit1_grunt" amount="1"/>
        <unit delay="2" templateName="unit2_troll_axethrower" amount="1"/>
        <unit delay="1" templateName="unit1_grunt" amount="2"/>
        <unit delay="0" templateName="unit13_knight" amount="1"/>
        <unit delay="1" templateName="unit13_knight" amount="1"/>
        <unit delay="2" templateName="unit13_knight" amount="1"/>
    </wave>
    <wave spawnPointX="2" spawnPointY="60" exitPointX="60" exitPointY="2" spawnInterval="1" startToMove="0">
        <unit templateName="unit5_daemon" amount="5"/>
        <unit templateName="unit2_troll_axethrower" amount="1"/>
        <unit templateName="unit1_grunt" amount="2"/>
        <unit templateName="unit12_gryphon_rider" amount="2"/>
    </wave>
</waves>