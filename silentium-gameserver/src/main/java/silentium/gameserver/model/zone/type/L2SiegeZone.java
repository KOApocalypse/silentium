/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package silentium.gameserver.model.zone.type;

import silentium.gameserver.data.xml.MapRegionData;
import silentium.gameserver.model.L2Clan;
import silentium.gameserver.model.actor.L2Character;
import silentium.gameserver.model.actor.instance.L2PcInstance;
import silentium.gameserver.model.actor.instance.L2SiegeSummonInstance;
import silentium.gameserver.model.zone.L2ZoneType;
import silentium.gameserver.network.SystemMessageId;

/**
 * A siege zone
 * 
 * @author durgus
 */
public class L2SiegeZone extends L2ZoneType
{
	private int _siegableId = -1;
	private boolean _isActiveSiege = false;
	private static final int DISMOUNT_DELAY = 5;

	public L2SiegeZone(int id)
	{
		super(id);
	}

	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("castleId"))
		{
			if (_siegableId != -1)
				throw new IllegalArgumentException("Siege object already defined!");
			_siegableId = Integer.parseInt(value);
		}
		else if (name.equals("clanHallId"))
		{
			if (_siegableId != -1)
				throw new IllegalArgumentException("Siege object already defined!");
			_siegableId = Integer.parseInt(value);
		}
		else
			super.setParameter(name, value);
	}

	@Override
	protected void onEnter(L2Character character)
	{
		if (_isActiveSiege)
		{
			character.setInsideZone(L2Character.ZONE_PVP, true);
			character.setInsideZone(L2Character.ZONE_SIEGE, true);
			character.setInsideZone(L2Character.ZONE_NOSUMMONFRIEND, true);

			if (character instanceof L2PcInstance)
			{
				L2PcInstance activeChar = (L2PcInstance) character;

				activeChar.setIsInSiege(true); // in siege

				activeChar.sendPacket(SystemMessageId.ENTERED_COMBAT_ZONE);

				if (activeChar.getMountType() == 2)
				{
					activeChar.sendPacket(SystemMessageId.AREA_CANNOT_BE_ENTERED_WHILE_MOUNTED_WYVERN);
					activeChar.enteredNoLanding(DISMOUNT_DELAY);
				}
			}
		}
	}

	@Override
	protected void onExit(L2Character character)
	{
		character.setInsideZone(L2Character.ZONE_PVP, false);
		character.setInsideZone(L2Character.ZONE_SIEGE, false);
		character.setInsideZone(L2Character.ZONE_NOSUMMONFRIEND, false);

		if (_isActiveSiege)
		{
			if (character instanceof L2PcInstance)
			{
				L2PcInstance activeChar = (L2PcInstance) character;

				activeChar.sendPacket(SystemMessageId.LEFT_COMBAT_ZONE);

				if (activeChar.getMountType() == 2)
					activeChar.exitedNoLanding();

				// Set pvp flag
				if (activeChar.getPvpFlag() == 0)
					activeChar.startPvPFlag();
			}
		}

		if (character instanceof L2PcInstance)
			((L2PcInstance) character).setIsInSiege(false);

		if (character instanceof L2SiegeSummonInstance)
			((L2SiegeSummonInstance) character).unSummon(((L2SiegeSummonInstance) character).getOwner());
	}

	@Override
	public void onDieInside(L2Character character)
	{
	}

	@Override
	public void onReviveInside(L2Character character)
	{
	}

	public void updateZoneStatusForCharactersInside()
	{
		if (_isActiveSiege)
		{
			for (L2Character character : _characterList.values())
			{
				if (character != null)
					onEnter(character);
			}
		}
		else
		{
			for (L2Character character : _characterList.values())
			{
				if (character == null)
					continue;

				character.setInsideZone(L2Character.ZONE_PVP, false);
				character.setInsideZone(L2Character.ZONE_SIEGE, false);
				character.setInsideZone(L2Character.ZONE_NOSUMMONFRIEND, false);

				if (character instanceof L2PcInstance)
				{
					((L2PcInstance) character).sendPacket(SystemMessageId.LEFT_COMBAT_ZONE);

					if (((L2PcInstance) character).getMountType() == 2)
						((L2PcInstance) character).exitedNoLanding();
				}
				else if (character instanceof L2SiegeSummonInstance)
					((L2SiegeSummonInstance) character).unSummon(((L2SiegeSummonInstance) character).getOwner());
			}
		}
	}

	/**
	 * Sends a message to all players in this zone
	 * 
	 * @param message
	 */
	public void announceToPlayers(String message)
	{
		for (L2PcInstance player : getPlayersInside())
			player.sendMessage(message);
	}

	public int getSiegeObjectId()
	{
		return _siegableId;
	}

	public boolean isActive()
	{
		return _isActiveSiege;
	}

	public void setIsActive(boolean val)
	{
		_isActiveSiege = val;
	}

	/**
	 * Removes all foreigners from the zone
	 * 
	 * @param owningClan
	 */
	public void banishForeigners(L2Clan owningClan)
	{
		for (L2PcInstance player : getPlayersInside())
		{
			if (player.getClan() == owningClan || player.isGM())
				continue;

			player.teleToLocation(MapRegionData.TeleportWhereType.Town);
		}
	}
}