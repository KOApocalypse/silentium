/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package silentium.tools.gsregistering;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import silentium.authserver.GameServerTable;
import silentium.authserver.configs.MainConfig;
import silentium.commons.ServerType;
import silentium.commons.database.DatabaseFactory;
import silentium.commons.utils.HexUtils;

public class GameServerRegister
{
	private static String _choice;
	private static boolean _choiceOk;

	public static void main(String[] args) throws IOException
	{
		ServerType.serverMode = ServerType.MODE_LOGINSERVER;

		MainConfig.load();
		DatabaseFactory.init();

		LineNumberReader _in = new LineNumberReader(new InputStreamReader(System.in));

		System.out.println("Welcome to L2J gameserver registering.");
		System.out.println("Enter the ID of the server you want to register.");
		System.out.println("-- Type 'help' to get a list of IDs.");
		System.out.println("-- Type 'clean' to unregister all registered gameservers from this LoginServer.");
		while (!_choiceOk)
		{
			System.out.println("Your choice:");
			_choice = _in.readLine();
			if (_choice.equalsIgnoreCase("help"))
			{
				for (Map.Entry<Integer, String> entry : GameServerTable.getInstance().getServerNames().entrySet())
				{
					System.out.println("Server ID: " + entry.getKey() + "\t- " + entry.getValue() + " - In Use: " + (GameServerTable.getInstance().hasRegisteredGameServerOnId(entry.getKey()) ? "YES" : "NO"));
				}
				System.out.println("You can also see 'servername.xml'.");
			}
			else if (_choice.equalsIgnoreCase("clean"))
			{
				System.out.print("This is going to UNREGISTER ALL servers from this LoginServer. Are you sure? (y/n) ");
				_choice = _in.readLine();
				if (_choice.equals("y"))
				{
					GameServerRegister.cleanRegisteredGameServersFromDB();
					GameServerTable.getInstance().getRegisteredGameServers().clear();
				}
				else
				{
					System.out.println("ABORTED");
				}
			}
			else
			{
				try
				{
					if (GameServerTable.getInstance().getServerNames().isEmpty())
					{
						System.out.println("No server names available, be sure 'servername.xml' is in the config directory.");
						System.exit(1);
					}

					final int id = new Integer(_choice).intValue();
					if (GameServerTable.getInstance().getServerNameById(id) == null)
					{
						System.out.println("No name for id: " + id);
						continue;
					}

					if (GameServerTable.getInstance().hasRegisteredGameServerOnId(id))
					{
						System.out.println("This ID isn't available.");
					}
					else
					{
						byte[] hexId = HexUtils.generateHex(16);

						GameServerTable.getInstance().registerServerOnDB(hexId, id, "");
						saveHexid(id, new BigInteger(hexId).toString(16), "hexid.txt");
						System.out.println("Server registered. Its hexid is saved to 'hexid.txt'");
						System.out.println("Put this file in the /config folder of your gameserver and rename it to 'hexid.txt'");
						return;
					}
				}
				catch (NumberFormatException nfe)
				{
					System.out.println("Type a number or 'help'.");
				}
			}
		}
	}

	public static void cleanRegisteredGameServersFromDB()
	{
		try (Connection con = DatabaseFactory.getConnection())
		{
			PreparedStatement statement = con.prepareStatement("DELETE FROM gameservers");
			statement.executeUpdate();
			statement.close();
		}
		catch (SQLException e)
		{
			System.out.println("SQL error while cleaning registered servers: " + e);
		}
	}

	public static void saveHexid(int serverId, String hexId, String fileName)
	{
		try
		{
			Properties hexSetting = new Properties();
			File file = new File(fileName);
			file.createNewFile();

			OutputStream out = new FileOutputStream(file);
			hexSetting.setProperty("ServerID", String.valueOf(serverId));
			hexSetting.setProperty("HexID", hexId);
			hexSetting.store(out, "the hexID to auth into login");
			out.close();
		}
		catch (Exception e)
		{
			System.out.println("Failed to save hex id to " + fileName + " file.");
			e.printStackTrace();
		}
	}
}