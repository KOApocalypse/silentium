/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package silentium.scripts.handlers.admin;

import javolution.text.TextBuilder;
import silentium.gameserver.data.html.StaticHtmPath;
import silentium.gameserver.handler.IAdminCommandHandler;
import silentium.gameserver.instancemanager.AuctionManager;
import silentium.gameserver.instancemanager.CastleManager;
import silentium.gameserver.instancemanager.ClanHallManager;
import silentium.gameserver.model.L2Clan;
import silentium.gameserver.model.L2Object;
import silentium.gameserver.model.actor.instance.L2PcInstance;
import silentium.gameserver.model.entity.Castle;
import silentium.gameserver.model.entity.ClanHall;
import silentium.gameserver.model.zone.type.L2ClanHallZone;
import silentium.gameserver.network.SystemMessageId;
import silentium.gameserver.network.clientpackets.Say2;
import silentium.gameserver.network.serverpackets.NpcHtmlMessage;
import silentium.gameserver.network.serverpackets.SystemMessage;
import silentium.gameserver.tables.ClanTable;

import java.util.StringTokenizer;

/**
 * This class handles all siege commands
 */
public class AdminSiege implements IAdminCommandHandler {
	private static final String[] ADMIN_COMMANDS = { "admin_siege", "admin_add_attacker", "admin_add_defender", "admin_add_guard", "admin_list_siege_clans", "admin_clear_siege_list", "admin_move_defenders", "admin_spawn_doors", "admin_endsiege", "admin_startsiege", "admin_setcastle", "admin_removecastle", "admin_clanhall", "admin_clanhallset", "admin_clanhalldel", "admin_clanhallopendoors", "admin_clanhallclosedoors", "admin_clanhallteleportself" };

	@Override
	public boolean useAdminCommand(String command, final L2PcInstance activeChar) {
		final StringTokenizer st = new StringTokenizer(command, " ");
		command = st.nextToken(); // Get actual command

		// Get castle
		Castle castle = null;
		ClanHall clanhall = null;

		if (command.startsWith("admin_clanhall"))
			clanhall = ClanHallManager.getInstance().getClanHallById(Integer.parseInt(st.nextToken()));
		else if (st.hasMoreTokens())
			castle = CastleManager.getInstance().getCastle(st.nextToken());

		if (clanhall == null && (castle == null || castle.getCastleId() < 0)) {
			showCastleSelectPage(activeChar);
			return true;
		}

		final L2Object target = activeChar.getTarget();
		L2PcInstance player = null;
		if (target instanceof L2PcInstance)
			player = (L2PcInstance) target;

		if (castle != null) {
			if ("admin_add_attacker".equalsIgnoreCase(command)) {
				if (player == null)
					activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
				else
					castle.getSiege().registerAttacker(player);
			} else if ("admin_add_defender".equalsIgnoreCase(command)) {
				if (player == null)
					activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
				else
					castle.getSiege().registerDefender(player);
			} else if ("admin_add_guard".equalsIgnoreCase(command)) {
				try {
					final int npcId = Integer.parseInt(st.nextToken());
					castle.getSiege().getSiegeGuardManager().addSiegeGuard(activeChar, npcId);
				} catch (Exception e) {
					activeChar.sendChatMessage(0, Say2.ALL, "SYS", "Usage: //add_guard npcId");
				}
			} else if ("admin_clear_siege_list".equalsIgnoreCase(command)) {
				castle.getSiege().clearSiegeClan();
			} else if ("admin_endsiege".equalsIgnoreCase(command)) {
				castle.getSiege().endSiege();
			} else if ("admin_list_siege_clans".equalsIgnoreCase(command)) {
				castle.getSiege().listRegisterClan(activeChar);
				return true;
			} else if ("admin_move_defenders".equalsIgnoreCase(command)) {
				activeChar.sendPacket(SystemMessage.sendString("Not implemented yet."));
			} else if ("admin_setcastle".equalsIgnoreCase(command)) {
				if (player == null || player.getClan() == null)
					activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
				else if (player.getClan().hasCastle())
					activeChar.sendChatMessage(0, Say2.ALL, "SYS", player.getName() + "'s clan already owns a castle.");
				else
					castle.setOwner(player.getClan());
			} else if ("admin_removecastle".equalsIgnoreCase(command)) {
				final L2Clan clan = ClanTable.getInstance().getClan(castle.getOwnerId());
				if (clan != null)
					castle.removeOwner(clan);
				else
					activeChar.sendChatMessage(0, Say2.ALL, "SYS", "Unable to remove castle for this clan.");
			} else if ("admin_spawn_doors".equalsIgnoreCase(command)) {
				castle.spawnDoor();
			} else if ("admin_startsiege".equalsIgnoreCase(command)) {
				castle.getSiege().startSiege();
			}
			showSiegePage(activeChar, castle.getName());
		} else if (clanhall != null) {
			if ("admin_clanhallset".equalsIgnoreCase(command)) {
				if (player == null || player.getClan() == null)
					activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
				else if (!ClanHallManager.getInstance().isFree(clanhall.getId()))
					activeChar.sendChatMessage(0, Say2.ALL, "SYS", "This ClanHall isn't free!");
				else if (!player.getClan().hasHideout()) {
					ClanHallManager.getInstance().setOwner(clanhall.getId(), player.getClan());
					if (AuctionManager.getInstance().getAuction(clanhall.getId()) != null)
						AuctionManager.getInstance().getAuction(clanhall.getId()).deleteAuctionFromDB();
				} else
					activeChar.sendChatMessage(0, Say2.ALL, "SYS", "You have already a ClanHall!");
			} else if ("admin_clanhalldel".equalsIgnoreCase(command)) {
				if (!ClanHallManager.getInstance().isFree(clanhall.getId())) {
					ClanHallManager.getInstance().setFree(clanhall.getId());
					AuctionManager.getInstance().initNPC(clanhall.getId());
				} else
					activeChar.sendChatMessage(0, Say2.ALL, "SYS", "This ClanHall is already Free!");
			} else if ("admin_clanhallopendoors".equalsIgnoreCase(command)) {
				clanhall.openCloseDoors(true);
			} else if ("admin_clanhallclosedoors".equalsIgnoreCase(command)) {
				clanhall.openCloseDoors(false);
			} else if ("admin_clanhallteleportself".equalsIgnoreCase(command)) {
				final L2ClanHallZone zone = clanhall.getZone();
				if (zone != null)
					activeChar.teleToLocation(zone.getSpawnLoc(), true);
			}
			showClanHallPage(activeChar, clanhall);
		}
		return true;
	}

	private static void showCastleSelectPage(final L2PcInstance activeChar) {
		int i = 0;
		final NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		adminReply.setFile(StaticHtmPath.AdminHtmPath + "castles.htm", activeChar);
		final TextBuilder cList = new TextBuilder();
		for (final Castle castle : CastleManager.getInstance().getCastles()) {
			if (castle != null) {
				final String name = castle.getName();
				cList.append("<td fixwidth=90><a action=\"bypass -h admin_siege ").append(name).append("\">").append(name).append("</a></td>");
				i++;
			}
			if (i > 2) {
				cList.append("</tr><tr>");
				i = 0;
			}
		}
		adminReply.replace("%castles%", cList.toString());
		cList.clear();
		i = 0;
		for (final ClanHall clanhall : ClanHallManager.getInstance().getClanHalls().values()) {
			if (clanhall != null) {
				cList.append("<td fixwidth=134><a action=\"bypass -h admin_clanhall ").append(clanhall.getId()).append("\">");
				cList.append(clanhall.getName()).append("</a></td>");
				i++;
			}
			if (i > 1) {
				cList.append("</tr><tr>");
				i = 0;
			}
		}
		adminReply.replace("%clanhalls%", cList.toString());
		cList.clear();
		i = 0;
		for (final ClanHall clanhall : ClanHallManager.getInstance().getFreeClanHalls().values()) {
			if (clanhall != null) {
				cList.append("<td fixwidth=134><a action=\"bypass -h admin_clanhall ").append(clanhall.getId()).append("\">");
				cList.append(clanhall.getName()).append("</a></td>");
				i++;
			}
			if (i > 1) {
				cList.append("</tr><tr>");
				i = 0;
			}
		}
		adminReply.replace("%freeclanhalls%", cList.toString());
		activeChar.sendPacket(adminReply);
	}

	private static void showSiegePage(final L2PcInstance activeChar, final String castleName) {
		final NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		adminReply.setFile(StaticHtmPath.AdminHtmPath + "castle.htm", activeChar);
		adminReply.replace("%castleName%", castleName);
		activeChar.sendPacket(adminReply);
	}

	private static void showClanHallPage(final L2PcInstance activeChar, final ClanHall clanhall) {
		final NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		adminReply.setFile(StaticHtmPath.AdminHtmPath + "clanhall.htm", activeChar);
		adminReply.replace("%clanhallName%", clanhall.getName());
		adminReply.replace("%clanhallId%", String.valueOf(clanhall.getId()));
		final L2Clan owner = ClanTable.getInstance().getClan(clanhall.getOwnerId());
		if (owner == null)
			adminReply.replace("%clanhallOwner%", "None");
		else
			adminReply.replace("%clanhallOwner%", owner.getName());
		activeChar.sendPacket(adminReply);
	}

	@Override
	public String[] getAdminCommandList() {
		return ADMIN_COMMANDS;
	}
}