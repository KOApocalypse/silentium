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

public class Q354_ConquestOfAlligatorIsland extends Quest implements ScriptFile {
	private static final String qn = "Q354_ConquestOfAlligatorIsland";

	// Items
	private static final int ALLIGATOR_TOOTH = 5863;
	private static final int TORN_MAP_FRAGMENT = 5864;
	private static final int PIRATES_TREASURE_MAP = 5915;

	// NPC
	private static final int KLUCK = 30895;

	public Q354_ConquestOfAlligatorIsland(final int scriptId, final String name, final String dname, final String path) {
		super(scriptId, name, dname, path);

		questItemIds = new int[] { ALLIGATOR_TOOTH, TORN_MAP_FRAGMENT };

		addStartNpc(KLUCK);
		addTalkId(KLUCK);

		addKillId(20804, 20805, 20806, 20807, 20808, 20991);
	}

	public static void onLoad() {
		new Q354_ConquestOfAlligatorIsland(354, "Q354_ConquestOfAlligatorIsland", "Conquest Of Alligator Island", "quests");
	}

	@Override
	public String onAdvEvent(final String event, final L2Npc npc, final L2PcInstance player) {
		String htmltext = event;
		final QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if ("30895-02.htm".equalsIgnoreCase(event)) {
			st.set("cond", "1");
			st.setState(QuestState.STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
		} else if ("30895-03.htm".equalsIgnoreCase(event)) {
			if (st.getQuestItemsCount(TORN_MAP_FRAGMENT) > 0)
				htmltext = "30895-03a.htm";
		} else if ("30895-05.htm".equalsIgnoreCase(event)) {
			final int amount = st.getQuestItemsCount(ALLIGATOR_TOOTH);
			if (amount > 0) {
				int reward = amount * 220 + 3100;
				if (amount >= 100) {
					reward += 7600;
					htmltext = "30895-05b.htm";
				}

				htmltext = "30895-05a.htm";
				st.takeItems(ALLIGATOR_TOOTH, -1);
				st.rewardItems(57, reward);
			}
		} else if ("30895-07.htm".equalsIgnoreCase(event)) {
			if (st.getQuestItemsCount(TORN_MAP_FRAGMENT) >= 10) {
				htmltext = "30895-08.htm";
				st.takeItems(TORN_MAP_FRAGMENT, 10);
				st.giveItems(PIRATES_TREASURE_MAP, 1);
				st.playSound(QuestState.SOUND_ITEMGET);
			}
		} else if ("30895-09.htm".equalsIgnoreCase(event)) {
			st.playSound(QuestState.SOUND_FINISH);
			st.exitQuest(true);
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
				htmltext = player.getLevel() >= 38 && player.getLevel() <= 49 ? "30895-01.htm" : "30895-00.htm";
				break;

			case QuestState.STARTED:
				htmltext = st.getQuestItemsCount(TORN_MAP_FRAGMENT) > 0 ? "30895-03a.htm" : "30895-03.htm";
				break;
		}

		return htmltext;
	}

	@Override
	public String onKill(final L2Npc npc, final L2PcInstance player, final boolean isPet) {
		final L2PcInstance partyMember = getRandomPartyMemberState(player, npc, QuestState.STARTED);
		if (partyMember == null)
			return null;

		final QuestState st = partyMember.getQuestState(qn);

		final int random = Rnd.get(100);
		if (random < 45) {
			st.giveItems(ALLIGATOR_TOOTH, 1);
			if (random < 10) {
				st.giveItems(TORN_MAP_FRAGMENT, 1);
				st.playSound(QuestState.SOUND_MIDDLE);
			} else
				st.playSound(QuestState.SOUND_ITEMGET);
		}

		return null;
	}
}