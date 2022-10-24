package epicmafia.packets;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import epicmafia.game.Game;
import epicmafia.game.IGameListener.Event;
import epicmafia.packet.EMPacket;

public class MsgPacket extends EMPacket
{

	public MsgPacket(Game emGame)
	{
		super(emGame);
	}

	@Override
	public Object getEMPacket()
	{
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("msg", msg);
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
				msg = ((String)msgObj);
		}
	}
	
	@Override
	public String getPacketName()
	{
		return "msg";
	}
	
	public String msg;
}
