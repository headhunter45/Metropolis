# Description
Metropolis generates a home for each user connecting to your world with roads between them. As new users connect the city expands.

WorldGuard regions are used for each home and for the city as a whole.

One region called "City" is created to protect the spawn and roads. As the assigned blocks expand this region is automatically updated.

Each user gets assigned a home region named "h_username" where username is replaced with their username. This region is surrounded by a road and located next to an existing region.

After creation these regions can be modified as normal via WorldGuard commands.

# Install
To install just drop the jar into your plugins folder.  All necessarry data will be created on first launch.

# Config
Edit the config.yml file.  You can set the height to clear above roads, the road width, the plot size (incluing half the road width on either side), the road material, and the level at which the road is generated.

# Changelog
*   v0.5
	    Added example docs
	    Added Commands to plugin.yml to move and evict homes
	    Added floor and road supports to config file and generation
*   v0.4.6
	    Added optional generation of a sign identifying owner in plots on creation
	    Added a Cuboid constructor that takes a worldedit selection
	    Added a debug mode that enables extra logging.  MetropolisPlugin.DEBUG should be true for snapshots and false for release builds.
	    Added an occupied Plots list to MetropolisPlugin to keep track of both player homes and reserved plots.
	    Added the Plot class as a parent of PlayerHome and moved relevant code to it. 
	    Added a command to reserve plots that aren't tied to a player.  This can be used to setup a larger protected area around spawn
*   v0.4.5
*   v0.4.4
	    Added a welcome message telling players where their home is.
	    Switched from PlayerLoginEvent to PlayerJoinEvent
*   v0.4.3
	    Removed call to saveconfig on plugin unload.
*   v0.4.2
	    Made city region refresh on server restart.
*   v0.4.1
*   v0.4
*   v0.3
	    Added command to generate a home for a user that is not currently logged in.
*   v0.2.1
	    Made config file save on load and disable.
*   v0.2
	    Added configuration options.
*   v0.1
	    Initial Release
