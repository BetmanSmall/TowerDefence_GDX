package com.betmansmall.game.map.Editor;
/**
 * Created by ������ on 15.11.2015.
 */

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.*;

import org.w3c.dom.*;

public class LoadMap {

    public LoadMap(String fileName, Map map)
    {
        File fXml=new File(fileName);

        try
        {
            DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
            DocumentBuilder db=dbf.newDocumentBuilder();
            Document doc=db.parse(fXml);

            //doc.getDocumentElement().normalize();

            NodeList nodeLst=doc.getElementsByTagName("map");
            for(int je=0;je<nodeLst.getLength();je++)
            {
                Node fstNode=nodeLst.item(je);
                NamedNodeMap attributes = fstNode.getAttributes();
                map.setWidth(Integer.valueOf(attributes.getNamedItem("width").getNodeValue()));
                map.setHeight(Integer.valueOf(attributes.getNamedItem("height").getNodeValue()));
                map.setTileWidth(Integer.valueOf(attributes.getNamedItem("tilewidth").getNodeValue()));
            }
            ArrayList<TileSet> TileSetList = new ArrayList<TileSet>();
            ArrayList<Layer> LayerList = new ArrayList<Layer>();
            ArrayList<ObjectGroup> ObjectGroupList = new ArrayList<ObjectGroup>();
            
            NodeList mainNodeList = doc.getDocumentElement().getChildNodes();
            for(int i=0;i<mainNodeList.getLength();i++){
            	Node n = mainNodeList.item(i);
            	if(n.getNodeName() == "tileset") {
                	TileSet tileset = new TileSet();
                	ArrayList<TerrainTypes> terrainTypesList = new ArrayList<TerrainTypes>();
                	ArrayList<TileTerrain> tileTerrainList = new ArrayList<TileTerrain>();
                	ArrayList<Properties> propertiesList = new ArrayList<Properties>();
	            	NodeList nodeTileSet = n.getChildNodes();
	                NamedNodeMap attributes = n.getAttributes();
	                if(attributes.getNamedItem("spacing") != null) {
	                	tileset.setFirstGid(Integer.valueOf(attributes.getNamedItem("firstgid").getNodeValue()));
	                	tileset.setName(attributes.getNamedItem("name").getNodeValue());
	                	tileset.setTileWidth(Integer.valueOf(attributes.getNamedItem("tilewidth").getNodeValue()));
	                	tileset.setTileHeight(Integer.valueOf(attributes.getNamedItem("tileheight").getNodeValue()));
	                	tileset.setSpacing(Integer.valueOf(attributes.getNamedItem("spacing").getNodeValue()));
	                	tileset.setMargin(Integer.valueOf(attributes.getNamedItem("margin").getNodeValue()));
	                } else {
	                	tileset.setFirstGid(Integer.valueOf(attributes.getNamedItem("firstgid").getNodeValue()));
	                	tileset.setName(attributes.getNamedItem("name").getNodeValue());
	                	tileset.setTileWidth(Integer.valueOf(attributes.getNamedItem("tilewidth").getNodeValue()));
	                	tileset.setTileHeight(Integer.valueOf(attributes.getNamedItem("tileheight").getNodeValue()));
	                }

	                for(int je=0;je<nodeTileSet.getLength();je++)
	                {
	                	TerrainTypes terrainTypes = new TerrainTypes();
	                	TileTerrain tileTerrain = new TileTerrain();
	                	Properties properties = new Properties();
	                    Node fstNode=nodeTileSet.item(je);
	                    if(fstNode.getNodeName() == "image"){
		                    NamedNodeMap attr = fstNode.getAttributes();
		                    Image image = new Image();
		                    image.setSource(attr.getNamedItem("source").getNodeValue());
		                    if(attr.getNamedItem("trans") != null)
		                    	image.setSource(attr.getNamedItem("trans").getNodeValue());
	                    	image.setWidth(Integer.valueOf(attr.getNamedItem("width").getNodeValue()));
		                    image.setHeight(Integer.valueOf(attr.getNamedItem("height").getNodeValue()));
		                    tileset.setImage(image);
	                    }
	                    else if(fstNode.getNodeName() == "terraintypes") {
	                    	ArrayList<Terrain> terrainArrayList = new ArrayList<Terrain>();
	                    	NodeList terrainList = fstNode.getChildNodes();
	                    	for(int tt=0;tt<terrainList.getLength();tt++)
	    	                {
	    	                    Node terrainNode=terrainList.item(tt);
	    	                    if(terrainNode.getNodeName() == "terrain"){
	    	                    	Terrain terrain = new Terrain();
	    		                    NamedNodeMap attr = terrainNode.getAttributes();
	    		                    terrain.setName(attr.getNamedItem("name").getNodeValue());
	    		                    terrain.setTile(Integer.valueOf(attr.getNamedItem("tile").getNodeValue()));
	    		                    terrainArrayList.add(terrain);
	    	                    }
	    	                }
	                    	terrainTypes.setTerrain(terrainArrayList);
	                    	terrainTypesList.add(terrainTypes);
	                    }
	                    else if(fstNode.getNodeName() == "tile") {
	                    	NamedNodeMap attr = fstNode.getAttributes();
	                    	tileTerrain.setId(Integer.valueOf(attr.getNamedItem("id").getNodeValue()));
	                    	tileTerrain.setTerrain(attr.getNamedItem("terrain").getNodeValue());
	                    }
	                    else if(fstNode.getNodeName() == "properties") {
	                    	NodeList propertiesNodeList = fstNode.getChildNodes();
	                    	ArrayList<Property> propertyList = new ArrayList<Property>();
	                    	for(int tt=0;tt<propertiesNodeList.getLength();tt++)
	    	                {
	    	                    Node propertiesNode=propertiesNodeList.item(tt);
	    	                    if(propertiesNode.getNodeName() == "property"){
	    	                    	Property property = new Property();
	    		                    NamedNodeMap attr = propertiesNode.getAttributes();
	    		                    property.setName(attr.getNamedItem("name").getNodeValue());
	    		                    property.setValue(attr.getNamedItem("value").getNodeValue());
	    		                    propertyList.add(property);
	    	                    }
	    	                }
	                    }
	                    terrainTypesList.add(terrainTypes);
	                    tileTerrainList.add(tileTerrain);
	                    propertiesList.add(properties);
	                }
	                tileset.setTileTerrain(tileTerrainList);
	                tileset.setTerrainTypes(terrainTypesList);
	                tileset.setProperties(propertiesList);
	                TileSetList.add(tileset);
            	} else if(n.getNodeName() == "layer") {
            		NodeList nodeTileSet = n.getChildNodes();
	                NamedNodeMap attributes = n.getAttributes();
	                ArrayList<Data> dataList = new ArrayList<Data>();
	                Layer layer = new Layer();
	                layer.setName(attributes.getNamedItem("name").getNodeValue());
	                layer.setWidth(Integer.valueOf(attributes.getNamedItem("width").getNodeValue()));
	                layer.setHeight(Integer.valueOf(attributes.getNamedItem("height").getNodeValue()));
	                
	                for(int je=0;je<nodeTileSet.getLength();je++)
	                {
	                	Node fstNode=nodeTileSet.item(je);
	                    if(fstNode.getNodeName() == "data"){
	                    	NodeList terrainList = fstNode.getChildNodes();
	                    	Data data = new Data();
	                    	ArrayList<Tile> tileList = new ArrayList<Tile>();
	                    	for(int tt=0;tt<terrainList.getLength();tt++)
	    	                {
	    	                    Node terrainNode=terrainList.item(tt);
	    	                    if(terrainNode.getNodeName() == "tile"){
	    	                    	Tile tile = new Tile();
	    		                    NamedNodeMap attr = terrainNode.getAttributes();
	    		                    tile.setGid(Integer.valueOf(attr.getNamedItem("gid").getNodeValue()));
	    		                    tileList.add(tile);
	    		                    data.setTile(tileList);
	    	                    }
	    	                }
	                    	dataList.add(data);
	                    }
	                    layer.setData(dataList);
	                }
	                LayerList.add(layer);
            	} else if(n.getNodeName() == "objectgroup") {
            		NodeList nodeTileSet = n.getChildNodes();
	                NamedNodeMap attributes = n.getAttributes();
	                ObjectGroup objectGroup = new ObjectGroup();
	                objectGroup.setName(attributes.getNamedItem("name").getNodeValue());
	                ArrayList<Object> objectList = new ArrayList<Object>();
	                for(int je=0;je<nodeTileSet.getLength();je++)
	                {
	                    Node fstNode=nodeTileSet.item(je);
	                    if(fstNode.getNodeName() == "object"){
		                    NamedNodeMap attr = fstNode.getAttributes();
		                    Object object = new Object();
		                    object.setId(Integer.valueOf(attr.getNamedItem("id").getNodeValue()));
		                    object.setName(attr.getNamedItem("name").getNodeValue());
		                    object.setGid(Integer.valueOf(attr.getNamedItem("gid").getNodeValue()));
		                    object.setX(Integer.valueOf(attr.getNamedItem("x").getNodeValue()));
		                    object.setY(Integer.valueOf(attr.getNamedItem("y").getNodeValue()));
		                    objectList.add(object);
	                    }
	                }
	                ObjectGroupList.add(objectGroup);
            	}
            }
            map.setTileSet(TileSetList);
            map.setLayer(LayerList);
            map.setObjectGroup(ObjectGroupList);
        }
        catch(Exception ei){
        	ei.printStackTrace();
        }
    }
}
