package epicmafia;

import java.io.IOException;
import java.net.MalformedURLException;

import epicmafia.game.Game;

public class Program
{
	public static void main(String[] args)
	{
		EpicMafia epicMafia = new EpicMafia("*", "*");
		try
		{
			if(epicMafia.loginToEpicMafia())
			{
				System.out.println("Successfully logged in!");
			}
			else
			{
				System.out.println("Failed... "+epicMafia.emLoginError);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
