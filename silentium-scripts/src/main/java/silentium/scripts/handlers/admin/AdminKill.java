/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package silentium.scripts.handlers.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import silentium.gameserver.configs.NPCConfig;
import silentium.gameserver.handler.IAdminCommandHandler;
import silentium.gameserver.model.L2Object;
import silentium.gameserver.model.L2World;
import silentium.gameserver.model.actor.L2Character;
import silentium.gameserver.model.actor.instance.L2PcInstance;
import silentium.gameserver.network.SystemMessageId;
import silentium.gameserver.network.clientpackets.Say2;

import java.util.StringTokenizer;

/**
 * This class handles following admin commands: - kill = kills target L2Character - kill_monster = kills target non-player - kill <radius> = If
 * radius is specified, then ALL players only in that radius will be killed. - kill_monster <radius> = If radius is specified, then ALL
 * non-players only in that radius will be killed.
 */
public class AdminKill implements IAdminCommandHandler {
	private static final Logger _log = LoggerFactory.getLogger(AdminKill.class.getName());
	private static final String[] ADMIN_COMMANDS = { "admin_kill", "admin_kill_monster" };

	@Override
	public boolean useAdminCommand(final String command, final L2PcInstance activeChar) {
		if (command.startsWith("admin_kill")) {
			final StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken(); // skip command

			if (st.hasMoreTokens()) {
				final String firstParam = st.nextToken();
				final L2PcInstance plyr = L2World.getInstance().getPlayer(firstParam);
				if (plyr != null) {
					if (st.hasMoreTokens()) {
						try {
							final int radius = Integer.parseInt(st.nextToken());
							for (final L2Character knownChar : plyr.getKnownList().getKnownCharactersInRadius(radius)) {
								if (knownChar == null || knownChar.equals(activeChar))
									continue;

								kill(activeChar, knownChar);
							}

							activeChar.sendChatMessage(0, Say2.ALL, "SYS", "Killed all characters within a " + radius + " unit radius.");
							return true;
						} catch (NumberFormatException e) {
							activeChar.sendChatMessage(0, Say2.ALL, "SYS", "Invalid radius.");
							return false;
						}
					}
					kill(activeChar, plyr);
				} else {
					try {
						final int radius = Integer.parseInt(firstParam);

						for (final L2Character knownChar : activeChar.getKnownList().getKnownCharactersInRadius(radius)) {
							if (knownChar == null || knownChar.equals(activeChar))
								continue;
							kill(activeChar, knownChar);
						}

						activeChar.sendChatMessage(0, Say2.ALL, "SYS", "Killed all characters within a " + radius + " unit radius.");
						return true;
					} catch (NumberFormatException e) {
						activeChar.sendChatMessage(0, Say2.ALL, "SYS", "Usage: //kill <player_name | radius>");
						return false;
					}
				}
			} else {
				final L2Object obj = activeChar.getTarget();
				if (!(obj instanceof L2Character))
					activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
				else
					kill(activeChar, (L2Character) obj);
			}
		}
		return true;
	}

	private static void kill(final L2PcInstance activeChar, final L2Character target) {
		if (target instanceof L2PcInstance) {
			if (!target.isGM())
				target.stopAllEffects(); // e.g. invincibility effect
			target.reduceCurrentHp(target.getMaxHp() + target.getMaxCp() + 1, activeChar, null);
		} else if (NPCConfig.CHAMPION_ENABLE && target.isChampion())
			target.reduceCurrentHp(target.getMaxHp() * NPCConfig.CHAMPION_HP + 1, activeChar, null);
		else
			target.reduceCurrentHp(target.getMaxHp() + 1, activeChar, null);

		_log.info("GM: " + activeChar.getName() + '(' + activeChar.getObjectId() + ')' + " killed character " + target.getObjectId());
	}

	@Override
	public String[] getAdminCommandList() {
		return ADMIN_COMMANDS;
	}
}
