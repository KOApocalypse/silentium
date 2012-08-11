/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program
 * is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have
 * received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package silentium.gameserver.network.clientpackets;

import silentium.gameserver.configs.MainConfig;
import silentium.gameserver.data.xml.AdminCommandAccessRightsData;
import silentium.gameserver.handler.AdminCommandHandler;
import silentium.gameserver.handler.IAdminCommandHandler;
import silentium.gameserver.model.actor.instance.L2PcInstance;
import silentium.gameserver.utils.GMAudit;

/**
 * This class handles all GM commands triggered by //command
 */
public final class SendBypassBuildCmd extends L2GameClientPacket
{
	private String _command;

	@Override
	protected void readImpl()
	{
		_command = readS();
		if (_command != null)
			_command = _command.trim();
	}

	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;

		String command = "admin_" + _command.split(" ")[0];

		final IAdminCommandHandler ach = AdminCommandHandler.getInstance().getAdminCommandHandler(command);
		if (ach == null)
		{
			if (activeChar.isGM())
				activeChar.sendMessage("The command " + command.substring(6) + " doesn't exist.");

			log.warn("No handler registered for admin command '" + command + "'");
			return;
		}

		if (!AdminCommandAccessRightsData.getInstance().hasAccess(command, activeChar.getAccessLevel()))
		{
			activeChar.sendMessage("You don't have the access right to use this command.");
			log.warn(activeChar.getName() + " tried to use admin command " + command + ", but have no access to use it.");
			return;
		}

		if (MainConfig.GMAUDIT)
			GMAudit.auditGMAction(activeChar.getName() + " [" + activeChar.getObjectId() + "]", _command, (activeChar.getTarget() != null ? activeChar.getTarget().getName() : "no-target"));

		ach.useAdminCommand("admin_" + _command, activeChar);
	}
}
