package epicmafia;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.parser.*;
import org.json.simple.*;

import epicmafia.game.Game;
import epicmafia.game.Lobby;
import epicmafia.game.Role;

import me.banhammer.util.FileUtils;
import me.banhammer.util.HTTPRequest;
import me.banhammer.util.HTTPResponse;

public class EpicMafia
{
	public String emUsername;
	public String emPassword;
	public String emCookie;
	public String emLoginError;
	public boolean emLoggedIn;
	public Lobby emLobby;
	public byte currentLobby = -1;
	public HttpClient httpClient;
	public HttpHost httpHost;
	public PoolingClientConnectionManager pccmMan;

	public EpicMafia(String emUsername, String emPassword)
	{
		this.emLoggedIn = false;
		this.emUsername = emUsername;
		this.emPassword = emPassword;
		httpHost = new HttpHost("www.epicmafia.com");
		pccmMan = new PoolingClientConnectionManager();
		httpClient = new DefaultHttpClient(pccmMan);
        //fiddler redirect
		//HttpHost httpProxy = new HttpHost("127.0.0.1", 8888, "http");
        //httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, httpProxy);
	}

	private long getUnixTime()
	{
		return (long) System.currentTimeMillis();
	}

	public boolean refreshLobby(byte lobbyId) throws IOException
	{
		boolean n = false;
		if(lobbyId != currentLobby)
			n = switchLobby(lobbyId);
		if(!n)
			return false;
		Lobby newLobby = new Lobby(lobbyId);
		HttpGet httpGet = new HttpGet("/game/find?page=1&ts=" + getUnixTime());
		httpGet.addHeader("Referer", "http://www.epicmafia.com/lobby");
		httpGet.addHeader("X-Requested-With", "XMLHttpRequest");
		HttpResponse httpResponse = httpClient.execute(httpHost, httpGet);
		String responseString = new String(FileUtils.readBytes(httpResponse.getEntity().getContent()));
		httpGet.releaseConnection();
		JSONObject lobbyJson = (JSONObject) JSONValue.parse(responseString);
		JSONArray lobbyGames = (JSONArray) lobbyJson.get("data");
		for(Object lobbyO : lobbyGames.toArray())
		{
			JSONObject lobbyObject = (JSONObject) lobbyO;
			Game newLobbyGame = new Game((long) lobbyObject.get("id"), this);
			newLobbyGame.gameType = (String) lobbyObject.get("gametype");
			newLobbyGame.hasPassword = (boolean) lobbyObject.get("password");
			newLobbyGame.hasCamera = (boolean) lobbyObject.get("cam");
			newLobbyGame.canReview = (boolean) lobbyObject.get("can_review");
			newLobbyGame.numPlayers = (long) lobbyObject.get("numplayers");
			newLobbyGame.maxPlayers = (long) lobbyObject.get("target");
			newLobbyGame.statusId = (long) lobbyObject.get("status_id");
			newLobbyGame.setupId = (long) lobbyObject.get("setup_id");
			newLobbyGame.hasWhisper = (boolean) lobbyObject.get("whisper");//
			newLobbyGame.startOnDay = (boolean) lobbyObject.get("startday");//
			newLobbyGame.mustLynch = (boolean) lobbyObject.get("lynch");//
			newLobbyGame.mustAct = (boolean) lobbyObject.get("mustact");//
			newLobbyGame.hasDawn = (boolean) lobbyObject.get("dawn");//
			newLobbyGame.hasNoReveal = (boolean) lobbyObject.get("noreveal");//
			newLobbyGame.hasLastWill = (boolean) lobbyObject.get("lastwill");//
			newLobbyGame.hasClosedRoles = (boolean) lobbyObject.get("closedroles");//
			newLobbyGame.hasAnonymous = (boolean) lobbyObject.get("anonymous");//
			newLobbyGame.hasMultipleMafias = (boolean) lobbyObject.get("multiple");//
			newLobbyGame.gameAction = (String) lobbyObject.get("action");
			newLobbyGame.canEnter = (boolean) lobbyObject.get("can_enter");
			newLobbyGame.gameStatus = (String) lobbyObject.get("status");
			Object setupObject = lobbyObject.get("setup");
			if((setupObject instanceof JSONArray))
			{
				JSONArray lobbyArrayA = (JSONArray) setupObject;
				JSONArray lobbyArrayB = (JSONArray) lobbyArrayA.get(0);
				for(Object jsonObjectO : lobbyArrayB.toArray())
				{
					JSONObject jsonRoleObject = (JSONObject) jsonObjectO;
					Role newRole = new Role();
					newRole.roleName = (String) jsonRoleObject.get("id");
					newRole.roleAmount = Integer.valueOf(jsonRoleObject.get("num").toString());
					newLobbyGame.gameRoles.add(newRole);
				}
			}
			newLobby.emGames.add(newLobbyGame);
		}
		emLobby = newLobby;
		currentLobby = lobbyId;
		return true;
	}

	public boolean switchLobby(byte lobbyId) throws IOException
	{
		HttpGet httpGet = new HttpGet("/lobby/"+lobbyId+"/enter");
		httpGet.addHeader("X-Requested-With", "XMLHttpRequest");
		HttpResponse httpResponse = httpClient.execute(httpHost, httpGet);
		String responseString = new String(FileUtils.readBytes(httpResponse.getEntity().getContent()));
		httpGet.releaseConnection();
		JSONArray lobbyJson = (JSONArray) JSONValue.parse(responseString);
		if(lobbyJson.get(0).toString().equals("1"))
		{
			return true;
		}
		return false;
	}
	
	public Game createGame(String setup, boolean ranked) throws IOException
	{
		String url = "/game/add/mafia?setupid=" + setup + "&ranked=" + (ranked?"1":"0");
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("Referer", "http://www.epicmafia.com/lobby");
		httpGet.addHeader("X-Requested-With", "XMLHttpRequest");
		HttpResponse httpResponse = httpClient.execute(httpHost, httpGet);
		String responseString = new String(FileUtils.readBytes(httpResponse.getEntity().getContent()));
		JSONObject jsonObject = (JSONObject) JSONValue.parse(responseString);
		if(jsonObject.get("status").equals(true))
		{
			Game game = new Game((long) jsonObject.get("table"), this);
			return game;
		}
		return null;
	}

	public Lobby getLobby()
	{
		return emLobby;
	}

	public boolean loginToEpicMafia() throws IOException
	{
		HttpPost httpPost = new HttpPost("/user/login");
		httpPost.setEntity(new StringEntity("user%5Busername%5D="+emUsername+"&user%5Bpassword%5D="+emPassword+"&reme=false"));
		httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		httpPost.setHeader("X-Requested-With", "XMLHttpRequest");
		httpPost.setHeader("Accept", "application/json, text/javascript, */*; q=0.01");
		httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.4 (KHTML, like Gecko) Chrome/22.0.1229.94 Safari/537.4");
		HttpResponse httpResponse = httpClient.execute(httpHost, httpPost);
		String responseString = new String(FileUtils.readBytes(httpResponse.getEntity().getContent()));
		httpPost.releaseConnection();
		JSONArray loginJson = (JSONArray) JSONValue.parse(responseString);
		if((boolean) loginJson.get(0) == true)
		{
			emLoggedIn = true;
			refreshLobby((byte) 5);
			return true;
		}
		emLoginError = loginJson.get(1).toString();
		return false;
	}
}
