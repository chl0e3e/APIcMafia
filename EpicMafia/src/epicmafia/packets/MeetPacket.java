package epicmafia.packets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONObject;

import epicmafia.game.Game;
import epicmafia.packet.EMPacket;

public class MeetPacket extends EMPacket
{

	public MeetPacket(Game emGame)
	{
		super(emGame);
	}

	@Override
	public Object getEMPacket()
	{
		//you do not send this packet!!!
		return null;
	}

	@Override
	public void readEMPacket(Object emPacket)
	{
		if(emPacket instanceof JSONObject)
		{
			JSONObject emObject = (JSONObject) emPacket;
			Object meetO = emObject.get("meet");
			if(meetO instanceof String)
				meet = (String) meetO;
			Object votetypeO = emObject.get("votetype");
			if(votetypeO instanceof String)
				votetype = (String) votetypeO;
			exist = (boolean) emObject.get("exist");
			say = (boolean) emObject.get("say");
			votesee = (boolean) emObject.get("votesee");
			voteself = (boolean) emObject.get("voteself");
			votenoone = (boolean) emObject.get("votenoone");
			disguiseChoices = (boolean) emObject.get("disguise_choices");
			Object newnameO = emObject.get("newname");
			if(newnameO instanceof String)
				newname = (String) newnameO;
			Object raw_nameO = emObject.get("raw_name");
			if(raw_nameO instanceof String)
				raw_name = (String) raw_nameO;
			Object disguiseO = emObject.get("disguise");
			if(disguiseO instanceof HashMap)
				disguise = (HashMap) disguiseO;
			Object membersO = emObject.get("members");
			if(membersO instanceof ArrayList)
				members = (ArrayList) membersO;
			Object basketO = emObject.get("basket");
			if(basketO instanceof ArrayList)
				basket = (ArrayList) basketO;
			Object meeting_nameO = emObject.get("meeting_name");
			if(meeting_nameO instanceof String)
				meeting_name = (String) meeting_nameO;
			Object inputsO = emObject.get("inputs");
			if(inputsO instanceof ArrayList)
				inputs = (ArrayList) inputsO;
			Object choosedataO = emObject.get("choosedata");
			if(choosedataO instanceof HashMap)
				choosedata = (HashMap) choosedataO;
		}
	}
	
	@Override
	public String getPacketName()
	{
		return "meet";
	}
	
	public String meet;
	public String votetype;
	public boolean exist;
	public boolean say;
	public boolean votesee;
	public boolean voteself;
	public boolean votenoone;
	public boolean disguiseChoices;
	public String newname;
	public String raw_name;
	public HashMap<String, String> disguise = new HashMap<String, String>();
	public List<String> members;
	public List<String> basket;
	public String meeting_name;
	public List<String> inputs;
	public HashMap<String, String> choosedata = new HashMap<String, String>();
}