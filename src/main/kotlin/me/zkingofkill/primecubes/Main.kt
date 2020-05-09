package me.zkingofkill.primecubes

import ConfigurationFile
import fr.minuskube.inv.InventoryManager
import org.bukkit.material.Command
import org.bukkit.plugin.java.JavaPlugin


class Main : JavaPlugin() {

    companion object {
        lateinit var singleton: Main
    }

    lateinit var inventoryManager: InventoryManager

    //lateinit var mysql: Mysql
    lateinit var upgradesFile: ConfigurationFile
    lateinit var messagesFile: ConfigurationFile
    lateinit var cubesFile: ConfigurationFile


    override fun onEnable() {
        singleton = this
        initConfig()

        inventoryManager = InventoryManager(this)
        inventoryManager.init()

        /* mysql = Mysql()
         mysql.init()

         User.delayedSaveAll()

         */

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