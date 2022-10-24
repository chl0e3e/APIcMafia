package epicmafia.packets;

import org.json.simple.JSONObject;

import epicmafia.game.Game;
import epicmafia.packet.EMPacket;

public class LeavePacket extends EMPacket
{

	public LeavePacket(Game emGame)
	{
		super(emGame);
	}

	@Override
	public Object getEMPacket()
	{
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("user", username);
		return jsonObject;
	}

	@Override
	public void readEMPacket(Object emPacket)
	{
		if(emPacket instanceof JSONObject)
		{
			JSONObject emObject = (JSONObject) emPacket;
			username = (String) emObject.get("user");
		}
	}
	
	@Override
	public String getPacketName() 
	{
		return "leave";
	}
	
	public String username;
}
