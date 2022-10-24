package epicmafia.game;

public class Player
{
	public String pName = "unknown";
	public String pRole = "unknown";
	public boolean isDead = false;
	public boolean isTyping = false;
	public boolean hasLeft = false;
	
	public Player(String pName)
	{
		this.pName = pName;
	}
}
