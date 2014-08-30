package com.majinnaibu.bukkitplugins.metropolis;

public class UserOverride {
	private String _username;
	private int _plotMultiplier;
	private int _maxPlots;
	
	public String getUsername(){return _username;}
	public int getPlotMultiplier(){return _plotMultiplier;}
	public int getMaxPlots(){return _maxPlots;}
	
	public UserOverride(String username, int plotMultiplier, int maxPlots){
		_username = username;
		_plotMultiplier = plotMultiplier;
		_maxPlots = maxPlots;
	}
}
