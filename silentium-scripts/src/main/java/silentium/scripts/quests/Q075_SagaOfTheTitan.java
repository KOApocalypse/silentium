/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package silentium.scripts.quests;

/**
 * @author Emperorc
 */
public class Q075_SagaOfTheTitan extends SagasSuperClass {
	private static final String name = "Q075_SagaOfTheTitan";
	private static final int scriptId = 75;
	private static final String dname = "Saga Of The Titan";
	private static final String path = "quests";

	public Q075_SagaOfTheTitan() {
		super(scriptId, name, dname, path);

		NPC = new int[] { 31327, 31624, 31289, 31290, 31607, 31646, 31649, 31651, 31654, 31655, 31658, 31290 };

		Items = new int[] { 7080, 7539, 7081, 7490, 7273, 7304, 7335, 7366, 7397, 7428, 7098, 0 };

		Mob = new int[] { 27292, 27224, 27283 };

		qn = name;
		classid = 113;
		prevclass = 0x2e;

		X = new int[] { 119518, 181215, 181227 };

		Y = new int[] { -28658, 36676, 36703 };

		Z = new int[] { -3811, -4812, -4816 };

		registerNPCs();
	}
}