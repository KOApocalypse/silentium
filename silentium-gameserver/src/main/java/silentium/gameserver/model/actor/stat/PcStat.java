/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package silentium.gameserver.model.actor.stat;

import silentium.gameserver.configs.MainConfig;
import silentium.gameserver.configs.PlayersConfig;
import silentium.gameserver.model.actor.L2Character;
import silentium.gameserver.model.actor.instance.L2PcInstance;
import silentium.gameserver.model.actor.instance.L2PetInstance;
import silentium.gameserver.model.base.Experience;
import silentium.gameserver.model.quest.QuestState;
import silentium.gameserver.network.SystemMessageId;
import silentium.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import silentium.gameserver.network.serverpackets.SocialAction;
import silentium.gameserver.network.serverpackets.StatusUpdate;
import silentium.gameserver.network.serverpackets.SystemMessage;
import silentium.gameserver.network.serverpackets.UserInfo;
import silentium.gameserver.skills.Stats;
import silentium.gameserver.tables.NpcTable;
import silentium.gameserver.tables.PetDataTable;
import silentium.gameserver.utils.Util;

public class PcStat extends PlayableStat
{
	private int _oldMaxHp; // stats watch
	private int _oldMaxMp; // stats watch
	private int _oldMaxCp; // stats watch

	public PcStat(L2PcInstance activeChar)
	{
		super(activeChar);
	}

	@Override
	public boolean addExp(long value)
	{
		L2PcInstance activeChar = getActiveChar();

		// Allowed to gain exp?
		if (!getActiveChar().getAccessLevel().canGainExp())
			return false;

		if (!super.addExp(value))
			return false;

		// Set new karma
		if (!activeChar.isCursedWeaponEquipped() && activeChar.getKarma() > 0 && !activeChar.isInsideZone(L2Character.ZONE_PVP))
		{
			int karmaLost = activeChar.calculateKarmaLost(value);
			if (karmaLost > 0)
				activeChar.setKarma(activeChar.getKarma() - karmaLost);
		}

		activeChar.sendPacket(new UserInfo(activeChar));
		return true;
	}

	/**
	 * Add Experience and SP rewards to the L2PcInstance, remove its Karma (if necessary) and Launch increase level task.<BR>
	 * <BR>
	 * <B><U> Actions </U> :</B><BR>
	 * <BR>
	 * <li>Remove Karma when the player kills L2MonsterInstance</li> <li>Send a Server->Client packet StatusUpdate to the L2PcInstance</li> <li>
	 * Send a Server->Client System Message to the L2PcInstance</li> <li>If the L2PcInstance increases it's level, send a Server->Client packet
	 * SocialAction (broadcast)</li> <li>If the L2PcInstance increases it's level, manage the increase level task (Max MP, Max MP,
	 * Recommandation, Expertise and beginner skills...)</li> <li>If the L2PcInstance increases it's level, send a Server->Client packet UserInfo
	 * to the L2PcInstance</li><BR>
	 * <BR>
	 * 
	 * @param addToExp
	 *            The Experience value to add
	 * @param addToSp
	 *            The SP value to add
	 */
	@Override
	public boolean addExpAndSp(long addToExp, int addToSp)
	{
		float ratioTakenByPet = 0;
		// Player is Gm and acces level is below or equal to GM_DONT_TAKE_EXPSP and is in party, don't give Xp/Sp
		L2PcInstance activeChar = getActiveChar();
		if (!activeChar.getAccessLevel().canGainExp())
			return false;

		// if this player has a pet that takes from the owner's Exp, give the pet Exp now
		if (activeChar.hasPet())
		{
			final L2PetInstance pet = (L2PetInstance) activeChar.getPet();
			if (pet.getStat().getExp() <= (PetDataTable.getInstance().getPetLevelData(pet.getNpcId(), 81).getPetMaxExp() + 10000))
			{
				if (Util.checkIfInShortRadius(PlayersConfig.ALT_PARTY_RANGE, pet, activeChar, true))
				{
					ratioTakenByPet = pet.getPetLevelData().getOwnerExpTaken();

					// only give exp/sp to the pet by taking from the owner if the pet has a positive ratio
					// allow possible customizations that would have the pet earning more than 100% of the
					// owner's exp/sp
					if (ratioTakenByPet > 0 && !pet.isDead())
						pet.addExpAndSp((long) (addToExp * ratioTakenByPet), (int) (addToSp * ratioTakenByPet));

					// now adjust the max ratio to avoid the owner earning negative exp/sp
					if (ratioTakenByPet > 1)
						ratioTakenByPet = 1;

					addToExp = (long) (addToExp * (1 - ratioTakenByPet));
					addToSp = (int) (addToSp * (1 - ratioTakenByPet));
				}
			}
		}

		if (!super.addExpAndSp(addToExp, addToSp))
			return false;

		if (addToExp == 0 && addToSp > 0)
		{
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.ACQUIRED_S1_SP);
			sm.addNumber(addToSp);
			activeChar.sendPacket(sm);
		}
		else if (addToExp > 0 && addToSp == 0)
		{
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.EARNED_S1_EXPERIENCE);
			sm.addNumber((int) addToExp);
			activeChar.sendPacket(sm);
		}
		else
		{
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_EARNED_S1_EXP_AND_S2_SP);
			sm.addNumber((int) addToExp);
			sm.addNumber(addToSp);
			activeChar.sendPacket(sm);
		}

		return true;
	}

	@Override
	public boolean removeExpAndSp(long removeExp, int removeSp)
	{
		return removeExpAndSp(removeExp, removeSp, true);
	}

	public boolean removeExpAndSp(long removeExp, int removeSp, boolean sendMessage)
	{
		int level = getLevel();
		if (!super.removeExpAndSp(removeExp, removeSp))
			return false;

		// Send messages.
		if (sendMessage)
		{
			getActiveChar().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.EXP_DECREASED_BY_S1).addNumber((int) removeExp));
			getActiveChar().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.SP_DECREASED_S1).addNumber(removeSp));

			if (getLevel() < level)
				getActiveChar().broadcastStatusUpdate();
		}
		return true;
	}

	@Override
	public final boolean addLevel(byte value)
	{
		if (getLevel() + value > Experience.MAX_LEVEL - 1)
			return false;

		boolean levelIncreased = super.addLevel(value);

		if (levelIncreased)
		{
			if (!MainConfig.DISABLE_TUTORIAL)
			{
				QuestState qs = getActiveChar().getQuestState("Tutorial");
				if (qs != null)
					qs.getQuest().notifyEvent("CE40", null, getActiveChar());
			}

			// If player reaches level 6, verify if the newbie value is already sets.
			if (getActiveChar().getLevel() == 6 && getActiveChar().getNewbieState() == 0)
				getActiveChar().updateNewbieState();

			getActiveChar().setCurrentCp(getMaxCp());
			getActiveChar().broadcastPacket(new SocialAction(getActiveChar(), 15));
			getActiveChar().sendPacket(SystemMessageId.YOU_INCREASED_YOUR_LEVEL);
		}

		getActiveChar().rewardSkills(); // Give Expertise skill of this level
		if (getActiveChar().getClan() != null)
		{
			getActiveChar().getClan().updateClanMember(getActiveChar());
			getActiveChar().getClan().broadcastToOnlineMembers(new PledgeShowMemberListUpdate(getActiveChar()));
		}

		if (getActiveChar().isInParty())
			getActiveChar().getParty().recalculatePartyLevel(); // Recalculate the party level

		StatusUpdate su = new StatusUpdate(getActiveChar());
		su.addAttribute(StatusUpdate.LEVEL, getLevel());
		su.addAttribute(StatusUpdate.MAX_CP, getMaxCp());
		su.addAttribute(StatusUpdate.MAX_HP, getMaxHp());
		su.addAttribute(StatusUpdate.MAX_MP, getMaxMp());
		getActiveChar().sendPacket(su);

		// Update the overloaded status of the L2PcInstance
		getActiveChar().refreshOverloaded();
		// Update the expertise status of the L2PcInstance
		getActiveChar().refreshExpertisePenalty();
		// Send a Server->Client packet UserInfo to the L2PcInstance
		getActiveChar().sendPacket(new UserInfo(getActiveChar()));

		return levelIncreased;
	}

	@Override
	public boolean addSp(int value)
	{
		if (!super.addSp(value))
			return false;

		StatusUpdate su = new StatusUpdate(getActiveChar());
		su.addAttribute(StatusUpdate.SP, getSp());
		getActiveChar().sendPacket(su);

		return true;
	}

	@Override
	public final long getExpForLevel(int level)
	{
		return Experience.LEVEL[level];
	}

	@Override
	public final L2PcInstance getActiveChar()
	{
		return (L2PcInstance) super.getActiveChar();
	}

	@Override
	public final long getExp()
	{
		if (getActiveChar().isSubClassActive())
			return getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).getExp();

		return super.getExp();
	}

	@Override
	public final void setExp(long value)
	{
		if (getActiveChar().isSubClassActive())
			getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).setExp(value);
		else
			super.setExp(value);
	}

	@Override
	public final byte getLevel()
	{
		if (getActiveChar().isSubClassActive())
			return getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).getLevel();

		return super.getLevel();
	}

	@Override
	public final void setLevel(byte value)
	{
		if (value > Experience.MAX_LEVEL - 1)
			value = Experience.MAX_LEVEL - 1;

		if (getActiveChar().isSubClassActive())
			getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).setLevel(value);
		else
			super.setLevel(value);
	}

	@Override
	public final int getMaxCp()
	{
		// Get the Max CP (base+modifier) of the L2PcInstance
		int val = super.getMaxCp();
		if (val != _oldMaxCp)
		{
			_oldMaxCp = val;

			// Launch a regen task if the new Max CP is higher than the old one
			if (getActiveChar().getStatus().getCurrentCp() != val)
				getActiveChar().getStatus().setCurrentCp(getActiveChar().getStatus().getCurrentCp()); // trigger start of
																										// regeneration
		}
		return val;
	}

	@Override
	public final int getMaxHp()
	{
		// Get the Max HP (base+modifier) of the L2PcInstance
		int val = super.getMaxHp();
		if (val != _oldMaxHp)
		{
			_oldMaxHp = val;

			// Launch a regen task if the new Max HP is higher than the old one
			if (getActiveChar().getStatus().getCurrentHp() != val)
				getActiveChar().getStatus().setCurrentHp(getActiveChar().getStatus().getCurrentHp()); // trigger start of
																										// regeneration
		}

		return val;
	}

	@Override
	public final int getMaxMp()
	{
		// Get the Max MP (base+modifier) of the L2PcInstance
		int val = super.getMaxMp();

		if (val != _oldMaxMp)
		{
			_oldMaxMp = val;

			// Launch a regen task if the new Max MP is higher than the old one
			if (getActiveChar().getStatus().getCurrentMp() != val)
				getActiveChar().getStatus().setCurrentMp(getActiveChar().getStatus().getCurrentMp()); // trigger start of
																										// regeneration
		}

		return val;
	}

	@Override
	public final int getSp()
	{
		if (getActiveChar().isSubClassActive())
			return getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).getSp();

		return super.getSp();
	}

	@Override
	public final void setSp(int value)
	{
		if (getActiveChar().isSubClassActive())
			getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).setSp(value);
		else
			super.setSp(value);
	}

	@Override
	public int getRunSpeed()
	{
		if (getActiveChar() == null)
			return 1;

		int val;

		L2PcInstance player = getActiveChar();
		if (player.isMounted())
		{
			int baseRunSpd = NpcTable.getInstance().getTemplate(getActiveChar().getMountNpcId()).getBaseRunSpd();
			val = (int) (calcStat(Stats.RUN_SPEED, baseRunSpd, null, null));
		}
		else
			val = super.getRunSpeed();

		return val;
	}

	@Override
	public float getMovementSpeedMultiplier()
	{
		if (getActiveChar() == null)
			return 1;

		if (getActiveChar().isMounted())
			return getRunSpeed() * 1f / NpcTable.getInstance().getTemplate(getActiveChar().getMountNpcId()).getBaseRunSpd();

		return super.getMovementSpeedMultiplier();
	}

	@Override
	public int getWalkSpeed()
	{
		if (getActiveChar() == null)
			return 1;

		return (getRunSpeed() * 70) / 100;
	}
}