package epicmafia.tcp;

import org.apache.commons.codec.binary.Base64;

public class TcpPacket
{
	public int tcpAck;
	public String tcpDataEncoded;
	public byte[] tcpData;
	public String tcpPacketType;
	
	TcpPacket(int tcpAck, byte[] tcpData)
	{
		this.tcpAck = tcpAck;
		setData(tcpData);
	}
	
	TcpPacket(int tcpAck, String tcpDataEncoded, String packetType)
	{
		this.tcpAck = tcpAck;
		if(packetType.equals("data"))
		{
			decodePacket();
			this.tcpDataEncoded = tcpDataEncoded;
		}
		else
			this.tcpData = tcpDataEncoded.getBytes();
		this.tcpPacketType = packetType;
	}
	
	public TcpPacket()
	{
		// do nothing, this is just if we want to self-populate the packet bit by bit (pun there, heh)
	}

	public void setData(byte[] tcpData)
	{
		this.tcpData = tcpData;
		encodePacket();
	}
	
	public byte[] getData()
	{
		return this.tcpData;
	}
	
	public int getFutureAck()
	{
		return tcpAck + 1;
	}
	
	public String getFutureAckAsString()
	{
		return getFutureAck() + "";
	}
	
	public int getFutureAckLength10()
	{
		return getFutureAckAsString().length() + 10;
	}
	
	public String getDataLengthParam()
	{
		return "data0" + tcpDataEncoded.length();
	}
	
	public String getFutureAckAnd14()
	{
		return getFutureAck() + "14";
	}
	
	public String constructPacket()
	{
		return getFutureAckLength10() + "," + getFutureAckAnd14() + "," + getDataLengthParam() + "," + tcpDataEncoded;
	}
	
	public String getDataAsString()
	{
		return new String(tcpData);
	}
	
	public String getEncoded()
	{
		return tcpDataEncoded;
	}
	
	public void encodePacket()
	{
		tcpDataEncoded = Base64.encodeBase64String(tcpData);
	}
	
	public void decodePacket()
	{
		tcpData = Base64.decodeBase64(tcpDataEncoded);
	}
}
