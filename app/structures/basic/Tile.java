package structures.basic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A basic representation of a tile on the game board. Tiles have both a pixel position
 * and a grid position. Tiles also have a width and height in pixels and a series of urls
 * that point to the different renderable textures that a tile might have.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Tile implements Comparable<Tile>{

	@JsonIgnore
	private static ObjectMapper mapper = new ObjectMapper(); // Jackson Java Object Serializer, is used to read java objects from a file
	
	List<String> tileTextures;
	int xpos;
	int ypos;
	int width;
	int height;
	int tilex;
	int tiley;
	
	//save the unit into the tile it locates on
	private Unit unitInTile;
	
	//the distance between this tile and other tile
	private int distance;
	
	//move mode, 1=x first, 2=y first
	private int moveMode=1;
	
	//attack Mode，true=attack directly, false=move and attack
	private boolean attackMode = true;
	
	//color mode, 0=default, 1=move/spell/summon(white), 2=attack(red)
	private int mode;
	
	//provoker of the provoked tiles
	private List<Unit> provoker = new ArrayList<>();
	
	public Tile() {}
	
	public Tile(String tileTexture, int xpos, int ypos, int width, int height, int tilex, int tiley) {
		super();
		tileTextures = new ArrayList<String>(1);
		tileTextures.add(tileTexture);
		this.xpos = xpos;
		this.ypos = ypos;
		this.width = width;
		this.height = height;
		this.tilex = tilex;
		this.tiley = tiley;
	}
	
	public Tile(List<String> tileTextures, int xpos, int ypos, int width, int height, int tilex, int tiley) {
		super();
		this.tileTextures = tileTextures;
		this.xpos = xpos;
		this.ypos = ypos;
		this.width = width;
		this.height = height;
		this.tilex = tilex;
		this.tiley = tiley;
	}
	public List<String> getTileTextures() {
		return tileTextures;
	}
	public void setTileTextures(List<String> tileTextures) {
		this.tileTextures = tileTextures;
	}
	public int getXpos() {
		return xpos;
	}
	public void setXpos(int xpos) {
		this.xpos = xpos;
	}
	public int getYpos() {
		return ypos;
	}
	public void setYpos(int ypos) {
		this.ypos = ypos;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getTilex() {
		return tilex;
	}
	public void setTilex(int tilex) {
		this.tilex = tilex;
	}
	public int getTiley() {
		return tiley;
	}
	public void setTiley(int tiley) {
		this.tiley = tiley;
	}
	
	public Unit getUnit() {
		return this.unitInTile;
	}
	public void setUnit(Unit unit) {
		this.unitInTile = unit;
	}
	
	//clear unit
	public void clearUnit() {
		this.unitInTile = null;
	}
	
	public List<Unit> getProvoker(){
		return this.provoker;
	}
	
	public boolean isAttackMode() {
		return attackMode;
	}

	public void setAttackMode(boolean attackMode) {
		this.attackMode = attackMode;
	}

	public int getMoveMode() {
		return moveMode;
	}

	public void setMoveMode(int moveMode) {
		this.moveMode = moveMode;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}
	
	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	/**
	 * Loads a tile from a configuration file
	 * parameters.
	 * @param configFile
	 * @return
	 */
	public static Tile constructTile(String configFile) {
		
		try {
			Tile tile = mapper.readValue(new File(configFile), Tile.class);
			return tile;
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return null;
		
	}
	
	//sort the Tile by distance in ascending order, used by Ai
	public int compareTo(Tile tile) {
		return this.distance - tile.distance;
	}
	
}
