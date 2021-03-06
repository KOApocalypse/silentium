/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package silentium.scripts.teleports;

import silentium.gameserver.model.actor.L2Npc;
import silentium.gameserver.model.actor.instance.L2PcInstance;
import silentium.gameserver.model.entity.sevensigns.SevenSigns;
import silentium.gameserver.model.quest.Quest;
import silentium.gameserver.scripting.ScriptFile;

/**
 * Spawn Gatekeepers at Lilith/Anakim deaths (after a 10sec delay).<BR>
 * Despawn them after 15 minutes.
 */
public class GatekeeperSpirit extends Quest implements ScriptFile {
	private static final int EnterGk = 31111;
	private static final int ExitGk = 31112;
	private static final int Lilith = 25283;
	private static final int Anakim = 25286;

	public static void onLoad() {
		new GatekeeperSpirit(-1, "GatekeeperSpirit", "Gatekeeper Spirit", "teleports");
	}

	public GatekeeperSpirit(final int scriptId, final String name, final String dname, final String path) {
		super(scriptId, name, dname, path);

		addStartNpc(EnterGk);
		addFirstTalkId(EnterGk);
		addTalkId(EnterGk);

		addKillId(Lilith, Anakim);
	}

	@Override
	public String onAdvEvent(final String event, final L2Npc npc, final L2PcInstance player) {
		if ("spawn_exitgk_lilith".equalsIgnoreCase(event)) {
			// exit_necropolis_boss_lilith
			addSpawn(ExitGk, 184410, -10111, -5488, 0, false, 900000);
		} else if ("spawn_exitgk_anakim".equalsIgnoreCase(event)) {
			// exit_necropolis_boss_anakim
			addSpawn(ExitGk, 184410, -13102, -5488, 0, false, 900000);
		}
		return super.onAdvEvent(event, npc, player);
	}

	@Override
	public String onFirstTalk(final L2Npc npc, final L2PcInstance player) {
		String htmltext = "";
		final int playerCabal = SevenSigns.getInstance().getPlayerCabal(player.getObjectId());
		final int sealAvariceOwner = SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_AVARICE);
		final int compWinner = SevenSigns.getInstance().getCabalHighestScore();

		if (playerCabal == sealAvariceOwner && playerCabal == compWinner) {
			switch (sealAvariceOwner) {
				case SevenSigns.CABAL_DAWN:
					htmltext = "dawn.htm";
					break;

				case SevenSigns.CABAL_DUSK:
					htmltext = "dusk.htm";
					break;

				case SevenSigns.CABAL_NULL:
					npc.showChatWindow(player);
					break;
			}
		} else
			npc.showChatWindow(player);

		return htmltext;
	}

	@Override
	public String onKill(final L2Npc npc, final L2PcInstance killer, final boolean isPet) {
		switch (npc.getNpcId()) {
			case Lilith:
				if (getQuestTimer("spawn_exitgk_lilith", null, null) == null)
					startQuestTimer("spawn_exitgk_lilith", 10000);
				break;

			case Anakim:
				if (getQuestTimer("spawn_exitgk_lilith", null, null) == null)
					startQuestTimer("spawn_exitgk_anakim", 10000);
				break;
		}
		return super.onKill(npc, killer, isPet);
	}
}