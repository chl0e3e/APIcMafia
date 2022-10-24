package epicmafia.packets;

import org.json.simple.JSONObject;

import epicmafia.game.Game;
import epicmafia.game.IGameListener.Event;
import epicmafia.packet.EMPacket;

public class PointPacket extends EMPacket
{

	public PointPacket(Game emGame)
	{
		super(emGame);
	}

	@Override
	public Object getEMPacket()
	{
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("meet", meet);
		jsonObject.put("target", target);
		jsonObject.put("unpoint", unpoint);
		return jsonObject;
	}

	@Override
	public void readEMPacket(Object emPacket)
	{
		if(emPacket instanceof JSONObject)
		{
			JSONObject emObject = (JSONObject) emPacket;
			user = (String) emObject.get("user");
			meet = (String) emObject.get("meet");
			target = (String) emObject.get("target");
			unpoint = (boolean) emObject.get("unpoint");
		}
	}
	
	@Override
	public String getPacketName()
	{
		return "point";
	}
	
	public String meet;
	public String target;
	public String user;
	public boolean unpoint;
}
