<?xml version="1.0" encoding="UTF-8"?>
<tileset version="1.9" tiledversion="1.9.2" name="bots" tilewidth="8" tileheight="8" tilecount="10" columns="0">
 <grid orientation="orthogonal" width="1" height="1"/>
 <tile id="0" probability="0.3">
  <image width="8" height="8" source="road.png"/>
 </tile>
 <tile id="1" probability="0.5">
  <image width="8" height="8" source="ground.png"/>
 </tile>
 <tile id="2">
  <image width="8" height="8" source="wall.png"/>
  <objectgroup draworder="index" id="2">
   <object id="1" x="0" y="0" width="8" height="8"/>
  </objectgroup>
 </tile>
 <tile id="4" probability="0.1">
  <image width="8" height="8" source="glass.png"/>
 </tile>
 <tile id="5" probability="0.9">
  <image width="8" height="8" source="grass.png"/>
 </tile>
 <tile id="7" probability="0.5">
  <image width="8" height="8" source="swamp.png"/>
 </tile>
 <tile id="8">
  <image width="8" height="8" source="water.png"/>
 </tile>
 <tile id="6" probability="0.05">
  <image width="8" height="8" source="radiation.png"/>
 </tile>
 <tile id="3">
  <image width="8" height="8" source="anomaly.png"/>
 </tile>
 <tile id="9">
  <image width="8" height="8" source="search.png"/>
 </tile>
 <wangsets>
  <wangset name="Ground" type="corner" tile="7">
   <wangcolor name="" color="#ff0000" tile="-1" probability="1"/>
   <wangtile tileid="0" wangid="0,1,0,1,0,1,0,1"/>
   <wangtile tileid="1" wangid="0,1,0,1,0,1,0,1"/>
   <wangtile tileid="4" wangid="0,1,0,1,0,1,0,1"/>
   <wangtile tileid="5" wangid="0,1,0,1,0,1,0,1"/>
   <wangtile tileid="6" wangid="0,1,0,1,0,1,0,1"/>
   <wangtile tileid="7" wangid="0,1,0,1,0,1,0,1"/>
  </wangset>
  <wangset name="Water" type="corner" tile="8">
   <wangcolor name="" color="#ff0000" tile="-1" probability="1"/>
   <wangtile tileid="8" wangid="0,1,0,1,0,1,0,1"/>
  </wangset>
  <wangset name="Anomaly" type="corner" tile="3">
   <wangcolor name="" color="#ff0000" tile="-1" probability="1"/>
   <wangtile tileid="3" wangid="0,1,0,1,0,1,0,1"/>
  </wangset>
  <wangset name="Radiation" type="corner" tile="6">
   <wangcolor name="" color="#ff0000" tile="-1" probability="1"/>
   <wangtile tileid="6" wangid="0,1,0,1,0,1,0,1"/>
  </wangset>
  <wangset name="Wall" type="corner" tile="2">
   <wangcolor name="" color="#ff0000" tile="-1" probability="1"/>
   <wangtile tileid="2" wangid="0,1,0,1,0,1,0,1"/>
  </wangset>
  <wangset name="Search" type="corner" tile="9">
   <wangcolor name="" color="#ff0000" tile="-1" probability="1"/>
   <wangtile tileid="9" wangid="0,1,0,1,0,1,0,1"/>
  </wangset>
 </wangsets>
</tileset>
