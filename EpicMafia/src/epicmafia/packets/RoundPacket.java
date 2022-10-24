package epicmafia.packets;

import org.json.simple.JSONObject;

import epicmafia.game.Game;
import epicmafia.packet.EMPacket;

public class RoundPacket extends EMPacket
{

	public RoundPacket(Game emGame)
	{
		super(emGame);
	}

	@Override
	public Object getEMPacket()
	{
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("state", state);
		jsonObject.put("data", data);
		return jsonObject;
	}

	@Override
	public void readEMPacket(Object emPacket)
	{
		if(emPacket instanceof JSONObject)
		{
			JSONObject emObject = (JSONObject) emPacket;
			state = (long) emObject.get("state");
			data = (String) emObject.get("data");
		}
	}
	
	@Override
	public String getPacketName()
	{
		return "round";
	}
	
	public long state;
	public String data;
}