package epicmafia.packets;

import org.json.simple.JSONObject;

import epicmafia.game.Game;
import epicmafia.packet.EMPacket;

public class FinishInputPacket extends EMPacket
{

	public FinishInputPacket(Game emGame)
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
		}
	}
	
	@Override
	public String getPacketName()
	{
		return "finish_input";
	}
	
	public long id;
}