package epicmafia.packet;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import epicmafia.game.Game;

public class EMPacketParser
{
	public Game emGame;
	
	public EMPacketParser(Game emGame)
	{
		this.emGame = emGame;
	}
	
	public EMPacket[] parsePacketsRaw(String packetData) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
	{
		List<EMPacket> packetList = new ArrayList<EMPacket>();
		String[] dataParts = packetData.split("\00");
		for(String part : dataParts)
		{
			if(part == null || part.equals(""))
				continue;
			List<EMPacket> resPackets = parsePackets(part);
			if(resPackets != null)
				packetList.addAll(resPackets);
		}
		return packetList.toArray(new EMPacket[0]);
	}
	
	public List<EMPacket> parsePackets(String packetData) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
	{
		List<EMPacket> emPackets = new ArrayList<EMPacket>();
		JSONArray jArray = (JSONArray) JSONValue.parse(packetData);
		for(int i=0;i<jArray.size();i++)
		{
			JSONArray jPacketArray = (JSONArray) jArray.get(i);
			String packetName = (String) jPacketArray.get(0);
			Class packetClass = EMPacket.getPacketForName(packetName);
			if(packetClass == null)
			{
				System.out.println("Unrecognized packet: "+packetName+" | "+packetData);
				continue;
			}
			EMPacket emPacket = (EMPacket) packetClass.getConstructor(Game.class).newInstance(emGame);
			if(jPacketArray.size() == 2)
				emPacket.readEMPacket(jPacketArray.get(1));
			emPackets.add(emPacket);
		}
		return emPackets;
	}
}
