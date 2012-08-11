/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program
 * is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have
 * received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package silentium.gameserver.skills.effects;

import silentium.gameserver.model.CharEffectList;
import silentium.gameserver.model.L2Effect;
import silentium.gameserver.model.actor.L2Playable;
import silentium.gameserver.skills.Env;
import silentium.gameserver.templates.skills.L2EffectType;

/**
 * @author kerberos_20
 */
public class EffectProtectionBlessing extends L2Effect
{
	public EffectProtectionBlessing(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.PROTECTION_BLESSING;
	}

	/** Notify started */
	@Override
	public boolean onStart()
	{
		return false;
	}

	/** Notify exited */
	@Override
	public void onExit()
	{
		((L2Playable) getEffected()).stopProtectionBlessing(this);
	}

	@Override
	public boolean onActionTime()
	{
		// just stop this effect
		return false;
	}

	@Override
	public int getEffectFlags()
	{
		return CharEffectList.EFFECT_FLAG_PROTECTION_BLESSING;
	}
}