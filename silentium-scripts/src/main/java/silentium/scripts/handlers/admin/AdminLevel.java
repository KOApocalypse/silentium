/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package silentium.scripts.handlers.admin;

import silentium.gameserver.handler.IAdminCommandHandler;
import silentium.gameserver.model.L2Object;
import silentium.gameserver.model.actor.L2Playable;
import silentium.gameserver.model.actor.instance.L2PcInstance;
import silentium.gameserver.model.base.Experience;
import silentium.gameserver.network.SystemMessageId;
import silentium.gameserver.network.clientpackets.Say2;

import java.util.StringTokenizer;

public class AdminLevel implements IAdminCommandHandler {
	private static final String[] ADMIN_COMMANDS = { "admin_addlevel", "admin_setlevel" };

	@Override
	public boolean useAdminCommand(final String command, final L2PcInstance activeChar) {
		if (activeChar == null)
			return false;

		final L2Object targetChar = activeChar.getTarget();

		final StringTokenizer st = new StringTokenizer(command, " ");
		final String actualCommand = st.nextToken(); // Get actual command

		String val = "";
		if (st.countTokens() >= 1)
			val = st.nextToken();

		if ("admin_addlevel".equalsIgnoreCase(actualCommand)) {
			try {
				if (targetChar instanceof L2Playable)
					((L2Playable) targetChar).getStat().addLevel(Byte.parseByte(val));
			} catch (NumberFormatException e) {
				activeChar.sendChatMessage(0, Say2.ALL, "SYS", "Wrong number format.");
				return false;
			}
		} else if ("admin_setlevel".equalsIgnoreCase(actualCommand)) {
			try {
				if (!(targetChar instanceof L2PcInstance)) {
					activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT); // incorrect target!
					return false;
				}
				final L2PcInstance targetPlayer = (L2PcInstance) targetChar;

				final byte lvl = Byte.parseByte(val);
				if (lvl >= 1 && lvl <= Experience.MAX_LEVEL) {
					final long pXp = targetPlayer.getExp();
					final long tXp = Experience.LEVEL[lvl];

					if (pXp > tXp)
						targetPlayer.removeExpAndSp(pXp - tXp, 0);
					else if (pXp < tXp)
						targetPlayer.addExpAndSp(tXp - pXp, 0);
				} else {
					activeChar.sendChatMessage(0, Say2.ALL, "SYS", "You must specify level between 1 and " + Experience.MAX_LEVEL + '.');
					return false;
				}
			} catch (NumberFormatException e) {
				activeChar.sendChatMessage(0, Say2.ALL, "SYS", "You must specify level between 1 and " + Experience.MAX_LEVEL + '.');
				return false;
			}
		}
		return true;
	}

	@Override
	public String[] getAdminCommandList() {
		return ADMIN_COMMANDS;
	}
}