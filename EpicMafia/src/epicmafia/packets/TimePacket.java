package epicmafia.packets;

import org.json.simple.JSONObject;

import epicmafia.game.Game;
import epicmafia.packet.EMPacket;

public class TimePacket extends EMPacket
{

	public TimePacket(Game emGame)
	{
		super(emGame);
	}

	@Override
	public Object getEMPacket()
	{
		JSONObject jsonObject = new JSONObject();
		//jsonObject.put("status", status);
		jsonObject.put("timeleft", timeleft);
		return jsonObject;
	}

	@Override
	public void readEMPacket(Object emPacket)
	{
		if(emPacket instanceof JSONObject)
		{
			JSONObject emObject = (JSONObject) emPacket;
			//status = (String) emObject.get("status");
			timeleft = (long) emObject.get("timeleft");
		}
	}
	
	@Override
	public String getPacketName()
	{
		return "time";
	}
	
	//public String status;
	public long timeleft;
}
