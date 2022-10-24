package epicmafia.game;

import java.util.ArrayList;
import java.util.List;

public class Lobby
{
	public byte lobbyId;
	public List<Game> emGames;
	
	public Lobby(byte lobbyId)
	{
		this.lobbyId = lobbyId;
		emGames = new ArrayList<Game>();
	}
	
	public String getName()
	{
		switch(lobbyId)
		{
		case 1: return "Training";
		case 3: return "Competitive";
		case 5: return "Sandbox";
		}
		return "Unknown";
	}
	
	public Game getGame(int gameIndex)
	{
		return emGames.get(gameIndex);
	}
}
