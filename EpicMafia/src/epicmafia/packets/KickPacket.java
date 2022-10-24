package epicmafia.packets;

import org.json.simple.JSONObject;

import epicmafia.game.Game;
import epicmafia.packet.EMPacket;

public class KickPacket extends EMPacket
{

	public KickPacket(Game emGame)
	{
		super(emGame);
	}

	@Override
	public Object getEMPacket()
	{
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("user", user);
		jsonObject.put("deranked", deranked);
		return jsonObject;
	}

	@Override
	public void readEMPacket(Object emPacket)
	{
		if(emPacket instanceof JSONObject)
		{
			JSONObject emObject = (JSONObject) emPacket;
			user = (String) emObject.get("user");
			deranked = (boolean) emObject.get("meet");
			suicide = (boolean) emObject.get("meet");
		}
	}
	
	@Override
	public String getPacketName()
	{
		return "kick";
	}
	
	public String user;
	public boolean deranked;
	public boolean suicide;
}