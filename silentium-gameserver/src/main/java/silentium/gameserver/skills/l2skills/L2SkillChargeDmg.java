/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package silentium.gameserver.skills.l2skills;

import silentium.gameserver.model.L2Effect;
import silentium.gameserver.model.L2ItemInstance;
import silentium.gameserver.model.L2Object;
import silentium.gameserver.model.L2Skill;
import silentium.gameserver.model.actor.L2Character;
import silentium.gameserver.model.actor.instance.L2PcInstance;
import silentium.gameserver.network.SystemMessageId;
import silentium.gameserver.network.serverpackets.SystemMessage;
import silentium.gameserver.skills.Env;
import silentium.gameserver.skills.Formulas;
import silentium.gameserver.templates.StatsSet;
import silentium.gameserver.templates.item.L2WeaponType;

public class L2SkillChargeDmg extends L2Skill
{
	public L2SkillChargeDmg(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(L2Character caster, L2Object[] targets)
	{
		if (caster.isAlikeDead())
			return;

		double modifier = 0;

		if (caster instanceof L2PcInstance)
			modifier = 0.7 + 0.3 * ((L2PcInstance) caster).getCharges();

		L2ItemInstance weapon = caster.getActiveWeaponInstance();
		boolean soul = (weapon != null && weapon.getChargedSoulshot() == L2ItemInstance.CHARGED_SOULSHOT && weapon.getItemType() != L2WeaponType.DAGGER);

		for (L2Character target : (L2Character[]) targets)
		{
			if (target.isAlikeDead())
				continue;

			// Calculate skill evasion
			boolean skillIsEvaded = Formulas.calcPhysicalSkillEvasion(target, this);
			if (skillIsEvaded)
			{
				SystemMessage sm;

				if (caster instanceof L2PcInstance)
				{
					sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DODGES_ATTACK).addCharName(target);
					((L2PcInstance) caster).sendPacket(sm);
				}
				if (target instanceof L2PcInstance)
				{
					sm = SystemMessage.getSystemMessage(SystemMessageId.AVOIDED_S1_ATTACK).addCharName(caster);
					((L2PcInstance) target).sendPacket(sm);
				}

				// no futher calculations needed.
				continue;
			}

			byte shld = Formulas.calcShldUse(caster, target, this);
			boolean crit = false;

			if (getBaseCritRate() > 0)
				crit = Formulas.calcCrit(getBaseCritRate() * 10 * Formulas.getSTRBonus(caster));

			// damage calculation, crit is static 2x
			double damage = Formulas.calcChargeSkillsDam(caster, target, this, shld, false, false, soul);
			if (crit)
				damage *= 2;

			if (damage > 0)
			{
				byte reflect = Formulas.calcSkillReflect(target, this);
				if (hasEffects())
				{
					if ((reflect & Formulas.SKILL_REFLECT_SUCCEED) != 0)
					{
						caster.stopSkillEffects(getId());
						getEffects(target, caster);
						SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT);
						sm.addSkillName(this);
						caster.sendPacket(sm);
					}
					else
					{
						// activate attacked effects, if any
						target.stopSkillEffects(getId());
						if (Formulas.calcSkillSuccess(caster, target, this, shld, false, false, true))
						{
							getEffects(caster, target, new Env(shld, false, false, false));

							SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT);
							sm.addSkillName(this);
							target.sendPacket(sm);
						}
						else
						{
							SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2);
							sm.addCharName(target);
							sm.addSkillName(this);
							caster.sendPacket(sm);
						}
					}
				}

				double finalDamage = damage * modifier;
				target.reduceCurrentHp(finalDamage, caster, this);

				// vengeance reflected damage
				if ((reflect & Formulas.SKILL_REFLECT_VENGEANCE) != 0)
					caster.reduceCurrentHp(damage, target, this);

				caster.sendDamageMessage(target, (int) finalDamage, false, crit, false);
			}
			else
				caster.sendDamageMessage(target, 0, false, false, true);
		}

		if (soul && weapon != null)
			weapon.setChargedSoulshot(L2ItemInstance.CHARGED_NONE);

		// effect self :]
		if (hasSelfEffects())
		{
			L2Effect effect = caster.getFirstEffect(getId());

			// Replace old effect with new one.
			if (effect != null && effect.isSelfEffect())
				effect.exit();

			// cast self effect if any
			getEffectsSelf(caster);
		}
	}
}