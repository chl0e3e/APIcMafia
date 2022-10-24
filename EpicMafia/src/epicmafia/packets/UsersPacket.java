package epicmafia.packets;

import java.util.HashMap;

import org.json.simple.JSONObject;

import epicmafia.game.Game;
import epicmafia.packet.EMPacket;

public class UsersPacket extends EMPacket
{

	public UsersPacket(Game emGame)
	{
		super(emGame);
	}

	@Override
	public Object getEMPacket()
	{
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("users", users);
		return jsonObject;
	}

	@Override
	public void readEMPacket(Object emPacket)
	{
		if(emPacket instanceof JSONObject)
		{
			JSONObject emObject = (JSONObject) emPacket;
			users = (HashMap) emObject.get("users");
		}
	}
	
	@Override
	public String getPacketName()
	{
		return "users";
	}
	
	public HashMap<String, String> users = new HashMap<String, String>();
}
