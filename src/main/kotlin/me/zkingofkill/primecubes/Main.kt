package me.zkingofkill.primecubes

import fr.minuskube.inv.InventoryManager
import me.zkingofkill.primecubes.command.CubeCommand
import me.zkingofkill.primecubes.cube.CubeProps
import me.zkingofkill.primecubes.database.Database
import me.zkingofkill.primecubes.database.SQLite
import me.zkingofkill.primecubes.listener.CubeListeners
import me.zkingofkill.primecubes.manager.CubeManager
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import utils.ConfigurationFile
import java.io.File


class Main : JavaPlugin() {

    companion object {
        lateinit var singleton: Main
    }

    lateinit var economy: Economy
    lateinit var inventoryManager: InventoryManager
    lateinit var messagesFile: ConfigurationFile
    lateinit var db: Database
    var shopGUIPlusHook: Boolean = false

    override fun onEnable() {
        singleton = this
        initConfig()

        this.db = SQLite(this)
        this.db.load()

        inventoryManager = InventoryManager(this)
        inventoryManager.init()
        if (Bukkit.getServer().pluginManager.getPlugin("Vault") != null) {
            val rsp =
                    Bukkit.getServer().servicesManager.getRegistration(Economy::class.java)
            economy = rsp.provider
        }

        if (Bukkit.getServer().pluginManager.getPlugin("ShopGUIPlus") != null) {
            shopGUIPlusHook = config.getBoolean("hookWithShopGuiPlus")
        }
        CubeManager.delayedSaveAll()
        CubeManager.init()

        server.scheduler.runTask(this) {
            CubeManager.list.addAll(this.db.loadCubes())
        }
        CubeProps.init()
        getCommand("cube").executor = CubeCommand()
        server.pluginManager.registerEvents(CubeListeners(), this)
    }

    override fun onDisable() {
        CubeManager.saveAll()
    }

    fun initConfig() {
        config.options().copyDefaults(true)
        val configFile = ConfigurationFile(this, "config.yml", "config.yml")
        configFile.options().copyDefaults(true)
        configFile.save()

        val cubesFolder = File(dataFolder, "cubes")
        if (!cubesFolder.exists()) {
            createCube("SmallCube", "SmallCube", 3, 3)
        }
        messagesFile = ConfigurationFile(this, "messages.yml", "messages.yml")
        messagesFile.options().copyDefaults(true)
        messagesFile.saveIfNotExists()
    }

    fun createCube(name: String, folderName: String, xz: Int = 3, y: Int = 3): Boolean {
        val file = File(dataFolder, "cubes/$folderName")
        return if (!file.exists()) {
            val cubesFile = ConfigurationFile(this, "cubes/$folderName/props.yml", "cubes/SmallCube/props.yml")
            cubesFile.options().copyDefaults(true)
            val id = if(CubeProps.list.isNotEmpty()) CubeProps.list.maxBy { it.typeId }!!.typeId+1 else 0
            val size = "$xz-$y"
            cubesFile.set("size", size)
            cubesFile.set("id", id)
            cubesFile.set("name", name)
            cubesFile.save()

            val upgFile = ConfigurationFile(this, "cubes/$folderName/upgrades.yml", "cubes/SmallCube/upgrades.yml")
            upgFile.options().copyDefaults(true)
            upgFile.save()

            val blksFile = ConfigurationFile(this, "cubes/$folderName/blocks.yml", "cubes/SmallCube/blocks.yml")
            blksFile.options().copyDefaults(true)
            blksFile.save()

            val mainGUI = ConfigurationFile(this, "cubes/$folderName/guis/main.yml",
                    "cubes/SmallCube/guis/main.yml")
            mainGUI.options().copyDefaults(true)
            mainGUI.save()

            val upgradesGUI = ConfigurationFile(this, "cubes/$folderName/guis/upgrades.yml",
                    "cubes/SmallCube/guis/upgrades.yml")
            upgradesGUI.options().copyDefaults(true)
            upgradesGUI.save()
            true
        } else false
    }

}