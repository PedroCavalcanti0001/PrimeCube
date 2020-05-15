package me.zkingofkill.primecubes.command

import me.zkingofkill.primecubes.Main
import me.zkingofkill.primecubes.cube.CubeProps
import me.zkingofkill.primecubes.utils.freeSlots
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class CubeCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender.hasPermission("primecubes.admin")) {
            if (args.isNotEmpty()) {
                if (args[0].equals("give", true)) {
                    if (args.size >= 3) {
                        val target = Bukkit.getPlayer(args[1])
                        if (target != null) {
                            if (args[2].matches("^[0-9]\\d*\$".toRegex())) {
                                val typeId = args[2].toInt()
                                val cubeProps = CubeProps.byTypeId(typeId)
                                if (cubeProps != null) {
                                    if (target.inventory.freeSlots(cubeProps.itemStack()) >= 1) {
                                        target.inventory.addItem(cubeProps.itemStack())
                                        sender.sendMessage(Main.singleton.messagesFile.getString("successfullyGiven")
                                                .replace("&", "§")
                                                .replace("{player}", target.name))
                                    } else {
                                        sender.sendMessage(Main.singleton.messagesFile.getString("noInventorySpace")
                                                .replace("&", "§"))
                                    }
                                } else {
                                    sender.sendMessage(Main.singleton.messagesFile.getString("cubeNotFound")
                                            .replace("&", "§"))
                                }
                            } else {
                                sender.sendMessage(Main.singleton.messagesFile.getString("allowedOnlyInteger")
                                        .replace("&", "§"))
                            }
                        } else {
                            sender.sendMessage(Main.singleton.messagesFile.getString("playerNotFound")
                                    .replace("&", "§"))
                        }
                    } else {
                        sender.sendMessage("/${label} give <player> <typeId>")
                    }
                }
            } else {
                sender.sendMessage("§c/${label} give <player> <typeId>")
            }
        } else {
            sender.sendMessage(Main.singleton.messagesFile.getString("noPermission")
                    .replace("&", "§"))
        }
        return false
    }
}