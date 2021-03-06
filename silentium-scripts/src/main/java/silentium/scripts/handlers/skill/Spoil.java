/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package silentium.scripts.handlers.skill;

import silentium.gameserver.ai.CtrlEvent;
import silentium.gameserver.handler.ISkillHandler;
import silentium.gameserver.model.L2Object;
import silentium.gameserver.model.L2Skill;
import silentium.gameserver.model.actor.L2Character;
import silentium.gameserver.model.actor.instance.L2MonsterInstance;
import silentium.gameserver.model.actor.instance.L2PcInstance;
import silentium.gameserver.network.SystemMessageId;
import silentium.gameserver.network.serverpackets.SystemMessage;
import silentium.gameserver.skills.Formulas;
import silentium.gameserver.templates.skills.L2SkillType;

/**
 * @author _drunk_
 */
public class Spoil implements ISkillHandler {
	private static final L2SkillType[] SKILL_IDS = { L2SkillType.SPOIL };

	@Override
	public void useSkill(final L2Character activeChar, final L2Skill skill, final L2Object... targets) {
		if (!(activeChar instanceof L2PcInstance))
			return;

		if (targets == null)
			return;

		for (final L2Object tgt : targets) {
			if (!(tgt instanceof L2MonsterInstance))
				continue;

			final L2MonsterInstance target = (L2MonsterInstance) tgt;

			if (target.isSpoil()) {
				activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ALREADY_SPOILED));
				continue;
			}

			// SPOIL SYSTEM by Lbaldi
			boolean spoil = false;
			if (!target.isDead()) {
				spoil = Formulas.calcMagicSuccess(activeChar, (L2Character) tgt, skill);

				if (spoil) {
					target.setSpoil(true);
					target.setIsSpoiledBy(activeChar.getObjectId());
					activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.SPOIL_SUCCESS));
				} else
					activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(target).addSkillName(skill.getDisplayId()));

				target.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, activeChar);
			}
		}
	}

	@Override
	public L2SkillType[] getSkillIds() {
		return SKILL_IDS;
	}
}