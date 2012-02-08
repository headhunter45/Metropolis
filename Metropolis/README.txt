Metropolis generates a home for each user connecting to your world with roads between them. As new users connect the city expands.

WorldGuard regions are used for each home and for the city as a whole.

One region called "City" is created to protect the spawn and roads. As the assigned blocks expand this region is automatically updated.

Each user gets assigned a home region named "h_username" where username is replaced with their username. This region is surrounded by a road and located next to an existing region.

After creation these regions can be modified as normal via WorldGuard commands.

INSTALL:
To install just drop the jar into your plugins folder.  All necessarry data will be created on first launch.

CONFIG:
Edit the config.yml file.  You can set the height to clear above roads, the road width, the plot size (incluing half the road width on either side), the road material, and the level at which the road is generated.

Changelog:
v0.3
	Added command to generate a home for a user that is not currently logged in.
v0.2.1
	Made config file save on load and disable.
v0.2
	Added configuration options.

v0.1
	Initial Release