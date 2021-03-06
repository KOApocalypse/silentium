/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package silentium.scripts.handlers.skill;

import silentium.gameserver.handler.ISkillHandler;
import silentium.gameserver.handler.SkillHandler;
import silentium.gameserver.model.L2Object;
import silentium.gameserver.model.L2Skill;
import silentium.gameserver.model.actor.L2Character;
import silentium.gameserver.model.actor.instance.L2DoorInstance;
import silentium.gameserver.model.actor.instance.L2PcInstance;
import silentium.gameserver.model.actor.instance.L2SiegeFlagInstance;
import silentium.gameserver.network.SystemMessageId;
import silentium.gameserver.network.serverpackets.StatusUpdate;
import silentium.gameserver.network.serverpackets.SystemMessage;
import silentium.gameserver.templates.skills.L2SkillType;

public class HealPercent implements ISkillHandler {
	private static final L2SkillType[] SKILL_IDS = { L2SkillType.HEAL_PERCENT, L2SkillType.MANAHEAL_PERCENT, L2SkillType.CPHEAL_PERCENT };

	@Override
	public void useSkill(final L2Character activeChar, final L2Skill skill, final L2Object... targets) {
		// check for other effects
		final ISkillHandler handler = SkillHandler.getInstance().getSkillHandler(L2SkillType.BUFF);
		if (handler != null)
			handler.useSkill(activeChar, skill, targets);

		boolean cp = false;
		boolean hp = false;
		boolean mp = false;

		switch (skill.getSkillType()) {
			case CPHEAL_PERCENT:
				cp = true;
				break;

			case HEAL_PERCENT:
				hp = true;
				break;

			case MANAHEAL_PERCENT:
				mp = true;
				break;
		}

		StatusUpdate su = null;
		SystemMessage sm;
		double amount = 0;
		final boolean full = skill.getPower() == 100.0;
		boolean targetPlayer = false;

		for (final L2Character target : (L2Character[]) targets) {
			if (target == null || target.isDead() || target.isInvul())
				continue;

			// Doors and flags can't be healed in any way
			if (target instanceof L2DoorInstance || target instanceof L2SiegeFlagInstance)
				continue;

			targetPlayer = target instanceof L2PcInstance;

			// Cursed weapon owner can't heal or be healed
			if (target != activeChar) {
				if (activeChar instanceof L2PcInstance && ((L2PcInstance) activeChar).isCursedWeaponEquipped())
					continue;

				if (targetPlayer && ((L2PcInstance) target).isCursedWeaponEquipped())
					continue;
			}

			if (hp) {
				amount = Math.min(full ? target.getMaxHp() : target.getMaxHp() * skill.getPower() / 100.0, target.getMaxHp() - target.getCurrentHp());
				target.setCurrentHp(amount + target.getCurrentHp());
			} else if (mp) {
				amount = Math.min(full ? target.getMaxMp() : target.getMaxMp() * skill.getPower() / 100.0, target.getMaxMp() - target.getCurrentMp());
				target.setCurrentMp(amount + target.getCurrentMp());
			}

			if (targetPlayer) {
				su = new StatusUpdate(target);

				if (cp) {
					amount = Math.min(full ? target.getMaxCp() : target.getMaxCp() * skill.getPower() / 100.0, target.getMaxCp() - target.getCurrentCp());
					target.setCurrentCp(amount + target.getCurrentCp());

					sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CP_WILL_BE_RESTORED);
					sm.addNumber((int) amount);
					target.sendPacket(sm);
					su.addAttribute(StatusUpdate.CUR_CP, (int) target.getCurrentCp());
				} else if (hp) {
					sm = activeChar != target ? SystemMessage.getSystemMessage(SystemMessageId.S2_HP_RESTORED_BY_S1).addCharName(activeChar) : SystemMessage.getSystemMessage(SystemMessageId.S1_HP_RESTORED);

					sm.addNumber((int) amount);
					target.sendPacket(sm);
					su.addAttribute(StatusUpdate.CUR_HP, (int) target.getCurrentHp());
				} else if (mp) {
					sm = activeChar != target ? SystemMessage.getSystemMessage(SystemMessageId.S2_MP_RESTORED_BY_S1).addCharName(activeChar) : SystemMessage.getSystemMessage(SystemMessageId.S1_MP_RESTORED);

					sm.addNumber((int) amount);
					target.sendPacket(sm);
					su.addAttribute(StatusUpdate.CUR_MP, (int) target.getCurrentMp());
				}

				target.sendPacket(su);
			}
		}
	}

	@Override
	public L2SkillType[] getSkillIds() {
		return SKILL_IDS;
	}
}