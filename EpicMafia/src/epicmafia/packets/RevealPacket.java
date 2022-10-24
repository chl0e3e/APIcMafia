package epicmafia.packets;

import org.json.simple.JSONObject;

import epicmafia.game.Game;
import epicmafia.game.IGameListener.Event;
import epicmafia.packet.EMPacket;

public class RevealPacket extends EMPacket
{

	public RevealPacket(Game emGame)
	{
		super(emGame);
	}

	@Override
	public Object getEMPacket()
	{
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("user", user);
		jsonObject.put("data", data);
		jsonObject.put("red", red);
		return jsonObject;
	}

	@Override
	public void readEMPacket(Object emPacket)
	{
		if(emPacket instanceof JSONObject)
		{
			JSONObject emObject = (JSONObject) emPacket;
			user = (String) emObject.get("user");
			data = (String) emObject.get("data");
			red = (boolean) emObject.get("red");
		}
	}
	
	@Override
	public String getPacketName()
	{
		return "reveal";
	}
	
	public String user;
	public String data;
	public boolean red;
}
