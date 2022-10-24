package epicmafia.game;

public interface IGameListener
{
	public void onEvent(Event event, Object... params);
	
	public enum Event
	{
		CHAT, KILL, LEFT, ROUND, // GAME PACKETS
		TYPING, STOPPEDTYPING, STARTINPUT, INPUT, FINISHINPUT, INPUTED, //INPUT PACKETS
		MEET, ENDMEET, TIME, POINT, // MEETING PACKETS
		USERS, JOIN, LEAVE, REVEAL, KICK, KICKVOTE, // USER POPULATION PACKETS
		CAMINIT, CAMSTART, CAMEND, // CAMERA PACKETS
		CRY, CONTACT, SYSTEM // CHAT PACKETS
	}
}