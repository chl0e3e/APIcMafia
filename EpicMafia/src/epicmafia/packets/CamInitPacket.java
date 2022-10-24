package epicmafia.packets;

import org.json.simple.JSONObject;

import epicmafia.game.Game;
import epicmafia.packet.EMPacket;

public class CamInitPacket extends EMPacket
{

	public CamInitPacket(Game emGame)
	{
		super(emGame);
	}

	@Override
	public Object getEMPacket()
	{
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("apikey", apikey);
		return jsonObject;
	}

	@Override
	public void readEMPacket(Object emPacket)
	{
		if(emPacket instanceof JSONObject)
		{
			JSONObject emObject = (JSONObject) emPacket;
			apikey = (String) emObject.get("apikey");
		}
	}
	
	@Override
	public String getPacketName()
	{
		return "cam_init";
	}
	
	public String apikey;
}
