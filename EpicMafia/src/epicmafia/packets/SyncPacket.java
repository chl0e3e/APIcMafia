package epicmafia.packets;

import org.json.simple.JSONObject;

import epicmafia.game.Game;
import epicmafia.packet.EMPacket;

public class SyncPacket extends EMPacket
{

	public SyncPacket(Game emGame)
	{
		super(emGame);
	}

	@Override
	public Object getEMPacket()
	{
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("timeleft", timeleft);
		return jsonObject;
	}

	@Override
	public void readEMPacket(Object emPacket)
	{
		if(emPacket instanceof JSONObject)
		{
			JSONObject emObject = (JSONObject) emPacket;
			timeleft = (long) emObject.get("timeleft");
		}
	}
	
	@Override
	public String getPacketName()
	{
		return "sync";
	}
	
	public long timeleft;
}