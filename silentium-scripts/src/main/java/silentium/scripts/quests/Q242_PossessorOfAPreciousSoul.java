/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package silentium.scripts.quests;

import silentium.gameserver.model.actor.L2Npc;
import silentium.gameserver.model.actor.instance.L2PcInstance;
import silentium.gameserver.model.quest.Quest;
import silentium.gameserver.model.quest.QuestState;
import silentium.gameserver.scripting.ScriptFile;

public class Q242_PossessorOfAPreciousSoul extends Quest implements ScriptFile {
	private static final String qn = "Q242_PossessorOfAPreciousSoul";

	// NPCs
	private static final int VIRGIL = 31742;
	private static final int KASSANDRA = 31743;
	private static final int OGMAR = 31744;
	private static final int MYSTERIOUS_KNIGHT = 31751;
	private static final int ANGEL_CORPSE = 31752;
	private static final int KALIS = 30759;
	private static final int MATILD = 30738;
	private static final int CORNERSTONE = 31748;
	private static final int FALLEN_UNICORN = 31746;
	private static final int PURE_UNICORN = 31747;

	// Monsters
	private static final int RESTRAINER_OF_GLORY = 27317;

	// Items
	private static final int VIRGILS_LETTER = 7677;
	private static final int GOLDEN_HAIR = 7590;
	private static final int SORCERY_INGREDIENT = 7596;
	private static final int ORB_OF_BINDING = 7595;
	private static final int CARADINE_LETTER_2 = 7678;

	public Q242_PossessorOfAPreciousSoul(final int scriptId, final String name, final String dname, final String path) {
		super(scriptId, name, dname, path);

		questItemIds = new int[] { GOLDEN_HAIR, SORCERY_INGREDIENT, ORB_OF_BINDING };

		addStartNpc(VIRGIL);
		addTalkId(VIRGIL, KASSANDRA, OGMAR, MYSTERIOUS_KNIGHT, ANGEL_CORPSE, KALIS, MATILD, CORNERSTONE, FALLEN_UNICORN, PURE_UNICORN);

		addKillId(RESTRAINER_OF_GLORY);

		// Unicorn in iddle mode
		saveGlobalQuestVar("unicorn", "0");
	}

	public static void onLoad() {
		new Q242_PossessorOfAPreciousSoul(242, "Q242_PossessorOfAPreciousSoul", "Possessor Of A Precious Soul 2", "quests");
	}

	@Override
	public String onAdvEvent(final String event, L2Npc npc, final L2PcInstance player) {
		String htmltext = event;
		final QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		// Kasandra
		if ("31743-05.htm".equalsIgnoreCase(event)) {
			st.set("cond", "2");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		// Ogmar
		else if ("31744-02.htm".equalsIgnoreCase(event)) {
			st.set("cond", "3");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		// Mysterious Knight
		else if ("31751-02.htm".equalsIgnoreCase(event)) {
			st.set("cond", "4");
			st.set("angel", "0");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		// Kalis
		else if ("30759-02.htm".equalsIgnoreCase(event)) {
			st.set("cond", "7");
			st.playSound(QuestState.SOUND_MIDDLE);
		} else if ("30759-05.htm".equalsIgnoreCase(event)) {
			if (st.hasQuestItems(SORCERY_INGREDIENT)) {
				st.set("orb", "0");
				st.set("cornerstone", "0");
				st.set("cond", "9");
				st.takeItems(GOLDEN_HAIR, 1);
				st.takeItems(SORCERY_INGREDIENT, 1);
				st.playSound(QuestState.SOUND_MIDDLE);
			} else {
				st.set("cond", "7");
				htmltext = "30759-02.htm";
			}
		}
		// Matild
		else if ("30738-02.htm".equalsIgnoreCase(event)) {
			st.set("cond", "8");
			st.giveItems(SORCERY_INGREDIENT, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		// Cornerstone
		else if ("31748-03.htm".equalsIgnoreCase(event)) {
			if (st.hasQuestItems(ORB_OF_BINDING)) {
				npc.deleteMe();
				st.takeItems(ORB_OF_BINDING, 1);

				int cornerstones = st.getInt("cornerstone");
				cornerstones++;
				if (cornerstones == 4) {
					st.unset("orb");
					st.unset("cornerstone");
					st.set("cond", "10");
					st.playSound(QuestState.SOUND_MIDDLE);
				} else
					st.set("cornerstone", Integer.toString(cornerstones));
			} else
				htmltext = null;
		}
		// Spawn Pure Unicorn
		else if ("spu".equalsIgnoreCase(event)) {
			st.addSpawn(PURE_UNICORN, 85884, -76588, -3470);
			return null;
		}
		// Despawn Pure Unicorn
		else if ("dspu".equalsIgnoreCase(event)) {
			npc.getSpawn().stopRespawn();
			npc.deleteMe();
			startQuestTimer("sfu", 2000, null, player);
			return null;
		}
		// Spawn Fallen Unicorn
		else if ("sfu".equalsIgnoreCase(event)) {
			npc = st.addSpawn(FALLEN_UNICORN, 85884, -76588, -3470);
			npc.getSpawn().startRespawn();
			return null;
		}
		return htmltext;
	}

	@Override
	public String onTalk(final L2Npc npc, final L2PcInstance player) {
		String htmltext = getNoQuestMsg();
		final QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		switch (st.getState()) {
			case QuestState.CREATED:
				if (st.hasQuestItems(VIRGILS_LETTER)) {
					if (!player.isSubClassActive() || player.getLevel() < 60) {
						htmltext = "31742-02.htm";
						st.exitQuest(true);
					} else {
						htmltext = "31742-03.htm";
						st.set("cond", "1");
						st.takeItems(VIRGILS_LETTER, 1);
						st.setState(QuestState.STARTED);
						st.playSound(QuestState.SOUND_ACCEPT);
					}
				}
				break;

			case QuestState.STARTED:
				if (!player.isSubClassActive())
					break;

				final int cond = st.getInt("cond");
				switch (npc.getNpcId()) {
					case VIRGIL:
						if (cond == 1)
							htmltext = "31742-04.htm";
						else if (cond == 2)
							htmltext = "31742-05.htm";
						break;

					case KASSANDRA:
						if (cond == 1)
							htmltext = "31743-01.htm";
						else if (cond == 2)
							htmltext = "31743-06.htm";
						else if (cond == 11) {
							htmltext = "31743-07.htm";
							st.giveItems(CARADINE_LETTER_2, 1);
							st.addExpAndSp(455764, 0);
							st.playSound(QuestState.SOUND_FINISH);
							st.exitQuest(false);
						}
						break;

					case OGMAR:
						if (cond == 2)
							htmltext = "31744-01.htm";
						else if (cond == 3)
							htmltext = "31744-03.htm";
						break;

					case MYSTERIOUS_KNIGHT:
						if (cond == 3)
							htmltext = "31751-01.htm";
						else if (cond == 4)
							htmltext = "31751-03.htm";
						else if (cond == 5) {
							if (st.hasQuestItems(GOLDEN_HAIR)) {
								st.set("cond", "6");
								st.playSound(QuestState.SOUND_MIDDLE);
								htmltext = "31751-04.htm";
							} else {
								htmltext = "31751-03.htm";
								st.set("cond", "4");
							}
						} else if (cond == 6)
							htmltext = "31751-05.htm";
						break;

					case ANGEL_CORPSE:
						if (cond == 4) {
							npc.deleteMe();
							int hair = st.getInt("angel");
							hair++;

							if (hair == 4) {
								st.unset("angel");
								st.set("cond", "5");
								st.giveItems(GOLDEN_HAIR, 1);
								st.playSound(QuestState.SOUND_MIDDLE);
								htmltext = "31752-02.htm";
							} else {
								st.set("angel", Integer.toString(hair));
								htmltext = "31752-01.htm";
							}
						} else if (cond == 5)
							htmltext = "31752-01.htm";
						break;

					case KALIS:
						if (cond == 6)
							htmltext = "30759-01.htm";
						else if (cond == 7)
							htmltext = "30759-03.htm";
						else if (cond == 8) {
							if (st.hasQuestItems(SORCERY_INGREDIENT))
								htmltext = "30759-04.htm";
							else {
								htmltext = "30759-03.htm";
								st.set("cond", "7");
							}
						} else if (cond == 9)
							htmltext = "30759-06.htm";
						break;

					case MATILD:
						if (cond == 7)
							htmltext = "30738-01.htm";
						else if (cond == 8)
							htmltext = "30738-03.htm";
						break;

					case CORNERSTONE:
						if (cond == 9) {
							htmltext = st.hasQuestItems(ORB_OF_BINDING) ? "31748-02.htm" : "31748-01.htm";
						}
						break;

					case FALLEN_UNICORN:
						if (cond == 9)
							htmltext = "31746-01.htm";
						else if (cond == 10) {
							if ("0".equals(loadGlobalQuestVar("unicorn"))) // Global variable check to prevent multiple spawns
							{
								saveGlobalQuestVar("unicorn", "1");
								npc.getSpawn().stopRespawn(); // Despawn fallen unicorn
								npc.deleteMe();
								startQuestTimer("spu", 3000, npc, player);
							}
							htmltext = "31746-02.htm";
						}
						break;

					case PURE_UNICORN:
						if (cond == 10) {
							st.set("cond", "11");
							st.playSound(QuestState.SOUND_MIDDLE);
							if ("1".equals(loadGlobalQuestVar("unicorn"))) // Global variable check to prevent multiple spawns
							{
								saveGlobalQuestVar("unicorn", "0");
								startQuestTimer("dspu", 3000, npc, player);
							}
							htmltext = "31747-01.htm";
						} else if (cond == 11)
							htmltext = "31747-02.htm";
						break;
				}
				break;

			case QuestState.COMPLETED:
				htmltext = Quest.getAlreadyCompletedMsg();
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(final L2Npc npc, final L2PcInstance player, final boolean isPet) {
		if (!player.isSubClassActive() || !checkPlayerCondition(player, npc, "cond", "9"))
			return null;

		final QuestState st = player.getQuestState(qn);
		int orbs = st.getInt("orb"); // check orbs internally, because player can use them before he gets them all
		if (orbs < 4) {
			st.giveItems(ORB_OF_BINDING, 1);
			st.playSound(QuestState.SOUND_ITEMGET);
			orbs++;
			st.set("orb", Integer.toString(orbs));
		}
		return null;
	}
}