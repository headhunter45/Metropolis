package com.majinnaibu.bukkitplugins.metropolis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.majinnaibu.bukkitplugins.metropolis.commands.MetropolisDebugGenerateTestHomesCommand;
import com.majinnaibu.bukkitplugins.metropolis.commands.MetropolisFlagResetCommand;
import com.majinnaibu.bukkitplugins.metropolis.commands.MetropolisHomeGenerateCommand;
import com.majinnaibu.bukkitplugins.metropolis.commands.MetropolisHomeListCommand;
import com.majinnaibu.bukkitplugins.metropolis.commands.MetropolisPlotReserveCommand;
import com.majinnaibu.bukkitplugins.metropolis.eventlisteners.PlayerJoinListener;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class MetropolisPlugin extends JavaPlugin {
	public static final boolean DEBUG = true;
	public static final Logger log=Logger.getLogger("Minecraft");
	
	public PluginDescriptionFile pdf = null;
	public WorldGuardPlugin worldGuard = null;
	public WorldEditPlugin worldEdit = null;
	public World world = null;
	public RegionManager regionManager = null;

	private List<Plot> _occupiedPlots;
	
	private PlayerJoinListener _playerJoinListener = null;
	
	int size = 1;
	
	private int plotSizeX = 24;
	private int plotSizeZ = 24;
	private int roadWidth = 4;
	private int roadLevel = 62;
	private int spaceAboveRoad = 2;
	private int roadMaterial = 4;
	private boolean generateRoadSupports = true;
	private int roadSupportMaterial = 2;
	private String worldName = "world";
	private boolean generateFloor = false;
	private int floorMaterial = 2;
	private int spaceAboveFloor = 2;
	private boolean generateSign = false;
	private boolean generateSpawn = true;
	private boolean setWorldSpawn = true;
	private int spawnFloorMaterial = 4;
	private boolean generateFloorSupports = false;
	private int floorSupportMaterial = 1;
	private boolean generateWall = false;
	private int wallMaterial = 20;
	private int wallHeight = 128;
		
	private Cuboid _spawnCuboid = null;
	private Cuboid _cityCuboid = null;
	private ProtectedRegion _spawnRegion = null;
	private ProtectedRegion _cityRegion = null;
	
	
	@Override
	public void onDisable() {
		log.info(String.format("%s disabled", pdf.getFullName()));
	}

	@Override
	public void onEnable() {
		pdf = getDescription();
		
		Configuration config = getConfig();
		config.options().copyDefaults(true);
		
		plotSizeX = config.getInt("plot.sizeX");
		plotSizeZ = config.getInt("plot.sizeZ");
		generateFloor = config.getBoolean("plot.floor.generate");
		floorMaterial = config.getInt("plot.floor.material");
		spaceAboveFloor = config.getInt("plot.floor.clearSpaceAbove");
		generateFloorSupports = config.getBoolean("plot.floor.supports.generate");
		floorSupportMaterial = config.getInt("plot.floor.supports.material");
		generateSign = config.getBoolean("plot.sign.generate");
		roadWidth = config.getInt("road.width");
		spaceAboveRoad = config.getInt("road.clearSpaceAbove");
		roadLevel = config.getInt("road.level");
		roadMaterial = config.getInt("road.material");
		generateRoadSupports = config.getBoolean("road.supports.generate");
		roadSupportMaterial = config.getInt("road.supports.material");
		generateSpawn = config.getBoolean("spawn.generate");
		setWorldSpawn = config.getBoolean("spawn.setAsWorldSpawn");
		spawnFloorMaterial = config.getInt("spawn.material");
		generateWall = config.getBoolean("wall.generate");
		wallMaterial = config.getInt("wall.material");
		wallHeight = config.getInt("wall.material");
		worldName =config.getString("worldname");
		saveConfig();
		
		log.info(String.format("Metropolis: world name is %s", worldName));
		
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
			
		_cityRegion = regionManager.getRegion("City");
		if(_cityRegion == null){
			_cityRegion = new ProtectedCuboidRegion("City", getPlotMin(0, 0), this.getPlotMax(0, 0));
			_cityRegion.setPriority(0);
			_cityRegion.setFlag(DefaultFlag.PVP, StateFlag.State.DENY);
			_cityRegion.setFlag(DefaultFlag.MOB_DAMAGE, StateFlag.State.DENY);
			_cityRegion.setFlag(DefaultFlag.MOB_SPAWNING, StateFlag.State.DENY);
			_cityRegion.setFlag(DefaultFlag.CREEPER_EXPLOSION, StateFlag.State.DENY);
			_cityRegion.setFlag(DefaultFlag.ENDER_BUILD, StateFlag.State.DENY);
			_cityRegion.setFlag(DefaultFlag.GHAST_FIREBALL, StateFlag.State.DENY);
			_cityRegion.setFlag(DefaultFlag.TNT, StateFlag.State.DENY);
			_cityRegion.setFlag(DefaultFlag.LAVA_FLOW, StateFlag.State.DENY);
			_cityRegion.setFlag(DefaultFlag.SNOW_FALL, StateFlag.State.DENY);
			regionManager.addRegion(_cityRegion);
		}
		
		_cityCuboid = new Cuboid(_cityRegion.getMinimumPoint(), _cityRegion.getMaximumPoint());
		
		_spawnRegion = regionManager.getRegion("Spawn");
		if(_spawnRegion == null){
			_spawnRegion = new ProtectedCuboidRegion("Spawn", getPlotMin(0, 0), this.getPlotMax(0,  0));
			_spawnRegion.setPriority(1);
			_spawnRegion.setFlag(DefaultFlag.PVP, StateFlag.State.DENY);
			_spawnRegion.setFlag(DefaultFlag.MOB_DAMAGE, StateFlag.State.DENY);
			_spawnRegion.setFlag(DefaultFlag.MOB_SPAWNING, StateFlag.State.DENY);
			_spawnRegion.setFlag(DefaultFlag.CREEPER_EXPLOSION, StateFlag.State.DENY);
			_spawnRegion.setFlag(DefaultFlag.ENDER_BUILD, StateFlag.State.DENY);
			_spawnRegion.setFlag(DefaultFlag.GHAST_FIREBALL, StateFlag.State.DENY);
			_spawnRegion.setFlag(DefaultFlag.TNT, StateFlag.State.DENY);
			_spawnRegion.setFlag(DefaultFlag.LAVA_FLOW, StateFlag.State.DENY);
			_spawnRegion.setFlag(DefaultFlag.SNOW_FALL, StateFlag.State.DENY);
			regionManager.addRegion(_spawnRegion);
			setupSpawn();
		}
		
		_spawnCuboid = new Cuboid(_spawnRegion.getMinimumPoint(), _spawnRegion.getMaximumPoint());
		
		_occupiedPlots = new ArrayList<Plot>();
		fillOccupiedPlots();
		resizeCityRegion();

		if(_playerJoinListener == null){
			_playerJoinListener = new PlayerJoinListener(this);
		}

		log.info(String.format("%s enabled", pdf.getFullName()));
		
		getCommand("metropolis-home-generate").setExecutor(new MetropolisHomeGenerateCommand(this));
		getCommand("metropolis-home-list").setExecutor(new MetropolisHomeListCommand(this));
		getCommand("metropolis-flag-reset").setExecutor(new MetropolisFlagResetCommand(this));
		getCommand("metropolis-plot-reserve").setExecutor(new MetropolisPlotReserveCommand(this));
		if(DEBUG){
			getCommand("gentesthomes").setExecutor(new MetropolisDebugGenerateTestHomesCommand(this));
		}
	}
	
	private void setupSpawn() {
		BlockVector min = getPlotMin(0, 0);
		BlockVector max = getPlotMax(0, 0);
		
		if(generateSpawn){
			int x= 0;
			int y=roadLevel;
			int z=0;
			
			for(x=min.getBlockX(); x<= max.getBlockX(); x++){
				for(z=min.getBlockZ(); z<= max.getBlockZ(); z++){
					for(y=roadLevel+1; y<world.getMaxHeight(); y++){
						Block block = world.getBlockAt(x, y, z);
						block.setTypeId(0);
					}
					y=roadLevel;
					Block block = world.getBlockAt(x, y, z);
					block.setTypeId(spawnFloorMaterial);
				}
			}
		}
		
		if(setWorldSpawn){
			Cuboid spawnCuboid = new Cuboid(min, max);
			world.setSpawnLocation(spawnCuboid.getCenterX(), roadLevel+1, spawnCuboid.getCenterZ());
		}
	}

	private void fillOccupiedPlots(){
		_occupiedPlots.clear();
		
		for(ProtectedRegion region: regionManager.getRegions().values()){
			if(region instanceof ProtectedCuboidRegion){
				ProtectedCuboidRegion cuboidRegion = (ProtectedCuboidRegion) region;
				if(cuboidRegion.getId().startsWith("h_")){
					PlayerHome home = PlayerHome.get(region);
					_occupiedPlots.add(home);
				}else if(cuboidRegion.getId().startsWith("r_")){
					_occupiedPlots.add(Plot.get(cuboidRegion));
				}
			}
		}
		
		size=calculateCitySize();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return super.onCommand(sender, command, label, args);
	}

	public PlayerHome getPlayerHome(Player player) {
		PlayerHome home = null;
		
		String regionName = "h_" + player.getName();
		ProtectedRegion homeRegion = regionManager.getRegion(regionName);

		if(homeRegion == null){
			if(DEBUG){
				log.info(String.format("Creating home for player %s", player.getName()));
			}
			home = generateHome(player.getName());
		}else{
			home = new PlayerHome(homeRegion);
		}
		
		return home;
	}

	private void generateFloor(Cuboid plotCuboid){
		int x=0;
		int y=roadLevel;
		int z=0;
		
		for(x = plotCuboid.minX + roadWidth/2; x <= plotCuboid.maxX - roadWidth/2; x++){
			for(z=plotCuboid.minZ + roadWidth/2; z<=plotCuboid.maxZ - roadWidth/2; z++){
				Block block = world.getBlockAt(x, y, z);
				//Set the floor block
				block.setTypeId(floorMaterial);
				
				//Set the support
				if(generateFloorSupports && isPhysicsMaterial(block.getType())){
					Block blockUnder = world.getBlockAt(x, y-1, z);
					if(!isSolidMaterial(blockUnder.getType())){
						blockUnder.setTypeId(floorSupportMaterial);
					}
				}
				
				for(int i=0; i<spaceAboveFloor; i++){
					block = world.getBlockAt(x, y+1+i, z);
					block.setTypeId(0);
				}
			}
		}
	}
	
	private void createRoads(Cuboid plotCuboid) {
		if(roadWidth>0){
			int x=0;
			int y= roadLevel;
			int z=0;
			
			if(plotCuboid == null){
				if(DEBUG){
					log.warning("plotCuboid is null");
				}
				return;
			}
			
			for(x=plotCuboid.minX; x<plotCuboid.minX + roadWidth/2; x++){
				for(z=plotCuboid.minZ; z<=plotCuboid.maxZ; z++){
					Block block = world.getBlockAt(x, y, z);
					//Set the road block
					block.setTypeId(roadMaterial);
					//Set the support
					if(generateRoadSupports && isPhysicsMaterial(block.getType())){
						Block blockUnder = world.getBlockAt(x, y-1, z);
						if(!isSolidMaterial(blockUnder.getType())){
							blockUnder.setTypeId(roadSupportMaterial);
						}
					}
					
					//Clear the space above
					for(int y1 = 0; y1 < spaceAboveRoad; y1++){
						block = world.getBlockAt(x, y+y1+1, z);
						block.setTypeId(0);
					}
				}
			}
			
			for(x=plotCuboid.maxX - roadWidth/2+1; x<=plotCuboid.maxX; x++){
				for(z=plotCuboid.minZ; z<=plotCuboid.maxZ; z++){
					Block block = world.getBlockAt(x, y, z);
					//Set the road block
					block.setTypeId(roadMaterial);
					//Set the support
					if(generateRoadSupports && isPhysicsMaterial(block.getType())){
						Block blockUnder = world.getBlockAt(x, y-1, z);
						if(!isSolidMaterial(blockUnder.getType())){
							blockUnder.setTypeId(roadSupportMaterial);
						}
					}
					
					//Clear the space above
					for(int y1 = 0; y1 < spaceAboveRoad; y1++){
						block = world.getBlockAt(x, y+y1+1, z);
						block.setTypeId(0);
					}
				}
			}
			
			for(z=plotCuboid.minZ; z<plotCuboid.minZ + roadWidth/2; z++){
				for(x=plotCuboid.minX; x<=plotCuboid.maxX; x++){
					Block block = world.getBlockAt(x, y, z);
					//Set the road block
					block.setTypeId(roadMaterial);
					//Set the support
					if(generateRoadSupports && isPhysicsMaterial(block.getType())){
						Block blockUnder = world.getBlockAt(x, y-1, z);
						if(!isSolidMaterial(blockUnder.getType())){
							blockUnder.setTypeId(roadSupportMaterial);
						}
					}
					
					//Clear the space above
					for(int y1 = 0; y1 < spaceAboveRoad; y1++){
						block = world.getBlockAt(x, y+y1+1, z);
						block.setTypeId(0);
					}
				}
			}
			
			for(z=plotCuboid.maxZ - roadWidth/2+1; z<=plotCuboid.maxZ; z++){
				for(x=plotCuboid.minX; x<=plotCuboid.maxX; x++){
					Block block = world.getBlockAt(x, y, z);
					//Set the road block
					block.setTypeId(roadMaterial);
					//Set the support
					if(generateRoadSupports && isPhysicsMaterial(block.getType())){
						Block blockUnder = world.getBlockAt(x, y-1, z);
						if(!isSolidMaterial(blockUnder.getType())){
							blockUnder.setTypeId(roadSupportMaterial);
						}
					}
					
					//Clear the space above
					for(int y1 = 0; y1 < spaceAboveRoad; y1++){
						block = world.getBlockAt(x, y+y1+1, z);
						block.setTypeId(0);
					}
				}
			}
		}
	}
	
	private boolean isSolidMaterial(Material material) {
		return  material != Material.AIR && 
				material != Material.WATER && 
				material != Material.LAVA && 
				material != Material.TORCH && 
				material != Material.REDSTONE_TORCH_OFF && 
				material != Material.REDSTONE_TORCH_ON;
	}

	private boolean isPhysicsMaterial(Material material) {
		return material == Material.GRAVEL || material == Material.SAND;
	}

	public boolean isBlockOccupied(int row, int col){
		Cuboid cuboid = new Cuboid(getPlotMin(row, col), getPlotMax(row, col));
		for(Plot plot: _occupiedPlots){
			if(plot.getCuboid().intersects(cuboid)){
				return true;
			}
		}
		
		
		if(cuboid.intersects(_spawnCuboid)){
			return true;
		}

		return false;
	}
	
	private Cuboid findNextUnownedHomeRegion() {
		int row = 0;
		int col = 0;
		int ring = 0;
		boolean done = false;
		
		while(!done){
			row = -ring;
			col = -ring;
			
			for(col = -ring; col <= ring; col++){
				if(!isBlockOccupied(row, col)){
					if(row != 0 || col != 0){
						if(DEBUG){log.info(String.format("row: %d, col: %d", row, col));}
						return new Cuboid(getPlotMin(row, col), getPlotMax(row, col)); 
					}
				}
			}
			
			col = ring;
			for(row=-ring + 1; row < ring; row++){
				if(!isBlockOccupied(row, col)){
					if(row != 0 || col != 0){
						if(DEBUG){log.info(String.format("row: %d, col: %d", row, col));}
						return new Cuboid(getPlotMin(row, col), getPlotMax(row, col));
					}
				}
			}
			
			row = ring;
			for(col = ring; col >= -ring; col--){
				if(!isBlockOccupied(row, col)){
					if(row != 0 || col != 0){
						if(DEBUG){log.info(String.format("row: %d, col: %d", row, col));}
						return new Cuboid(getPlotMin(row, col), getPlotMax(row, col));
					}
				}
			}
			
			col = -ring;
			for(row = ring; row > -ring; row--){
				if(!isBlockOccupied(row, col)){
					if(row != 0 || col != 0){
						if(DEBUG){log.info(String.format("row: %d, col: %d", row, col));}
						return new Cuboid(getPlotMin(row, col), getPlotMax(row, col));
					}
				}
			}
			
			ring++;
		}
		
		if(DEBUG){log.info(String.format("row: %d, col: %d", row, col));}
		return new Cuboid(getPlotMin(row, col), getPlotMax(row, col));
	}
	
	private void resizeCityRegion() {
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
		int iSize = 3;
		
		for(Plot home: _occupiedPlots){
			int plotCol=Math.abs(getPlotXFromMin(home.getCuboid()));
			int plotRow=Math.abs(getPlotZFromMin(home.getCuboid()));
			if(DEBUG){log.info(String.format("col: %d, row: %d, iSize: %d", plotCol, plotRow, iSize));}
			iSize = Math.max(Math.max(plotRow*2+1, plotCol*2+1), iSize);
		}

		if(DEBUG){log.info(String.format("City size is %d", iSize));}
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
		if(!_occupiedPlots.contains(home)){
			_occupiedPlots.add(home);
		}
	}

	
	public PlayerHome generateHome(String playerName) {
		if(DEBUG){log.info(String.format("Generating home for %s", playerName));}
		Cuboid plotCuboid = null;
		Cuboid homeCuboid = null;
		ProtectedRegion homeRegion = null;
		String regionName = "h_" + playerName;
		homeRegion = regionManager.getRegion("h_" + playerName);
		if(homeRegion != null){
			return PlayerHome.get(homeRegion);
		}
		
		plotCuboid = findNextUnownedHomeRegion();
		homeCuboid = plotCuboid.inset(roadWidth/2, roadWidth/2);
		homeRegion = new ProtectedCuboidRegion(regionName, homeCuboid.getMin(), homeCuboid.getMax());
		homeRegion.setFlag(DefaultFlag.PVP, StateFlag.State.DENY);
		homeRegion.setFlag(DefaultFlag.MOB_DAMAGE, StateFlag.State.DENY);
		homeRegion.setFlag(DefaultFlag.MOB_SPAWNING, StateFlag.State.DENY);
		homeRegion.setFlag(DefaultFlag.CREEPER_EXPLOSION, StateFlag.State.DENY);
		homeRegion.setFlag(DefaultFlag.ENDER_BUILD, StateFlag.State.DENY);
		homeRegion.setFlag(DefaultFlag.GHAST_FIREBALL, StateFlag.State.DENY);
		homeRegion.setFlag(DefaultFlag.TNT, StateFlag.State.DENY);
		
		DefaultDomain d = homeRegion.getOwners();
		d.addPlayer(playerName);
		homeRegion.setPriority(1);
		regionManager.addRegion(homeRegion);
		try {
			regionManager.save();
		} catch (ProtectionDatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		setHomeOccupied(playerName, homeRegion.getMinimumPoint(), homeRegion.getMaximumPoint());
		
		createRoads(plotCuboid);
		
		if(generateFloor){
			generateFloor(plotCuboid);
		}
		
		if(DEBUG){log.info(String.format("generateSign: %s", String.valueOf(generateSign)));}
		if(generateSign){
			generateSign(plotCuboid, playerName);
		}
		
		if(DEBUG){log.info(String.format("Done generating home for %s", playerName));}
		
		return new PlayerHome(homeRegion);
	}

	private void generateSign(Cuboid plotCuboid, String playerName) {
		Block signBlock = world.getBlockAt(plotCuboid.getCenterX(), roadLevel+1, plotCuboid.getCenterZ());
		signBlock.setType(Material.SIGN_POST);
		Sign sign = (Sign)signBlock.getState();
		sign.setLine(0, "Home of");
		
		sign.setLine(1, playerName.substring(0, Math.min(15, playerName.length())));
		if(playerName.length() > 15){
			sign.setLine(2, playerName.substring(16, Math.min(30, playerName.length())));
			if(playerName.length() > 45){
				sign.setLine(3, playerName.substring(31, Math.min(45, playerName.length())));
			}
		}		
	}

	public List<Plot> getCityBlocks() {
		return Collections.unmodifiableList(_occupiedPlots);
	}
	
	public World getWorld(){
		return world;
	}

	public void reserveCuboid(String regionName, Cuboid cuboid) {
		ProtectedCuboidRegion reservedRegion = new ProtectedCuboidRegion(regionName, cuboid.getMin(), cuboid.getMax());
		reservedRegion.setFlag(DefaultFlag.PVP, StateFlag.State.DENY);
		reservedRegion.setFlag(DefaultFlag.MOB_DAMAGE, StateFlag.State.DENY);
		reservedRegion.setFlag(DefaultFlag.MOB_SPAWNING, StateFlag.State.DENY);
		reservedRegion.setFlag(DefaultFlag.CREEPER_EXPLOSION, StateFlag.State.DENY);
		reservedRegion.setFlag(DefaultFlag.ENDER_BUILD, StateFlag.State.DENY);
		reservedRegion.setFlag(DefaultFlag.GHAST_FIREBALL, StateFlag.State.DENY);
		reservedRegion.setFlag(DefaultFlag.TNT, StateFlag.State.DENY);
		reservedRegion.setFlag(DefaultFlag.LAVA_FLOW, StateFlag.State.DENY);
		reservedRegion.setFlag(DefaultFlag.SNOW_FALL, StateFlag.State.DENY);
		regionManager.addRegion(reservedRegion);
		
		_occupiedPlots.add(Plot.get(reservedRegion));
	}
}
