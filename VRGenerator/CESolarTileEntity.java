package VRGenerator;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class CESolarTileEntity extends CETileEntityGenerator implements IInventory,ISidedInventory
{
    public boolean sunIsVisible;
    public static Random randomizer = new Random();
    public int ticker;
    public boolean initialized;
	
    public ItemStack inventory[] = new ItemStack[1];
	//人工光ソーラー
	//発電レベル
	public int level;
	//出力EU/t
	public static int[] power = { 1, 8, 64, 512};
	//光レベル計算用
	private static int[] lightPower = { 1,2,4,8,16,32,64,128,256,512,1024 };

	public CESolarTileEntity(int lv)
	{
		super();
		level = lv;
		production = power[level];
	}

	public CESolarTileEntity()
	{
		this(0);
	}

	public void updateSunVisibility()
	{
		//真上のブロックが太陽光を浴びていれば（この判定がかなり重い）
		if(isSunVisible(worldObj, xCoord, yCoord + 1, zCoord))
		{
			//通常通り発電(ソーラーと同じ）
			production = power[level];
			sunIsVisible = true;
		}
		else
		{
			//内部インベントリを調べて
			if(inventory[0] != null)
			{
				double light = 0;
				Item item = inventory[0].getItem();
				int id = inventory[0].itemID;
				//溶岩なら1/8
				if(id == 10 || id == 11)
				{
					light = power[level] / 8.0d;
				}
				//光源15なら1/2
				else if(item instanceof ItemBlock && Block.lightValue[id] == 15)
				{
					light = power[level] / 2.0d;
				}
				//光源14なら1/4
				else if(item instanceof ItemBlock && Block.lightValue[id] == 14)
				{
					light = power[level] / 4.0d;
				}
				//光源13～7なら1/16
				else if(item instanceof ItemBlock && Block.lightValue[id] >= 7)
				{
					light = power[level] / 16.0d;
				}
				
				//トータル発電量がプラスなら
				if(light > 0.0d)
				{
					//最低1EU/t発電
					production = (light < 1.0d) ? 1: (int)light;
					sunIsVisible = true;
					return;
				}
			}
			sunIsVisible = false;
		}
	}

	public String getInvName()
	{
		return "CE Solar "+power;
	}

	public void readFromNBT(NBTTagCompound nbttagcompound)
	{
		super.readFromNBT(nbttagcompound);

		NBTTagList nbttaglist = nbttagcompound.getTagList("Items");
		inventory = new ItemStack[getSizeInventory()];

		for (int i = 0; i < nbttaglist.tagCount(); i++)
		{
			NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(i);
			byte byte0 = nbttagcompound1.getByte("Slot");

			if (byte0 >= 0 && byte0 < inventory.length)
			{
				inventory[byte0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
			}
		}

		level = nbttagcompound.getInteger("level");
		production = power[level];
	}

	public void writeToNBT(NBTTagCompound nbttagcompound)
	{
		super.writeToNBT(nbttagcompound);

		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < inventory.length; i++){
			if (inventory[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte)i);
				inventory[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}

		nbttagcompound.setTag("Items", nbttaglist);

		nbttagcompound.setInteger("level", level);
	}

	public String getGuiClassName(EntityPlayer entityplayer)
	{
		return "CESolarGui";
	}

	public String getGuiName()
	{
		return String.format("CE.block.Solar.%d.name", level);
	}

	public int tickRate()
	{
		return 128;
	}
    public static boolean isSunVisible(World world, int i, int j, int k)
    {
    	world.calculateInitialSkylight();
    	int light = world.getBlockLightValue(i, j, k);
        return light == 15;
    }
	//不要な処理を削除して軽量化
    public void updateEntity()
    {
    	super.updateEntity();
    	if(this.worldObj.getTotalWorldTime() % 80L == 0L)
    		this.updateSunVisibility();
        onInventoryChanged();
        if (ticksSinceLastActiveUpdate % 256 == 0)
        {
//            setActive(activityMeter > 0);
            activityMeter = 0;
        }
        activityMeter++;
        ticksSinceLastActiveUpdate++;
//        if(this.sunIsVisible)
//        	sendEnergy(getMaxEnergyOutput());
    }
	@Override
	public int getSizeInventory() {
		return this.inventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int var1) {
		return this.inventory[0];
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (inventory[i] != null){
			if (inventory[i].stackSize <= j){
				ItemStack itemstack = inventory[i];
				inventory[i] = null;
				return itemstack;
			}

			ItemStack itemstack1 = inventory[i].splitStack(j);

			if (inventory[i].stackSize == 0){
				inventory[i] = null;
			}

			return itemstack1;
		}else{
			return null;
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int var1) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int var1, ItemStack var2) {
		if(var1 < inventory.length){
			inventory[var1] = var2;

			if (var2 != null && var2.stackSize > getInventoryStackLimit()){
				var2.stackSize = getInventoryStackLimit();
			}
		}
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void onInventoryChanged() {}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1) {
		if (worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) != this){
			return false;
		}else{
			return var1.getDistance((double)xCoord + 0.5D, (double)yCoord + 0.5D, (double)zCoord + 0.5D) <= 64D;
		}
	}

	@Override
	public void openChest() {}

	@Override
	public void closeChest() {}

	@Override
	public boolean isInvNameLocalized() {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return false;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		return new int[]{0};
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		return itemstack.getItem() instanceof ItemBlock && Block.lightValue[itemstack.itemID] > 0;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return true;
	}

}
