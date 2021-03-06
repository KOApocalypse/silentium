/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package silentium.scripts.quests;

import silentium.commons.utils.Rnd;
import silentium.gameserver.configs.MainConfig;
import silentium.gameserver.model.actor.L2Npc;
import silentium.gameserver.model.actor.instance.L2PcInstance;
import silentium.gameserver.model.quest.Quest;
import silentium.gameserver.model.quest.QuestState;
import silentium.gameserver.scripting.ScriptFile;

public class Q431_WeddingMarch extends Quest implements ScriptFile {
	private static final String qn = "Q431_WeddingMarch";

	// NPC
	private static final int KANTABILON = 31042;

	// Item
	private static final int SILVER_CRYSTAL = 7540;

	// Reward
	private static final int WEDDING_ECHO_CRYSTAL = 7062;

	public Q431_WeddingMarch(final int scriptId, final String name, final String dname, final String path) {
		super(scriptId, name, dname, path);

		questItemIds = new int[] { SILVER_CRYSTAL };

		addStartNpc(KANTABILON);
		addTalkId(KANTABILON);

		addKillId(20786, 20787);
	}

	public static void onLoad() {
		new Q431_WeddingMarch(431, "Q431_WeddingMarch", "Wedding March", "quests");
	}

	@Override
	public String onAdvEvent(final String event, final L2Npc npc, final L2PcInstance player) {
		String htmltext = event;
		final QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if ("31042-02.htm".equalsIgnoreCase(event)) {
			st.set("cond", "1");
			st.setState(QuestState.STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
		} else if ("31042-05.htm".equalsIgnoreCase(event)) {
			if (st.getQuestItemsCount(SILVER_CRYSTAL) < 50)
				htmltext = "31042-03.htm";
			else {
				st.takeItems(SILVER_CRYSTAL, -1);
				st.giveItems(WEDDING_ECHO_CRYSTAL, 25);
				st.playSound(QuestState.SOUND_FINISH);
				st.exitQuest(true);
			}
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
				if (player.getLevel() >= 38 && player.getLevel() <= 43)
					htmltext = "31042-01.htm";
				else {
					htmltext = "31042-00.htm";
					st.exitQuest(true);
				}
				break;

			case QuestState.STARTED:
				final int cond = st.getInt("cond");
				if (cond == 1)
					htmltext = "31042-02.htm";
				else if (cond == 2) {
					htmltext = st.getQuestItemsCount(SILVER_CRYSTAL) < 50 ? "31042-03.htm" : "31042-04.htm";
				}
				break;
		}

		return htmltext;
	}

	@Override
	public String onKill(final L2Npc npc, final L2PcInstance player, final boolean isPet) {
		final L2PcInstance partyMember = getRandomPartyMember(player, npc, "1");
		if (partyMember == null)
			return null;

		final QuestState st = partyMember.getQuestState(qn);
		final int count = st.getQuestItemsCount(SILVER_CRYSTAL);

		if (st.getInt("cond") == 1 && count < 50) {
			int chance = (int) (100 * MainConfig.RATE_QUEST_DROP);
			int numItems = chance / 100;
			chance %= 100;

			if (Rnd.get(100) < chance)
				numItems++;

			if (numItems > 0) {
				if (count + numItems >= 50) {
					numItems = 50 - count;
					st.set("cond", "2");
					st.playSound(QuestState.SOUND_MIDDLE);
				} else
					st.playSound(QuestState.SOUND_ITEMGET);

				st.giveItems(SILVER_CRYSTAL, numItems);
			}
		}

		return null;
	}
}