/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package silentium.scripts.vmasters;

import silentium.gameserver.model.actor.L2Npc;
import silentium.gameserver.model.actor.instance.L2PcInstance;

public class ElvenHumanFighters1 extends OccupationEngine {
	public ElvenHumanFighters1(final int scriptId, final String name, final String dname, final String path) {
		super(scriptId, name, dname, path);

		for (final int i : new int[] { 30066, 30288, 30373 }) {
			addStartNpc(i);
			addTalkId(i);
		}
	}

	public static void onLoad() {
		new ElvenHumanFighters1(-1, "ElvenHumanFighters1", "Elven Human Fighters 1", "vmasters");
	}

	@Override
	public String onTalk(final L2Npc npc, final L2PcInstance player) {
		if (player.isSubClassActive())
			return null;
		if (player.getRace().ordinal() == 0 || player.getRace().ordinal() == 1) {
			if (player.getClassId().level() == 1) // first occupation change already made
				return npc.getNpcId() + "-38.htm";
			else if (player.getClassId().level() >= 2) // second/third occupation change already made
				return npc.getNpcId() + "-39.htm";
			else if (player.getClassId().getId() == 18) // elven fighter
				return npc.getNpcId() + "-01.htm";
			else return player.getClassId().getId() == 0 ? npc.getNpcId() + "-08.htm" : npc.getNpcId() + "-40.htm";
		} else
			return npc.getNpcId() + "-40.htm"; // other races
	}
}