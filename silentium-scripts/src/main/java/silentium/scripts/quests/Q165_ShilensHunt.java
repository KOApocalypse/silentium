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

public class Q165_ShilensHunt extends Quest implements ScriptFile {
	private static final String qn = "Q165_ShilensHunt";

	// Items
	private static final int DARK_BEZOAR = 1160;
	private static final int LESSER_HEALING_POTION = 1060;

	// NPC
	private static final int NELSYA = 30348;

	public Q165_ShilensHunt(final int scriptId, final String name, final String dname, final String path) {
		super(scriptId, name, dname, path);

		questItemIds = new int[] { DARK_BEZOAR };

		addStartNpc(NELSYA);
		addTalkId(NELSYA);

		addKillId(20456, 20529, 20532, 20536);
	}

	public static void onLoad() {
		new Q165_ShilensHunt(165, "Q165_ShilensHunt", "Shilens Hunt", "quests");
	}

	@Override
	public String onAdvEvent(final String event, final L2Npc npc, final L2PcInstance player) {
		final String htmltext = event;
		final QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if ("30348-03.htm".equalsIgnoreCase(event)) {
			st.set("cond", "1");
			st.setState(QuestState.STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
		}

		return htmltext;
	}

	@Override
	public String onTalk(final L2Npc npc, final L2PcInstance player) {
		String htmltext = Quest.getNoQuestMsg();
		final QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		switch (st.getState()) {
			case QuestState.CREATED:
				if (player.getRace().ordinal() == 2) {
					if (player.getLevel() >= 3 && player.getLevel() <= 7)
						htmltext = "30348-02.htm";
					else {
						htmltext = "30348-01.htm";
						st.exitQuest(true);
					}
				} else {
					htmltext = "30348-00.htm";
					st.exitQuest(true);
				}
				break;

			case QuestState.STARTED:
				if (st.getQuestItemsCount(DARK_BEZOAR) >= 13) {
					htmltext = "30348-05.htm";
					st.takeItems(DARK_BEZOAR, -1);
					st.rewardItems(LESSER_HEALING_POTION, 5);
					st.addExpAndSp(1000, 0);
					st.exitQuest(false);
					st.playSound(QuestState.SOUND_FINISH);
				} else
					htmltext = "30348-04.htm";
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

		if (st.getInt("cond") == 1)
			if (st.dropQuestItems(DARK_BEZOAR, 1, 13, 200000))
				st.set("cond", "2");

		return null;
	}
}