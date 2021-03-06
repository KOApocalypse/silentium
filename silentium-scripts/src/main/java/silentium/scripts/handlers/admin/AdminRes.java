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
import silentium.gameserver.handler.IAdminCommandHandler;
import silentium.gameserver.model.L2Object;
import silentium.gameserver.model.L2World;
import silentium.gameserver.model.actor.L2Character;
import silentium.gameserver.model.actor.instance.L2PcInstance;
import silentium.gameserver.network.SystemMessageId;
import silentium.gameserver.network.clientpackets.Say2;
import silentium.gameserver.taskmanager.DecayTaskManager;

/**
 * This class handles following admin commands:<br>
 * - res = resurrects a player<br>
 * - res_monster = resurrects a Npc/Monster/...
 */
public class AdminRes implements IAdminCommandHandler {
	private static final Logger _log = LoggerFactory.getLogger(AdminRes.class.getName());
	private static final String[] ADMIN_COMMANDS = { "admin_res", "admin_res_monster" };

	@Override
	public boolean useAdminCommand(final String command, final L2PcInstance activeChar) {
		if (command.startsWith("admin_res "))
			handleRes(activeChar, command.split(" ")[1]);
		else if ("admin_res".equals(command))
			handleRes(activeChar);
		else if (command.startsWith("admin_res_monster "))
			handleNonPlayerRes(activeChar, command.split(" ")[1]);
		else if ("admin_res_monster".equals(command))
			handleNonPlayerRes(activeChar);

		return true;
	}

	@Override
	public String[] getAdminCommandList() {
		return ADMIN_COMMANDS;
	}

	private static void handleRes(final L2PcInstance activeChar) {
		handleRes(activeChar, null);
	}

	private static void handleRes(final L2PcInstance activeChar, final String resParam) {
		L2Object obj = activeChar.getTarget();

		if (resParam != null) {
			// Check if a player name was specified as a param.
			final L2PcInstance plyr = L2World.getInstance().getPlayer(resParam);

			if (plyr != null)
				obj = plyr;
			else {
				// Otherwise, check if the param was a radius.
				try {
					final int radius = Integer.parseInt(resParam);

					for (final L2PcInstance knownPlayer : activeChar.getKnownList().getKnownPlayersInRadius(radius))
						doResurrect(knownPlayer);

					activeChar.sendChatMessage(0, Say2.ALL, "SYS", "Resurrected all players within a " + radius + " unit radius.");
					return;
				} catch (NumberFormatException e) {
					activeChar.sendChatMessage(0, Say2.ALL, "SYS", "Enter a valid player name or radius.");
					return;
				}
			}
		}

		if (obj == null)
			obj = activeChar;

		doResurrect((L2Character) obj);

		_log.info("GM: " + activeChar.getName() + '(' + activeChar.getObjectId() + ") resurrected character " + obj.getObjectId());
	}

	private static void handleNonPlayerRes(final L2PcInstance activeChar) {
		handleNonPlayerRes(activeChar, "");
	}

	private static void handleNonPlayerRes(final L2PcInstance activeChar, final String radiusStr) {
		final L2Object obj = activeChar.getTarget();

		try {
			int radius = 0;

			if (!radiusStr.isEmpty()) {
				radius = Integer.parseInt(radiusStr);

				for (final L2Character knownChar : activeChar.getKnownList().getKnownCharactersInRadius(radius))
					if (!(knownChar instanceof L2PcInstance))
						doResurrect(knownChar);

				activeChar.sendChatMessage(0, Say2.ALL, "SYS", "Resurrected all non-players within a " + radius + " unit radius.");
			}
		} catch (NumberFormatException e) {
			activeChar.sendChatMessage(0, Say2.ALL, "SYS", "Enter a valid radius.");
			return;
		}

		if (obj instanceof L2PcInstance) {
			activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
			return;
		}

		doResurrect((L2Character) obj);
	}

	private static void doResurrect(final L2Character targetChar) {
		if (!targetChar.isDead())
			return;

		// If the target is a player, then restore the XP lost on death.
		if (targetChar instanceof L2PcInstance)
			((L2PcInstance) targetChar).restoreExp(100.0);
			// If the target is an NPC, then abort it's auto decay and respawn.
		else
			DecayTaskManager.getInstance().cancelDecayTask(targetChar);

		targetChar.doRevive();
	}
}
