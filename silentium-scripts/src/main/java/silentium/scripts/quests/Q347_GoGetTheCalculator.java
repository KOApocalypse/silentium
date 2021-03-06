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

public class Q347_GoGetTheCalculator extends Quest implements ScriptFile {
	private static final String qn = "Q347_GoGetTheCalculator";

	// NPCs
	private static final int BRUNON = 30526;
	private static final int SILVERA = 30527;
	private static final int SPIRON = 30532;
	private static final int BALANKI = 30533;

	// Items
	private static final int GEMSTONE_BEAST_CRYSTAL = 4286;
	private static final int CALCULATOR_Q = 4285;
	private static final int CALCULATOR_REAL = 4393;

	public Q347_GoGetTheCalculator(final int scriptId, final String name, final String dname, final String path) {
		super(scriptId, name, dname, path);

		questItemIds = new int[] { 4286 };

		addStartNpc(BRUNON);
		addTalkId(BRUNON, SILVERA, SPIRON, BALANKI);

		addKillId(20540);
	}

	public static void onLoad() {
		new Q347_GoGetTheCalculator(347, "Q347_GoGetTheCalculator", "Go Get The Calculator", "quests");
	}

	@Override
	public String onAdvEvent(final String event, final L2Npc npc, final L2PcInstance player) {
		String htmltext = event;
		final QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if ("30526-05.htm".equalsIgnoreCase(event)) {
			st.set("cond", "1");
			st.setState(QuestState.STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
		} else if ("30533-03.htm".equalsIgnoreCase(event)) {
			if (st.getQuestItemsCount(57) >= 100) {
				htmltext = "30533-02.htm";
				st.takeItems(57, 100);

				if (st.getInt("cond") == 3)
					st.set("cond", "4");
				else
					st.set("cond", "2");

				st.playSound(QuestState.SOUND_MIDDLE);
			}
		} else if ("30532-02.htm".equalsIgnoreCase(event)) {
			if (st.getInt("cond") == 2)
				st.set("cond", "4");
			else
				st.set("cond", "3");

			st.playSound(QuestState.SOUND_MIDDLE);
		} else if ("30526-08.htm".equalsIgnoreCase(event)) {
			st.takeItems(CALCULATOR_Q, -1);
			st.giveItems(CALCULATOR_REAL, 1);
			st.playSound(QuestState.SOUND_FINISH);
			st.exitQuest(true);
		} else if ("30526-09.htm".equalsIgnoreCase(event)) {
			st.takeItems(CALCULATOR_Q, -1);
			st.rewardItems(57, 1000);
			st.playSound(QuestState.SOUND_FINISH);
			st.exitQuest(true);
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
				htmltext = player.getLevel() >= 12 ? "30526-01.htm" : "30526-00.htm";
				break;

			case QuestState.STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getNpcId()) {
					case BRUNON:
						htmltext = st.getQuestItemsCount(CALCULATOR_Q) == 0 ? "30526-06.htm" : "30526-07.htm";
						break;

					case SPIRON:
						if (cond >= 1 && cond <= 3)
							htmltext = "30532-01.htm";
						else if (cond >= 4)
							htmltext = "30532-05.htm";
						break;

					case BALANKI:
						if (cond >= 1 && cond <= 3)
							htmltext = "30533-01.htm";
						else if (cond >= 4)
							htmltext = "30533-04.htm";
						break;

					case SILVERA:
						if (cond < 4)
							htmltext = "30527-00.htm";
						else if (cond == 4) {
							htmltext = "30527-01.htm";
							st.set("cond", "5");
							st.playSound(QuestState.SOUND_MIDDLE);
						} else if (cond == 5) {
							if (st.getQuestItemsCount(GEMSTONE_BEAST_CRYSTAL) >= 10) {
								htmltext = "30527-03.htm";
								st.set("cond", "6");
								st.takeItems(GEMSTONE_BEAST_CRYSTAL, -1);
								st.giveItems(CALCULATOR_Q, 1);
								st.playSound(QuestState.SOUND_MIDDLE);
							} else
								htmltext = "30527-02.htm";
						} else if (cond == 6)
							htmltext = "30527-04.htm";
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

		if (st.getInt("cond") == 5)
			st.dropQuestItems(GEMSTONE_BEAST_CRYSTAL, 1, 10, 500000);

		return null;
	}
}