package epicmafia.packets;

import org.json.simple.JSONObject;

import epicmafia.game.Game;
import epicmafia.packet.EMPacket;

public class EventPacket extends EMPacket
{

	public EventPacket(Game emGame)
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
			id = (String) emObject.get("id");
		}
	}
	
	@Override
	public String getPacketName()
	{
		return "event";
	}
	
	public String id;
}