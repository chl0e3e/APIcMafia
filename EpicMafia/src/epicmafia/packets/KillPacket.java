package epicmafia.packets;

import org.json.simple.JSONObject;

import epicmafia.game.Game;
import epicmafia.game.IGameListener.Event;
import epicmafia.packet.EMPacket;

public class KillPacket extends EMPacket
{

	public KillPacket(Game emGame)
	{
		super(emGame);
	}

	@Override
	public Object getEMPacket()
	{
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("target", target);
		return jsonObject;
	}

	@Override
	public void readEMPacket(Object emPacket)
	{
		if(emPacket instanceof JSONObject)
		{
			JSONObject emObject = (JSONObject) emPacket;
			target = (String) emObject.get("target");
		}
	}
	
	@Override
	public String getPacketName()
	{
		return "kill";
	}
	
	public String target;
}
