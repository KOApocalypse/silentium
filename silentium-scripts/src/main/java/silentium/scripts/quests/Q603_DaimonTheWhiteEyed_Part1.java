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

public class Q603_DaimonTheWhiteEyed_Part1 extends Quest implements ScriptFile {
	private static final String qn = "Q603_DaimonTheWhiteEyed_Part1";

	// NPCs
	private static final int EYE_OF_ARGOS = 31683;
	private static final int MYSTERIOUS_TABLET_1 = 31548;
	private static final int MYSTERIOUS_TABLET_2 = 31549;
	private static final int MYSTERIOUS_TABLET_3 = 31550;
	private static final int MYSTERIOUS_TABLET_4 = 31551;
	private static final int MYSTERIOUS_TABLET_5 = 31552;

	// Monsters
	private static final int CANYON_BANDERSNATCH_SLAVE = 21297;
	private static final int BUFFALO_SLAVE = 21299;
	private static final int GRENDEL_SLAVE = 21304;

	// Items
	private static final int EVIL_SPIRIT_BEADS = 7190;
	private static final int BROKEN_CRYSTAL = 7191;
	private static final int UNFINISHED_SUMMON_CRYSTAL = 7192;

	public Q603_DaimonTheWhiteEyed_Part1(final int scriptId, final String name, final String dname, final String path) {
		super(scriptId, name, dname, path);

		questItemIds = new int[] { EVIL_SPIRIT_BEADS, BROKEN_CRYSTAL };

		addStartNpc(EYE_OF_ARGOS);
		addTalkId(EYE_OF_ARGOS, MYSTERIOUS_TABLET_1, MYSTERIOUS_TABLET_2, MYSTERIOUS_TABLET_3, MYSTERIOUS_TABLET_4, MYSTERIOUS_TABLET_5);

		addKillId(BUFFALO_SLAVE, GRENDEL_SLAVE, CANYON_BANDERSNATCH_SLAVE);
	}

	public static void onLoad() {
		new Q603_DaimonTheWhiteEyed_Part1(603, "Q603_DaimonTheWhiteEyed_Part1", "Daimon The White Eyed 1", "quests");
	}

	@Override
	public String onAdvEvent(final String event, final L2Npc npc, final L2PcInstance player) {
		String htmltext = event;
		final QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		// Eye of Argos
		if ("31683-03.htm".equalsIgnoreCase(event)) {
			st.set("cond", "1");
			st.setState(QuestState.STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
		} else if ("31683-06.htm".equalsIgnoreCase(event)) {
			if (st.getQuestItemsCount(BROKEN_CRYSTAL) > 4) {
				st.set("cond", "7");
				st.takeItems(BROKEN_CRYSTAL, -1);
				st.playSound(QuestState.SOUND_MIDDLE);
			} else
				htmltext = "31683-07.htm";
		} else if ("31683-10.htm".equalsIgnoreCase(event)) {
			if (st.getQuestItemsCount(EVIL_SPIRIT_BEADS) > 199) {
				st.takeItems(EVIL_SPIRIT_BEADS, -1);
				st.giveItems(UNFINISHED_SUMMON_CRYSTAL, 1);
				st.playSound(QuestState.SOUND_FINISH);
				st.exitQuest(true);
			} else {
				st.set("cond", "7");
				htmltext = "31683-11.htm";
			}
		}
		// Mysterious tablets
		else if ("31548-02.htm".equalsIgnoreCase(event)) {
			st.set("cond", "2");
			st.giveItems(BROKEN_CRYSTAL, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		} else if ("31549-02.htm".equalsIgnoreCase(event)) {
			st.set("cond", "3");
			st.giveItems(BROKEN_CRYSTAL, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		} else if ("31550-02.htm".equalsIgnoreCase(event)) {
			st.set("cond", "4");
			st.giveItems(BROKEN_CRYSTAL, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		} else if ("31551-02.htm".equalsIgnoreCase(event)) {
			st.set("cond", "5");
			st.giveItems(BROKEN_CRYSTAL, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		} else if ("31552-02.htm".equalsIgnoreCase(event)) {
			st.set("cond", "6");
			st.giveItems(BROKEN_CRYSTAL, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
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
				if (player.getLevel() < 73 || player.getLevel() > 78) {
					htmltext = "31683-02.htm";
					st.exitQuest(true);
				} else
					htmltext = "31683-01.htm";
				break;

			case QuestState.STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getNpcId()) {
					case EYE_OF_ARGOS:
						if (cond >= 1 && cond <= 5)
							htmltext = "31683-04.htm";
						else if (cond == 6)
							htmltext = "31683-05.htm";
						else if (cond == 7)
							htmltext = "31683-08.htm";
						else if (cond == 8)
							htmltext = "31683-09.htm";
						break;

					case MYSTERIOUS_TABLET_1:
						if (cond == 1)
							htmltext = "31548-01.htm";
						else if (cond >= 2)
							htmltext = "31548-03.htm";
						break;

					case MYSTERIOUS_TABLET_2:
						if (cond == 2)
							htmltext = "31549-01.htm";
						else if (cond >= 3)
							htmltext = "31549-03.htm";
						break;

					case MYSTERIOUS_TABLET_3:
						if (cond == 3)
							htmltext = "31550-01.htm";
						else if (cond >= 4)
							htmltext = "31550-03.htm";
						break;

					case MYSTERIOUS_TABLET_4:
						if (cond == 4)
							htmltext = "31551-01.htm";
						else if (cond >= 5)
							htmltext = "31551-03.htm";
						break;

					case MYSTERIOUS_TABLET_5:
						if (cond == 5)
							htmltext = "31552-01.htm";
						else if (cond >= 6)
							htmltext = "31552-03.htm";
						break;
				}
				break;
		}

		return htmltext;
	}

	@Override
	public String onKill(final L2Npc npc, final L2PcInstance player, final boolean isPet) {
		final L2PcInstance partyMember = getRandomPartyMember(player, npc, "7");
		if (partyMember == null)
			return null;

		final QuestState st = partyMember.getQuestState(qn);

		if (st.dropAlwaysQuestItems(EVIL_SPIRIT_BEADS, 1, 200))
			st.set("cond", "8");

		return null;
	}
}