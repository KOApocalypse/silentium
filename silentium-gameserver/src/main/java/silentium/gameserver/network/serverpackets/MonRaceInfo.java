/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program
 * is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have
 * received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package silentium.gameserver.network.serverpackets;

import silentium.gameserver.model.actor.L2Npc;

/**
 * format : dddc dddh (ddc)
 */
public class MonRaceInfo extends L2GameServerPacket
{
	private final int _unknown1;
	private final int _unknown2;
	private final L2Npc[] _monsters;
	private final int[][] _speeds;

	public MonRaceInfo(int unknown1, int unknown2, L2Npc[] monsters, int[][] speeds)
	{
		/*
		 * -1 0 to initial the race 0 15322 to start race 13765 -1 in middle of race -1 0 to end the race
		 */
		_unknown1 = unknown1;
		_unknown2 = unknown2;
		_monsters = monsters;
		_speeds = speeds;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xdd);

		writeD(_unknown1);
		writeD(_unknown2);
		writeD(8);

		for (int i = 0; i < 8; i++)
		{
			writeD(_monsters[i].getObjectId());
			writeD(_monsters[i].getTemplate().getNpcId() + 1000000);
			writeD(14107); // origin X
			writeD(181875 + (58 * (7 - i))); // origin Y
			writeD(-3566); // origin Z
			writeD(12080); // end X
			writeD(181875 + (58 * (7 - i))); // end Y
			writeD(-3566); // end Z
			writeF(_monsters[i].getTemplate().getCollisionHeight());
			writeF(_monsters[i].getTemplate().getCollisionRadius());
			writeD(120); // ?? unknown

			for (int j = 0; j < 20; j++)
			{
				if (_unknown1 == 0)
					writeC(_speeds[i][j]);
				else
					writeC(0);
			}
			writeD(0);
		}
	}
}