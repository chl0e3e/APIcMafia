package epicmafia.packets;

import org.json.simple.JSONObject;

import epicmafia.game.Game;
import epicmafia.packet.EMPacket;

public class ChatPacket extends EMPacket
{

	public ChatPacket(Game emGame)
	{
		super(emGame);
	}

	@Override
	public Object getEMPacket()
	{
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("msg", msg);
		if(meet != null)
			jsonObject.put("meet", meet);
		return jsonObject;
	}

	@Override
	public void readEMPacket(Object emPacket)
	{
		if(emPacket instanceof JSONObject)
		{
			JSONObject emObject = (JSONObject) emPacket;
			Object msgObj = emObject.get("msg");
			if(msgObj instanceof String)
				msg = (String) msgObj;
			time = (long) emObject.get("t");
			Object userObj = emObject.get("user");
			if(userObj instanceof String)
				user = (String) userObj;
			Object meetObj = emObject.get("meet");
			if(meetObj instanceof String)
				meet = (String) meetObj;
			Object roleObj = emObject.get("role");
			if(roleObj instanceof String)
				role = (String) roleObj;
			if((boolean) emObject.get("crying"))
				crying = true;
			if((boolean) emObject.get("contact"))
				contact = true;
			if((boolean) emObject.get("dead"))
				dead = true;
		}
	}
	
	@Override
	public String getPacketName()
	{
		return "<";
	}
	
	public String msg;
	public String meet;
	public boolean contact = false;
	public boolean crying = false;
	public boolean dead = false;
	public String role;
	public String user;
	public long time;
}
