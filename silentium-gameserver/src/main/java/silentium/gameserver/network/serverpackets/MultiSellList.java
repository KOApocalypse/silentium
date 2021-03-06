/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package silentium.gameserver.network.serverpackets;

import silentium.gameserver.model.L2Multisell.MultiSellEntry;
import silentium.gameserver.model.L2Multisell.MultiSellIngredient;
import silentium.gameserver.model.L2Multisell.MultiSellListContainer;
import silentium.gameserver.tables.ItemTable;
import silentium.gameserver.templates.item.L2Item;

public class MultiSellList extends L2GameServerPacket
{
	protected int _listId, _page, _finished;
	protected MultiSellListContainer _list;

	public MultiSellList(MultiSellListContainer list, int page, int finished)
	{
		_list = list;
		_listId = list.getListId();
		_page = page;
		_finished = finished;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xd0);
		writeD(_listId); // list id
		writeD(_page); // page
		writeD(_finished); // finished
		writeD(0x28); // size of pages
		writeD(_list == null ? 0 : _list.getEntries().size()); // list lenght

		if (_list != null)
		{
			L2Item item;
			int itemId;

			for (MultiSellEntry ent : _list.getEntries())
			{
				writeD(ent.getEntryId());
				writeD(0x00); // C6
				writeD(0x00); // C6
				writeC(1);
				writeH(ent.getProducts().size());
				writeH(ent.getIngredients().size());

				for (MultiSellIngredient i : ent.getProducts())
				{
					item = ItemTable.getInstance().getTemplate(i.getItemId());

					writeH(i.getItemId());
					writeD(item.getBodyPart());
					writeH(item.getType2());
					writeD(i.getItemCount());
					writeH(i.getEnchantmentLevel());
					writeD(0x00); // TODO: i.getAugmentId()
					writeD(0x00); // TODO: i.getManaLeft()
				}

				for (MultiSellIngredient i : ent.getIngredients())
				{
					itemId = i.getItemId();
					item = ItemTable.getInstance().getTemplate(itemId);

					writeH(itemId);
					writeH((itemId != 65336) ? item.getType2() : 65535);
					writeD(i.getItemCount());
					writeH(i.getEnchantmentLevel());
					writeD(0x00); // TODO: i.getAugmentId()
					writeD(0x00); // TODO: i.getManaLeft()
				}
			}
		}
	}
}