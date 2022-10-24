package epicmafia.bot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.exception.IrcException;
import org.pircbotx.exception.NickAlreadyInUseException;

import epicmafia.EpicMafia;
import epicmafia.game.Game;
import epicmafia.game.IGameListener;
import epicmafia.game.Meeting;
import epicmafia.game.Player;
import epicmafia.packets.ChatPacket;
import epicmafia.packets.EndMeetPacket;
import epicmafia.packets.FinishInputPacket;
import epicmafia.packets.JoinPacket;
import epicmafia.packets.LeavePacket;
import epicmafia.packets.MeetPacket;
import epicmafia.packets.MsgPacket;
import epicmafia.packets.PointPacket;
import epicmafia.packets.RevealPacket;
import epicmafia.packets.StartInputPacket;

public class EMBot extends ListenerAdapter implements IGameListener
{
	public String botNick;
	public String botServer;
	public String botChannel;
	public String botPrefix = ".";
	public int botServerPort;
	public PircBotX pircBot;
	public EpicMafia epicMafia;
	public Game curGame;
	
	public EMBot(String botNick, String botServer, int botServerPort)
	{
		System.out.println("Making EMBot");
		this.botNick = botNick;
		this.botServer = botServer;
		this.botServerPort = botServerPort;
		this.pircBot = new PircBotX();
	}

	public static void main(String[] args)
	{
		EMBot emBot = new EMBot("EMBot2", "irc.esper.net", 6667);
		try
		{
			emBot.startBot("#epicsantamafia");
		}
		catch (NickAlreadyInUseException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (IrcException e)
		{
			e.printStackTrace();
		}
	}
	
	private void startBot(String botChannel) throws NickAlreadyInUseException, IOException, IrcException
	{
		System.out.println("Starting bot");
		this.botChannel = botChannel;
		this.pircBot.getListenerManager().addListener(this);
		this.pircBot.setName(botNick);
		this.pircBot.connect(botServer, botServerPort);
		this.pircBot.joinChannel(botChannel);
		epicMafia = new EpicMafia("*", "*");
		epicMafia.loginToEpicMafia();
		System.out.println("Started bot");
		this.pircBot.sendMessage(botChannel, "EMBot is Ready!");
	}

	@Override
	public void onEvent(Event event, Object... params)
	{
		if(event == Event.CHAT)
		{
			ChatPacket chatPacket = (ChatPacket) params[0];
			Player player = curGame.gamePlayers.get(chatPacket.user);
			if(player == null)
				player = new Player(chatPacket.user);
			
			String meet = "unknown";
			if(chatPacket.meet != null)
				meet = chatPacket.meet;
			else
				meet = (curGame.currentMeetings.size() > 0 ? curGame.currentMeetings.get(curGame.currentMeetings.keySet().toArray()[0]).mName : "UNKNOWN");
			meet = "[" + Colors.BROWN + meet + Colors.NORMAL + "]";
			String rolestring = (player.pRole != null ? "[" + Colors.DARK_BLUE + player.pRole.toUpperCase() + Colors.NORMAL + "] " : " ");
			String deadstring = (chatPacket.dead ? " [" + Colors.RED + "DEAD" + Colors.NORMAL + "] " : " ");
			this.pircBot.sendMessage(botChannel, rolestring.toUpperCase() + meet.toUpperCase() + deadstring + "<" + player.pName + "> " + Colors.RED + chatPacket.msg);
		}
		else if(event == Event.CRY)
		{
			ChatPacket chatPacket = (ChatPacket) params[0];
			this.pircBot.sendMessage(botChannel, "Someone cried: "+chatPacket.msg);
		}
		else if(event == Event.CONTACT)
		{
			ChatPacket chatPacket = (ChatPacket) params[0];
			if(chatPacket.role != null)
				if(chatPacket.user != null)
				{
					this.pircBot.sendMessage(botChannel, "You contacted the "+chatPacket.role+": "+chatPacket.msg);
				}
				else
				{
					this.pircBot.sendMessage(botChannel, "You received a message from the "+chatPacket.role+": "+chatPacket.msg);
				}
		}
		else if(event == Event.SYSTEM)
		{
			MsgPacket chatPacket = (MsgPacket) params[0];
			this.pircBot.sendMessage(botChannel, "[SYS] "+chatPacket.msg);
		}
		else if(event == Event.ENDMEET)
		{
			EndMeetPacket endMeetPacket = (EndMeetPacket) params[0];
			this.pircBot.sendMessage(botChannel, "End of meeting: " + endMeetPacket.meet);
		}
		else if(event == Event.MEET)
		{
			MeetPacket meetPacket = (MeetPacket) params[0];
			this.pircBot.sendMessage(botChannel, "Start of meeting: "+meetPacket.meet);
			String attendants = "";
			for(String member : meetPacket.members)
			{
				attendants += member + ", ";
			}
			this.pircBot.sendMessage(botChannel, "Attendants: "+attendants);
		}
		else if(event == Event.POINT)
		{
			PointPacket pointPacket = (PointPacket) params[0];
			Player player = curGame.gamePlayers.get(pointPacket.user);
			if(player == null)
				player = new Player(pointPacket.user);
			
			String meet = " [" + Colors.PURPLE + "UNKNOWN" + Colors.NORMAL + "] ";
			if(pointPacket.meet != null)
				meet = " [" + Colors.CYAN + pointPacket.meet + Colors.NORMAL + "] ";
			String role = "[" + player.pRole.toUpperCase() + "]";
			this.pircBot.sendMessage(botChannel, role.toUpperCase() + meet.toUpperCase() + player.pName + " points at " + pointPacket.target);
		}
		else if(event == Event.JOIN)
		{
			JoinPacket joinPacket = (JoinPacket) params[0];
			this.pircBot.sendMessage(botChannel, joinPacket.username + " joined the game");
		}
		else if(event == Event.LEAVE)
		{
			LeavePacket leavePacket = (LeavePacket) params[0];
			this.pircBot.sendMessage(botChannel, leavePacket.username + " left the game");
		}
		else if(event == Event.REVEAL)
		{
			RevealPacket revealPacket = (RevealPacket) params[0];
			this.pircBot.sendMessage(botChannel, revealPacket.user + " has been revealed as a "+revealPacket.data);
		}
		else if(event == Event.STARTINPUT)
		{
			StartInputPacket startInputPacket = (StartInputPacket) params[0];
			this.pircBot.sendMessage(botChannel, "Started input: "+startInputPacket.id);
		}
		else if(event == Event.FINISHINPUT)
		{
			FinishInputPacket startInputPacket = (FinishInputPacket) params[0];
			this.pircBot.sendMessage(botChannel, "Finished input: "+startInputPacket.id);
		}
	}

	@Override
	public void onMessage(MessageEvent event) throws Exception
	{
		String message = event.getMessage();
		if(message.startsWith(botPrefix))
		{
			String commandMessage = message.substring(botPrefix.length());
			String[] commandSplit = commandMessage.split(" ");
			if(commandSplit[0].equals("switchlobby") && commandSplit.length == 2)
			{
				byte lobbyId = Byte.valueOf(commandSplit[1]);
				this.pircBot.sendMessage(botChannel, (epicMafia.refreshLobby(lobbyId) ? "Successfully switched lobby!" : "Failed to switch lobby!"));
			}
			else if(commandSplit[0].equals("switchlobby"))
				this.pircBot.sendMessage(botChannel, botPrefix+"switchlobby <lobbynum> | 1 = Training, 3 = Competitive, 5 = Sandbox");
			else if(commandSplit[0].equals("lobby"))
				for(Game game : epicMafia.emLobby.emGames)
					this.pircBot.sendMessage(botChannel, game.gameId + " | " + game.gameStatus + " | " + game.gameAction + " | " + game.numPlayers + "/" + game.maxPlayers + " | " + game.setupId);
			else if(commandSplit[0].equals("join"))
			{
				if(curGame == null)
				{
					curGame = new Game(Long.valueOf(commandSplit[1]), epicMafia);
					boolean bConnected = curGame.joinGame(true);
					curGame.registerGameListener(this);
					this.pircBot.sendMessage(botChannel, (bConnected ? "Successfully joined game " + commandSplit[1] : "Failed to join game..."));
				}
				else
					this.pircBot.sendMessage(botChannel, "But, you're already in a game!");
			}
			else if(commandSplit[0].equals("point") && commandSplit.length == 3)
			{
				curGame.currentMeetings.get(commandSplit[1]).pointPlayer(commandSplit[2]);
			}
			else if(commandSplit[0].equals("leave"))
			{
				LeavePacket leavePacket = new LeavePacket(curGame);
				curGame.sendPacket(leavePacket);
				curGame = null;
			}
			else if(commandSplit[0].equals("players"))
			{
				if(commandSplit.length == 1)
				{
					String alive = "";
					String dead = "";
					String left = "";
					for(Player p : curGame.gamePlayers.values())
					{
						if(p.hasLeft)
							left += "["+p.pRole+"]"+p.pName+", ";
						else if(p.isDead)
							dead += "["+p.pRole+"]"+p.pName+", ";
						else
							alive += "["+p.pRole+"]"+p.pName+", ";
					}
					this.pircBot.sendMessage(botChannel, "Alive: "+alive + " Dead: "+dead+" Left: "+left);
				}
				else if(commandSplit.length == 2)
				{
					String result = "";
					for(String player : curGame.currentMeetings.get(commandSplit[1]).members)
					{
						result += player + ", ";
					}
					this.pircBot.sendMessage(botChannel, result);
				}
			}
			else if(commandSplit[0].equals("bomb") && commandSplit.length == 2)
			{
				String input = commandSplit[1];
				curGame.passBomb(input);
			}
			else if(commandSplit[0].equals("option") && commandSplit.length == 2)
			{
				String input = commandSplit[1];
				curGame.sendOption(input);
			}
			else if(commandSplit[0].equals("create") && commandSplit.length > 1)
			{
				boolean ranked = false;
				if(commandSplit.length == 3)
					ranked = Boolean.getBoolean(commandSplit[2]);
				curGame = epicMafia.createGame(commandSplit[1], ranked);
				if(curGame == null)
				{
					this.pircBot.sendMessage(botChannel, "Failed to create game!");
					return;
				}
				boolean bConnected = curGame.joinGame(true);
				curGame.registerGameListener(this);
				this.pircBot.sendMessage(botChannel, (bConnected ? "Successfully joined game " + curGame.gameId + ". | Join at http://www.epicmafia.com/game/"+curGame.gameId : "Failed to join game..."));
			}
		}
		else
		{
			if(curGame != null&&event.getUser().getNick().equalsIgnoreCase("TheEndermen"))
				curGame.sendChat(message, false);
		}
	}
}
