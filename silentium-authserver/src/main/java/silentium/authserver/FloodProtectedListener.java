/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package silentium.authserver;

import javolution.util.FastMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import silentium.authserver.configs.MainConfig;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

/**
 * @author -Wooden-
 */
public abstract class FloodProtectedListener extends Thread {
	private final Logger _log = LoggerFactory.getLogger(FloodProtectedListener.class.getName());
	private final Map<String, ForeignConnection> _floodProtection = new FastMap<>();
	private final String _listenIp;
	private final int _port;
	private final ServerSocket _serverSocket;

	public FloodProtectedListener(final String listenIp, final int port) throws IOException {
		_port = port;
		_listenIp = listenIp;
		_serverSocket = "*".equals(_listenIp) ? new ServerSocket(_port) : new ServerSocket(_port, 50,
				InetAddress.getByName(_listenIp));
	}

	@Override
	public void run() {
		Socket connection = null;

		while (true) {
			try {
				connection = _serverSocket.accept();
				if (MainConfig.FLOOD_PROTECTION) {
					ForeignConnection fConnection = _floodProtection.get(connection.getInetAddress().getHostAddress());
					if (fConnection != null) {
						fConnection.connectionNumber += 1;
						if (fConnection.connectionNumber > MainConfig.FAST_CONNECTION_LIMIT && System.currentTimeMillis() - fConnection.lastConnection < MainConfig.NORMAL_CONNECTION_TIME || System.currentTimeMillis() - fConnection.lastConnection < MainConfig.FAST_CONNECTION_TIME || fConnection.connectionNumber > MainConfig.MAX_CONNECTION_PER_IP) {
							fConnection.lastConnection = System.currentTimeMillis();
							connection.close();
							fConnection.connectionNumber -= 1;
							if (!fConnection.isFlooding)
								_log.warn("Potential Flood from " + connection.getInetAddress().getHostAddress());
							fConnection.isFlooding = true;
							continue;
						}
						if (fConnection.isFlooding) // if connection was flooding server but now passed the check
						{
							fConnection.isFlooding = false;
							_log.info(connection.getInetAddress().getHostAddress() + " is not considered as flooding anymore.");
						}
						fConnection.lastConnection = System.currentTimeMillis();
					} else {
						fConnection = new ForeignConnection(System.currentTimeMillis());
						_floodProtection.put(connection.getInetAddress().getHostAddress(), fConnection);
					}
				}
				addClient(connection);
			} catch (Exception e) {
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e2) {
				}
				if (isInterrupted()) {
					// shutdown?
					try {
						_serverSocket.close();
					} catch (IOException io) {
						_log.error(e.getLocalizedMessage(), io);
					}
					break;
				}
			}
		}
	}

	protected static class ForeignConnection {
		public int connectionNumber;
		public long lastConnection;
		public boolean isFlooding = false;

		/**
		 * @param time
		 */
		public ForeignConnection(final long time) {
			lastConnection = time;
			connectionNumber = 1;
		}
	}

	public abstract void addClient(Socket s);

	public void removeFloodProtection(final String ip) {
		if (!MainConfig.FLOOD_PROTECTION)
			return;
		final ForeignConnection fConnection = _floodProtection.get(ip);
		if (fConnection != null) {
			fConnection.connectionNumber -= 1;
			if (fConnection.connectionNumber == 0) {
				_floodProtection.remove(fConnection);
			}
		} else {
			_log.warn("Removing a flood protection for a GameServer that was not in the connection map??? :" + ip);
		}
	}

	public void close() {
		try {
			_serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
