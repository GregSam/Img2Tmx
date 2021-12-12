# Img2Tmx
## Small tool to compare image level map against tile set to make *.tmx file

convert *.png map files into tmx files 
 
operate from command line with command:

**java -jar Img2Tmx.jar tileWidth tileHeight TileSet.png MapFile.png**

after processing both files it will display result in new window and save it to *.tmx file

unrecognized tiles will show as pink in result display.   
In Tiled You just need to point to *.tsx file and You are done.

### separation into 4 layers.

using by the same command   
**java -jar Img2Tmx.jar tileWidth tileHeight TileSet.png MapFile.png**

To get tiles in new layers put tile’s tile set number in txt file attached to program (they need to be in the same folder as jar file).   
numbers needs to be in 1 column like:

1
2
3
4

make sure there is no empty line at the end.   


### autogenerate the tileset from tiles in the image

to get it to work just change tileSetFile.png to genNewSet.

command:   
**java - jar Img2Tmx_v3.jar tilex tiley genNewSet mapFile.png**

it will generate new tile set based on the map.

