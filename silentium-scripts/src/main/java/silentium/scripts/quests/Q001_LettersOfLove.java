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

public class Q001_LettersOfLove extends Quest implements ScriptFile {
	private static final String qn = "Q001_LettersOfLove";

	// Npcs
	private static final int DARIN = 30048;
	private static final int ROXXY = 30006;
	private static final int BAULRO = 30033;

	// Items
	private static final int DARINGS_LETTER = 687;
	private static final int RAPUNZELS_KERCHIEF = 688;
	private static final int DARINGS_RECEIPT = 1079;
	private static final int BAULROS_POTION = 1080;

	// Reward
	private static final int NECKLACE = 906;

	public Q001_LettersOfLove(final int scriptId, final String name, final String dname, final String path) {
		super(scriptId, name, dname, path);

		questItemIds = new int[] { DARINGS_LETTER, RAPUNZELS_KERCHIEF, DARINGS_RECEIPT, BAULROS_POTION };

		addStartNpc(DARIN);
		addTalkId(DARIN, ROXXY, BAULRO);
	}

	public static void onLoad() {
		new Q001_LettersOfLove(1, "Q001_LettersOfLove", "Letters Of Love", "quests");
	}

	@Override
	public String onAdvEvent(final String event, final L2Npc npc, final L2PcInstance player) {
		final String htmltext = event;
		final QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if ("30048-06.htm".equalsIgnoreCase(event)) {
			st.set("cond", "1");
			st.setState(QuestState.STARTED);
			st.giveItems(DARINGS_LETTER, 1);
			st.playSound(QuestState.SOUND_ACCEPT);
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
				if (player.getLevel() >= 2 && player.getLevel() <= 5)
					htmltext = "30048-02.htm";
				else {
					htmltext = "30048-01.htm";
					st.exitQuest(true);
				}
				break;

			case QuestState.STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getNpcId()) {
					case DARIN:
						if (cond == 1)
							htmltext = "30048-07.htm";
						else if (cond == 2 && st.getQuestItemsCount(RAPUNZELS_KERCHIEF) == 1) {
							htmltext = "30048-08.htm";
							st.takeItems(RAPUNZELS_KERCHIEF, 1);
							st.giveItems(DARINGS_RECEIPT, 1);
							st.set("cond", "3");
							st.playSound(QuestState.SOUND_MIDDLE);
						} else if (cond == 3)
							htmltext = "30048-09.htm";
						else if (cond == 4 && st.getQuestItemsCount(BAULROS_POTION) == 1) {
							htmltext = "30048-10.htm";
							st.takeItems(BAULROS_POTION, 1);
							st.giveItems(NECKLACE, 1);
							st.playSound(QuestState.SOUND_FINISH);
							st.exitQuest(false);
						}
						break;

					case ROXXY:
						if (cond == 1 && st.getQuestItemsCount(RAPUNZELS_KERCHIEF) == 0 && st.getQuestItemsCount(DARINGS_LETTER) > 0) {
							htmltext = "30006-01.htm";
							st.takeItems(DARINGS_LETTER, 1);
							st.giveItems(RAPUNZELS_KERCHIEF, 1);
							st.set("cond", "2");
							st.playSound(QuestState.SOUND_MIDDLE);
						} else if (cond == 2 && st.getQuestItemsCount(RAPUNZELS_KERCHIEF) > 0)
							htmltext = "30006-02.htm";
						else if (cond > 2 && (st.getQuestItemsCount(BAULROS_POTION) > 0 || st.getQuestItemsCount(DARINGS_RECEIPT) > 0))
							htmltext = "30006-03.htm";
						break;

					case BAULRO:
						if (cond == 3 && st.getQuestItemsCount(DARINGS_RECEIPT) == 1) {
							htmltext = "30033-01.htm";
							st.takeItems(DARINGS_RECEIPT, 1);
							st.giveItems(BAULROS_POTION, 1);
							st.set("cond", "4");
							st.playSound(QuestState.SOUND_MIDDLE);
						} else if (cond == 4)
							htmltext = "30033-02.htm";
						break;
				}
				break;

			case QuestState.COMPLETED:
				htmltext = Quest.getAlreadyCompletedMsg();
				break;
		}

		return htmltext;
	}
}