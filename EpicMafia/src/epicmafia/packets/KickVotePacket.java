package epicmafia.packets;

import org.json.simple.JSONObject;

import epicmafia.game.Game;
import epicmafia.packet.EMPacket;

public class KickVotePacket extends EMPacket
{

	public KickVotePacket(Game emGame)
	{
		super(emGame);
	}

	@Override
	public Object getEMPacket()
	{
		JSONObject jsonObject = new JSONObject();
		return jsonObject;
	}

	@Override
	public void readEMPacket(Object emPacket)
	{
		
	}
	
	@Override
	public String getPacketName()
	{
		return "kickvote";
	}
}