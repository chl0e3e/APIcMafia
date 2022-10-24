package epicmafia.packets;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import epicmafia.game.Game;
import epicmafia.game.IGameListener.Event;
import epicmafia.packet.EMPacket;

public class LeftPacket extends EMPacket
{

	public LeftPacket(Game emGame)
	{
		super(emGame);
	}

	@Override
	public Object getEMPacket()
	{
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("left", left);
		return jsonObject;
	}

	@Override
	public void readEMPacket(Object emPacket)
	{
		if(emPacket instanceof JSONObject)
		{
			JSONObject emObject = (JSONObject) emPacket;
			Object leftObj = emObject.get("left");
			if(leftObj instanceof String)
				left.add((String)leftObj);
			else if(leftObj instanceof JSONArray)
				left = (ArrayList) leftObj;
		}
	}
	
	@Override
	public String getPacketName()
	{
		return "left";
	}
	
	public List<String> left = new ArrayList<String>();
}
