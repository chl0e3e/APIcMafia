package epicmafia.packet;

import java.util.HashMap;

import epicmafia.game.Game;
import epicmafia.game.IGameListener.Event;
import epicmafia.packets.AuthPacket;
import epicmafia.packets.CamEndPacket;
import epicmafia.packets.CamInitPacket;
import epicmafia.packets.CamStartPacket;
import epicmafia.packets.ChatPacket;
import epicmafia.packets.CountdownPacket;
import epicmafia.packets.DelMeetPacket;
import epicmafia.packets.EndMeetPacket;
import epicmafia.packets.EventPacket;
import epicmafia.packets.FinishInputPacket;
import epicmafia.packets.InputPacket;
import epicmafia.packets.InputedPacket;
import epicmafia.packets.JoinPacket;
import epicmafia.packets.KickPacket;
import epicmafia.packets.KickVotePacket;
import epicmafia.packets.KillPacket;
import epicmafia.packets.LeavePacket;
import epicmafia.packets.LeftPacket;
import epicmafia.packets.MeetPacket;
import epicmafia.packets.MsgPacket;
import epicmafia.packets.OptionPacket;
import epicmafia.packets.OwnerPacket;
import epicmafia.packets.PointPacket;
import epicmafia.packets.RevealPacket;
import epicmafia.packets.RoundPacket;
import epicmafia.packets.StoppedTypingPacket;
import epicmafia.packets.SyncPacket;
import epicmafia.packets.TimePacket;
import epicmafia.packets.TypingPacket;
import epicmafia.packets.UsersPacket;

public abstract class EMPacket
{
	
	public Game emGame;
	
	public EMPacket(Game emGame)
	{
		this.emGame = emGame;
	}
	
	public static HashMap<String, Class> packetMap = new HashMap<String, Class>();
	
	static
	{
		registerPacket("auth", AuthPacket.class);
		registerPacket("cam_end", CamEndPacket.class);
		registerPacket("cam_init", CamInitPacket.class);
		registerPacket("cam_start", CamStartPacket.class);
		registerPacket("<", ChatPacket.class);
		registerPacket("countdown", CountdownPacket.class);
		registerPacket("del_meet", DelMeetPacket.class);
		registerPacket("end_meet", EndMeetPacket.class);
		registerPacket("event", EventPacket.class);
		registerPacket("finish_input", FinishInputPacket.class);
		registerPacket("inputed", InputedPacket.class);
		registerPacket("input", InputPacket.class);
		registerPacket("join", JoinPacket.class);
		registerPacket("kick", KickPacket.class);
		registerPacket("kickvote", KickVotePacket.class);
		registerPacket("kill", KillPacket.class);
		registerPacket("leave", LeavePacket.class);
		registerPacket("left", LeftPacket.class);
		registerPacket("meet", MeetPacket.class);
		registerPacket("msg", MsgPacket.class);
		registerPacket("option", OptionPacket.class);
		registerPacket("owner", OwnerPacket.class);
		registerPacket("point", PointPacket.class);
		registerPacket("reveal", RevealPacket.class);
		registerPacket("round", RoundPacket.class);
		registerPacket("u", StoppedTypingPacket.class);
		registerPacket("sync", SyncPacket.class);
		registerPacket("time", TimePacket.class);
		registerPacket("k", TypingPacket.class);
		registerPacket("users", UsersPacket.class);
	}
	
	public static void registerPacket(String packetName, Class emPacket)
	{
		if(!packetMap.containsKey(packetName))
			packetMap.put(packetName, emPacket);
	}
	
	public static Class getPacketForName(String packetName)
	{
		if(packetMap.containsKey(packetName))
			return packetMap.get(packetName);
		return null;
	}
	
	public abstract Object getEMPacket();
	
	public abstract void readEMPacket(Object emPacket);
	
	public abstract String getPacketName();
}