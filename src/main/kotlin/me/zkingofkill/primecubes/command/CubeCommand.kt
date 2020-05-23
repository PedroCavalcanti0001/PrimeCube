package me.zkingofkill.primecubes.command

import me.zkingofkill.primecubes.Main
import me.zkingofkill.primecubes.cube.CubeProps
import me.zkingofkill.primecubes.util.freeSlots
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import utils.ItemStackBuilder

class CubeCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender.hasPermission("primecubes.admin")) {
            if (args.isNotEmpty()) {
                if (args[0].equals("reload", true)) {
                    Main.singleton.initConfig()
                    CubeProps.init()
                    sender.sendMessage(Main.singleton.messagesFile.getString("reloadedConfiguration")
                            .replace("&", "§"))
                    return true
                }
                if (args[0].equals("create", true)) {
                    if (args.size >= 5) {
                        val folderName = args[2]
                        val name = args[1]
                        val xz = args[3]
                        val y = args[4]
                        if (folderName.matches("^[a-zA-Z]+$".toRegex())) {
                            if (xz.matches("^[1-9]\\d*\$".toRegex()) && y.matches("^[1-9]\\d*\$".toRegex())) {
                                val createCube = Main.singleton.createCube(name, folderName, xz.toInt(), y.toInt())
                                if (createCube) {
                                    CubeProps.init()
                                    sender.sendMessage(Main.singleton.messagesFile.getString("successfullyCreated")
                                            .replace("&", "§"))
                                } else {
                                    sender.sendMessage(Main.singleton.messagesFile.getString("thisCubeAlreadyExists")
                                            .replace("&", "§"))
                                }
                            } else {
                                sender.sendMessage(Main.singleton.messagesFile.getString("invalidWidthHeight")
                                        .replace("&", "§"))
                            }
                        } else {
                            sender.sendMessage(Main.singleton.messagesFile.getString("invalidFolderName")
                                    .replace("&", "§"))
                        }
                    } else {
                        sender.sendMessage("§c/${label} create <cubeName> <folderName> <xz> <y>")
                    }
                    return true
                }
                if (args[0].equals("give", true)) {
                    if (args.size >= 4) {
                        val target = Bukkit.getPlayer(args[1])
                        if (target != null) {
                            if (args[2].matches("^[0-9]\\d*\$".toRegex())) {
                                val typeId = args[2].toInt()
                                val cubeProps = CubeProps.byTypeId(typeId)
                                if (args[3].matches("^[0-9]\\d*\$".toRegex())) {
                                    if (cubeProps != null) {
                                        if (target.inventory.freeSlots(cubeProps.itemStack()) >= 1) {
                                            target.inventory.addItem(
                                                    ItemStackBuilder(cubeProps
                                                            .itemStack()
                                                            .clone())
                                                            .setAmount(args[3].toInt())
                                                            .build())
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
                                }else{
                                    sender.sendMessage(Main.singleton.messagesFile.getString("allowedOnlyInteger")
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
                        sender.sendMessage("§c/${label} give <player> <typeId> <amount>")
                    }
                }
            } else {
                sender.sendMessage("§c/${label} give <player> <typeId> <amount>")
                sender.sendMessage("§c/${label} reload")
                sender.sendMessage("§c/${label} create <cubeName> <folderName> <xz> <y>")
            }
        } else {
            sender.sendMessage(Main.singleton.messagesFile.getString("noPermission")
                    .replace("&", "§"))
        }
        return false
    }
}