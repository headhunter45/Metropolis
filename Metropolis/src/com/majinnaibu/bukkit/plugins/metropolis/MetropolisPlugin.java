package com.majinnaibu.bukkit.plugins.metropolis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.majinnaibu.bukkit.plugins.metropolis.commands.MetropolisHomeGenerateCommand;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class MetropolisPlugin extends JavaPlugin {
	public static final Logger log=Logger.getLogger("Minecraft");
	
	public PluginDescriptionFile pdf = null;
	public WorldGuardPlugin worldGuard = null;
	public WorldEditPlugin worldEdit = null;
	public World world = null;
	public RegionManager regionManager = null;

	private List<PlayerHome> _occupiedHomes;
	
	private LoginListener _loginListener = null;
	
	int size = 0;
	
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
		
		
		Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
		if(plugin == null || !(plugin instanceof WorldGuardPlugin)){
			throw new RuntimeException("WorldGuard must be loaded first");
		}
		
		worldGuard = (WorldGuardPlugin) plugin;
		
		plugin = getServer().getPluginManager().getPlugin("WorldEdit");
		if(plugin == null || !(plugin instanceof WorldEditPlugin)){
			throw new RuntimeException("WorldEdit must be loaded first");
		}
		worldEdit = (WorldEditPlugin) plugin;
		
		world = getServer().getWorld(worldName);
		
		regionManager = worldGuard.getRegionManager(world);
	
		_occupiedHomes = new ArrayList<PlayerHome>();
		
		fillOccupiedHomes();

		ProtectedRegion cityRegion = regionManager.getRegion("City");
		if(cityRegion == null){
			cityRegion = new ProtectedCuboidRegion("City", getPlotMin(0, 0), this.getPlotMax(0, 0));
			cityRegion.setPriority(0);
			regionManager.addRegion(cityRegion);
			//TODO determine appropriate chest flags
		}
		
		//setupDatabase();
		
		if(_loginListener == null){
			_loginListener = new LoginListener(this);
		}

		Map<String, ProtectedRegion> regions = regionManager.getRegions();
		
		for(ProtectedRegion region: regions.values()){
			if(region.getId().startsWith("h_")){
				setHomeOccupied(region.getId().substring(2), region.getMinimumPoint(), region.getMaximumPoint());
			}
		}

		log.info(String.format("%s enabled", pdf.getFullName()));
		
		
		getCommand("metropolis-home-generate").setExecutor(new MetropolisHomeGenerateCommand(this));
		
		
		/*
		Set<String>strings = config.getKeys(true);
		log.info(String.valueOf(strings.size()));
		for(String str : strings){
			log.info(str);
		}
		*/
		
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
				_occupiedHomes.add(new PlayerHome(cuboidRegion));
			}
		}
		
		Collections.sort(_occupiedHomes);
	}

	PlayerHome getPlayerHome(Player player) {
		PlayerHome home = null;
		
		String regionName = "h_" + player.getName();
		ProtectedRegion homeRegion = regionManager.getRegion(regionName);
		
		log.info(homeRegion == null? "null" : homeRegion.toString());
		if(homeRegion == null){
			home = generateHome(player.getName());
		}
		
		return home;
	}

	/*
	@Override
	public List<Class<?>> getDatabaseClasses(){
		List<Class<?>> list = new ArrayList<Class<?>>();
		
		list.add(PlayerHome.class);
		
		return list;
	}
	/**/
	
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
		int homeIndex = 0;
		if(_occupiedHomes.size() == 0){
			if(size < 1){
				size=1;
			}
			
			expandCityRegion();
			
			return new Cuboid(getPlotMin(-1, -1), getPlotMax(-1, -1));
			
		}
		
		PlayerHome home = _occupiedHomes.get(homeIndex);
		
		for(int row = -size/2; row<=size/2; row++){
			for(int col = -size/2; col <= size/2; col++){
				if(home == null){
					expandCityRegion();
					return new Cuboid(getPlotMin(row, col), getPlotMax(row, col));
				}else if(Cuboid.isBlockLessThan(getPlotMin(row, col), home.getCuboid().getMin())){
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
		
		size++;
		return new Cuboid(getPlotMin(-size/2, -size/2), getPlotMax(-size/2, -size/2));
	}
	
	private void expandCityRegion() {
		ProtectedRegion cityRegion = regionManager.getRegion("City");
		if(cityRegion instanceof ProtectedCuboidRegion){
			ProtectedCuboidRegion region = (ProtectedCuboidRegion)cityRegion;
			BlockVector min = region.getMinimumPoint();
			BlockVector max = region.getMaximumPoint();
			
			min = new BlockVector(min.getBlockX() - plotSizeX, min.getBlockY(), min.getBlockZ() - plotSizeZ);
			max = new BlockVector(max.getBlockX() + plotSizeX, max.getBlockY(), max.getBlockZ() + plotSizeZ);
			
			region.setMinimumPoint(min);
			region.setMaximumPoint(max);
		}
	}

	public BlockVector getPlotMin(int row, int col){
		return new BlockVector(col * plotSizeX, 0, row * plotSizeZ);
	}
	
	public BlockVector getPlotMax(int row, int col){
		return new BlockVector(col * plotSizeX + plotSizeX-1, 128, row * plotSizeZ + plotSizeZ-1);
	}

	private void setHomeOccupied(String owner, BlockVector minimumPoint, BlockVector maximumPoint) {
		
		PlayerHome home = new PlayerHome(owner, minimumPoint, maximumPoint);
		if(!_occupiedHomes.contains(home)){
			_occupiedHomes.add(home);
			Collections.sort(_occupiedHomes);
		}
	}

	public Cuboid getSpawnCuboid(){
		// TODO Auto-generated method stub
		return null;
	}
	
	public Cuboid getNextUnusedHome(){
		// TODO Auto-generated method stub
		return null;
	}

	public PlayerHome generateHome(String string) {
		Cuboid plotCuboid = null;
		Cuboid homeCuboid = null;
		ProtectedRegion homeRegion = null;
		String regionName = "h_" + string;
		
		plotCuboid = findNextUnownedHomeRegion();
		homeCuboid = plotCuboid.inset(roadWidth/2, roadWidth/2);
		homeRegion = new ProtectedCuboidRegion(regionName, homeCuboid.getMin(), homeCuboid.getMax());
		DefaultDomain d = homeRegion.getOwners();
		d.addPlayer(string);
		homeRegion.setPriority(1);
		regionManager.addRegion(homeRegion);
	
		createRoads(plotCuboid);
		
		return new PlayerHome(homeRegion);
	}
	
}
