/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package silentium.gameserver.scripting;

/**
 * Abstract class for classes that are meant to be implemented by scripts.<BR>
 * 
 * @author KenM
 */
public abstract class ManagedScript
{
	private final Class<? extends ScriptFile> _scriptFile;
	private long _lastLoadTime;
	private boolean _isActive;

	public ManagedScript()
	{
		_scriptFile = L2ScriptEngineManager.getInstance().getCurrentLoadingScript();
		setLastLoadTime(System.currentTimeMillis());
	}

	public void setActive(boolean status)
	{
		_isActive = status;
	}

	public boolean isActive()
	{
		return _isActive;
	}

	/**
	 * @return Returns the scriptFile.
	 */
	public Class<? extends ScriptFile> getScriptFile()
	{
		return _scriptFile;
	}

	/**
	 * @param lastLoadTime
	 *            The lastLoadTime to set.
	 */
	protected void setLastLoadTime(long lastLoadTime)
	{
		_lastLoadTime = lastLoadTime;
	}

	/**
	 * @return Returns the lastLoadTime.
	 */
	protected long getLastLoadTime()
	{
		return _lastLoadTime;
	}

	public abstract String getScriptName();

	public abstract ScriptManager<?> getScriptManager();
}