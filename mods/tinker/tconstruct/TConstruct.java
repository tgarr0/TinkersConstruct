package mods.tinker.tconstruct;

import mods.tinker.tconstruct.library.TConstructRegistry;
import mods.tinker.tconstruct.library.TabTools;
import mods.tinker.tconstruct.player.TPlayerHandler;
import mods.tinker.tconstruct.village.TVillageTrades;
import mods.tinker.tconstruct.village.VillageSmelteryHandler;
import mods.tinker.tconstruct.village.VillageToolStationHandler;
import mods.tinker.tconstruct.worldgen.TBaseWorldGenerator;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;

/** TConstruct, the tool mod.
 * Craft your tools with style, then modify until the original is gone!
 * @author: mDiyo
 * @dependencies: IC2 API, EBXL API
 */

@Mod(modid = "TConstruct", name = "TConstruct", version = "1.5.1_1.3dev.16", dependencies = "required-after:Forge@[7.7.1.659,)")
@NetworkMod(serverSideRequired = false, clientSideRequired = true, channels = { "TConstruct" }, packetHandler = mods.tinker.tconstruct.TPacketHandler.class)
public class TConstruct
{
    /** The value of one ingot in millibuckets */
    public static final int ingotLiquidValue = 144;
    public static final int liquidUpdateAmount = 6;

    /* Instance of this mod, used for grabbing prototype fields */
    @Instance("TConstruct")
    public static TConstruct instance;
    /* Proxies for sides, used for graphics processing */
    @SidedProxy(clientSide = "mods.tinker.tconstruct.client.TProxyClient", serverSide = "mods.tinker.tconstruct.server.TProxyServer")
    public static TProxyCommon proxy;

    @PreInit
    public void preInit (FMLPreInitializationEvent evt)
    {
        PHConstruct.initProps();
        TConstructRegistry.materialTab = new TabTools("TConstructMaterials");
        TConstructRegistry.toolTab = new TabTools("TConstructTools");
        TConstructRegistry.blockTab = new TabTools("TConstructBlocks");
        content = new TContent();

        events = new TEventHandler();
        events.unfuxOreDictionary();
        MinecraftForge.EVENT_BUS.register(events);
        content.oreRegistry();

        proxy.registerRenderer();
        proxy.registerTickHandler();
        proxy.addNames();
        proxy.readManuals();
        //proxy.registerKeys();

        GameRegistry.registerWorldGenerator(new TBaseWorldGenerator());
        GameRegistry.registerFuelHandler(content);
        GameRegistry.registerCraftingHandler(new TCraftingHandler());
        NetworkRegistry.instance().registerGuiHandler(instance, new TGuiHandler());

        TVillageTrades trades = new TVillageTrades();
        VillagerRegistry.instance().registerVillagerType(78943, "/mods/tinker/textures/mob/villagertools.png");
        VillagerRegistry.instance().registerVillageTradeHandler(78943, trades);
        VillagerRegistry.instance().registerVillageCreationHandler(new VillageToolStationHandler());
        VillagerRegistry.instance().registerVillageCreationHandler(new VillageSmelteryHandler());
        /*VillagerRegistry.instance().registerVillagerType(78944, "/mods/tinker/textures/mob/villagersmeltery.png");
        VillagerRegistry.instance().registerVillageTradeHandler(78944, trades);*/

        //DimensionManager.unregisterProviderType(0);
        //DimensionManager.registerProviderType(0, OverworldProvider.class, true);
    }

    @PostInit
    public void postInit (FMLPostInitializationEvent evt)
    {
        playerTracker = new TPlayerHandler();
        GameRegistry.registerPlayerTracker(playerTracker);
        MinecraftForge.EVENT_BUS.register(playerTracker);

        content.modIntegration();
    }
    
    public static TEventHandler events;
    public static TPlayerHandler playerTracker;
    public static TContent content;
}