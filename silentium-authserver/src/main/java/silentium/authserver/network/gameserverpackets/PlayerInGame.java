/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package silentium.authserver.network.gameserverpackets;

import silentium.authserver.GameServerTable;
import silentium.authserver.GameServerThread;
import silentium.authserver.configs.MainConfig;
import silentium.authserver.network.clientpackets.ClientBasePacket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author -Wooden-
 * @rework Ashe
 */
public class PlayerInGame extends ClientBasePacket {
	protected static final Logger _log = LoggerFactory.getLogger(PlayerInGame.class.getName());

	/**
	 * @param decrypt
	 * @param server
	 */
	public PlayerInGame(byte[] decrypt, GameServerThread server) {
		super(decrypt);
		int size = readH();
		for (int i = 0; i < size; i++) {
			String account = readS();
			server.addAccountOnGameServer(account);
			if (MainConfig.PACKET_HANDLER_DEBUG) {
				_log.info("Account " + account + " logged in GameServer: [" + server.getServerId() + "] " + GameServerTable.getInstance().getServerNameById(server.getServerId()));
			}
		}
	}

}