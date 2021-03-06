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

public class Q158_SeedOfEvil extends Quest implements ScriptFile {
	private static final String qn = "Q158_SeedOfEvil";

	// Item
	private static final int CLAY_TABLET = 1025;

	// NPC
	private static final int BIOTIN = 30031;

	// Mob
	private static final int NERKAS = 27016;

	// Reward
	private static final int ENCHANT_ARMOR_D = 956;

	public Q158_SeedOfEvil(final int scriptId, final String name, final String dname, final String path) {
		super(scriptId, name, dname, path);

		questItemIds = new int[] { CLAY_TABLET };

		addStartNpc(BIOTIN);
		addTalkId(BIOTIN);

		addKillId(NERKAS);
	}

	public static void onLoad() {
		new Q158_SeedOfEvil(158, "Q158_SeedOfEvil", "Seed Of Evil", "quests");
	}

	@Override
	public String onAdvEvent(final String event, final L2Npc npc, final L2PcInstance player) {
		final String htmltext = event;
		final QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if ("30031-04.htm".equalsIgnoreCase(event)) {
			st.set("cond", "1");
			st.setState(QuestState.STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
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
				if (player.getLevel() >= 21 && player.getLevel() <= 26)
					htmltext = "30031-03.htm";
				else {
					htmltext = "30031-02.htm";
					st.exitQuest(true);
				}
				break;

			case QuestState.STARTED:
				if (st.getQuestItemsCount(CLAY_TABLET) == 0)
					htmltext = "30031-05.htm";
				else {
					htmltext = "30031-06.htm";
					st.playSound(QuestState.SOUND_FINISH);
					st.takeItems(CLAY_TABLET, -1);
					st.giveItems(ENCHANT_ARMOR_D, 1);
					st.exitQuest(false);
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

		if (st.getInt("cond") == 1) {
			st.set("cond", "2");
			st.giveItems(CLAY_TABLET, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		}

		return null;
	}
}