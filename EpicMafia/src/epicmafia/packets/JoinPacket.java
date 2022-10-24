package epicmafia.packets;

import org.json.simple.JSONObject;

import epicmafia.game.Game;
import epicmafia.packet.EMPacket;

public class JoinPacket extends EMPacket
{

	public JoinPacket(Game emGame)
	{
		super(emGame);
	}

	@Override
	public Object getEMPacket()
	{
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("gameid", gameId);
		jsonObject.put("user", username);
		jsonObject.put("pass", password);
		return jsonObject;
	}

	@Override
	public void readEMPacket(Object emPacket)
	{
		if(emPacket instanceof JSONObject)
		{
			JSONObject emObject = (JSONObject) emPacket;
			//gameId = (String) emObject.get("gameid");
			username = (String) emObject.get("user");
			//password = (String) emObject.get("pass");
			data = (String) emObject.get("data");
		}
	}
	
	@Override
	public String getPacketName()
	{
		return "join";
	}
	
	public String gameId;
	public String username;
	public String password;
	public String data;
}
