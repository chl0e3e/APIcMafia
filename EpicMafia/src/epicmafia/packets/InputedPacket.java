package epicmafia.packets;

import org.json.simple.JSONObject;

import epicmafia.game.Game;
import epicmafia.packet.EMPacket;

public class InputedPacket extends EMPacket
{

	public InputedPacket(Game emGame)
	{
		super(emGame);
	}

	@Override
	public Object getEMPacket()
	{
		return null;
	}

	@Override
	public void readEMPacket(Object emPacket)
	{
		if(emPacket instanceof JSONObject)
		{
			JSONObject emObject = (JSONObject) emPacket;
			meet = (String) emObject.get("meet");
			inputname = (String) emObject.get("inputname");
			user = (String) emObject.get("user");
			data = emObject.get("data");
			you = (boolean) emObject.get("you");
		}
	}
	
	@Override
	public String getPacketName()
	{
		return "inputed";
	}
	
	public String meet;
	public String inputname;
	public String user;
	public Object data;
	public boolean you;
}