package epicmafia.game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import epicmafia.packets.ChatPacket;
import epicmafia.packets.PointPacket;

public class Meeting
{
	public String mName;
	public String mVoteType;
	public boolean exist;
	public boolean canSay;
	public boolean voteSee;
	public boolean voteSelf;
	public boolean voteNoOne;
	public boolean disguiseChoices;
	public String newName;
	public String rawName;
	public List<String> members = new ArrayList<String>();
	public List<String> basket = new ArrayList<String>();
	public List<String> inputs = new ArrayList<String>();
	public HashMap<String, String> choosedata = new HashMap<String, String>();
	public HashMap<String, String> disguise = new HashMap<String, String>();
	public HashMap<String, String> chatEntries = new HashMap<String, String>();
	public HashMap<String, String> choices = new HashMap<String, String>();
	public Game mGame;
	public boolean isEnded = false;
	public boolean hasPointed = false;
	
	public Meeting(String mName, Game mGame)
	{
		this.mGame = mGame;
		this.mName = mName;
	}
	
	public boolean sendChat(String msg) throws IOException
	{
		ChatPacket chatPacket = new ChatPacket(mGame);
		chatPacket.meet = mName;
		chatPacket.msg = msg;
		return mGame.sendPacket(chatPacket);
	}
	
	public boolean pointPlayer(String target) throws IOException
	{
		if(members.size() == 1 && mVoteType.equals("individual") && hasPointed)
			return false;
		PointPacket pointPacket = new PointPacket(mGame);
		pointPacket.meet = mName;
		pointPacket.target = target;
		if(choices.containsKey(mGame.epicMafia.emUsername) && choices.get(mGame.epicMafia.emUsername).equalsIgnoreCase(target))
		{
			pointPacket.unpoint = true;
			choices.remove(mGame.epicMafia.emUsername);
		}
		else
			choices.put(mGame.epicMafia.emUsername, target);//add choice as self
		return mGame.sendPacket(pointPacket);
	}
	
	public void addChat(Player player, String msg)
	{
		if(player != null && player.pName != null && msg != null)
			chatEntries.put(player.pName, msg);
	}
	
	public void addChat(String player, String msg)
	{
		chatEntries.put(player, msg);
	}
}
