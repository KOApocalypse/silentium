/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package silentium.scripts.ai;

import javolution.util.FastList;
import silentium.commons.utils.Rnd;
import silentium.gameserver.ai.CtrlIntention;
import silentium.gameserver.ai.DefaultMonsterAI;
import silentium.gameserver.configs.NPCConfig;
import silentium.gameserver.instancemanager.GrandBossManager;
import silentium.gameserver.model.actor.L2Attackable;
import silentium.gameserver.model.actor.L2Npc;
import silentium.gameserver.model.actor.instance.L2GrandBossInstance;
import silentium.gameserver.model.actor.instance.L2MonsterInstance;
import silentium.gameserver.model.actor.instance.L2PcInstance;
import silentium.gameserver.model.zone.type.L2BossZone;
import silentium.gameserver.network.serverpackets.PlaySound;
import silentium.gameserver.network.serverpackets.SocialAction;
import silentium.gameserver.scripting.ScriptFile;
import silentium.gameserver.skills.SkillHolder;
import silentium.gameserver.templates.StatsSet;

import java.util.List;

/**
 * Queen Ant AI
 *
 * @author Emperorc
 */
public class QueenAnt extends DefaultMonsterAI implements ScriptFile {
	private static final int QUEEN = 29001;
	private static final int LARVA = 29002;
	private static final int NURSE = 29003;
	private static final int GUARD = 29004;
	private static final int ROYAL = 29005;

	private static final int[] MOBS = { QUEEN, LARVA, NURSE, GUARD, ROYAL };

	private static final int QUEEN_X = -21610;
	private static final int QUEEN_Y = 181594;
	private static final int QUEEN_Z = -5734;

	// Status Tracking
	private static final byte ALIVE = 0; // Queen Ant is spawned.
	private static final byte DEAD = 1; // Queen Ant has been killed.

	private static L2BossZone _zone;

	private static final SkillHolder HEAL1 = new SkillHolder(4020, 1);
	private static final SkillHolder HEAL2 = new SkillHolder(4024, 1);

	private L2MonsterInstance _queen = null;
	private L2MonsterInstance _larva = null;
	private final List<L2MonsterInstance> _nurses = new FastList<>(5);

	public static void onLoad() {
		new QueenAnt(-1, "QueenAnt", "QueenAnt", "ai");
	}

	public QueenAnt(final int scriptId, final String name, final String dname, final String path) {
		super(scriptId, name, dname, path);

		registerMobs(MOBS, QuestEventType.ON_SPAWN, QuestEventType.ON_KILL);
		addFactionCallId(NURSE);

		_zone = GrandBossManager.getInstance().getZone(QUEEN_X, QUEEN_Y, QUEEN_Z);

		final StatsSet info = GrandBossManager.getInstance().getStatsSet(QUEEN);
		if (GrandBossManager.getInstance().getBossStatus(QUEEN) == DEAD) {
			// load the unlock date and time for queen ant from DB
			final long temp = info.getLong("respawn_time") - System.currentTimeMillis();

			// the unlock time has not yet expired.
			if (temp > 0)
				startQuestTimer("queen_unlock", temp, null, null);
				// the time has already expired while the server was offline. Immediately spawn queen ant.
			else {
				final L2GrandBossInstance queen = (L2GrandBossInstance) addSpawn(QUEEN, QUEEN_X, QUEEN_Y, QUEEN_Z, 0, false, 0);
				GrandBossManager.getInstance().setBossStatus(QUEEN, ALIVE);
				spawnBoss(queen);
			}
		} else {
			int loc_x = info.getInteger("loc_x");
			int loc_y = info.getInteger("loc_y");
			int loc_z = info.getInteger("loc_z");
			final int heading = info.getInteger("heading");
			final int hp = info.getInteger("currentHP");
			final int mp = info.getInteger("currentMP");
			if (!_zone.isInsideZone(loc_x, loc_y, loc_z)) {
				loc_x = QUEEN_X;
				loc_y = QUEEN_Y;
				loc_z = QUEEN_Z;
			}
			final L2GrandBossInstance queen = (L2GrandBossInstance) addSpawn(QUEEN, loc_x, loc_y, loc_z, heading, false, 0);
			queen.setCurrentHpMp(hp, mp);
			spawnBoss(queen);
		}
	}

	private void spawnBoss(final L2GrandBossInstance npc) {
		if (Rnd.get(100) < 33)
			_zone.movePlayersTo(-19480, 187344, -5600);
		else if (Rnd.get(100) < 50)
			_zone.movePlayersTo(-17928, 180912, -5520);
		else
			_zone.movePlayersTo(-23808, 182368, -5600);

		GrandBossManager.getInstance().addBoss(npc);
		startQuestTimer("action", 10000, npc, null, true);
		startQuestTimer("heal", 1000, null, null, true);
		npc.broadcastPacket(new PlaySound(1, "BS02_D", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));

		_queen = npc;
		_larva = (L2MonsterInstance) addSpawn(LARVA, -21600, 179482, -5846, Rnd.get(360), false, 0);
	}

	@Override
	public String onAdvEvent(final String event, final L2Npc npc, final L2PcInstance player) {
		if ("heal".equalsIgnoreCase(event)) {
			boolean notCasting;
			final boolean larvaNeedHeal = _larva != null && _larva.getCurrentHp() < _larva.getMaxHp();
			final boolean queenNeedHeal = _queen != null && _queen.getCurrentHp() < _queen.getMaxHp();
			for (final L2MonsterInstance nurse : _nurses) {
				if (nurse == null || nurse.isDead() || nurse.isCastingNow())
					continue;

				notCasting = nurse.getAI().getIntention() != CtrlIntention.AI_INTENTION_CAST;
				if (larvaNeedHeal) {
					if (nurse.getTarget() != _larva || notCasting) {
						nurse.setTarget(_larva);
						nurse.useMagic(Rnd.nextBoolean() ? HEAL1.getSkill() : HEAL2.getSkill());
					}
					continue;
				}

				if (queenNeedHeal) {
					if (nurse.getLeader() == _larva) // skip larva's minions
						continue;

					if (nurse.getTarget() != _queen || notCasting) {
						nurse.setTarget(_queen);
						nurse.useMagic(HEAL1.getSkill());
					}
					continue;
				}

				// if nurse not casting - remove target
				if (notCasting && nurse.getTarget() != null)
					nurse.setTarget(null);
			}
		} else if ("action".equalsIgnoreCase(event) && npc != null) {
			if (Rnd.get(3) == 0) {
				if (Rnd.get(2) == 0)
					npc.broadcastPacket(new SocialAction(npc, 3));
				else
					npc.broadcastPacket(new SocialAction(npc, 4));
			}
		} else if ("queen_unlock".equalsIgnoreCase(event)) {
			final L2GrandBossInstance queen = (L2GrandBossInstance) addSpawn(QUEEN, QUEEN_X, QUEEN_Y, QUEEN_Z, 0, false, 0);
			GrandBossManager.getInstance().setBossStatus(QUEEN, ALIVE);
			spawnBoss(queen);
		}
		return super.onAdvEvent(event, npc, player);
	}

	@Override
	public String onSpawn(final L2Npc npc) {
		final L2MonsterInstance mob = (L2MonsterInstance) npc;
		switch (npc.getNpcId()) {
			case LARVA:
				mob.setIsImmobilized(true);
				mob.setIsMortal(false);
				mob.setIsRaidMinion(true);
				break;
			case NURSE:
				mob.disableCoreAI(true);
				mob.setIsRaidMinion(true);
				_nurses.add(mob);
				break;
			case ROYAL:
			case GUARD:
				mob.setIsRaidMinion(true);
				break;
		}
		return super.onSpawn(npc);
	}

	@Override
	public String onFactionCall(final L2Npc npc, final L2Npc caller, final L2PcInstance attacker, final boolean isPet) {
		if (caller == null || npc == null)
			return super.onFactionCall(npc, caller, attacker, isPet);

		if (!npc.isCastingNow() && npc.getAI().getIntention() != CtrlIntention.AI_INTENTION_CAST) {
			if (caller.getCurrentHp() < caller.getMaxHp()) {
				npc.setTarget(caller);
				((L2Attackable) npc).useMagic(HEAL1.getSkill());
			}
		}
		return null;
	}

	@Override
	public String onKill(final L2Npc npc, final L2PcInstance killer, final boolean isPet) {
		// Acts only once.
		if (GrandBossManager.getInstance().getBossStatus(QUEEN) == ALIVE) {
			final int npcId = npc.getNpcId();
			if (npcId == QUEEN) {
				npc.broadcastPacket(new PlaySound(1, "BS02_D", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));
				GrandBossManager.getInstance().setBossStatus(QUEEN, DEAD);

				// time is 36hour +/- 17hour
				final long respawnTime = (long) NPCConfig.SPAWN_INTERVAL_AQ + Rnd.get(NPCConfig.RANDOM_SPAWN_TIME_AQ);
				startQuestTimer("queen_unlock", respawnTime, null, null);
				cancelQuestTimer("action", npc, null);
				cancelQuestTimer("heal", null, null);

				// also save the respawn time so that the info is maintained past reboots
				final StatsSet info = GrandBossManager.getInstance().getStatsSet(QUEEN);
				info.set("respawn_time", System.currentTimeMillis() + respawnTime);
				GrandBossManager.getInstance().setStatsSet(QUEEN, info);

				_nurses.clear();
				_larva.deleteMe();
				_larva = null;
				_queen = null;
			} else {
				if (npcId == ROYAL) {
					final L2MonsterInstance mob = (L2MonsterInstance) npc;
					if (mob.getLeader() != null)
						mob.getLeader().getMinionList().onMinionDie(mob, (280 + Rnd.get(40)) * 1000);
				} else if (npcId == NURSE) {
					final L2MonsterInstance mob = (L2MonsterInstance) npc;
					_nurses.remove(mob);
					if (mob.getLeader() != null)
						mob.getLeader().getMinionList().onMinionDie(mob, 10000);
				}
			}
		}
		return super.onKill(npc, killer, isPet);
	}
}