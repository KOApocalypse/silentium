/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package silentium.scripts.quests;

import silentium.commons.utils.Rnd;
import silentium.gameserver.model.actor.L2Npc;
import silentium.gameserver.model.actor.instance.L2PcInstance;
import silentium.gameserver.model.quest.Quest;
import silentium.gameserver.model.quest.QuestState;
import silentium.gameserver.scripting.ScriptFile;

public class Q325_GrimCollector extends Quest implements ScriptFile {
	private static final String qn = "Q325_GrimCollector";

	// Items
	private static final int ANATOMY_DIAGRAM = 1349;
	private static final int ZOMBIE_HEAD = 1350;
	private static final int ZOMBIE_HEART = 1351;
	private static final int ZOMBIE_LIVER = 1352;
	private static final int SKULL = 1353;
	private static final int RIB_BONE = 1354;
	private static final int SPINE = 1355;
	private static final int ARM_BONE = 1356;
	private static final int THIGH_BONE = 1357;
	private static final int COMPLETE_SKELETON = 1358;

	// NPCs
	private static final int CURTIS = 30336;
	private static final int VARSAK = 30342;
	private static final int SAMED = 30434;

	public Q325_GrimCollector(final int scriptId, final String name, final String dname, final String path) {
		super(scriptId, name, dname, path);

		questItemIds = new int[] { ZOMBIE_HEAD, ZOMBIE_HEART, ZOMBIE_LIVER, SKULL, RIB_BONE, SPINE, ARM_BONE, THIGH_BONE, COMPLETE_SKELETON, ANATOMY_DIAGRAM };

		addStartNpc(CURTIS);
		addTalkId(CURTIS, VARSAK, SAMED);

		addKillId(20026, 20029, 20035, 20042, 20045, 20457, 20458, 20051, 20514, 20515);
	}

	public static void onLoad() {
		new Q325_GrimCollector(325, "Q325_GrimCollector", "Grim Collector", "quests");
	}

	private int getNumberOfPieces(final QuestState st) {
		return st.getQuestItemsCount(ZOMBIE_HEAD) + st.getQuestItemsCount(SPINE) + st.getQuestItemsCount(ARM_BONE) + st.getQuestItemsCount(ZOMBIE_HEART) + st.getQuestItemsCount(ZOMBIE_LIVER) + st.getQuestItemsCount(SKULL) + st.getQuestItemsCount(RIB_BONE) + st.getQuestItemsCount(THIGH_BONE) + st.getQuestItemsCount(COMPLETE_SKELETON);
	}

	private void payback(final QuestState st) {
		final int count = getNumberOfPieces(st);
		if (count > 0) {
			int reward = 30 * st.getQuestItemsCount(ZOMBIE_HEAD) + 20 * st.getQuestItemsCount(ZOMBIE_HEART) + 20 * st.getQuestItemsCount(ZOMBIE_LIVER) + 100 * st.getQuestItemsCount(SKULL) + 40 * st.getQuestItemsCount(RIB_BONE) + 14 * st.getQuestItemsCount(SPINE) + 14 * st.getQuestItemsCount(ARM_BONE) + 14 * st.getQuestItemsCount(THIGH_BONE) + 341 * st.getQuestItemsCount(COMPLETE_SKELETON);
			if (count > 10)
				reward += 1629;

			if (st.getQuestItemsCount(COMPLETE_SKELETON) > 0)
				reward += 543;

			st.takeItems(ZOMBIE_HEAD, -1);
			st.takeItems(ZOMBIE_HEART, -1);
			st.takeItems(ZOMBIE_LIVER, -1);
			st.takeItems(SKULL, -1);
			st.takeItems(RIB_BONE, -1);
			st.takeItems(SPINE, -1);
			st.takeItems(ARM_BONE, -1);
			st.takeItems(THIGH_BONE, -1);
			st.takeItems(COMPLETE_SKELETON, -1);

			st.rewardItems(57, reward);
		}
	}

	@Override
	public String onAdvEvent(final String event, final L2Npc npc, final L2PcInstance player) {
		String htmltext = event;
		final QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if ("30336-03.htm".equalsIgnoreCase(event)) {
			st.set("cond", "1");
			st.setState(QuestState.STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
		} else if ("30434-03.htm".equalsIgnoreCase(event)) {
			st.giveItems(ANATOMY_DIAGRAM, 1);
			st.playSound(QuestState.SOUND_ITEMGET);
		} else if ("30434-06.htm".equalsIgnoreCase(event)) {
			st.takeItems(ANATOMY_DIAGRAM, -1);
			payback(st);
			st.playSound(QuestState.SOUND_FINISH);
			st.exitQuest(true);
		} else if ("30434-07.htm".equalsIgnoreCase(event)) {
			payback(st);
			st.playSound(QuestState.SOUND_MIDDLE);
		} else if ("30434-09.htm".equalsIgnoreCase(event)) {
			final int skeletons = st.getQuestItemsCount(COMPLETE_SKELETON);
			if (skeletons > 0) {
				st.takeItems(COMPLETE_SKELETON, -1);
				st.playSound(QuestState.SOUND_MIDDLE);
				st.rewardItems(57, 543 + 341 * skeletons);
			}
		} else if ("30342-03.htm".equalsIgnoreCase(event)) {
			if (st.getQuestItemsCount(SPINE) > 0 && st.getQuestItemsCount(ARM_BONE) > 0 && st.getQuestItemsCount(SKULL) > 0 && st.getQuestItemsCount(RIB_BONE) > 0 && st.getQuestItemsCount(THIGH_BONE) > 0) {
				st.takeItems(SPINE, 1);
				st.takeItems(SKULL, 1);
				st.takeItems(ARM_BONE, 1);
				st.takeItems(RIB_BONE, 1);
				st.takeItems(THIGH_BONE, 1);

				if (Rnd.get(10) < 9) {
					st.giveItems(COMPLETE_SKELETON, 1);
					st.playSound(QuestState.SOUND_ITEMGET);
				} else
					htmltext = "30342-04.htm";
			} else
				htmltext = "30342-02.htm";
		}

		return htmltext;
	}

	@Override
	public String onTalk(final L2Npc npc, final L2PcInstance player) {
		final QuestState st = player.getQuestState(qn);
		String htmltext = getNoQuestMsg();
		if (st == null)
			return htmltext;

		switch (st.getState()) {
			case QuestState.CREATED:
				if (player.getLevel() >= 15 && player.getLevel() <= 26)
					htmltext = "30336-02.htm";
				else {
					htmltext = "30336-01.htm";
					st.exitQuest(true);
				}
				break;

			case QuestState.STARTED:
				switch (npc.getNpcId()) {
					case CURTIS:
						htmltext = st.getQuestItemsCount(ANATOMY_DIAGRAM) < 1 ? "30336-04.htm" : "30336-05.htm";
						break;

					case SAMED:
						if (st.getQuestItemsCount(ANATOMY_DIAGRAM) == 0)
							htmltext = "30434-01.htm";
						else {
							htmltext = getNumberOfPieces(st) == 0 ? "30434-04.htm" : st.getQuestItemsCount(COMPLETE_SKELETON) == 0 ? "30434-05.htm" : "30434-08.htm";
						}
						break;

					case VARSAK:
						htmltext = "30342-01.htm";
						break;
				}
				break;
		}

		return htmltext;
	}

	@Override
	public String onKill(final L2Npc npc, final L2PcInstance player, final boolean isPet) {
		final QuestState st = player.getQuestState(qn);
		if (st == null)
			return null;

		if (st.isStarted() && st.getQuestItemsCount(ANATOMY_DIAGRAM) > 0) {
			final int n = Rnd.get(100);
			switch (npc.getNpcId()) {
				case 20026:
					if (n <= 90) {
						st.playSound(QuestState.SOUND_ITEMGET);
						if (n <= 40)
							st.giveItems(ZOMBIE_HEAD, 1);
						else if (n <= 60)
							st.giveItems(ZOMBIE_HEART, 1);
						else
							st.giveItems(ZOMBIE_LIVER, 1);
					}
					break;

				case 20029:
					st.playSound(QuestState.SOUND_ITEMGET);
					if (n <= 44)
						st.giveItems(ZOMBIE_HEAD, 1);
					else if (n <= 66)
						st.giveItems(ZOMBIE_HEART, 1);
					else
						st.giveItems(ZOMBIE_LIVER, 1);
					break;

				case 20035:
					if (n <= 79) {
						st.playSound(QuestState.SOUND_ITEMGET);
						if (n <= 5)
							st.giveItems(SKULL, 1);
						else if (n <= 15)
							st.giveItems(RIB_BONE, 1);
						else if (n <= 29)
							st.giveItems(SPINE, 1);
						else
							st.giveItems(THIGH_BONE, 1);
					}
					break;

				case 20042:
					if (n <= 86) {
						st.playSound(QuestState.SOUND_ITEMGET);
						if (n <= 6)
							st.giveItems(SKULL, 1);
						else if (n <= 19)
							st.giveItems(RIB_BONE, 1);
						else if (n <= 69)
							st.giveItems(ARM_BONE, 1);
						else
							st.giveItems(THIGH_BONE, 1);
					}
					break;

				case 20045:
					if (n <= 97) {
						st.playSound(QuestState.SOUND_ITEMGET);
						if (n <= 9)
							st.giveItems(SKULL, 1);
						else if (n <= 59)
							st.giveItems(SPINE, 1);
						else if (n <= 77)
							st.giveItems(ARM_BONE, 1);
						else
							st.giveItems(THIGH_BONE, 1);
					}
					break;

				case 20051:
					if (n <= 99) {
						st.playSound(QuestState.SOUND_ITEMGET);
						if (n <= 9)
							st.giveItems(SKULL, 1);
						else if (n <= 59)
							st.giveItems(RIB_BONE, 1);
						else if (n <= 79)
							st.giveItems(SPINE, 1);
						else
							st.giveItems(ARM_BONE, 1);
					}
					break;

				case 20514:
					if (n <= 51) {
						st.playSound(QuestState.SOUND_ITEMGET);
						if (n <= 2)
							st.giveItems(SKULL, 1);
						else if (n <= 8)
							st.giveItems(RIB_BONE, 1);
						else if (n <= 17)
							st.giveItems(SPINE, 1);
						else if (n <= 18)
							st.giveItems(ARM_BONE, 1);
						else
							st.giveItems(THIGH_BONE, 1);
					}
					break;

				case 20515:
					if (n <= 60) {
						st.playSound(QuestState.SOUND_ITEMGET);
						if (n <= 3)
							st.giveItems(SKULL, 1);
						else if (n <= 11)
							st.giveItems(RIB_BONE, 1);
						else if (n <= 22)
							st.giveItems(SPINE, 1);
						else if (n <= 24)
							st.giveItems(ARM_BONE, 1);
						else
							st.giveItems(THIGH_BONE, 1);
					}
					break;

				case 20457:
				case 20458:
					st.playSound(QuestState.SOUND_ITEMGET);
					if (n <= 42)
						st.giveItems(ZOMBIE_HEAD, 1);
					else if (n <= 67)
						st.giveItems(ZOMBIE_HEART, 1);
					else
						st.giveItems(ZOMBIE_LIVER, 1);
					break;
			}
		}

		return null;
	}
}