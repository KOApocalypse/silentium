/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package silentium.gameserver.model.actor.knownlist;

import silentium.gameserver.model.L2Object;
import silentium.gameserver.model.actor.L2Character;
import silentium.gameserver.model.actor.L2Npc;
import silentium.gameserver.model.actor.L2Playable;
import silentium.gameserver.model.actor.instance.L2CabaleBufferInstance;
import silentium.gameserver.model.actor.instance.L2FestivalGuideInstance;
import silentium.gameserver.model.actor.instance.L2NpcInstance;

public class NpcKnownList extends CharKnownList
{
	public NpcKnownList(L2Npc activeChar)
	{
		super(activeChar);
	}

	@Override
	public L2Npc getActiveChar()
	{
		return (L2Npc) super.getActiveChar();
	}

	@Override
	public int getDistanceToForgetObject(L2Object object)
	{
		return (int) 1.5 * getDistanceToWatchObject(object);
	}

	@Override
	public int getDistanceToWatchObject(L2Object object)
	{
		if (object instanceof L2FestivalGuideInstance)
			return 4000;

		if (object instanceof L2CabaleBufferInstance)
			return 900;

		if (object instanceof L2NpcInstance || !(object instanceof L2Character))
			return 0;

		if (object instanceof L2Playable)
			return 1500;

		return 500;
	}
}