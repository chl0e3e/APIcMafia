package epicmafia.packets;

import org.json.simple.JSONObject;

import epicmafia.game.Game;
import epicmafia.game.IGameListener.Event;
import epicmafia.packet.EMPacket;

public class CamEndPacket extends EMPacket
{

	public CamEndPacket(Game emGame)
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
		}
	}
	
	@Override
	public String getPacketName()
	{
		return "cam_end";
	}
}
