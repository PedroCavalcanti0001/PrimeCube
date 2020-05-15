package me.zkingofkill.primecubes

import fr.minuskube.inv.InventoryManager
import me.arcaniax.hdb.api.HeadDatabaseAPI
import me.zkingofkill.primecubes.command.CubeCommand
import me.zkingofkill.primecubes.listener.CubeListeners
import me.zkingofkill.primecubes.manager.CubeManager
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import utils.ConfigurationFile


class Main : JavaPlugin() {

    companion object {
        lateinit var singleton: Main
    }

    lateinit var economy: Economy
    lateinit var inventoryManager: InventoryManager
    //lateinit var mysql: Mysql
    lateinit var upgradesFile: ConfigurationFile
    lateinit var messagesFile: ConfigurationFile
    lateinit var cubesFile: ConfigurationFile
    lateinit var headDatabaseAPI: HeadDatabaseAPI


    override fun onEnable() {
        singleton = this
        initConfig()

        inventoryManager = InventoryManager(this)
        inventoryManager.init()
        headDatabaseAPI = HeadDatabaseAPI()

        /* mysql = Mysql()
         mysql.init()

         User.delayedSaveAll()

         */
        if (Bukkit.getServer().pluginManager.getPlugin("Vault") != null) {
            val rsp =
                    Bukkit.getServer().servicesManager.getRegistration(Economy::class.java)
            economy = rsp.provider
        }
        CubeManager.init()

        getCommand("cube").executor = CubeCommand()
        server.pluginManager.registerEvents(CubeListeners(), this)
    }

    override fun onDisable() {

    }

    fun initConfig() {
        config.options().copyDefaults(true)
        val configFile = ConfigurationFile(this, "config.yml", "config.yml")
        configFile.options().copyDefaults(true)
        configFile.save()

        upgradesFile = ConfigurationFile(this, "upgrades.yml", "upgrades.yml")
        upgradesFile.options().copyDefaults(true)
        upgradesFile.saveIfNotExists()

        cubesFile = ConfigurationFile(this, "cubes.yml", "cubes.yml")
        cubesFile.options().copyDefaults(true)
        cubesFile.saveIfNotExists()

        messagesFile = ConfigurationFile(this, "messages.yml", "messages.yml")
        messagesFile.options().copyDefaults(true)
        messagesFile.saveIfNotExists()
    }

}