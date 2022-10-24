package epicmafia.tcp;

public interface ITcpListener
{
	public void receivePackets(TcpPacket[] tcpPackets);
	
	public String getName();
}
