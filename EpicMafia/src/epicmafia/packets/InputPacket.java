package epicmafia.packets;

import org.json.simple.JSONObject;

import epicmafia.game.Game;
import epicmafia.packet.EMPacket;

public class InputPacket extends EMPacket
{

	public InputPacket(Game emGame)
	{
		super(emGame);
	}

	@Override
	public Object getEMPacket()
	{
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", id);
		jsonObject.put("input", input);
		return jsonObject;
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
		return "input";
	}
	
	public long id;
	public Object input;
}