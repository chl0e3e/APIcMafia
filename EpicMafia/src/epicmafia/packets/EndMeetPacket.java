package epicmafia.packets;

import org.json.simple.JSONObject;

import epicmafia.game.Game;
import epicmafia.packet.EMPacket;

public class EndMeetPacket extends EMPacket
{

	public EndMeetPacket(Game emGame)
	{
		super(emGame);
	}

	@Override
	public Object getEMPacket()
	{
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("meet", meet);
		return jsonObject;
	}

	@Override
	public void readEMPacket(Object emPacket)
	{
		if(emPacket instanceof JSONObject)
		{
			JSONObject emObject = (JSONObject) emPacket;
			meet = (String) emObject.get("meet");
		}
	}
	
	@Override
	public String getPacketName()
	{
		return "end_meet";
	}
	
	public String meet;
}