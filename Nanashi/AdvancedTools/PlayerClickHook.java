package Nanashi.AdvancedTools;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

public class PlayerClickHook
{
	@ForgeSubscribe
	public void clickEvent(PlayerInteractEvent event)
	{
		EntityPlayer player = event.entityPlayer;
		ItemStack holdItem = event.entityPlayer.getCurrentEquippedItem();
		if(event.action == Action.LEFT_CLICK_BLOCK &&holdItem != null && holdItem.getItem() instanceof ItemUGTool){
			ItemUGTool ugTool = (ItemUGTool) holdItem.getItem();
			ugTool.side = event.face;
		}
	}
}