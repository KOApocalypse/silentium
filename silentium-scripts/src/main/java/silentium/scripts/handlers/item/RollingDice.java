/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package silentium.scripts.handlers.item;

import silentium.commons.utils.Rnd;
import silentium.gameserver.handler.IItemHandler;
import silentium.gameserver.model.L2ItemInstance;
import silentium.gameserver.model.actor.L2Character;
import silentium.gameserver.model.actor.L2Playable;
import silentium.gameserver.model.actor.instance.L2PcInstance;
import silentium.gameserver.network.SystemMessageId;
import silentium.gameserver.network.serverpackets.Dice;
import silentium.gameserver.network.serverpackets.SystemMessage;
import silentium.gameserver.utils.Broadcast;

public class RollingDice implements IItemHandler {
	@Override
	public void useItem(final L2Playable playable, final L2ItemInstance item, final boolean forceUse) {
		if (!(playable instanceof L2PcInstance))
			return;

		final L2PcInstance activeChar = (L2PcInstance) playable;
		final int itemId = item.getItemId();

		if (itemId == 4625 || itemId == 4626 || itemId == 4627 || itemId == 4628) {
			final int number = rollDice(activeChar);
			if (number == 0) {
				activeChar.sendPacket(SystemMessageId.YOU_MAY_NOT_THROW_THE_DICE_AT_THIS_TIME_TRY_AGAIN_LATER);
				return;
			}

			Broadcast.toSelfAndKnownPlayers(activeChar, new Dice(activeChar.getObjectId(), item.getItemId(), number, activeChar.getX() - 30, activeChar.getY() - 30, activeChar.getZ()));

			final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_ROLLED_S2);
			sm.addPcName(activeChar);
			sm.addNumber(number);

			activeChar.sendPacket(sm);
			if (activeChar.isInsideZone(L2Character.ZONE_PEACE))
				Broadcast.toKnownPlayers(activeChar, sm);
			else if (activeChar.isInParty())
				activeChar.getParty().broadcastToPartyMembers(activeChar, sm);
		}
	}

	private static int rollDice(final L2PcInstance player) {
		if (!player.getFloodProtectors().getRollDice().tryPerformAction("rollDice"))
			return 0;

		return Rnd.get(1, 6);
	}
}