/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package silentium.authserver.network.clientpackets;

import silentium.authserver.LoginController;
import silentium.authserver.SessionKey;
import silentium.authserver.configs.MainConfig;
import silentium.authserver.network.serverpackets.LoginFail.LoginFailReason;
import silentium.authserver.network.serverpackets.PlayFail.PlayFailReason;
import silentium.authserver.network.serverpackets.PlayOk;

/**
 * Fromat is ddc d: first part of session id d: second part of session id c: server ID
 */
public class RequestServerLogin extends L2LoginClientPacket {
	private int _skey1;
	private int _skey2;
	private int _serverId;

	public int getSessionKey1() {
		return _skey1;
	}

	public int getSessionKey2() {
		return _skey2;
	}

	public int getServerID() {
		return _serverId;
	}

	@Override
	public boolean readImpl() {
		if (_buf.remaining() >= 9) {
			_skey1 = readD();
			_skey2 = readD();
			_serverId = readC();
			return true;
		}
		return false;
	}

	@Override
	public void run() {
		final SessionKey sk = getClient().getSessionKey();

		// if we didnt showed the license we cant check these values
		if (!MainConfig.SHOW_LICENCE || sk.checkLoginPair(_skey1, _skey2)) {
			if (LoginController.getInstance().isLoginPossible(getClient(), _serverId)) {
				getClient().setJoinedGS(true);
				getClient().sendPacket(new PlayOk(sk));
			} else
				getClient().close(PlayFailReason.REASON_TOO_MANY_PLAYERS);
		} else
			getClient().close(LoginFailReason.REASON_ACCESS_FAILED);
	}
}
