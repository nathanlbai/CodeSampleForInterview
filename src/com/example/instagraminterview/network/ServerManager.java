package com.example.instagraminterview.network;

public class ServerManager {
private static ServerManager instance;
	
	protected ServerManager() {
		
	}
	
	public ServerManager getInstance() {
		if (instance == null) {
			synchronized (ServerManager.class) {
				instance = new ServerManager();
			}
		}
		
		return instance;
	}

}
