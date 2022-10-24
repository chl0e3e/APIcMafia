package epicmafia.tcp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import me.banhammer.util.FileUtils;

import org.apache.http.Consts;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.impl.conn.PoolingClientConnectionManager;

/**
 * A TCP connection using HTTP hooks on an orbited server
 * @author TheEndermen
 */
public class TcpConnection
{
	public int tcpAck = 2;
	public int tcpSendAck = 1;
	
	public String tcpSession;
	public String tcpUrl;
	public String noCacheCode;
	
	public HttpClient httpClient;
	public HttpHost httpHost;
	
	public boolean isConnected;
	public boolean isOpen;
	
	public Thread receiverThread;
	
	public HashMap<String, ITcpListener> tcpListeners = new HashMap<String, ITcpListener>();
	public HashMap<String, String> tcpOptions = new HashMap<String, String>();
	
	public TcpConnection(HttpHost httpHost, HttpClient httpClient, String tcpUrl)
	{
		this.httpHost = httpHost;
		this.tcpUrl = tcpUrl;
		this.httpClient = httpClient;
		this.noCacheCode = Float.toString(new Random().nextFloat());
	}
	
	public boolean connectTcp() throws IOException
	{
		HttpGet httpGet = new HttpGet(tcpUrl + "?nocache="+noCacheCode);
		HttpResponse httpResponse = httpClient.execute(httpHost, httpGet);
		String responseString = new String(FileUtils.readBytes(httpResponse.getEntity().getContent()));
		if(responseString.length() == 32)
		{
			tcpSession = responseString;
			isConnected = true;
			//startReceiverThread();
			return true;
		}
		return false;
	}
	
	public boolean registerTcpListener(ITcpListener iTcpListener)
	{
		if(!tcpListeners.containsKey(iTcpListener.getName()))
		{
			tcpListeners.put(iTcpListener.getName(), iTcpListener);
			return true;
		}
		return false;
	}
	
	public boolean unregisterTcpListener(ITcpListener iTcpListener)
	{
		if(tcpListeners.containsKey(iTcpListener.getName()))
		{
			tcpListeners.remove(iTcpListener.getName());
			return true;
		}
		return false;
	}
	
	public boolean isFirst = true;
	
	public TcpPacket[] receiveData() throws IllegalStateException, IOException
	{
		List<TcpPacket> tcpPackets = new ArrayList<TcpPacket>();
		HttpGet httpGet = new HttpGet(tcpUrl + "/"+tcpSession+"/longpoll?nocache="+noCacheCode+(isFirst?"":("&ack="+tcpAck)));
		HttpResponse httpResponse = httpClient.execute(httpHost, httpGet);
		String responseString = new String(FileUtils.readBytes(httpResponse.getEntity().getContent()));
		httpGet.releaseConnection();
		int k=0;
		int commaPos = -1;
		int offset = 0;
		int argEnd = -1;
		int argSize = -1;
		int part = 0;
		TcpPacket currentPacket = new TcpPacket();
		while(true)
		{
			k++;
			if(k>2000)
				throw new IOException("Borked transport");
			if(commaPos == -1)
				commaPos = responseString.indexOf(",", offset);
			if(commaPos == -1)
				break; //no more commas
			if(argEnd == -1)
			{
                argSize = Integer.parseInt(responseString.substring(offset + 1, commaPos));
                argEnd = commaPos + 1 + argSize;
			}
            String data = responseString.substring(commaPos + 1, argEnd);
            if(data.length() != argSize)
            	throw new IOException("We've done something wrong");
            switch(part)
            {
            case 0: currentPacket.tcpAck = Integer.valueOf(data); break;
            case 1: currentPacket.tcpPacketType = data; break;
            case 2:
            	currentPacket.tcpData = data.getBytes();
            	if(currentPacket.tcpPacketType.equals("data")) 
            	{
            		currentPacket.tcpDataEncoded = new String(currentPacket.tcpData);
            		currentPacket.decodePacket();
            	}
            	break;
            }
            boolean isLast = (responseString.charAt(offset) == '0');
            offset = argEnd;
            argEnd = -1;
            commaPos = -1;
            part++;
            if(isLast)
            {
            	part = 0;
            	tcpPackets.add(currentPacket);
            	currentPacket = new TcpPacket();
            }
		}
		for(int i=0;i<tcpPackets.size();i++)
		{
			TcpPacket curPack = tcpPackets.get(i);
			if(curPack.tcpPacketType.equals("opt"))
			{
				String[] optParts = curPack.getDataAsString().split(",");
				tcpOptions.put(optParts[0], optParts[1]);
			}
			else if(curPack.tcpPacketType.equals("open"))
				isOpen = true;
			else if(curPack.tcpPacketType.equals("close"))
			{
				isOpen = false;
				isConnected = false;
			}
			tcpAck = curPack.tcpAck;
		}
		isFirst = false;
		return tcpPackets.toArray(new TcpPacket[0]);
	}
	
	public void startReceiverAsync()
	{
		receiverThread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				startReceiver();
			}
		});
		receiverThread.start();
	}
	
	public void startReceiver()
	{
		while(isConnected)
		{
			long startTime = System.currentTimeMillis();
			TcpPacket[] tcpResponse;
			try
			{
				tcpResponse = receiveData();
				for(ITcpListener tcpListener : tcpListeners.values())
					tcpListener.receivePackets(tcpResponse);
			}
			catch(IOException e)
			{
				isConnected = false;
				isOpen = false;
			}
			long finishTime = System.currentTimeMillis();
			long sleepTime = 1000 - (finishTime - startTime);
			if(sleepTime > 0)
			{
				try
				{
					Thread.sleep(sleepTime);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	public boolean sendPackets(TcpPacket[] tcpPackets) throws IOException
	{
		String postData = "";
		for(TcpPacket tcpPacket : tcpPackets)
			postData += tcpPacket.constructPacket();
		boolean result = sendData(postData);
		return result;
	}
	
	public boolean sendData(String postData) throws IOException
	{
		StringEntity reqEntity = new StringEntity(postData);
		HttpPost httpPost = new HttpPost(tcpUrl + "/"+tcpSession + "?nocache=" + noCacheCode + "&ack=" + tcpAck);
		httpPost.setHeader("Content-Type", "application/xml");
		httpPost.setEntity(reqEntity);
		HttpResponse httpResponse = httpClient.execute(httpHost, httpPost);
		String responseString = new String(FileUtils.readBytes(httpResponse.getEntity().getContent()));
		httpPost.releaseConnection();
		if(responseString.equals("OK"))
			return true;
		return false;
	}
	
	public boolean sendPacket(TcpPacket tcpPacket) throws IOException
	{
		return sendPackets(new TcpPacket[] { tcpPacket });
	}
	
	////////////////////////////////////////////////////////////////////////////////////////
	///packet construction
	////////////////////////////////////////////////////////////////////////////////////////
	
	public TcpPacket constructPacket(byte[] tcpData)
	{
		return new TcpPacket(tcpSendAck++, tcpData);
	}
	
	public TcpPacket constructPacket(String tcpData)
	{
		return constructPacket(tcpData.getBytes());
	}
	
	public TcpPacket constructPacket(String tcpData, String sendEncoding) throws UnsupportedEncodingException
	{
		return constructPacket(tcpData.getBytes(sendEncoding));
	}
}
