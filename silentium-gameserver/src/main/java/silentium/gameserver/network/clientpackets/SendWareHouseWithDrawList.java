/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program
 * is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have
 * received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package silentium.gameserver.network.clientpackets;

import silentium.gameserver.configs.ClansConfig;
import silentium.gameserver.configs.MainConfig;
import silentium.gameserver.configs.PlayersConfig;
import silentium.gameserver.model.L2Clan;
import silentium.gameserver.model.L2ItemInstance;
import silentium.gameserver.model.actor.L2Npc;
import silentium.gameserver.model.actor.instance.L2PcInstance;
import silentium.gameserver.model.itemcontainer.ClanWarehouse;
import silentium.gameserver.model.itemcontainer.ItemContainer;
import silentium.gameserver.model.itemcontainer.PcWarehouse;
import silentium.gameserver.network.SystemMessageId;
import silentium.gameserver.network.serverpackets.EnchantResult;
import silentium.gameserver.network.serverpackets.InventoryUpdate;
import silentium.gameserver.network.serverpackets.ItemList;
import silentium.gameserver.network.serverpackets.StatusUpdate;
import silentium.gameserver.network.serverpackets.SystemMessage;
import silentium.gameserver.utils.Util;

/**
 * 32 SendWareHouseWithDrawList cd (dd) WootenGil rox :P
 */
public final class SendWareHouseWithDrawList extends L2GameClientPacket
{
	private static final int BATCH_LENGTH = 8; // length of the one item
	private WarehouseItem _items[] = null;

	@Override
	protected void readImpl()
	{
		final int count = readD();
		if (count <= 0 || count > PlayersConfig.MAX_ITEM_IN_PACKET || count * BATCH_LENGTH != _buf.remaining())
			return;

		_items = new WarehouseItem[count];
		for (int i = 0; i < count; i++)
		{
			int objId = readD();
			int cnt = readD();
			if (objId < 1 || cnt < 0)
			{
				_items = null;
				return;
			}
			_items[i] = new WarehouseItem(objId, cnt);
		}
	}

	@Override
	protected void runImpl()
	{
		if (_items == null)
			return;

		final L2PcInstance player = getClient().getActiveChar();
		if (player == null)
			return;

		if (player.isProcessingTransaction())
		{
			player.sendPacket(SystemMessageId.ALREADY_TRADING);
			return;
		}

		if (player.getActiveEnchantItem() != null)
		{
			player.setActiveEnchantItem(null);
			player.sendPacket(EnchantResult.CANCELLED);
			player.sendPacket(SystemMessageId.ENCHANT_SCROLL_CANCELLED);
		}

		final ItemContainer warehouse = player.getActiveWarehouse();
		if (warehouse == null)
			return;

		final L2Npc manager = player.getCurrentFolkNPC();
		if ((manager == null || !manager.isWarehouse() || !manager.canInteract(player)) && !player.isGM())
			return;

		if (!(warehouse instanceof PcWarehouse) && !player.getAccessLevel().allowTransaction())
		{
			player.sendMessage("Transactions are disabled for your Access Level.");
			return;
		}

		// Alt game - Karma punishment
		if (!PlayersConfig.KARMA_PLAYER_CAN_USE_WH && player.getKarma() > 0)
			return;

		if (ClansConfig.ALT_MEMBERS_CAN_WITHDRAW_FROM_CLANWH)
		{
			if (warehouse instanceof ClanWarehouse && ((player.getClanPrivileges() & L2Clan.CP_CL_VIEW_WAREHOUSE) != L2Clan.CP_CL_VIEW_WAREHOUSE))
				return;
		}
		else
		{
			if (warehouse instanceof ClanWarehouse && !player.isClanLeader())
			{
				// this msg is for depositing but maybe good to send some msg?
				player.sendPacket(SystemMessageId.ONLY_CLAN_LEADER_CAN_RETRIEVE_ITEMS_FROM_CLAN_WAREHOUSE);
				return;
			}
		}

		int weight = 0;
		int slots = 0;

		for (WarehouseItem i : _items)
		{
			// Calculate needed slots
			L2ItemInstance item = warehouse.getItemByObjectId(i.getObjectId());
			if (item == null || item.getCount() < i.getCount())
			{
				Util.handleIllegalPlayerAction(player, player.getName() + " of account " + player.getAccountName() + " tried to withdraw non-existent item from warehouse.", MainConfig.DEFAULT_PUNISH);
				return;
			}

			weight += i.getCount() * item.getItem().getWeight();
			if (!item.isStackable())
				slots += i.getCount();
			else if (player.getInventory().getItemByItemId(item.getItemId()) == null)
				slots++;
		}

		// Item Max Limit Check
		if (!player.getInventory().validateCapacity(slots))
		{
			sendPacket(SystemMessage.getSystemMessage(SystemMessageId.SLOTS_FULL));
			return;
		}

		// Weight limit Check
		if (!player.getInventory().validateWeight(weight))
		{
			sendPacket(SystemMessage.getSystemMessage(SystemMessageId.WEIGHT_LIMIT_EXCEEDED));
			return;
		}

		// Proceed to the transfer
		InventoryUpdate playerIU = MainConfig.FORCE_INVENTORY_UPDATE ? null : new InventoryUpdate();
		for (WarehouseItem i : _items)
		{
			L2ItemInstance oldItem = warehouse.getItemByObjectId(i.getObjectId());
			if (oldItem == null || oldItem.getCount() < i.getCount())
			{
				log.warn("Error withdrawing a warehouse object for " + player.getName() + " (olditem == null)");
				return;
			}

			final L2ItemInstance newItem = warehouse.transferItem(warehouse.getName(), i.getObjectId(), i.getCount(), player.getInventory(), player, manager);
			if (newItem == null)
			{
				log.warn("Error withdrawing a warehouse object for " + player.getName() + " (newitem == null)");
				return;
			}

			if (playerIU != null)
			{
				if (newItem.getCount() > i.getCount())
					playerIU.addModifiedItem(newItem);
				else
					playerIU.addNewItem(newItem);
			}
		}

		// Send updated item list to the player
		if (playerIU != null)
			player.sendPacket(playerIU);
		else
			player.sendPacket(new ItemList(player, false));

		// Update current load status on player
		StatusUpdate su = new StatusUpdate(player);
		su.addAttribute(StatusUpdate.CUR_LOAD, player.getCurrentLoad());
		player.sendPacket(su);
	}

	private static class WarehouseItem
	{
		private final int _objectId;
		private final int _count;

		public WarehouseItem(int id, int num)
		{
			_objectId = id;
			_count = num;
		}

		public int getObjectId()
		{
			return _objectId;
		}

		public int getCount()
		{
			return _count;
		}
	}
}
