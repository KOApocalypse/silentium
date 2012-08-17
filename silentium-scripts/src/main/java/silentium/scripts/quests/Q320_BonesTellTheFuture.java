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
import silentium.gameserver.model.base.Race;
import silentium.gameserver.model.quest.Quest;
import silentium.gameserver.model.quest.QuestState;
import silentium.gameserver.scripting.ScriptFile;

public class Q320_BonesTellTheFuture extends Quest implements ScriptFile
{
	private final static String qn = "Q320_BonesTellTheFuture";

	// Quest item
	private final int BONE_FRAGMENT = 809;

	public Q320_BonesTellTheFuture(int questId, String name, String descr)
	{
		super(questId, name, descr);

		questItemIds = new int[] { BONE_FRAGMENT };

		addStartNpc(30359);
		addTalkId(30359);

		addKillId(20517, 20518, 20022, 20455);
	}

	public static void onLoad()
	{
		new Q320_BonesTellTheFuture(320, "Q320_BonesTellTheFuture", "Bones Tell the Future");
	}

	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if (event.equalsIgnoreCase("30359-04.htm"))
		{
			st.set("cond", "1");
			st.setState(QuestState.STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
		}

		return event;
	}

	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		QuestState st = player.getQuestState(qn);
		String htmltext = getNoQuestMsg();
		if (st == null)
			return htmltext;

		switch (st.getState())
		{
			case QuestState.CREATED:
				if (st.getPlayer().getRace() != Race.DarkElf)
				{
					htmltext = "30359-00.htm";
					st.exitQuest(true);
				}
				else if (player.getLevel() >= 10 && player.getLevel() <= 18)
					htmltext = "30359-03.htm";
				else
				{
					htmltext = "30359-02.htm";
					st.exitQuest(true);
				}
				break;

			case QuestState.STARTED:
				if (st.getQuestItemsCount(BONE_FRAGMENT) < 10)
					htmltext = "30359-05.htm";
				else
				{
					htmltext = "30359-06.htm";
					st.takeItems(BONE_FRAGMENT, -1);
					st.rewardItems(57, 8470);
					st.playSound(QuestState.SOUND_FINISH);
					st.exitQuest(true);
				}
				break;
		}

		return htmltext;
	}

	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return null;

		if (st.getInt("cond") == 1)
			if (st.dropQuestItems(BONE_FRAGMENT, 1, 10, 200000))
				st.set("cond", "2");

		return null;
	}
}