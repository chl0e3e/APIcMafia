package epicmafia.packets;

import org.json.simple.JSONObject;

import epicmafia.game.Game;
import epicmafia.packet.EMPacket;

public class CamStartPacket extends EMPacket
{

	public CamStartPacket(Game emGame)
	{
		super(emGame);
	}

	@Override
	public Object getEMPacket()
	{
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("sid", sid);
		jsonObject.put("token", token);
		return jsonObject;
	}

	@Override
	public void readEMPacket(Object emPacket)
	{
		if(emPacket instanceof JSONObject)
		{
			JSONObject emObject = (JSONObject) emPacket;
			sid = (String) emObject.get("sid");
			token = (String) emObject.get("token");
		}
	}
	
	@Override
	public String getPacketName()
	{
		return "cam_start";
	}
	
	public String sid;
	public String token;
}
