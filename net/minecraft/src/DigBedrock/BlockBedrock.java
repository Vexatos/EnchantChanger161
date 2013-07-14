package net.minecraft.src.DigBedrock;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class BlockBedrock extends Block
{
	public BlockBedrock(int par1)
	{
		super(par1, Material.rock);
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.func_111022_d("bedrock");
	}

	public void onBlockClicked(World world, int i, int j, int k, EntityPlayer entityplayer)
	{
		if(j==0)
		{
			setBlockUnbreakable();
			return;
		}
		if(j>40)
		{
			setHardness(5.0F);
			return;
		}
		else{
			setHardness(200.0F / j);
			return;
		}
	}

//ダイヤピッケルでのみ岩盤を採掘可能に設定
	public float getPlayerRelativeBlockHardness(EntityPlayer par1EntityPlayer, World par2World, int par3, int par4, int par5)
	{
		if(par1EntityPlayer.getCurrentEquippedItem() != null && par1EntityPlayer.getCurrentEquippedItem().itemID == Item.pickaxeDiamond.itemID)
		{
			return super.getPlayerRelativeBlockHardness(par1EntityPlayer, par2World, par3, par4, par5);
		}
		return 0.0F;
	}

}
