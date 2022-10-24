package epicmafia.packets;

import org.json.simple.JSONObject;

import epicmafia.game.Game;
import epicmafia.packet.EMPacket;

public class AuthPacket extends EMPacket
{

	public AuthPacket(Game emGame)
	{
		super(emGame);
	}

	@Override
	public Object getEMPacket()
	{
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("spectate", spectate);
		return jsonObject;
	}

	@Override
	public void readEMPacket(Object emPacket)
	{
		if(emPacket instanceof JSONObject)
		{
			JSONObject emObject = (JSONObject) emPacket;
			spectate = (boolean) emObject.get("spectate");
		}
	}
	
	@Override
	public String getPacketName()
	{
		return "auth";
	}
	
	public boolean spectate;
}
