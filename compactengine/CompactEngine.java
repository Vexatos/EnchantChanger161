package compactengine;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.util.ChatMessageComponent;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.fluids.Fluid;
import buildcraft.BuildCraftCore;
import buildcraft.BuildCraftEnergy;
import buildcraft.BuildCraftSilicon;
import buildcraft.BuildCraftTransport;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid="CompactEngine", name="CompactEngine", version="build 5(for mc1.6.2-1.6.4  bc4.2.1  Forge#965 )", dependencies ="required-after:BuildCraft|Energy")
@NetworkMod(clientSideRequired=true, serverSideRequired=false)
public class CompactEngine
{
	@Instance("CompactEngine")
	public static CompactEngine instance;
	@SidedProxy(clientSide = "compactengine.Client.ClientProxy", serverSide = "compactengine.CommonProxy")
	public static CommonProxy proxy;

	public static BlockCompactEngine engineBlock;
	public static ItemBlock engineItem;
	public static Item energyChecker;
	private static Fluid buildcraftFluidOil;

	public static int blockID_CompactEngine;
	public static int itemID_energyChecker;
	public static boolean isAddCompactEngine512and2048;
	public static int CompactEngineExplosionPowerLevel;
	public static int CompactEngineExplosionTimeLevel;
	public static int CompactEngineExplosionAlertMinute;
	public static boolean neverExplosion;
//	public static int OilFlowingSpeed;
	
	public static ItemStack engine1;
	public static ItemStack engine2;
	public static ItemStack engine3;
	public static ItemStack engine4;
//	public static ItemStack engine5 = new ItemStack(engineBlock, 1, 4);
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		blockID_CompactEngine = config.get(Configuration.CATEGORY_BLOCK, "CompactEngineId", 1529).getInt();
		itemID_energyChecker = config.get(Configuration.CATEGORY_ITEM, "EnergyCheckerId", 19500).getInt();
		isAddCompactEngine512and2048 = config.get(Configuration.CATEGORY_GENERAL, "Add high compact engine", false,"add Engine is x512 (Note explosion)").getBoolean(false);
		CompactEngineExplosionPowerLevel = config.get(Configuration.CATEGORY_GENERAL, "CompactEngineExplosionPowerLevel", 1,"min=0, max=3").getInt();
		CompactEngineExplosionTimeLevel = config.get(Configuration.CATEGORY_GENERAL, "CompactEngineExplosionTimeLevel", 1,"min=0, max=3").getInt();
		CompactEngineExplosionAlertMinute = config.get(Configuration.CATEGORY_GENERAL, "CompactEngineExplosionAlertMinute", 3,"0 is not alert display, min=0.0D, max=30.0D").getInt();
		neverExplosion = config.get(Configuration.CATEGORY_GENERAL, "neverExplosion", false, "Engine No Explosion").getBoolean(false);
//		OilFlowingSpeed = config.get(Configuration.CATEGORY_GENERAL, "OilFlowingSpeed", 20, "Change OilFlowingSpeed. Default:20tick").getInt();
		config.save();
		engineBlock =new BlockCompactEngine(blockID_CompactEngine);	
		GameRegistry.registerBlock(engineBlock, "compactengineblock");
		engineItem  = new ItemCompactEngine(blockID_CompactEngine - 256);		
		GameRegistry.registerItem(engineItem, "compactengineitem", "CompactEngine");
		energyChecker = new ItemEnergyChecker(itemID_energyChecker - 256).setUnlocalizedName("compactengine:energyChecker").setTextureName("compactengine:energyChecker");
		GameRegistry.registerItem(energyChecker, "energychecker", "CompactEngine");
	}
	@Mod.EventHandler
	public void load(FMLInitializationEvent event)
	{
//		Block.blocksList[BuildCraftEnergy.fluidOil.getBlockID()] = null;
//		oilMoving = (new BlockOilFlowing2(BuildCraftEnergy.fluidOil.getBlockID(), Material.water));
//		buildcraftFluidOil = new Fluid("oil");
//		ObfuscationReflectionHelper.setPrivateValue(BuildCraftEnergy.class, BuildCraftEnergy.instance, buildcraftFluidOil, 6);
//		BuildCraftEnergy.fluidOil = FluidRegistry.getFluid("oil");
//		BuildCraftEnergy.fluidOil.setBlockID(BuildCraftEnergy.blockOil);
		engine1 = new ItemStack(engineBlock, 1, 0);
		engine2 = new ItemStack(engineBlock, 1, 1);
		engine3 = new ItemStack(engineBlock, 1, 2);
		engine4 = new ItemStack(engineBlock, 1, 3);
		proxy.registerTileEntitySpecialRenderer();
		GameRegistry.registerTileEntity(TileCompactEngine8.class, "tile.compactengine8");
		GameRegistry.registerTileEntity(TileCompactEngine32.class, "tile.compactengine32");
		GameRegistry.registerTileEntity(TileCompactEngine128.class, "tile.compactengine128");
		GameRegistry.registerTileEntity(TileCompactEngine512.class, "tile.compactengine512");

		ItemStack woodEngine = new ItemStack(BuildCraftEnergy.engineBlock, 1, 0);
		ItemStack ironEngine = new ItemStack(BuildCraftEnergy.engineBlock, 1, 2);
		ItemStack ironGear = new ItemStack(BuildCraftCore.ironGearItem);
		ItemStack diaGear = new ItemStack(BuildCraftCore.diamondGearItem);
		ItemStack diaChip = new ItemStack(BuildCraftSilicon.redstoneChipset, 1, 3);
		ItemStack goldORGate = new ItemStack(BuildCraftTransport.pipeGate, 1, 4);
		ItemStack diaORGate = new ItemStack(BuildCraftTransport.pipeGate, 1, 6);

		GameRegistry.addRecipe(engine1, new Object[]{"www", "wgw", "www", 'w', woodEngine, 'g', ironGear});
		GameRegistry.addRecipe(engine2, new Object[]{"geg", "eie", "geg", 'e', engine1, 'g', diaGear, 'i', ironEngine});
		GameRegistry.addRecipe(engine3, new Object[]{"geg", "eie", "geg", 'e', engine2, 'g', diaChip, 'i', ironEngine});

		if(isAddCompactEngine512and2048)
		{
			GameRegistry.addRecipe(engine4, new Object[]{"geg", "eie", "geg", 'e', engine3, 'g', goldORGate, 'i', ironEngine});
//			GameRegistry.addRecipe(engine5, new Object[]{"geg", "eie", "geg", 'e', engine4, 'g', diaORGate, 'i', ironEngine});
		}
		GameRegistry.addRecipe(new ItemStack(energyChecker), new Object[]{"w", "i",
			'w', BuildCraftTransport.pipePowerWood, 'i', Item.ingotIron});
	}
	public static void addChat(String message)
	{

		if(FMLCommonHandler.instance().getEffectiveSide().isClient())
		{
			Minecraft mc = Minecraft.getMinecraft();
			mc.ingameGUI.getChatGUI().printChatMessage(message);
		}
		else if(FMLCommonHandler.instance().getEffectiveSide().isServer())
		{
			PacketDispatcher.sendPacketToAllPlayers(new Packet3Chat(ChatMessageComponent.createFromText(message)));
		}
	}

	public static void addChat(String format,Object... args)
	{	
		addChat(String.format(format,args));
	}
}