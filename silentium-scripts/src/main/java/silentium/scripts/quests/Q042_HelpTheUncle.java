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

public class Q042_HelpTheUncle extends Quest implements ScriptFile {
	private static final String qn = "Q042_HelpTheUncle";

	// NPCs
	private static final int WATERS = 30828;
	private static final int SOPHYA = 30735;

	// Items
	private static final int TRIDENT = 291;
	private static final int MAP_PIECE = 7548;
	private static final int MAP = 7549;
	private static final int PET_TICKET = 7583;

	// Monsters
	private static final int MONSTER_EYE_DESTROYER = 20068;
	private static final int MONSTER_EYE_GAZER = 20266;

	public Q042_HelpTheUncle(final int scriptId, final String name, final String dname, final String path) {
		super(scriptId, name, dname, path);

		questItemIds = new int[] { MAP_PIECE, MAP };

		addStartNpc(WATERS);
		addTalkId(WATERS, SOPHYA);

		addKillId(MONSTER_EYE_DESTROYER, MONSTER_EYE_GAZER);
	}

	public static void onLoad() {
		new Q042_HelpTheUncle(42, "Q042_HelpTheUncle", "Help The Uncle", "quests");
	}

	@Override
	public String onAdvEvent(final String event, final L2Npc npc, final L2PcInstance player) {
		final String htmltext = event;
		final QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if ("30828-01.htm".equalsIgnoreCase(event)) {
			st.set("cond", "1");
			st.setState(QuestState.STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
		} else if ("30828-03.htm".equalsIgnoreCase(event) && st.getQuestItemsCount(TRIDENT) >= 1) {
			st.set("cond", "2");
			st.takeItems(TRIDENT, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		} else if ("30828-05.htm".equalsIgnoreCase(event) && st.getQuestItemsCount(MAP_PIECE) >= 30) {
			st.takeItems(MAP_PIECE, 30);
			st.giveItems(MAP, 1);
			st.set("cond", "4");
			st.playSound(QuestState.SOUND_MIDDLE);
		} else if ("30735-06.htm".equalsIgnoreCase(event) && st.getQuestItemsCount(MAP) == 1) {
			st.takeItems(MAP, 1);
			st.set("cond", "5");
			st.playSound(QuestState.SOUND_MIDDLE);
		} else if ("30828-07.htm".equalsIgnoreCase(event)) {
			st.giveItems(PET_TICKET, 1);
			st.playSound(QuestState.SOUND_FINISH);
			st.exitQuest(false);
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
				if (player.getLevel() >= 25)
					htmltext = "30828-00.htm";
				else {
					htmltext = "<html><body>This quest can only be taken by characters that have a minimum level of 25. Return when you are more experienced.</body></html>";
					st.exitQuest(true);
				}
				break;

			case QuestState.STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getNpcId()) {
					case WATERS:
						if (cond == 1)
							htmltext = st.getQuestItemsCount(TRIDENT) == 0 ? "30828-01a.htm" : "30828-02.htm";
						else if (cond == 2)
							htmltext = "30828-03a.htm";
						else if (cond == 3)
							htmltext = "30828-04.htm";
						else if (cond == 4)
							htmltext = "30828-05a.htm";
						else if (cond == 5)
							htmltext = "30828-06.htm";
						break;

					case SOPHYA:
						if (cond == 4 && st.getQuestItemsCount(MAP) >= 1)
							htmltext = "30735-05.htm";
						else if (cond == 5)
							htmltext = "30735-06a.htm";
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
		final QuestState st = player.getQuestState(qn);
		if (st == null)
			return null;

		if (st.getInt("cond") == 2)
			if (st.dropAlwaysQuestItems(MAP_PIECE, 1, 30))
				st.set("cond", "3");

		return null;
	}
}