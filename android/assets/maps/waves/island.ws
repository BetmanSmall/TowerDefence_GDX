<?xml version="1.0" encoding="UTF-8"?>
<waves>
    <!-- delay - время между последним крипом из предыдущей пачки крипов и нашима первым + spawnInterval + interval -->
    <!-- а interval - время между крипами одного типа + spawnInterval -->
    <!-- а spawnInterval - дополнительное или же одно единственное время между всеми крипами в этой волне. -->
    <!-- startToMove - ну тут все понятно. столько пройдет времени после нажатия пользователем кнопки пуска крипов -->
    <!--startToMove -> delay-->
    <!-- <wave spawnPointX="1" spawnPointY="28" exitPointX="5" exitPointY="7" spawnInterval="1" startToMove="0"> -->
        <!-- <unit templateName="unit1_grunt" amount="1"/> -->
    <!-- </wave> -->
    <!-- <wave spawnPointX="3" spawnPointY="30" exitPointX="28" exitPointY="27" spawnInterval="1" startToMove="0"> -->
        <!-- <unit templateName="unit1_grunt" amount="2"/> -->
    <!-- </wave> -->
    <wave spawnPointX="5" spawnPointY="7" exitPointX="1" exitPointY="28" spawnInterval="1" startToMove="0">
        <unit templateName="unit2_troll_axethrower" amount="2"/>
        <unit templateName="unit1_grunt" amount="2"/>
    </wave>
    <!-- <wave spawnPointX="27" spawnPointY="1" exitPointX="3" exitPointY="30" spawnInterval="1" startToMove="0"> -->
        <!-- <unit templateName="unit2_troll_axethrower" amount="2"/> -->
        <!-- <unit templateName="unit1_grunt" amount="2"/> -->
    <!-- </wave> -->
    <!-- <waveForUser spawnPointX="0" spawnPointY="31" exitPointX="15" exitPointY="0"/> -->
</waves>