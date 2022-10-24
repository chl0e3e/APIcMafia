package epicmafia.packets;

import org.json.simple.JSONObject;

import epicmafia.game.Game;
import epicmafia.packet.EMPacket;

public class StartInputPacket extends EMPacket
{

	public StartInputPacket(Game emGame)
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
			id = (long) emObject.get("id");
			data = (String) emObject.get("data");
		}
	}
	
	@Override
	public String getPacketName()
	{
		return "start_input";
	}
	
	public long id;
	public String data;
}