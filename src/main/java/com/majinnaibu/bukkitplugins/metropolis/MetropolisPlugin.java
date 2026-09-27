/*
This file is part of Metropolis.

Metropolis is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Metropolis is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with Metropolis. If not, see <https://www.gnu.org/licenses/agpl-3.0.txt>.
*/

package com.majinnaibu.bukkitplugins.metropolis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.majinnaibu.bukkitplugins.metropolis.commands.MetropolisCommand;
import com.majinnaibu.bukkitplugins.metropolis.commands.MetropolisDebugGenerateTestHomesCommand;
import com.majinnaibu.bukkitplugins.metropolis.commands.MetropolisFlagResetCommand;
import com.majinnaibu.bukkitplugins.metropolis.commands.MetropolisHomeAcquire;
import com.majinnaibu.bukkitplugins.metropolis.commands.MetropolisHomeEvictCommand;
import com.majinnaibu.bukkitplugins.metropolis.commands.MetropolisHomeGenerateCommand;
import com.majinnaibu.bukkitplugins.metropolis.commands.MetropolisHomeGoCommand;
import com.majinnaibu.bukkitplugins.metropolis.commands.MetropolisHomeListCommand;
import com.majinnaibu.bukkitplugins.metropolis.commands.MetropolisHomeMoveCommand;
import com.majinnaibu.bukkitplugins.metropolis.commands.MetropolisPlotGoCommand;
import com.majinnaibu.bukkitplugins.metropolis.commands.MetropolisPlotReserveCommand;
import com.majinnaibu.bukkitplugins.metropolis.eventlisteners.PlayerJoinListener;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MetropolisPlugin extends JavaPlugin {
  public static final boolean DEBUG = false;
  private static final int version = 1;

  public static final int ROAD_NORTH = 1;
  public static final int ROAD_SOUTH = 2;
  public static final int ROAD_EAST = 4;
  public static final int ROAD_WEST = 8;

  public PluginDescriptionFile pdf = null;
  public WorldGuardPlugin worldGuard = null;
  public WorldEditPlugin worldEdit = null;
  public World world = null;
  public RegionManager regionManager = null;

  private List<Plot> _occupiedPlots;
  private HashMap<UUID, List<Plot>> _ownedPlots;
  private HashMap<UUID, UserOverride> _userOverrides;
  private List<HomePermissionOverride> _permissionOverrides;
  private HashMap<UUID, Integer> _currentHomes;
  private List<Cuboid> _savedWorldGuardBounds = List.of();

  private PlayerJoinListener _playerJoinListener = null;

  int size = 1;

  private int plotSizeX = 24;
  private int plotSizeY = 256;
  private int plotSizeZ = 24;
  private int plotOffsetX;
  private int plotOffsetY;
  private int plotOffsetZ;
  private int gridSizeX = 28;
  private int gridSizeY = 256;
  private int maxLevels = 1;
  private int streetWidth = 4;
  private int avenueWidth = 4;
  private int gridSizeZ = 28;
  private int roadLevel = 62;
  private int avenueLevel = 62;
  private int streetSpaceAbove = 2;
  private int avenueSpaceAbove = 2;
  private Material streetMaterial = Material.COBBLESTONE;
  private Material avenueMaterial = Material.COBBLESTONE;
  private boolean generateStreetSupports = true;
  private boolean generateAvenueSupports = true;
  private Material streetSupportMaterial = Material.STONE;
  private Material avenueSupportMaterial = Material.STONE;
  private boolean generateStreetStairs;
  private Material streetStairMaterial = Material.COBBLESTONE_STAIRS;
  private int streetStairWidth;
  private int streetStairsEveryNBlocks;
  private boolean generateAvenueStairs;
  private Material avenueStairMaterial = Material.COBBLESTONE_STAIRS;
  private int avenueStairWidth;
  private int avenueStairsEveryNBlocks;
  private String worldName = "world";
  private boolean generateFloor = false;
  private Material floorMaterial = Material.GRASS_BLOCK;
  private int spaceAboveFloor = 2;
  private boolean generateSign = false;
  private boolean generateSpawn = true;
  private boolean setWorldSpawn = true;
  private int spawnSizeX = 1;
  private int spawnSizeY = 1;
  private int spawnSizeZ = 1;
  private Material spawnFloorMaterial = Material.COBBLESTONE;
  private boolean generateFloorSupports = false;
  private Material floorSupportMaterial = Material.STONE;
  private boolean generateWall = false;
  private Material wallMaterial = Material.GLASS;
  private int wallHeight = 128;
  int _maxPlots = 1;
  int _plotMultiplier = 1;
  private int initialHomeCount = 1;

  private Cuboid _spawnCuboid = null;
  private Cuboid _cityCuboid = null;
  private ProtectedRegion _spawnRegion = null;
  private ProtectedRegion _cityRegion = null;
  private PlotGridLayout plotGridLayout;

  @Override
  public void onDisable() {
    getLogger().info(String.format("%s disabled", pdf.getFullName()));
  }

  @Override
  public void onEnable() {
    pdf = getDescription();

    _ownedPlots = new HashMap<UUID, List<Plot>>();
    _userOverrides = new HashMap<UUID, UserOverride>();
    _currentHomes = new HashMap<UUID, Integer>();
    loadCurrentHomes();

    if (DEBUG) {
      getLogger().info("Checking config");
    }
    Configuration config = getConfig();
    if (!config.contains("version")) {
      // new
      if (DEBUG) {
        getLogger().info("No config exists.  Assuming new installation.");
      }
    } else {
      int configVersion = safeGetIntFromConfig(config, "version");
      if (configVersion < version) {
        if (DEBUG) {
          getLogger()
              .info(
                  String.format(
                      "Updating config from version v%s to v%s.", configVersion, version));
        }
        if (configVersion != version) {
          // upgrade config
          config.set("version", version);
        }
        saveConfig();
        if (DEBUG) {
          getLogger().info("Config updated");
        }
      }
    }

    config.set("version", version);
    saveConfig();

    config.options().copyDefaults(true);

    if (DEBUG) {
      getLogger().info("Reading configuration from file.");
    }
    plotSizeX = safeGetIntFromConfig(config, "plot.sizeX");
    plotSizeY = optionalIntFromConfig(config, "plot.sizeY", 256);
    plotSizeZ = safeGetIntFromConfig(config, "plot.sizeZ");
    plotOffsetX = safeGetIntFromConfig(config, "plot.offsetX");
    plotOffsetY = safeGetIntFromConfig(config, "plot.offsetY");
    plotOffsetZ = safeGetIntFromConfig(config, "plot.offsetZ");
    maxLevels = optionalIntFromConfig(config, "plot.maxLevels", "maxLevels", 1);
    generateFloor = safeGetBooleanFromConfig(config, "plot.floor.generate");
    floorMaterial = safeGetMaterialFromConfig(config, "plot.floor.material");
    spaceAboveFloor = safeGetIntFromConfig(config, "plot.floor.clearSpaceAbove");
    generateFloorSupports = safeGetBooleanFromConfig(config, "plot.floor.supports.generate");
    floorSupportMaterial = safeGetMaterialFromConfig(config, "plot.floor.supports.material");
    generateSign = safeGetBooleanFromConfig(config, "plot.sign.generate");
    streetWidth = safeGetIntFromConfig(config, "road.streets.width");
    avenueWidth = safeGetIntFromConfig(config, "road.avenues.width");
    streetSpaceAbove = safeGetIntFromConfig(config, "road.streets.clearSpaceAbove");
    avenueSpaceAbove = safeGetIntFromConfig(config, "road.avenues.clearSpaceAbove");
    roadLevel = safeGetIntFromConfig(config, "road.streets.level");
    avenueLevel = safeGetIntFromConfig(config, "road.avenues.level");
    streetMaterial = safeGetMaterialFromConfig(config, "road.streets.material");
    avenueMaterial = safeGetMaterialFromConfig(config, "road.avenues.material");
    generateStreetSupports = safeGetBooleanFromConfig(config, "road.streets.supports.generate");
    generateAvenueSupports = safeGetBooleanFromConfig(config, "road.avenues.supports.generate");
    streetSupportMaterial = safeGetMaterialFromConfig(config, "road.streets.supports.material");
    avenueSupportMaterial = safeGetMaterialFromConfig(config, "road.avenues.supports.material");
    generateStreetStairs = safeGetBooleanFromConfig(config, "road.streets.stairs.generate");
    if (generateStreetStairs) {
      streetStairMaterial = safeGetMaterialFromConfig(config, "road.streets.stairs.material");
      streetStairWidth = safeGetIntFromConfig(config, "road.streets.stairs.width");
      streetStairsEveryNBlocks = safeGetIntFromConfig(config, "road.streets.stairs.everyNBlocks");

      if (streetStairWidth < 1 || streetStairWidth >= streetWidth) {
        getLogger()
            .severe(
                "road.streets.stairs.width must be positive and narrower than road.streets.width;"
                    + " street stairs are disabled.");
        generateStreetStairs = false;
      }
      if (streetStairsEveryNBlocks < 1) {
        getLogger()
            .severe(
                "road.streets.stairs.everyNBlocks must be positive; street stairs are disabled.");
        generateStreetStairs = false;
      }
      if (!(streetStairMaterial.createBlockData() instanceof Stairs)) {
        getLogger()
            .severe(
                "road.streets.stairs.material must be a stair block; street stairs are disabled.");
        generateStreetStairs = false;
      }
    }
    generateAvenueStairs = safeGetBooleanFromConfig(config, "road.avenues.stairs.generate");
    if (generateAvenueStairs) {
      avenueStairMaterial = safeGetMaterialFromConfig(config, "road.avenues.stairs.material");
      avenueStairWidth = safeGetIntFromConfig(config, "road.avenues.stairs.width");
      avenueStairsEveryNBlocks = safeGetIntFromConfig(config, "road.avenues.stairs.everyNBlocks");

      if (avenueStairWidth < 1 || avenueStairWidth >= avenueWidth) {
        getLogger()
            .severe(
                "road.avenues.stairs.width must be positive and narrower than road.avenues.width;"
                    + " avenue stairs are disabled.");
        generateAvenueStairs = false;
      }
      if (avenueStairsEveryNBlocks < 1) {
        getLogger()
            .severe(
                "road.avenues.stairs.everyNBlocks must be positive; avenue stairs are disabled.");
        generateAvenueStairs = false;
      }
      if (!(avenueStairMaterial.createBlockData() instanceof Stairs)) {
        getLogger()
            .severe(
                "road.avenues.stairs.material must be a stair block; avenue stairs are disabled.");
        generateAvenueStairs = false;
      }
    }
    generateSpawn = safeGetBooleanFromConfig(config, "spawn.generate");
    setWorldSpawn = safeGetBooleanFromConfig(config, "spawn.setAsWorldSpawn");
    spawnSizeX = optionalIntFromConfig(config, "spawn.sizeX", 1);
    spawnSizeY = optionalIntFromConfig(config, "spawn.sizeY", 1);
    spawnSizeZ = optionalIntFromConfig(config, "spawn.sizeZ", 1);
    spawnFloorMaterial = safeGetMaterialFromConfig(config, "spawn.material");
    generateWall = safeGetBooleanFromConfig(config, "wall.generate");
    wallMaterial = safeGetMaterialFromConfig(config, "wall.material");
    wallHeight = safeGetIntFromConfig(config, "wall.height");
    worldName = safeGetStringFromConfig(config, "worldname");
    _plotMultiplier = safeGetIntFromConfig(config, "plot.multiplier");
    _maxPlots = safeGetIntFromConfig(config, "plot.maxPerPlayer");
    initialHomeCount = normalizeInitialHomeCount(safeGetIntFromConfig(config, "plot.initial"));

    buildUserOverrides();
    _permissionOverrides =
        HomePermissionOverride.load(
            config.getConfigurationSection("permissionOverrides"),
            warning -> getLogger().warning(warning));

    saveConfig();
    if (DEBUG) {
      getLogger().info("Done reading config.");
    }

    getLogger().info(String.format("Metropolis: world name is %s", worldName));

    Server server = getServer();
    if (server == null) {
      throw new RuntimeException("getServer() is null");
    }
    PluginManager pluginManager = server.getPluginManager();
    if (pluginManager == null) {
      throw new RuntimeException("server.getPluginManager() is null");
    }

    Plugin plugin = pluginManager.getPlugin("WorldGuard");
    if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
      throw new RuntimeException("WorldGuard must be loaded first");
    }

    worldGuard = (WorldGuardPlugin) plugin;

    plugin = pluginManager.getPlugin("WorldEdit");
    if (plugin == null || !(plugin instanceof WorldEditPlugin)) {
      throw new RuntimeException("WorldEdit must be loaded first");
    }
    worldEdit = (WorldEditPlugin) plugin;

    world = server.getWorld(worldName);
    if (world == null) {
      throw new RuntimeException(String.format("The world %s does not exist", worldName));
    }

    if (streetWidth < 0 || avenueWidth < 0) {
      throw new IllegalArgumentException("Road widths must not be negative.");
    }
    plotGridLayout =
        new PlotGridLayout(
            plotSizeX,
            plotSizeY,
            plotSizeZ,
            streetWidth,
            avenueWidth,
            plotOffsetX,
            plotOffsetY,
            plotOffsetZ,
            roadLevel);
    gridSizeX = plotGridLayout.gridSizeX();
    gridSizeY = plotGridLayout.gridSizeY();
    gridSizeZ = plotGridLayout.gridSizeZ();

    regionManager =
        WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
    if (regionManager == null) {
      throw new RuntimeException("WorldGuard regions don't seem to be enabled.");
    }

    _cityRegion = regionManager.getRegion("City");
    if (_cityRegion == null) {
      _cityRegion =
          new ProtectedCuboidRegion("City", getPlotMin(0, 0, 1), this.getPlotMax(0, 0, 1));
      _cityRegion.setPriority(0);
      _cityRegion.setFlag(Flags.PVP, StateFlag.State.DENY);
      _cityRegion.setFlag(Flags.MOB_DAMAGE, StateFlag.State.DENY);
      _cityRegion.setFlag(Flags.MOB_SPAWNING, StateFlag.State.DENY);
      _cityRegion.setFlag(Flags.CREEPER_EXPLOSION, StateFlag.State.DENY);
      _cityRegion.setFlag(Flags.ENDER_BUILD, StateFlag.State.DENY);
      _cityRegion.setFlag(Flags.GHAST_FIREBALL, StateFlag.State.DENY);
      _cityRegion.setFlag(Flags.TNT, StateFlag.State.DENY);
      _cityRegion.setFlag(Flags.LAVA_FLOW, StateFlag.State.DENY);
      _cityRegion.setFlag(Flags.SNOW_FALL, StateFlag.State.DENY);
      regionManager.addRegion(_cityRegion);
    }

    _cityCuboid = new Cuboid(_cityRegion.getMinimumPoint(), _cityRegion.getMaximumPoint());

    _spawnRegion = regionManager.getRegion("Spawn");
    if (_spawnRegion == null) {
      Cuboid spawnCuboid = createSpawnCuboid();
      _spawnRegion = new ProtectedCuboidRegion("Spawn", spawnCuboid.getMin(), spawnCuboid.getMax());
      _spawnRegion.setPriority(1);
      _spawnRegion.setFlag(Flags.PVP, StateFlag.State.DENY);
      _spawnRegion.setFlag(Flags.MOB_DAMAGE, StateFlag.State.DENY);
      _spawnRegion.setFlag(Flags.MOB_SPAWNING, StateFlag.State.DENY);
      _spawnRegion.setFlag(Flags.CREEPER_EXPLOSION, StateFlag.State.DENY);
      _spawnRegion.setFlag(Flags.ENDER_BUILD, StateFlag.State.DENY);
      _spawnRegion.setFlag(Flags.GHAST_FIREBALL, StateFlag.State.DENY);
      _spawnRegion.setFlag(Flags.TNT, StateFlag.State.DENY);
      _spawnRegion.setFlag(Flags.LAVA_FLOW, StateFlag.State.DENY);
      _spawnRegion.setFlag(Flags.SNOW_FALL, StateFlag.State.DENY);
      regionManager.addRegion(_spawnRegion);

      _spawnCuboid = new Cuboid(_spawnRegion.getMinimumPoint(), _spawnRegion.getMaximumPoint());

      setupSpawn();
    } else {
      _spawnCuboid = new Cuboid(_spawnRegion.getMinimumPoint(), _spawnRegion.getMaximumPoint());
    }

    _spawnCuboid = new Cuboid(_spawnRegion.getMinimumPoint(), _spawnRegion.getMaximumPoint());

    if (DEBUG) {
      /*
      getLogger().info("Metropolis: first 25 plots");

      int n = 5;

      for (int ix=-n; ix<=n; ix++){
      	for (int iz=-n; iz<=n; iz++){
      		getLogger().info(getCuboid(iz, ix).toString());
      	}
      }
      */

      getLogger()
          .info(String.format("streetWidth = %d, avenueWidth = %d", streetWidth, avenueWidth));
    }

    _occupiedPlots = new ArrayList<Plot>();
    fillOccupiedPlots();
    resizeCityRegion();

    _playerJoinListener = new PlayerJoinListener(this);
    getServer().getPluginManager().registerEvents(_playerJoinListener, this);

    getLogger().info(String.format("%s enabled", pdf.getFullName()));

    RegisterCommandHandler("metropolis", new MetropolisCommand(this));

    RegisterCommandHandler(
        "metropolis-debug-generatetesthomes", new MetropolisDebugGenerateTestHomesCommand(this));

    RegisterCommandHandler("metropolis-flag-reset", new MetropolisFlagResetCommand(this));

    RegisterCommandHandler("metropolis-home-evict", new MetropolisHomeEvictCommand(this));
    RegisterCommandHandler("metropolis-home-acquire", new MetropolisHomeAcquire(this));
    RegisterCommandHandler("metropolis-home-generate", new MetropolisHomeGenerateCommand(this));
    RegisterCommandHandler("metropolis-home-go", new MetropolisHomeGoCommand(this));
    RegisterCommandHandler("metropolis-home-list", new MetropolisHomeListCommand(this));
    RegisterCommandHandler("metropolis-home-move", new MetropolisHomeMoveCommand(this));

    RegisterCommandHandler("metropolis-plot-go", new MetropolisPlotGoCommand(this));
    RegisterCommandHandler("metropolis-plot-reserve", new MetropolisPlotReserveCommand(this));
  }

  private void loadCurrentHomes() {
    File homesFile = new File(getDataFolder(), "currentHomes.yml");
    try {
      _currentHomes.putAll(
          CurrentHomesStore.load(
              homesFile, ownerName -> getServer().getOfflinePlayer(ownerName).getUniqueId()));
      saveCurrentHomes();
    } catch (IOException | org.bukkit.configuration.InvalidConfigurationException e) {
      getLogger().log(java.util.logging.Level.SEVERE, "Unable to load currentHomes.yml", e);
    }
  }

  private void buildUserOverrides() {
    if (getConfig().isList("userOverrides")) {
      List<?> list = getConfig().getList("userOverrides");

      for (Object o2 : list) {
        if (o2 instanceof HashMap<?, ?>) {
          HashMap<?, ?> map = (HashMap<?, ?>) o2;
          String username = "";
          if (map.containsKey("username")) {
            Object o3 = map.get("username");
            if (o3 instanceof String) {
              username = (String) o3;
            }
          }

          int plotMultiplier = _plotMultiplier;
          if (map.containsKey("plotMultiplier")) {
            Object o3 = map.get("plotMultiplier");
            if (o3 instanceof Integer) {
              plotMultiplier = (Integer) o3;
            }
          }

          int maxPlots = _maxPlots;
          if (map.containsKey("maxPlots")) {
            Object o3 = map.get("maxPlots");
            if (o3 instanceof Integer) {
              maxPlots = (Integer) o3;
            }
          }

          UUID playerId = getServer().getOfflinePlayer(username).getUniqueId();
          UserOverride override = new UserOverride(username, plotMultiplier, maxPlots);
          _userOverrides.put(playerId, override);
        }
      }
    }
  }

  private Cuboid getCuboid(int row, int col) {
    // This is only used for debug info
    BlockVector3 min = getPlotMin(row, col, 1);
    BlockVector3 max = getPlotMax(row, col, 1);
    return new Cuboid(min, max);
  }

  private void RegisterCommandHandler(String commandName, CommandExecutor executor) {
    PluginCommand command = getCommand(commandName);
    if (command == null) {
      throw new RuntimeException(
          String.format("The command %s does not appear to exist", commandName));
    } else {
      command.setExecutor(executor);
    }
  }

  private String safeGetStringFromConfig(Configuration config, String name) {
    if (config.isString(name)) {
      return config.getString(name);
    } else {
      throwInvalidConfigException();
      return null;
    }
  }

  private boolean safeGetBooleanFromConfig(Configuration config, String... names) {
    for (String name : names) {
      if (config.isBoolean(name)) {
        return config.getBoolean(name);
      }
    }
    throwInvalidConfigException();
    return false;
  }

  private int safeGetIntFromConfig(Configuration config, String... names) {
    for (String name : names) {
      if (config.isInt(name)) {
        return config.getInt(name);
      }
    }
    throwInvalidConfigException();
    return 0;
  }

  private int optionalIntFromConfig(Configuration config, String[] names, int defaultValue) {
    for (String name : names) {
      if (config.isInt(name)) {
        return config.getInt(name);
      }
    }
    return defaultValue;
  }

  private int optionalIntFromConfig(Configuration config, String name, int defaultValue) {
    return config.isInt(name) ? config.getInt(name) : defaultValue;
  }

  private int optionalIntFromConfig(
      Configuration config, String firstName, String secondName, int defaultValue) {
    if (config.isInt(firstName)) {
      return config.getInt(firstName);
    }
    if (config.isInt(secondName)) {
      return config.getInt(secondName);
    }
    return defaultValue;
  }

  private Material safeGetMaterialFromConfig(Configuration config, String... names) {
    for (String name : names) {
      Material material = null;
      if (config.isInt(name)) {
        material =
            switch (config.getInt(name)) {
              case 1 -> Material.STONE;
              case 2 -> Material.GRASS_BLOCK;
              case 4 -> Material.COBBLESTONE;
              case 7 -> Material.BEDROCK;
              default -> null;
            };
      } else if (config.isString(name)) {
        material = Material.matchMaterial(config.getString(name));
      }
      if (material != null) {
        return material;
      }
    }
    getLogger().severe("Invalid material configured at " + String.join(" or ", names));
    throwInvalidConfigException();
    return Material.STONE;
  }

  private void throwInvalidConfigException() {
    getLogger()
        .info(
            "Metropolis: ERROR config file is invalid.  Please correct Metropolis/config.yml and restart the server.");
    throw new RuntimeException("Config file is invalid.");
  }

  private void setupSpawn() {
    getLogger().info("Metropolis: Spawn Cuboid is " + _spawnCuboid.toString());

    if (generateSpawn) {
      int x = 0;
      int y = _spawnCuboid.minY;
      int z = 0;

      // floor
      for (x = _spawnCuboid.getMinX(); x <= _spawnCuboid.getMaxX(); x++) {
        for (z = _spawnCuboid.getMinZ(); z <= _spawnCuboid.getMaxZ(); z++) {
          for (y = _spawnCuboid.minY + 1; y <= _spawnCuboid.maxY; y++) {
            Block block = world.getBlockAt(x, y, z);
            block.setType(Material.AIR);
          }

          y = _spawnCuboid.minY;
          Block block = world.getBlockAt(x, y, z);
          block.setType(spawnFloorMaterial);
        }
      }

      // roads
      createRoads(_spawnCuboid);
    }

    if (setWorldSpawn) {
      world.setSpawnLocation(
          _spawnCuboid.getCenterX(), _spawnCuboid.minY + 1, _spawnCuboid.getCenterZ());
    }
  }

  private Cuboid createSpawnCuboid() {
    Cuboid spawnCuboid =
        SpawnLayout.bounds(
            spawnSizeX,
            spawnSizeY,
            spawnSizeZ,
            plotSizeX,
            plotSizeY,
            plotSizeZ,
            avenueWidth,
            streetWidth,
            roadLevel,
            plotOffsetX,
            plotOffsetY,
            plotOffsetZ);
    if (!SpawnLayout.withinBuildHeight(spawnCuboid, world.getMinHeight(), world.getMaxHeight())) {
      getLogger()
          .severe(
              String.format(
                  "Configured spawn bounds (%d to %d) exceed world build height (%d to %d).",
                  spawnCuboid.minY,
                  spawnCuboid.maxY,
                  world.getMinHeight(),
                  world.getMaxHeight() - 1));
      throw new IllegalArgumentException("Configured spawn height exceeds world build height.");
    }
    return spawnCuboid;
  }

  private void fillOccupiedPlots() {
    _occupiedPlots.clear();
    _ownedPlots.clear();

    for (ProtectedRegion region : regionManager.getRegions().values()) {
      if (region instanceof ProtectedCuboidRegion) {
        ProtectedCuboidRegion cuboidRegion = (ProtectedCuboidRegion) region;
        if (cuboidRegion.getId().startsWith("h_")) {
          PlayerHome home = PlayerHome.get(region);
          _occupiedPlots.add(home);
          addOwnedPlot(home.getPlayerId(), home);
        } else if (cuboidRegion.getId().startsWith("r_")) {
          _occupiedPlots.add(Plot.get(cuboidRegion));
        }
      }
    }

    for (UUID playerId : _ownedPlots.keySet()) {
      if (!_currentHomes.containsKey(playerId)) {
        PlayerHome firstHome = getFirstOwnedHome(playerId);
        if (firstHome != null) {
          _currentHomes.put(playerId, firstHome.getNumber());
        }
      }
    }

    size = calculateCitySize();
    saveCurrentHomes();
  }

  private void addOwnedPlot(UUID playerId, Plot plot) {
    if (_ownedPlots.containsKey(playerId)) {
      List<Plot> plots = _ownedPlots.get(playerId);
      plots.add(plot);
    } else {
      List<Plot> plots = new ArrayList<Plot>();
      plots.add(plot);
      _ownedPlots.put(playerId, plots);
    }
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    return super.onCommand(sender, command, label, args);
  }

  public PlayerHome getPlayerHome(Player player) {
    PlayerHome home = null;

    UUID playerId = player.getUniqueId();
    int homeNumber = _currentHomes.getOrDefault(playerId, 1);
    String regionName = String.format("h_%d_%s", homeNumber, player.getUniqueId());
    ProtectedRegion homeRegion = regionManager.getRegion(regionName);

    if (homeRegion == null) {
      PlayerHome existingHome = getOwnedHome(playerId, homeNumber);
      if (existingHome != null) {
        existingHome.setPlayerName(player.getName());
        return existingHome;
      }
      existingHome = getFirstOwnedHome(playerId);
      if (existingHome != null) {
        existingHome.setPlayerName(player.getName());
        setHome(playerId, existingHome.getNumber());
        return existingHome;
      }
      if (!shouldAutoGenerateInitialHome()) {
        return null;
      }
      if (DEBUG) {
        getLogger().info(String.format("Creating home for player %s", player.getName()));
      }
      home = generateHome(player, 1);
    } else {
      home = new PlayerHome(homeRegion);
      home.setPlayerName(player.getName());
    }

    return home;
  }

  public static int normalizeInitialHomeCount(int count) {
    return Math.max(0, count);
  }

  public static int normalizeMaxLevels(int count) {
    return Math.max(1, count);
  }

  public static int clampMaxLevels(int requestedLevels, int availableLevels) {
    int normalizedRequested = normalizeMaxLevels(requestedLevels);
    int normalizedAvailable = Math.max(0, availableLevels);
    return Math.min(normalizedRequested, normalizedAvailable);
  }

  public static boolean shouldAutoGenerateInitialHome(int count) {
    return count > 0;
  }

  public int getInitialHomeCount() {
    return initialHomeCount;
  }

  public void setInitialHomeCount(int count) {
    initialHomeCount = normalizeInitialHomeCount(count);
  }

  public boolean shouldAutoGenerateInitialHome() {
    return shouldAutoGenerateInitialHome(initialHomeCount);
  }

  private void generateFloor(Cuboid plotCuboid) {
    int x = 0;
    int y = plotCuboid.minY;
    int z = 0;

    for (x = plotCuboid.minX; x <= plotCuboid.maxX; x++) {
      for (z = plotCuboid.minZ; z <= plotCuboid.maxZ; z++) {
        setFloor(x, y, z);

        clearSpaceAbove(x, y, z);
      }
    }
  }

  private void clearSpaceAbove(int x, int y, int z) {
    Block block = null;

    for (int i = 0; i < spaceAboveFloor; i++) {
      block = world.getBlockAt(x, y + 1 + i, z);
      block.setType(Material.AIR);
    }
  }

  private void setFloor(int x, int y, int z) {
    // if(DEBUG){getLogger().info(String.format("setting road at (%d, %d, %d)", x, y, z));}

    Block block = world.getBlockAt(x, y, z);

    // Set the floor block
    block.setType(floorMaterial);

    // Set the support
    if (generateFloorSupports && isPhysicsMaterial(block.getType())) {
      Block blockUnder = world.getBlockAt(x, y - 1, z);
      if (!isSolidMaterial(blockUnder.getType())) {
        blockUnder.setType(floorSupportMaterial);
      }
    }
  }

  private void createRoads(Cuboid plotCuboid) {
    if (plotCuboid == null) {
      if (DEBUG) {
        getLogger().warning("plotCuboid is null");
      }
      return;
    }

    if (streetWidth > 0 || avenueWidth > 0) {
      for (RoadLayout.BlockPosition roadBlock :
          RoadLayout.surroundingRoad(plotCuboid, streetWidth, avenueWidth)) {
        int roadY =
            roadBlock.roadType() == RoadLayout.RoadType.AVENUE
                ? plotCuboid.minY + avenueLevel - roadLevel
                : plotCuboid.minY;
        Set<AvenueStairwayLayout.Step> stairOpenings =
            getUpperRoadOpenings(plotCuboid, roadY, roadBlock.roadType());
        setRoad(roadBlock.x(), roadY, roadBlock.z(), roadBlock.roadType(), stairOpenings);
      }

      int upperLevel = Math.floorDiv(plotCuboid.minY - roadLevel, getLevelPitchY());
      if (upperLevel > 0 && getLevelStartY(upperLevel) == plotCuboid.minY) {
        generateRoadStairs(plotCuboid, upperLevel - 1, RoadLayout.RoadType.AVENUE);
        generateRoadStairs(plotCuboid, upperLevel - 1, RoadLayout.RoadType.STREET);
      }
    }
  }

  private void setRoad(
      int x,
      int y,
      int z,
      RoadLayout.RoadType roadType,
      Set<AvenueStairwayLayout.Step> stairOpenings) {
    if (stairOpenings.contains(new AvenueStairwayLayout.Step(x, y, z))) {
      return;
    }

    boolean useAvenueSettings = roadType == RoadLayout.RoadType.AVENUE;
    RoadBlockWriter.build(
        world,
        x,
        y,
        z,
        useAvenueSettings ? avenueMaterial : streetMaterial,
        useAvenueSettings ? avenueSupportMaterial : streetSupportMaterial,
        useAvenueSettings ? generateAvenueSupports : generateStreetSupports,
        useAvenueSettings ? avenueSpaceAbove : streetSpaceAbove);
  }

  private boolean isSolidMaterial(Material material) {
    return material.isBlock()
        && material != Material.AIR
        && material != Material.WATER
        && material != Material.LAVA
        && material != Material.TORCH
        && material != Material.REDSTONE_TORCH;
  }

  private boolean isPhysicsMaterial(Material material) {
    return material == Material.GRAVEL || material == Material.SAND;
  }

  public boolean isBlockOccupied(int row, int col) {
    return isBlockOccupied(row, col, 0, 1);
  }

  public boolean isBlockOccupied(int row, int col, int level, int plotMultiplier) {
    Cuboid cuboid =
        new Cuboid(
            getGridMin(row, col, plotMultiplier, level),
            getGridMax(row, col, plotMultiplier, level));
    for (Plot plot : _occupiedPlots) {
      if (plot.getCuboid().intersects(cuboid)) {
        return true;
      }
    }

    if (cuboid.intersects(_spawnCuboid)) {
      return true;
    }

    return WorldGuardRegionOccupancy.overlapsAny(cuboid, _savedWorldGuardBounds);
  }

  private boolean areBlocksOccupied(int row, int col, int i) {
    return areBlocksOccupied(row, col, i, 0);
  }

  private boolean areBlocksOccupied(int row, int col, int i, int level) {
    for (int ix = col; ix < col + i; ix++) {
      for (int iy = row; iy < row + i; iy++) {
        if (isBlockOccupied(iy, ix, level, 1)) {
          return true;
        }
      }
    }

    return false;
  }

  private Cuboid findNextUnownedHomeRegion(int plotMultiplier) {
    refreshSavedWorldGuardBounds();

    int row = 0;
    int col = 0;
    int ring = 0;
    int min = -ring;
    int max = ring - (plotMultiplier - 1);
    boolean done = false;
    int levelCount = getEffectiveMaxLevels();
    if (levelCount == 0) {
      getLogger().severe("No configured plot level fits within the world's build height.");
      return null;
    }

    while (!done) {
      row = min;
      col = min;

      for (int level = 0; level < levelCount; level++) {
        // Top
        for (col = min; col <= max; col++) {
          if (isAvailableSupportedPlot(row, col, plotMultiplier, level)) {
            if (DEBUG) {
              getLogger().info(String.format("row: %d, col: %d, level: %d", row, col, level));
            }
            return new Cuboid(
                getPlotMin(row, col, plotMultiplier, level),
                getPlotMax(row, col, plotMultiplier, level));
          }
        }

        // Right side
        col = max;
        for (row = min + 1; row < max; row++) {
          if (isAvailableSupportedPlot(row, col, plotMultiplier, level)) {
            if (DEBUG) {
              getLogger().info(String.format("row: %d, col: %d, level: %d", row, col, level));
            }
            return new Cuboid(
                getPlotMin(row, col, plotMultiplier, level),
                getPlotMax(row, col, plotMultiplier, level));
          }
        }

        // Bottom
        row = max;
        for (col = max; col >= min; col--) {
          if (isAvailableSupportedPlot(row, col, plotMultiplier, level)) {
            if (DEBUG) {
              getLogger().info(String.format("row: %d, col: %d, level: %d", row, col, level));
            }
            return new Cuboid(
                getPlotMin(row, col, plotMultiplier, level),
                getPlotMax(row, col, plotMultiplier, level));
          }
        }

        // Left
        col = min;
        for (row = max; row > min; row--) {
          if (isAvailableSupportedPlot(row, col, plotMultiplier, level)) {
            if (row != 0 || col != 0) {
              if (DEBUG) {
                getLogger().info(String.format("row: %d, col: %d, level: %d", row, col, level));
              }
              return new Cuboid(
                  getPlotMin(row, col, plotMultiplier, level),
                  getPlotMax(row, col, plotMultiplier, level));
            }
          }
        }
      }

      ring++;
      min = -ring;
      max = ring - (plotMultiplier - 1);
      if (ring > 256) {
        done = true;
      }
    }

    getLogger()
        .severe("Unable to find an available supported plot within the allocation search area.");
    return null;
  }

  private void refreshSavedWorldGuardBounds() {
    _savedWorldGuardBounds =
        WorldGuardRegionOccupancy.boundsToAvoid(regionManager.getRegions().values());
  }

  private boolean isAvailableSupportedPlot(int row, int col, int plotMultiplier, int level) {
    return !areBlocksOccupied(row, col, plotMultiplier, level)
        && PlotLevelSupport.hasCompleteSupport(
            row, col, plotMultiplier, level, this::isGeneratedLogicalBlock);
  }

  private boolean isGeneratedLogicalBlock(int row, int col, int level) {
    Cuboid gridCell = new Cuboid(getGridMin(row, col, 1, level), getGridMax(row, col, 1, level));
    if (_occupiedPlots != null) {
      for (Plot plot : _occupiedPlots) {
        if (plot instanceof PlayerHome && plot.getCuboid().intersects(gridCell)) {
          return true;
        }
      }
    }
    return _spawnCuboid != null && _spawnCuboid.intersects(gridCell);
  }

  private void resizeCityRegion() {
    size = calculateCitySize();
    ProtectedRegion cityRegion = regionManager.getRegion("City");
    if (cityRegion instanceof ProtectedCuboidRegion) {
      ProtectedCuboidRegion region = (ProtectedCuboidRegion) cityRegion;

      BlockVector3 min;
      BlockVector3 max;

      min = getPlotMin(-size / 2, -size / 2, 1);
      max = getPlotMax(size / 2, size / 2, 1);

      ProtectedCuboidRegion resizedRegion = new ProtectedCuboidRegion(region.getId(), min, max);
      resizedRegion.copyFrom(region);
      regionManager.removeRegion(region.getId());
      regionManager.addRegion(resizedRegion);
      _cityRegion = resizedRegion;
      _cityCuboid = new Cuboid(min, max);
      saveRegions();
    }
  }

  private int calculateCitySize() {
    int iSize = 3;

    for (Plot home : _occupiedPlots) {
      int plotCol = Math.abs(getPlotXFromMin(home.getCuboid()));
      int plotRow = Math.abs(getPlotZFromMin(home.getCuboid()));
      if (DEBUG) {
        getLogger().info(String.format("col: %d, row: %d, iSize: %d", plotCol, plotRow, iSize));
      }
      iSize = Math.max(Math.max(plotRow * 2 + 1, plotCol * 2 + 1), iSize);
    }

    if (DEBUG) {
      getLogger().info(String.format("City size is %d", iSize));
    }
    return iSize;
  }

  public BlockVector3 getPlotMin(int row, int col, int plotMultiplier) {
    return getPlotMin(row, col, plotMultiplier, 0);
  }

  public BlockVector3 getPlotMin(int row, int col, int plotMultiplier, int level) {
    BlockVector3 bv = plotGridLayout.plotMin(row, col, level);
    getLogger().info(String.format("getPlotMin (%d, %d, %d)", bv.x(), bv.y(), bv.z()));
    return bv;
  }

  public BlockVector3 getPlotMax(int row, int col, int plotMultiplier) {
    return getPlotMax(row, col, plotMultiplier, 0);
  }

  public BlockVector3 getPlotMax(int row, int col, int plotMultiplier, int level) {
    BlockVector3 bv = plotGridLayout.plotMax(row, col, plotMultiplier, plotMultiplier, level);
    getLogger().info(String.format("getPlotMax (%d, %d, %d)", bv.x(), bv.y(), bv.z()));
    return bv;
  }

  public BlockVector3 getGridMin(int row, int col, int plotMultiplier) {
    return getGridMin(row, col, plotMultiplier, 0);
  }

  public BlockVector3 getGridMin(int row, int col, int plotMultiplier, int level) {
    BlockVector3 bv = plotGridLayout.gridMin(row, col, level);
    getLogger().info(String.format("getGridMin (%d, %d, %d)", bv.x(), bv.y(), bv.z()));
    return bv;
  }

  public BlockVector3 getGridMax(int row, int col, int plotMultiplier) {
    return getGridMax(row, col, plotMultiplier, 0);
  }

  public BlockVector3 getGridMax(int row, int col, int plotMultiplier, int level) {
    BlockVector3 bv = plotGridLayout.gridMax(row, col, plotMultiplier, plotMultiplier, level);
    getLogger().info(String.format("getGridMax (%d, %d, %d)", bv.x(), bv.y(), bv.z()));
    return bv;
  }

  private int getPlotXFromMin(Cuboid cuboid) {
    return plotGridLayout.plotXIndexFromMin(cuboid);
  }

  private int getPlotZFromMin(Cuboid cuboid) {
    return plotGridLayout.plotZIndexFromMin(cuboid);
  }

  private int getLevelPitchY() {
    return plotGridLayout.gridSizeY();
  }

  private int getEffectiveMaxLevels() {
    int streetBaseY = getLevelStartY(0);
    int avenueBaseY = streetBaseY + avenueLevel - roadLevel;
    int availableStreetLevels = getAvailableLevelsAt(streetBaseY);
    int availableAvenueLevels =
        avenueWidth > 0 ? getAvailableLevelsAt(avenueBaseY) : availableStreetLevels;
    int maxWorldLevels = Math.min(availableStreetLevels, availableAvenueLevels);
    return clampMaxLevels(maxLevels, maxWorldLevels);
  }

  private int getAvailableLevelsAt(int baseY) {
    if (world == null || baseY < world.getMinHeight() || plotSizeY < 1) {
      return 0;
    }

    int highestPlotBase = world.getMaxHeight() - plotSizeY;
    if (baseY > highestPlotBase) {
      return 0;
    }
    return (highestPlotBase - baseY) / getLevelPitchY() + 1;
  }

  private int getLevelStartY(int level) {
    return plotGridLayout.levelStartY(level);
  }

  private void setHomeOccupied(
      UUID ownerId,
      String ownerName,
      int homeNumber,
      BlockVector3 minimumPoint,
      BlockVector3 maximumPoint) {
    PlayerHome home = new PlayerHome(ownerId, ownerName, homeNumber, minimumPoint, maximumPoint);
    if (!_occupiedPlots.contains(home)) {
      _occupiedPlots.add(home);
      addOwnedPlot(ownerId, home);
    }
  }

  public PlayerHome generateHome(String playerName) {
    return generateHome(getServer().getOfflinePlayer(playerName));
  }

  public PlayerHome generateHome(OfflinePlayer player) {
    int homeNumber = _currentHomes.getOrDefault(player.getUniqueId(), 1);
    return generateHome(player, homeNumber);
  }

  public PlayerHome generateHome(OfflinePlayer player, int homeNumber) {
    UUID playerId = player.getUniqueId();
    String playerName = player.getName() == null ? playerId.toString() : player.getName();
    int multiplier = getPlotMultiplier(player);

    if (DEBUG) {
      getLogger().info(String.format("Generating home for %s", playerName));
    }
    Cuboid homeCuboid = null;
    ProtectedRegion phomeRegion = null;
    String regionName = getHomeRegionName(playerId, homeNumber);
    phomeRegion = regionManager.getRegion(regionName);
    if (phomeRegion != null) {
      return PlayerHome.get(phomeRegion);
    }
    PlayerHome existingHome = getOwnedHome(playerId, homeNumber);
    if (existingHome != null) {
      return existingHome;
    }

    homeCuboid = findNextUnownedHomeRegion(multiplier);
    if (homeCuboid == null) {
      return null;
    }

    getLogger().info("Metropolis Generating home in " + homeCuboid.toString());

    ProtectedCuboidRegion newHomeRegion =
        new ProtectedCuboidRegion(regionName, homeCuboid.getMin(), homeCuboid.getMax());
    newHomeRegion.setFlag(Flags.PVP, StateFlag.State.DENY);
    newHomeRegion.setFlag(Flags.MOB_DAMAGE, StateFlag.State.DENY);
    newHomeRegion.setFlag(Flags.MOB_SPAWNING, StateFlag.State.DENY);
    newHomeRegion.setFlag(Flags.CREEPER_EXPLOSION, StateFlag.State.DENY);
    newHomeRegion.setFlag(Flags.ENDER_BUILD, StateFlag.State.DENY);
    newHomeRegion.setFlag(Flags.GHAST_FIREBALL, StateFlag.State.DENY);
    newHomeRegion.setFlag(Flags.TNT, StateFlag.State.DENY);

    DefaultDomain d = newHomeRegion.getOwners();
    d.addPlayer(playerId);
    newHomeRegion.setPriority(1);

    regionManager.addRegion(newHomeRegion);
    try {
      regionManager.save();
    } catch (Exception e) {
      getLogger().info("Metropolis: ERROR Problem saving region");
      e.printStackTrace();
    }

    try {
      regionManager.save();
    } catch (Exception e) {
      getLogger().info("Metropolis: ERROR Problem saving region");
      e.printStackTrace();
    }
    getLogger()
        .info(
            String.format(
                "New home region (%d, %d, %d) (%d, %d, %d)",
                newHomeRegion.getMinimumPoint().x(),
                newHomeRegion.getMinimumPoint().y(),
                newHomeRegion.getMinimumPoint().z(),
                newHomeRegion.getMaximumPoint().x(),
                newHomeRegion.getMaximumPoint().y(),
                newHomeRegion.getMaximumPoint().z()));

    setHomeOccupied(
        playerId,
        playerName,
        homeNumber,
        newHomeRegion.getMinimumPoint(),
        newHomeRegion.getMaximumPoint());
    _currentHomes.put(playerId, homeNumber);
    saveCurrentHomes();

    createRoads(homeCuboid);

    if (generateFloor) {
      generateFloor(homeCuboid);
    }

    if (DEBUG) {
      getLogger().info(String.format("generateSign: %s", String.valueOf(generateSign)));
    }
    if (generateSign) {
      generateSign(homeCuboid, playerName);
    }

    if (DEBUG) {
      getLogger().info(String.format("Done generating home for %s", playerName));
    }

    PlayerHome home = new PlayerHome(newHomeRegion);
    home.setPlayerName(playerName);
    return home;
  }

  private void generateSign(Cuboid plotCuboid, String playerName) {
    Block signBlock =
        world.getBlockAt(plotCuboid.getCenterX(), plotCuboid.minY + 1, plotCuboid.getCenterZ());
    signBlock.setType(Material.OAK_SIGN);
    Sign sign = (Sign) signBlock.getState();
    sign.setLine(0, "Home of");

    sign.setLine(1, playerName.substring(0, Math.min(15, playerName.length())));
    if (playerName.length() > 15) {
      sign.setLine(2, playerName.substring(16, Math.min(30, playerName.length())));
      if (playerName.length() > 45) {
        sign.setLine(3, playerName.substring(31, Math.min(45, playerName.length())));
      }
    }

    sign.update(true);
  }

  private void generateRoadStairs(Cuboid plotCuboid, int lowerLevel, RoadLayout.RoadType roadType) {
    List<AvenueStairwayLayout.Flight> flights =
        getRoadStairFlights(plotCuboid, lowerLevel, roadType);
    if (flights.isEmpty()) {
      return;
    }

    Material stairMaterial =
        roadType == RoadLayout.RoadType.AVENUE ? avenueStairMaterial : streetStairMaterial;
    for (AvenueStairwayLayout.Flight flight : flights) {
      AvenueStairwayBuilder.build(world, flight, stairMaterial);
    }
  }

  private Set<AvenueStairwayLayout.Step> getUpperRoadOpenings(
      Cuboid plotCuboid, int roadY, RoadLayout.RoadType roadType) {
    Set<AvenueStairwayLayout.Step> openings = new HashSet<>();
    openings.addAll(getUpperRoadOpeningsForPlot(plotCuboid, roadY, roadType));
    if (_occupiedPlots != null) {
      for (Plot plot : _occupiedPlots) {
        if (plot instanceof PlayerHome home) {
          openings.addAll(getUpperRoadOpeningsForPlot(home.getCuboid(), roadY, roadType));
        }
      }
    }
    return openings;
  }

  private List<AvenueStairwayLayout.Step> getUpperRoadOpeningsForPlot(
      Cuboid plotCuboid, int roadY, RoadLayout.RoadType roadType) {
    int baseY = (roadType == RoadLayout.RoadType.AVENUE ? avenueLevel : roadLevel) + plotOffsetY;
    if (!areStairsEnabled(roadType) || roadY <= baseY) {
      return List.of();
    }

    int upperLevel = Math.floorDiv(roadY - baseY, getLevelPitchY());
    if (baseY + upperLevel * getLevelPitchY() != roadY || upperLevel < 1) {
      return List.of();
    }

    return getRoadStairFlights(plotCuboid, upperLevel - 1, roadType).stream()
        .flatMap(flight -> flight.roadOpenings().stream())
        .toList();
  }

  private boolean areStairsEnabled(RoadLayout.RoadType roadType) {
    return roadType == RoadLayout.RoadType.AVENUE ? generateAvenueStairs : generateStreetStairs;
  }

  private List<AvenueStairwayLayout.Flight> getRoadStairFlights(
      Cuboid plotCuboid, int lowerLevel, RoadLayout.RoadType roadType) {
    if (!areStairsEnabled(roadType)
        || lowerLevel < 0
        || lowerLevel >= getEffectiveMaxLevels() - 1) {
      return List.of();
    }

    if (roadType == RoadLayout.RoadType.AVENUE) {
      return AvenueStairwayLayout.forUpperRoadSegments(
          plotCuboid,
          plotSizeZ,
          gridSizeZ,
          getPlotZFromMin(plotCuboid),
          avenueStairsEveryNBlocks,
          avenueWidth,
          avenueStairWidth,
          avenueLevel + plotOffsetY + lowerLevel * getLevelPitchY(),
          getLevelPitchY(),
          3);
    }

    return AvenueStairwayLayout.forUpperStreetSegments(
        plotCuboid,
        plotSizeX,
        gridSizeX,
        getPlotXFromMin(plotCuboid),
        streetStairsEveryNBlocks,
        streetWidth,
        streetStairWidth,
        roadLevel + plotOffsetY + lowerLevel * getLevelPitchY(),
        getLevelPitchY(),
        3);
  }

  public List<Plot> getCityBlocks() {
    return Collections.unmodifiableList(_occupiedPlots);
  }

  public World getWorld() {
    return world;
  }

  public void reserveCuboid(String regionName, Cuboid cuboid) {
    ProtectedCuboidRegion reservedRegion =
        new ProtectedCuboidRegion(regionName, cuboid.getMin(), cuboid.getMax());
    reservedRegion.setFlag(Flags.PVP, StateFlag.State.DENY);
    reservedRegion.setFlag(Flags.MOB_DAMAGE, StateFlag.State.DENY);
    reservedRegion.setFlag(Flags.MOB_SPAWNING, StateFlag.State.DENY);
    reservedRegion.setFlag(Flags.CREEPER_EXPLOSION, StateFlag.State.DENY);
    reservedRegion.setFlag(Flags.ENDER_BUILD, StateFlag.State.DENY);
    reservedRegion.setFlag(Flags.GHAST_FIREBALL, StateFlag.State.DENY);
    reservedRegion.setFlag(Flags.TNT, StateFlag.State.DENY);
    reservedRegion.setFlag(Flags.LAVA_FLOW, StateFlag.State.DENY);
    reservedRegion.setFlag(Flags.SNOW_FALL, StateFlag.State.DENY);
    regionManager.addRegion(reservedRegion);

    _occupiedPlots.add(Plot.get(reservedRegion));
    saveRegions();
  }

  public Cuboid getCityCuboid() {
    return _cityCuboid;
  }

  public boolean getGenerateWall() {
    return generateWall;
  }

  public Material getWallMaterial() {
    return wallMaterial;
  }

  public int getWallheight() {
    return wallHeight;
  }

  public ProtectedRegion getRegion(String regionName) {
    if (regionManager == null) {
      return null;
    }

    return regionManager.getRegion(regionName);
  }

  public void removeRegion(String regionId) {
    if (regionManager == null) {
      return;
    }

    try {
      regionManager.removeRegion(regionId);
    } catch (Exception ex) {
      getLogger()
          .info(String.format("[ERROR] Metropolis: Unable to remove region {%s}.", regionId));
      return;
    }
  }

  public void saveRegions() {
    try {
      regionManager.save();
    } catch (Exception ex) {
      getLogger().info(String.format("[SEVERE] Metropolis: Unable to save WorldGuard regions."));
      return;
    }
  }

  public int getNumPlots(UUID playerId) {
    if (_ownedPlots.containsKey(playerId)) {
      List<Plot> plots = _ownedPlots.get(playerId);
      if (plots == null) {
        return 0;
      } else {
        return plots.size();
      }
    } else {
      return 0;
    }
  }

  public int getMaxPlots(UUID playerId) {
    if (_userOverrides.containsKey(playerId)) {
      return _userOverrides.get(playerId).getMaxPlots();
    } else {
      return _maxPlots;
    }
  }

  public int getMaxPlots(Player player) {
    return HomePermissionOverride.resolveMaxPlots(
        _userOverrides.get(player.getUniqueId()),
        _permissionOverrides,
        player::hasPermission,
        _maxPlots);
  }

  public void assignPlot(OfflinePlayer player) {
    generateHome(player);
  }

  public PlayerHome acquireHome(OfflinePlayer player) {
    List<Plot> ownedPlots = _ownedPlots.getOrDefault(player.getUniqueId(), List.of());
    int nextHomeNumber = HomeNumberAllocator.nextHomeNumber(ownedPlots);
    PlayerHome home = generateHome(player, nextHomeNumber);
    if (home != null) {
      setHome(player.getUniqueId(), home.getNumber());
    }
    return home;
  }

  private int getPlotMultiplier(UUID playerId) {
    if (_userOverrides.containsKey(playerId)) {
      return _userOverrides.get(playerId).getPlotMultiplier();
    } else {
      return _plotMultiplier;
    }
  }

  private int getPlotMultiplier(OfflinePlayer player) {
    if (player instanceof Player onlinePlayer) {
      return HomePermissionOverride.resolvePlotMultiplier(
          _userOverrides.get(player.getUniqueId()),
          _permissionOverrides,
          onlinePlayer::hasPermission,
          _plotMultiplier);
    }
    return HomePermissionOverride.resolvePlotMultiplier(
        _userOverrides.get(player.getUniqueId()),
        _permissionOverrides,
        ignored -> false,
        _plotMultiplier);
  }

  public Plot getPlot(String string) {
    /**
     * string is the name of the region to get a plot for
     *
     * <p>loop through all regions and find one with the specified name return null if there is none
     */
    for (Plot plot : _occupiedPlots) {
      if (plot.getRegionName().equals(string)) {
        return plot;
      }
    }

    return null;
  }

  public Player getPlayer(String name) {
    Player player = getServer().getPlayerExact(name);
    if (player != null) {
      return player;
    }
    return PlayerLookup.findOnline(name, getServer().getOnlinePlayers());
  }

  public OfflinePlayer getOfflinePlayer(String name) {
    Player onlinePlayer = getPlayer(name);
    if (onlinePlayer != null) {
      return onlinePlayer;
    }
    for (OfflinePlayer offlinePlayer : getServer().getOfflinePlayers()) {
      if (name.equalsIgnoreCase(offlinePlayer.getName())) {
        return offlinePlayer;
      }
    }
    return null;
  }

  public String teleportPlayerToPlot(Player player, Plot plot) {
    Location loc = plot.getViableSpawnLocation(world);

    if (loc == null) {
      return "No safe teleport location exists in plot " + plot.getRegionName() + ".";
    }
    return player.teleport(loc) ? null : "Unable to teleport to plot " + plot.getRegionName() + ".";
  }

  public List<Plot> getReservationsForPlayer(UUID playerId) {
    if (regionManager == null) {
      return List.of();
    }
    return PlotReservationLookup.forPlayer(regionManager.getRegions().values(), playerId);
  }

  public boolean homeExists(UUID playerId, int homeNumber) {
    for (Plot plot : _occupiedPlots) {
      if (plot instanceof PlayerHome home
          && home.getPlayerId().equals(playerId)
          && home.getNumber() == homeNumber) {
        return true;
      }
    }

    return false;
  }

  public void setHome(UUID playerId, int newHomeNumber) {
    _currentHomes.put(playerId, newHomeNumber);
    saveCurrentHomes();
  }

  public String getHomeRegionName(UUID playerId, int homeNumber) {
    PlayerHome existingHome = getOwnedHome(playerId, homeNumber);
    return existingHome == null
        ? String.format("h_%d_%s", homeNumber, playerId)
        : existingHome.getRegionName();
  }

  public String getCurrentHomeRegionName(UUID playerId) {
    return getHomeRegionName(playerId, _currentHomes.getOrDefault(playerId, 1));
  }

  private PlayerHome getOwnedHome(UUID playerId, int homeNumber) {
    List<Plot> plots = _ownedPlots.get(playerId);
    if (plots == null) {
      return null;
    }
    for (Plot plot : plots) {
      if (plot instanceof PlayerHome home && home.getNumber() == homeNumber) {
        return home;
      }
    }
    return null;
  }

  private PlayerHome getFirstOwnedHome(UUID playerId) {
    List<Plot> plots = _ownedPlots.get(playerId);
    if (plots == null) {
      return null;
    }
    return plots.stream()
        .filter(PlayerHome.class::isInstance)
        .map(PlayerHome.class::cast)
        .min(java.util.Comparator.comparingInt(PlayerHome::getNumber))
        .orElse(null);
  }

  private void saveCurrentHomes() {
    File homesFile = new File(getDataFolder(), "currentHomes.yml");
    try {
      CurrentHomesStore.save(homesFile, _currentHomes);
    } catch (IOException e) {
      getLogger().log(java.util.logging.Level.SEVERE, "Unable to save currentHomes.yml", e);
    }
  }
}
