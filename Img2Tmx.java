/**
 * @author Grzegorz Samociak
 * 
 * This program takes png map file and compare it against provided tile set file.
 * Then makes *.tmx file of it.
 * 
 * 
 * 
 */
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.xml.bind.DatatypeConverter;

public class Img2Tmx extends JFrame{

	private static final long serialVersionUID = 1L;
	private static JPanel mainPanel;
	private static BufferedImage buffTile;
	private static BufferedImage buffMapLoaded;
	private ArrayList<String> tileSetHash = new ArrayList<String>();
	private ArrayList<String> mapHash = new ArrayList<String>();
	private ArrayList<Integer> tmxL1Map = new ArrayList<Integer>();
	
	private ArrayList<Integer> tmxL2Tiles = new ArrayList<Integer>();
	private ArrayList<Integer> tmxL2Map = new ArrayList<Integer>();
	private ArrayList<Integer> tmxL3Tiles = new ArrayList<Integer>();
	private ArrayList<Integer> tmxL3Map = new ArrayList<Integer>();
	private ArrayList<Integer> tmxL4Tiles = new ArrayList<Integer>();
	private ArrayList<Integer> tmxL4Map = new ArrayList<Integer>();

	private ArrayList<BufferedImage> tileSetBI = new ArrayList<BufferedImage>();
	private ArrayList<BufferedImage> mapSetBI = new ArrayList<BufferedImage>();
	private static int tileWidth;
	private static int tileHeight;
	private String orientation;
	private static String tileSetFile;
	private static String mapFile;
	private BufferedImage BIMapOut;
	private BufferedImage tileSet;
	
	
	
	public static void main(String[] args) throws IOException {
		
		if (args.length<=0){
			System.out.println(
					"Usage: \n"
					+ ">>Img2Tmx px py TileSet.png MapImage.png \n"
					+ "where: \n"
					+ "px is width of the tile\n"
					+ "py is height of the tile\n"
					+ "TileSet.png is tile set file, change to genNewSet to generate new tile set from map\n"
					+ "MapImage.png is image of the map\n");
		}
		if (args.length >0){
			
			System.out.println("Tile size is " + args[0]+" by "+args[1]);
			System.out.println("Tile set file: "+args[2]);
			System.out.println("Map Image file: "+args[3]);
			
			tileWidth = Integer.parseInt(args[0]);
			tileHeight = Integer.parseInt(args[1]);
			tileSetFile = args[2];
			mapFile = args[3];
			
			
			Img2Tmx img2tmx = new Img2Tmx();
			img2tmx.initUI();
			img2tmx.setVisible(true);
			
		}
		
		
	}
	
	public Img2Tmx() throws IOException {
		readLayerFile("layer2.txt", tmxL2Tiles);
		readLayerFile("layer3.txt", tmxL3Tiles);
		readLayerFile("layer4.txt", tmxL4Tiles);
		
	}
	
private void readLayerFile(String fileName, ArrayList<Integer> arrL) throws IOException {
	final String dir = System.getProperty("user.dir");
	BufferedReader br = new BufferedReader(new FileReader(dir + "/"+fileName));
	try {
	    StringBuilder sb = new StringBuilder();
	    String line = br.readLine();

	    while (line != null) {
	        sb.append(line);
	        arrL.add(Integer.parseInt(line));
	        sb.append(System.lineSeparator());
	        line = br.readLine();
	    }
	} finally {
	    br.close();
	}
}



private void initUI() throws IOException {
	final String dir = System.getProperty("user.dir");
	
	try 
	{
		buffMapLoaded = ImageIO.read(new File(dir+ "/"+mapFile));
	} 
	catch (IOException e) 
	{
	    e.printStackTrace();
	}
    
    if (!tileSetFile.equalsIgnoreCase("genNewSet")) {
    	try 
    	{
    		tileSet = ImageIO.read(new File(dir+ "/"+tileSetFile));
    	} 
    	catch (IOException e) 
    	{
    	    e.printStackTrace();
    	}
    		
    	
    	System.out.println("Processing TileSet");
        proessTiles(tileSet,tileSetHash, tileSetBI);
        
        System.out.println("Processing MapFile");
        proessTiles(buffMapLoaded, mapHash, mapSetBI);
    	
    }else if(tileSetFile.equals("genNewSet")) {
    	System.out.println("You have chosen to generate new tile set");
    	
    	System.out.println("Processing MapFile");
        proessTiles(buffMapLoaded, mapHash, mapSetBI);
        generateNewTileSet();
    }
    setTitle("Image2Tmx");
    setSize(buffMapLoaded.getWidth()+20, buffMapLoaded.getHeight()+50);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(EXIT_ON_CLOSE);  
    
    mainPanel = new JPanel();
    
    compare();
	ImageIcon set2 = new ImageIcon(BIMapOut);
    JLabel set2Label = new JLabel(set2);
    mainPanel.add(set2Label);
    add(mainPanel);
    
    makeTMX();
    System.out.println("It is done!");
    }

private void generateNewTileSet() throws IOException {
	System.out.println("Generating new tileset");
	//work on mapHash
	
	for (int i=0; i<mapHash.size()-1;i++) {
		if(!tileSetHash.contains(mapHash.get(i))) {
			tileSetHash.add(mapHash.get(i));
		}
	}
	
	//working on buffered image
	System.out.println("tiles in new tileset: "+ tileSetHash.size());
	int size = (int) Math.sqrt(tileSetHash.size());
	
	BufferedImage newTsBI = new BufferedImage((size)*tileWidth,(size+2)*tileHeight,BufferedImage.TYPE_INT_RGB);
	
	int bItilesX = buffMapLoaded.getWidth()/tileWidth;
	
	for(int i=0; i<tileSetHash.size(); i++) {
		for (int j=0; j<mapHash.size(); j++) {
			if (tileSetHash.get(i).equals(mapHash.get(j))) {
				
				for(int a=0; a<tileWidth; a++) {
					for(int b=0;b<tileHeight; b++) {
						int newTsBIx = (i/size)*tileWidth+a;
						int newTsBIy = (i%size)*tileHeight+b;
						int bix = (j/bItilesX)*tileWidth+a;
						int biy = (j%bItilesX)*tileHeight+b;
						newTsBI.setRGB(newTsBIy, newTsBIx, buffMapLoaded.getRGB(biy, bix));
					}
				}break;
			}
		
		}
	}
	//save to file
	ImageIO.write(newTsBI, "png", new File("newTileSet.png"));
	
}
private void makeTMX() throws FileNotFoundException, UnsupportedEncodingException{
	
	orientation = "orthogonal";
	
	final String dir = System.getProperty("user.dir");
	
	StringBuffer sb = new StringBuffer(mapFile);
	//Deleting file extension
	sb.deleteCharAt(sb.length()-1);
	sb.deleteCharAt(sb.length()-1);
	sb.deleteCharAt(sb.length()-1);
	
	
	PrintWriter printWriter = new PrintWriter(dir+"/"+sb+"tmx","UTF-8");
	printWriter.println("<?xml version='1.0' encoding='UTF-8'?>");
	printWriter.println("<map version='1.0' tiledversion='1.0.3' orientation='"+orientation+"' renderorder='right-down' width='"
						+buffMapLoaded.getWidth()/tileWidth+"' height='"+buffMapLoaded.getHeight()/tileHeight
						+"' tilewidth='"+tileWidth+"' tileheight='"+tileHeight+"' nextobjectid='1'>");
	printWriter.println(" <tileset firstgid='1' source='untitled.tsx'/>");
	
	printLayer(printWriter, tmxL4Map, 4);
	printLayer(printWriter, tmxL3Map, 3);
	printLayer(printWriter, tmxL2Map, 2);
	printLayer(printWriter, tmxL1Map, 1);
	
	printWriter.println("</map>");
	printWriter.close();
	
}

private void printLayer(PrintWriter printWriter, ArrayList<Integer> arrL, Integer lcount) {
	printWriter.println("<layer name='Tile Layer "+lcount+"' width='"+buffMapLoaded.getWidth()/tileWidth+"' height='"+buffMapLoaded.getHeight()/tileHeight+"'>");
	printWriter.println("  <data>");
	
	for (int i=0; i <arrL.size(); i++){
		printWriter.println("   <tile gid='"+arrL.get(i)+"'/>");
	}
	printWriter.println("  </data>\n </layer>");
}


//compare 2 arraylists with buffered images
private void compare(){
	BIMapOut = new BufferedImage(buffMapLoaded.getWidth(),buffMapLoaded.getHeight(),BufferedImage.TYPE_INT_RGB);
	int width = BIMapOut.getWidth()/tileWidth;
	
	for(int i=0; i <mapHash.size(); i++){
		tmxL1Map.add(i,0);
		tmxL2Map.add(i,0);
		tmxL3Map.add(i,0);
		tmxL4Map.add(i,0);
		
		int y= i/width;
		int x= i%width;
		
		
		for (int j=0; j<tileSetHash.size(); j++){
			if (mapHash.get(i).equals(tileSetHash.get(j))){
				
				if (tmxL2Tiles.contains(j)){
					tmxL2Map.set(i, j+1);
				}else if (tmxL3Tiles.contains(j)){
					tmxL3Map.set(i, j+1);
				}else if (tmxL4Tiles.contains(j)){
					tmxL4Map.set(i, j+1);
				}else
				tmxL1Map.set(i, j+1);
				
				for(int q=0; q<tileWidth; q++){
					for(int z=0; z<tileHeight; z++){
						BIMapOut.setRGB(x*tileWidth+q, y*tileHeight+z, mapSetBI.get(i).getRGB(q,z));
					}
				}
				
				break;
				}else{
				
				for(int q=0; q<tileWidth; q++){
					for(int z=0; z<tileHeight; z++){
						BIMapOut.setRGB(x*tileWidth+q, y*tileHeight+z,0xFF00FF);
					}
				}
				
			}
		}
	}
}

private void proessTiles(BufferedImage bi, ArrayList<String> arrL, ArrayList<BufferedImage> BIList) throws IOException{
	buffTile = new BufferedImage(tileWidth,tileHeight,BufferedImage.TYPE_INT_RGB);
	
	int width = bi.getWidth(null)/tileWidth;
	int height = bi.getHeight(null)/tileWidth;
	
	
	for(int offsetY=0; offsetY<height; offsetY++){
		for(int offsetX=0; offsetX<width; offsetX++){
				for(int y =offsetY*tileHeight; y<offsetY*tileHeight+tileHeight; y++){
					for (int x=offsetX*tileWidth; x<offsetX*tileWidth+tileWidth; x++){
					
					buffTile.setRGB(x-(offsetX*tileWidth), y-(offsetY*tileHeight), bi.getRGB(x, y));
				}
			}
			hashImage(buffTile,arrL);
			
			BIList.add(buffTile);
			buffTile = new BufferedImage(tileWidth,tileHeight,BufferedImage.TYPE_INT_RGB);
		}
	}
}

private void hashImage(BufferedImage bI, ArrayList<String> arrL) throws IOException{
	try {
		MessageDigest md = MessageDigest.getInstance("MD5");
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write( bI, "jpg", baos );
		baos.flush();
		byte[] imageInByte = baos.toByteArray();
		baos.close();

		
		md.update(imageInByte);
		
		byte[] digest = md.digest();
		
		String myHash = DatatypeConverter.printHexBinary(digest).toUpperCase();
		
		//put hash in an array
		arrL.add(myHash);
		
		
	} catch (NoSuchAlgorithmException e) {
		e.printStackTrace();
	}
	
}

}
