package epicmafia.game;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.banhammer.util.FileUtils;
import me.banhammer.util.HTTPRequest;
import me.banhammer.util.HTTPResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import epicmafia.EpicMafia;
import epicmafia.game.IGameListener.Event;
import epicmafia.packet.EMPacket;
import epicmafia.packet.EMPacketParser;
import epicmafia.packets.AuthPacket;
import epicmafia.packets.CamEndPacket;
import epicmafia.packets.CamInitPacket;
import epicmafia.packets.CamStartPacket;
import epicmafia.packets.ChatPacket;
import epicmafia.packets.DelMeetPacket;
import epicmafia.packets.EndMeetPacket;
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
import epicmafia.packets.StartInputPacket;
import epicmafia.packets.StoppedTypingPacket;
import epicmafia.packets.TimePacket;
import epicmafia.packets.TypingPacket;
import epicmafia.packets.UsersPacket;
import epicmafia.tcp.ITcpListener;
import epicmafia.tcp.TcpConnection;
import epicmafia.tcp.TcpPacket;

public class Game implements ITcpListener
{
	//bools
	public boolean hasPassword;
	public boolean hasCamera;
	public boolean canReview;
	public boolean hasWhisper;
	public boolean startOnDay;
	public boolean mustLynch;
	public boolean mustAct;
	public boolean hasDawn;
	public boolean hasNoReveal;
	public boolean hasLastWill;
	public boolean hasClosedRoles;
	public boolean hasAnonymous;
	public boolean hasMultipleMafias;
	public boolean canEnter;
	public boolean isOwner;
	public boolean canSpectate;
	
	//numbers
	public long gameId;
	public long numPlayers;
	public long maxPlayers;
	public long statusId;
	public long setupId;
	public long timeLeft;
	public long roundState;
	
	//strings
	public String gameAction;
	public String gameType;
	public String hashedPassword;
	public String gameStatus;
	public String gamePassword;
	
	//collections
	public List<Role> gameRoles = new ArrayList<Role>();
	public HashMap<String, Player> gamePlayers = new HashMap<String, Player>();
	public List<IGameListener> gameListener = new ArrayList<IGameListener>();
	public List<Meeting> gameMeetings = new ArrayList<Meeting>();
	public List<Long> gameInputs = new ArrayList<Long>();
	public HashMap<String, Meeting> currentMeetings = new HashMap<String, Meeting>();
	
	//
	public EpicMafia epicMafia;
	public TcpConnection tcpConnection;
	public EMPacketParser packetParser;
	
	public Game(long gameId, EpicMafia emGame)
	{
		this.gameId = gameId;
		this.epicMafia = emGame;
		this.packetParser = new EMPacketParser(this);
		this.isOwner = false;
	}
	
	public boolean registerGameListener(IGameListener packetListener)
	{
		if(!gameListener.contains(packetListener))
		{
			gameListener.add(packetListener);
			return true;
		}
		return false;
	}
	
	public boolean deregisterGameListener(IGameListener packetListener)
	{
		if(gameListener.contains(packetListener))
		{
			gameListener.remove(packetListener);
			return true;
		}
		return false;
	}
	
	public String getPasswordHash(String emPassword) throws IOException
	{
		HttpGet httpGet = new HttpGet("/game/" + gameId + "/password?password=" + emPassword);
		HttpResponse httpResponse = epicMafia.httpClient.execute(epicMafia.httpHost, httpGet);
		String responseString = new String(FileUtils.readBytes(httpResponse.getEntity().getContent()));
		JSONObject jObject = (JSONObject) JSONValue.parse(responseString);
		if((boolean) jObject.get("s") == true)
			return (String) jObject.get("password");
		return null;
	}
	
	public boolean sendChat(String msg, boolean cry) throws IOException
	{
		for(Meeting meet : currentMeetings.values())
		{
			if(meet.canSay)
			{
				ChatPacket chatPacket = new ChatPacket(this);
				chatPacket.meet = meet.mName;
				chatPacket.msg = msg;
				if(cry)
					chatPacket.crying = true;
				return sendPacket(chatPacket);
			}
		}
		ChatPacket chatPacket = new ChatPacket(this);
		chatPacket.msg = msg;
		if(cry)
			chatPacket.crying = true;
		return sendPacket(chatPacket);
	}
	
	public boolean joinGame(boolean async) throws IOException
	{
		String uriAdd = "";
		if(gamePassword != null)
			uriAdd = "?password=" + getPasswordHash(gamePassword);
		HttpGet httpGet = new HttpGet("/game/" + gameId + uriAdd);
		HttpResponse httpResponse = epicMafia.httpClient.execute(epicMafia.httpHost, httpGet);
		String responseString = new String(FileUtils.readBytes(httpResponse.getEntity().getContent()));
		httpGet.releaseConnection();
		//TODO: Convert this to regex
		Pattern respPattern = Pattern.compile("<input id=\"pass\" type=\"hidden\" value=\"(.*)\"/>");
		Matcher respMatcher = respPattern.matcher(responseString);
		if(respMatcher.find())
		{
			hashedPassword = respMatcher.group(1).substring(0, 40);
			if(hashedPassword.length() == 40)
			{
				tcpConnection = new TcpConnection(epicMafia.httpHost, epicMafia.httpClient, "/tcp");
				tcpConnection.registerTcpListener(this);
				if(tcpConnection.connectTcp())
				{
					if(async)
						tcpConnection.startReceiverAsync();
					else
						tcpConnection.startReceiver();
					return true;
				}
			}
		}
		return false;
	}
	
	public void onEvent(Event event, Object... param)
	{
		for(IGameListener handler : gameListener)
		{
			handler.onEvent(event, param);
		}
	}
	
	public void handlePackets(EMPacket[] emPackets)
	{
		for(EMPacket emPacket : emPackets)
		{
			if(emPacket instanceof ChatPacket)
			{
				ChatPacket chatPacket = (ChatPacket) emPacket;
				Meeting backup = currentMeetings.size() == 0 ? null : currentMeetings.get(currentMeetings.keySet().toArray()[0]);
				Meeting currentMeeting = (chatPacket.meet == null ? backup : currentMeetings.get(chatPacket.meet));
				if(chatPacket.contact)
				{
					if(currentMeeting!=null)
						currentMeeting.addChat("CONTACT", chatPacket.msg);
					onEvent(Event.CONTACT, emPacket);
					continue;
				}
				else if(chatPacket.crying)
				{
					if(backup!=null)
						currentMeeting.addChat("CRY", chatPacket.msg);
					onEvent(Event.CRY, emPacket);
					continue;
				}
				if(currentMeeting!=null)
					currentMeeting.addChat(gamePlayers.get(chatPacket.user), chatPacket.msg);
				onEvent(Event.CHAT, emPacket);
			}
			else if(emPacket instanceof MsgPacket)
				onEvent(Event.SYSTEM, emPacket);
			else if(emPacket instanceof JoinPacket)
			{
				JoinPacket joinPacket = (JoinPacket) emPacket;
				gamePlayers.put(joinPacket.username, new Player(joinPacket.username));
				onEvent(Event.JOIN, emPacket);
			}
			else if(emPacket instanceof LeavePacket)
			{
				LeavePacket leavePacket = (LeavePacket) emPacket;
				gamePlayers.remove(leavePacket.username);
				onEvent(Event.LEAVE, emPacket);
			}
			else if(emPacket instanceof TypingPacket)
			{
				TypingPacket typingPacket = (TypingPacket) emPacket;
				gamePlayers.get(typingPacket.user).isTyping = true;
				onEvent(Event.TYPING, emPacket);
			}
			else if(emPacket instanceof StoppedTypingPacket)
			{
				StoppedTypingPacket typingPacket = (StoppedTypingPacket) emPacket;
				gamePlayers.get(typingPacket.user).isTyping = false;
				onEvent(Event.STOPPEDTYPING, emPacket);
			}
			else if(emPacket instanceof CamInitPacket)
				onEvent(Event.CAMINIT, emPacket);
			else if(emPacket instanceof CamStartPacket)
				onEvent(Event.CAMSTART, emPacket);
			else if(emPacket instanceof CamEndPacket)
				onEvent(Event.CAMEND, emPacket);
			else if(emPacket instanceof OwnerPacket)
				this.isOwner = true;
			else if(emPacket instanceof EndMeetPacket)
			{
				EndMeetPacket endMeetPacket = (EndMeetPacket) emPacket;
				gameMeetings.add(currentMeetings.get(endMeetPacket.meet));
				currentMeetings.remove(endMeetPacket.meet);
				onEvent(Event.ENDMEET, emPacket);
			}
			else if(emPacket instanceof UsersPacket)
			{
				UsersPacket usersPacket = (UsersPacket) emPacket;
				for(String key : usersPacket.users.keySet())
					gamePlayers.put(key, new Player(key));
			}
			else if(emPacket instanceof MeetPacket)
			{
				MeetPacket meetingPacket = (MeetPacket) emPacket;
				Meeting newMeeting = new Meeting(meetingPacket.meet, this);
				newMeeting.basket = meetingPacket.basket;
				newMeeting.inputs = meetingPacket.inputs;
				newMeeting.choosedata = meetingPacket.choosedata;
				newMeeting.disguise = meetingPacket.disguise;
				newMeeting.disguiseChoices = meetingPacket.disguiseChoices;
				newMeeting.exist = meetingPacket.exist;
				newMeeting.canSay = meetingPacket.say;
				newMeeting.members = meetingPacket.members;
				newMeeting.mVoteType = meetingPacket.votetype;
				newMeeting.newName = meetingPacket.newname;
				newMeeting.rawName = meetingPacket.raw_name;
				newMeeting.voteNoOne = meetingPacket.votenoone;
				newMeeting.voteSelf = meetingPacket.voteself;
				newMeeting.voteSee = meetingPacket.votesee;
				currentMeetings.put(newMeeting.mName, newMeeting);
				onEvent(Event.MEET, emPacket);
			}
			else if(emPacket instanceof AuthPacket)
			{
				AuthPacket authPacket = (AuthPacket) emPacket;
				canSpectate = authPacket.spectate;
			}
			else if(emPacket instanceof KillPacket)
			{
				KillPacket killPacket = (KillPacket) emPacket;
				gamePlayers.get(killPacket.target).isDead = true;
				onEvent(Event.KILL, emPacket);
			}
			else if(emPacket instanceof LeftPacket)
			{
				LeftPacket leftPacket = (LeftPacket) emPacket;
				for(String p : leftPacket.left)
					gamePlayers.get(p).hasLeft = true;
				onEvent(Event.LEFT, emPacket);
			}
			else if(emPacket instanceof PointPacket)
			{
				PointPacket pointPacket = (PointPacket) emPacket;
				if(pointPacket.unpoint)
					if(currentMeetings.get(pointPacket.meet).choices.containsKey(pointPacket.user))
						currentMeetings.get(pointPacket.meet).choices.remove(pointPacket.user);
				else
					currentMeetings.get(pointPacket.meet).choices.put(pointPacket.user, pointPacket.target);
				onEvent(Event.POINT, emPacket);
			}
			else if(emPacket instanceof RevealPacket)
			{
				RevealPacket revealPacket = (RevealPacket) emPacket;
				gamePlayers.get(revealPacket.user).pRole = revealPacket.data;
				onEvent(Event.REVEAL, emPacket);
			}
			else if(emPacket instanceof TimePacket)
			{
				TimePacket timePacket = (TimePacket) emPacket;
				timeLeft = timePacket.timeleft;
			}
			else if(emPacket instanceof RoundPacket)
			{
				RoundPacket roundPacket = (RoundPacket) emPacket;
				this.roundState = roundPacket.state;
				onEvent(Event.ROUND, emPacket);
			}
			else if(emPacket instanceof KickPacket)
			{
				KickPacket kickPacket = (KickPacket) emPacket;
				gamePlayers.get(kickPacket.user).hasLeft = true;
				onEvent(Event.KICK, emPacket);
			}
			else if(emPacket instanceof StartInputPacket)
			{
				StartInputPacket startInputPacket = (StartInputPacket) emPacket;
				gameInputs.add(startInputPacket.id);
				onEvent(Event.STARTINPUT, emPacket);
			}
			else if(emPacket instanceof FinishInputPacket)
			{
				FinishInputPacket finishInputPacket = (FinishInputPacket) emPacket;
				gameInputs.remove(finishInputPacket.id);
				onEvent(Event.FINISHINPUT, emPacket);
			}
			else if(emPacket instanceof KickVotePacket)
				onEvent(Event.KICKVOTE, emPacket);
			else if(emPacket instanceof InputedPacket)
				onEvent(Event.INPUTED, emPacket);
			else
				System.out.println("UNHANDLED PACKET: "+emPacket.getClass().getSimpleName());
		}
	}
	
	public boolean passBomb(String target) throws IOException
	{
		JSONObject input = new JSONObject();
		input.put("player", target);
		return sendInput(this.gameInputs.get(0), input);
	}
	
	public boolean sendOption(String option) throws IOException
	{
		OptionPacket optionPacket = new OptionPacket(this);
		optionPacket.field = option;
		return sendPacket(optionPacket);
	}
	
	public boolean sendInput(long id, Object input) throws IOException
	{
		InputPacket inputPacket = new InputPacket(this);
		inputPacket.id = id;
		inputPacket.input = input;
		return sendPacket(inputPacket);
	}
	
	public boolean sendPackets(EMPacket[] emPackets) throws IOException
	{
		String finalData = "";
		for(EMPacket emPacket : emPackets)
		{
			JSONArray jArray = new JSONArray();
			jArray.add(0, emPacket.getPacketName());
			Object packetObject = emPacket.getEMPacket();
			if(packetObject != null)
				jArray.add(1, packetObject);
			finalData += jArray.toJSONString();
		}
		TcpPacket tcpPacket = tcpConnection.constructPacket(finalData);
		return tcpConnection.sendPacket(tcpPacket);
	}
	
	public boolean sendPacket(EMPacket emPacket) throws IOException
	{
		return sendPackets(new EMPacket[]{emPacket});
	}

	@Override
	public void receivePackets(TcpPacket[] tcpPackets)
	{
		try
		{
			for(TcpPacket tcpPacket : tcpPackets)
			{
				if(tcpPacket.tcpPacketType.equals("open"))
					tcpConnection.sendPacket(tcpConnection.constructPacket("127.0.0.1:6001"));
				else if(tcpPacket.tcpPacketType.equals("data"))
				{
					if(tcpPacket.getDataAsString().equals("1"))
					{
						JoinPacket joinPacket = new JoinPacket(this);
						joinPacket.gameId = gameId + "";
						joinPacket.username = epicMafia.emUsername;
						joinPacket.password = hashedPassword;
						sendPacket(joinPacket);
					}
					else
					{
						handlePackets(packetParser.parsePacketsRaw(tcpPacket.getDataAsString()));
					}
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchMethodException e)
		{
			e.printStackTrace();
		} 
		catch (SecurityException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public String getName()
	{
		return "Game-"+gameId;
	}
}
