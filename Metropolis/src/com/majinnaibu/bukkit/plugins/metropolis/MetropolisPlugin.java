package com.majinnaibu.bukkit.plugins.metropolis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.majinnaibu.bukkit.plugins.metropolis.commands.MetropolisHomeGenerateCommand;
import com.majinnaibu.bukkit.plugins.metropolis.commands.MetropolisHomeListCommand;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class MetropolisPlugin extends JavaPlugin {
	public static final Logger log=Logger.getLogger("Minecraft");
	
	public PluginDescriptionFile pdf = null;
	public WorldGuardPlugin worldGuard = null;
	//public WorldEditPlugin worldEdit = null;
	public World world = null;
	public RegionManager regionManager = null;

	private List<PlayerHome> _occupiedHomes;
	
	private LoginListener _loginListener = null;
	
	int size = 1;
	
	int plotSizeX = 24;
	int plotSizeZ = 24;
	int roadWidth = 4;
	int roadLevel = 62;
	int spaceAboveRoad = 2;
	int roadMaterial = 4;
	String worldName = "world";
	
	@Override
	public void onDisable() {
		log.info(String.format("%s disabled", pdf.getFullName()));
		saveConfig();
	}

	@Override
	public void onEnable() {
		pdf = getDescription();
		
		Configuration config = getConfig();
		config.options().copyDefaults(true);
		
		plotSizeX = config.getInt("plot.sizeX");
		plotSizeZ = config.getInt("plot.sizeZ");
		roadWidth = config.getInt("road.width");
		spaceAboveRoad = config.getInt("road.clearSpaceAbove");
		roadLevel = config.getInt("road.level");
		roadMaterial = config.getInt("road.material");
		worldName =config.getString("worldname");
		saveConfig();
		
		log.info(String.format("Metropolis: world name is %s", worldName));
		
		Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
		if(plugin == null || !(plugin instanceof WorldGuardPlugin)){
			throw new RuntimeException("WorldGuard must be loaded first");
		}
		
		worldGuard = (WorldGuardPlugin) plugin;
		
		/*
		plugin = getServer().getPluginManager().getPlugin("WorldEdit");
		if(plugin == null || !(plugin instanceof WorldEditPlugin)){
			throw new RuntimeException("WorldEdit must be loaded first");
		}
		worldEdit = (WorldEditPlugin) plugin;
		*/
		
		world = getServer().getWorld(worldName);
//		for(World world: getServer().getWorlds()){
//			log.info(String.format("name: %s", world.getName()));
//		}
		
		regionManager = worldGuard.getRegionManager(world);
			
		ProtectedRegion cityRegion = regionManager.getRegion("City");
		if(cityRegion == null){
			cityRegion = new ProtectedCuboidRegion("City", getPlotMin(0, 0), this.getPlotMax(0, 0));
			cityRegion.setPriority(0);
			regionManager.addRegion(cityRegion);
			//TODO determine appropriate chest flags
		}
		
		//setupDatabase();
		
		_occupiedHomes = new ArrayList<PlayerHome>();
		fillOccupiedHomes();

		if(_loginListener == null){
			_loginListener = new LoginListener(this);
		}

		log.info(String.format("%s enabled", pdf.getFullName()));
		
		getCommand("metropolis-home-generate").setExecutor(new MetropolisHomeGenerateCommand(this));
		getCommand("metropolis-home-list").setExecutor(new MetropolisHomeListCommand(this));
	}
	
	/*
	private void setupDatabase() {
		try{
			getDatabase().find(PlayerHome.class).findRowCount();
		}catch(PersistenceException ex){
			System.out.println("Installing database for " + pdf.getName() + " due to first time usage");
			installDDL();
		}
	}
	/**/
	
	private void fillOccupiedHomes() {
		_occupiedHomes = new ArrayList<PlayerHome>();
		
		for(ProtectedRegion region : regionManager.getRegions().values()){
			if(region instanceof ProtectedCuboidRegion && region.getId().startsWith("h_")){
				ProtectedCuboidRegion cuboidRegion = (ProtectedCuboidRegion) region;
				PlayerHome home = new PlayerHome(cuboidRegion);
				
				_occupiedHomes.add(home);
				/*
				if(getDatabase().find(PlayerHome.class).where().eq("regionName", home.getRegionName()).findRowCount() == 0){
					getDatabase().insert(home);
				}
				/**/
			}
		}
		
		size = calculateCitySize();
		//log.info(String.valueOf(iSize));
		
		/*
		for(PlayerHome home : getDatabase().find(PlayerHome.class).findList()){
			_occupiedHomes.add(home);
		}
		
		Map<String, ProtectedRegion> map = regionManager.getRegions();
		for(String regionId : map.keySet()){
			log.info(String.format("key: %s, id: %s", regionId, map.get(regionId).getId()));
		}
		
		
		log.info(String.format("Metropolis: %d occupied homes", _occupiedHomes.size()));
		/**/
		
		Collections.sort(_occupiedHomes);
	}

	PlayerHome getPlayerHome(Player player) {
		PlayerHome home = null;
		
		String regionName = "h_" + player.getName();
		ProtectedRegion homeRegion = regionManager.getRegion(regionName);

		
		//log.info(homeRegion == null? "null" : homeRegion.toString());
		if(homeRegion == null){
			log.info(String.format("Creating home for player %s", player.getName()));
			home = generateHome(player.getName());
		}
		
		return home;
	}

	@Override
	public List<Class<?>> getDatabaseClasses(){
		List<Class<?>> list = new ArrayList<Class<?>>();
		
		list.add(PlayerHome.class);
		list.add(Cuboid.class);
		
		return list;
	}
	
	private void createRoads(Cuboid plotCuboid) {
		if(roadWidth>0){
			int x=0;
			int y= roadLevel;
			int z=0;
			
			if(plotCuboid == null){
				log.info("plotCuboid is null");
				return;
			}
			
			for(x=plotCuboid.minX; x<plotCuboid.minX + roadWidth/2; x++){
				for(z=plotCuboid.minZ; z<=plotCuboid.maxZ; z++){
					Block block = world.getBlockAt(x, y, z);
					block.setTypeId(roadMaterial);
					for(int y1 = 0; y1 < spaceAboveRoad; y1++){
						block = world.getBlockAt(x, y+y1+1, z);
						block.setTypeId(0);
					}
				}
			}
			
			for(x=plotCuboid.maxX - roadWidth/2+1; x<=plotCuboid.maxX; x++){
				for(z=plotCuboid.minZ; z<=plotCuboid.maxZ; z++){
					Block block = world.getBlockAt(x, y, z);
					block.setTypeId(roadMaterial);
					for(int y1 = 0; y1 < spaceAboveRoad; y1++){
						block = world.getBlockAt(x, y+y1+1, z);
						block.setTypeId(0);
					}
				}
			}
			
			for(z=plotCuboid.minZ; z<plotCuboid.minZ + roadWidth/2; z++){
				for(x=plotCuboid.minX; x<=plotCuboid.maxX; x++){
					Block block = world.getBlockAt(x, y, z);
					block.setTypeId(roadMaterial);
					for(int y1 = 0; y1 < spaceAboveRoad; y1++){
						block = world.getBlockAt(x, y+y1+1, z);
						block.setTypeId(0);
					}
				}
			}
			
			for(z=plotCuboid.maxZ - roadWidth/2+1; z<=plotCuboid.maxZ; z++){
				for(x=plotCuboid.minX; x<=plotCuboid.maxX; x++){
					Block block = world.getBlockAt(x, y, z);
					block.setTypeId(roadMaterial);
					for(int y1 = 0; y1 < spaceAboveRoad; y1++){
						block = world.getBlockAt(x, y+y1+1, z);
						block.setTypeId(0);
					}
				}
			}
		}
	}

	private Cuboid findNextUnownedHomeRegion() {
		if(size <= 2){size=3;}
		int homeIndex = 0;
		int rowMin = -size/2;
		int rowMax = size/2;
		int colMin = -size/2;
		int colMax = size/2;
		
		log.info(String.format("size: %d, rowMin: %d, rowMax: %d, colMin: %d, colMax: %d", size, rowMin, rowMax, colMin, colMax));
		
		int row = rowMin;
		int col = colMin;
		
		if(_occupiedHomes.size() == 0){return new Cuboid(getPlotMin(row, col), getPlotMax(row, col));}
		
		PlayerHome home = _occupiedHomes.get(homeIndex);
		
		for(col=colMin; col <= colMax; col++){
			for(row=rowMin; row<= rowMax; row++){
				if(row != 0 || col != 0){
					log.info(String.format("row: %d, col: %d", row, col));
					if(home == null){
						return new Cuboid(getPlotMin(row, col), getPlotMax(row, col));
					}
					else if(Cuboid.isBlockLessThan(getPlotMin(row, col), home.getPlotMin(roadWidth))){
						return new Cuboid(getPlotMin(row, col), getPlotMax(row, col));
					}else{
						homeIndex++;
						if(homeIndex < _occupiedHomes.size()){
							home = _occupiedHomes.get(homeIndex);
						}else{
							home = null;
						}
					}
				}
			}
		}
		
		expandCityRegion();
		return new Cuboid(getPlotMin(-size/2, -size/2), getPlotMax(-size/2, -size/2));
		
		/*
		log.info(String.valueOf(size));
		int homeIndex = 0;
		if(_occupiedHomes.size() == 0){
			log.info("_occupiedHomes.size is 0");
			if(size < 1){
				size=1;
			}
			
			expandCityRegion();
			
			log.info(String.format("row: %d, col: %d", -1, -1));
			return new Cuboid(getPlotMin(-1, -1), getPlotMax(-1, -1));
		}
		
		PlayerHome home = _occupiedHomes.get(homeIndex);
		int row=0;
		int col=0;
		log.info(String.format("row-min: %d, row-max: %d, col-min: %d, col-max: %d", -size/2, size/2, -size/2, size/2));
		for(row = -size/2; row<=size/2; row++){
			for(col = -size/2; col <= size/2; col++){
				log.info(String.format(
						"checking row: %d, col: %d, homeIndex: %d, home is %s",
						row,
						col,
						homeIndex,
						home==null?"null":"not null"));
				if(home == null){
					log.info("home is null");
					expandCityRegion();
					log.info(String.format("row: %d, col: %d", row, col));
					return new Cuboid(getPlotMin(row, col), getPlotMax(row, col));
				}else if(Cuboid.isBlockLessThan(getPlotMin(row, col), home.getCuboid().getMin())){
					log.info("Cuboid.isBlockLessThan(getPlotMin(row, col), home.getCuboid().getMin())");
					log.info(String.format("row: %d, col: %d", row, col));
					return new Cuboid(getPlotMin(row, col), getPlotMax(row, col));
				}else{
					log.info("else");
					homeIndex++;
					if(homeIndex < _occupiedHomes.size()){
						home = _occupiedHomes.get(homeIndex);
					}else{
						home = null;
					}
				}
			}
		}
		
		size+=2;
		
		log.info(String.format("row: %d, col: %d", row, col));
		return new Cuboid(getPlotMin(row, col), getPlotMax(row, col));
		//log.info(String.format("row: %d, col: %d", -size/2, -size/2));
		//return new Cuboid(getPlotMin(-size/2, -size/2), getPlotMax(-size/2, -size/2));
		/**/
	}
	
	private void expandCityRegion() {
		size=calculateCitySize();
		ProtectedRegion cityRegion = regionManager.getRegion("City");
		if(cityRegion instanceof ProtectedCuboidRegion){
			ProtectedCuboidRegion region = (ProtectedCuboidRegion)cityRegion;
			
			BlockVector min;
			BlockVector max;
			
			min = getPlotMin(-size/2, -size/2);
			max = getPlotMax(size/2, size/2);
			
			region.setMinimumPoint(min);
			region.setMaximumPoint(max);
		}
	}

	private int calculateCitySize() {
		int iSize = 0;
		
		for(PlayerHome home: _occupiedHomes){
			int plotCol=Math.abs(getPlotXFromMin(home.getCuboid()));
			int plotRow=Math.abs(getPlotZFromMin(home.getCuboid()));
			//log.info(String.format("iSize: %d, plotRow: %d, plotCol: %d", iSize, plotCol, plotRow));
			iSize = Math.max(Math.max(plotRow*2+1, plotCol*2+1), iSize);
			
		}

		//log.info(String.format("iSize: %d", iSize));
		return iSize;
	}

	public BlockVector getPlotMin(int row, int col){
		return new BlockVector(col * plotSizeX, 0, row * plotSizeZ);
	}
	
	public BlockVector getPlotMax(int row, int col){
		return new BlockVector(col * plotSizeX + plotSizeX-1, 128, row * plotSizeZ + plotSizeZ-1);
	}
	
	private int getPlotXFromMin(Cuboid cuboid) {
		int minX = cuboid.getMin().getBlockX() - roadWidth/2;
		
		return minX/plotSizeX;
	}

	private int getPlotZFromMin(Cuboid cuboid) {
		int minZ = cuboid.getMin().getBlockZ() - roadWidth/2;
		
		return minZ/plotSizeZ;
	}

	private void setHomeOccupied(String owner, BlockVector minimumPoint, BlockVector maximumPoint) {
		
		PlayerHome home = new PlayerHome(owner, minimumPoint, maximumPoint);
		if(!_occupiedHomes.contains(home)){
			_occupiedHomes.add(home);
			Collections.sort(_occupiedHomes);
		}
	}

	
	public PlayerHome generateHome(String playerName) {
		log.info(String.format("Generating home for %s", playerName));
		Cuboid plotCuboid = null;
		Cuboid homeCuboid = null;
		ProtectedRegion homeRegion = null;
		String regionName = "h_" + playerName;
		
		plotCuboid = findNextUnownedHomeRegion();
		homeCuboid = plotCuboid.inset(roadWidth/2, roadWidth/2);
		homeRegion = new ProtectedCuboidRegion(regionName, homeCuboid.getMin(), homeCuboid.getMax());
		DefaultDomain d = homeRegion.getOwners();
		d.addPlayer(playerName);
		homeRegion.setPriority(1);
		regionManager.addRegion(homeRegion);
		try {
			regionManager.save();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		setHomeOccupied(playerName, homeRegion.getMinimumPoint(), homeRegion.getMaximumPoint());
		
		createRoads(plotCuboid);
		
		log.info(String.format("Done generating home for %s", playerName));
		
		return new PlayerHome(homeRegion);
	}

	public List<PlayerHome> getCityBlocks() {
		return Collections.unmodifiableList(_occupiedHomes);
	}
	
}
