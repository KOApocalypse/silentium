/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package silentium.authserver.network.gameserverpackets;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import silentium.authserver.GameServerThread;
import silentium.authserver.network.GameServerPacketHandler.GameServerState;
import silentium.authserver.network.clientpackets.ClientBasePacket;
import silentium.commons.crypt.NewCrypt;

/**
 * @author -Wooden-
 * @reworked Ashe
 */
public class BlowFishKey extends ClientBasePacket {
	protected static final Logger _log = LoggerFactory.getLogger(BlowFishKey.class.getName());

	/**
	 * @param decrypt
	 * @param server
	 */
	public BlowFishKey(final byte[] decrypt, final GameServerThread server) {
		super(decrypt);
		int size = readD();
		byte[] tempKey = readB(size);
		try {
			byte[] tempDecryptKey;
			Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
			rsaCipher.init(Cipher.DECRYPT_MODE, server.getPrivateKey());
			tempDecryptKey = rsaCipher.doFinal(tempKey);
			// there are nulls before the key we must remove them
			int i = 0;
			int len = tempDecryptKey.length;
			for (; i < len; i++) {
				if (tempDecryptKey[i] != 0) {
					break;
				}
			}
			byte[] key = new byte[len - i];
			System.arraycopy(tempDecryptKey, i, key, 0, len - i);
			server.SetBlowFish(new NewCrypt(key));
			server.setLoginConnectionState(GameServerState.BF_CONNECTED);
		} catch (GeneralSecurityException e) {
			_log.error("Error While decrypting blowfish key (RSA): "+ e.getMessage(), e);
		}
	}
}