package CaveStory;

import java.io.DataOutputStream;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class LivingEventHooks
{
	private int CanBoost = CaveStory.BoostPower;
	private boolean boosterSwitch = CaveStory.BoosterDefaultSwitch;
	private boolean toggle = false;
	private boolean spawnCloud = false;
	@ForgeSubscribe
	public void LivingUpdate(LivingUpdateEvent event)
	{
		if(event.entityLiving != null && event.entityLiving instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) event.entityLiving;

			if(player.worldObj.isRemote)
			{
				if(toggle && !BoosterKeyHandler.boosterKeydown)
					boostKeyCheck(player);
				toggle = BoosterKeyHandler.boosterKeydown;
			}
			boost(player, player.worldObj);
		}
	}
	@SideOnly(Side.CLIENT)
	public void boostKeyCheck(EntityPlayer player)
	{
		boosterSwitch =!boosterSwitch;
//		boosterSwitch = BoosterKeyHandler.boosterKeydown;
		String switchdata="";
		if(boosterSwitch)
		{
			switchdata ="ON";
		}
		else
		{
			switchdata ="OFF";
		}
		player.addChatMessage("BoosterSwitch-" + switchdata);
		PacketDispatcher.sendPacketToServer(PacketHandler.getPacket(this));
	}
	public void boost(EntityPlayer ep, World world)
	{
		if(ep.inventory.armorInventory[2]!=null || CaveStory.Alwaysflying)
		{
			if(!ep.onGround && boosterSwitch)
			{
				boolean canjump = false;
				try {
					canjump =(Integer)ObfuscationReflectionHelper.getPrivateValue(EntityLivingBase.class, ep, 56)==0;
				} catch (Exception e) {
					e.printStackTrace();
				}

				if(CanBoost > 0 || CaveStory.Alwaysflying)
				{
					if(world.isRemote)
					{
						EntityPlayerSP epsp = (EntityPlayerSP) ep;
						this.spawnCloud = false;
						if(CaveStory.Alwaysflying ||ep.inventory.armorInventory[2].itemID == CaveStory.CaveStoryID+1)
						{
							if(epsp.movementInput.moveForward > 0)
							{
								float f1 = ep.rotationYaw * 0.01745329F;
								ep.motionY=ep.motionX=ep.motionZ =0;
								ep.motionX -= MathHelper.sin(f1) * getmove();
								ep.motionZ += MathHelper.cos(f1) * getmove();
								this.spawnCloud = true;
							}
							else if(epsp.movementInput.moveForward < 0)
							{
								float f1 = ep.rotationYaw * 0.01745329F;
								ep.motionY=ep.motionX=ep.motionZ =0;
								ep.motionX -= MathHelper.sin(f1) * -getmove();
								ep.motionZ += MathHelper.cos(f1) * -getmove();
								this.spawnCloud = true;
							}
							else if(epsp.movementInput.moveStrafe < 0)
							{
								float f1 = ep.rotationYaw * 0.01745329F;
								ep.motionY=ep.motionX=ep.motionZ =0;
								ep.motionX -= MathHelper.sin(f1+(float)(Math.PI/2)) * getmove();
								ep.motionZ += MathHelper.cos(f1+(float)(Math.PI/2)) * getmove();
								this.spawnCloud = true;
							}
							else if(epsp.movementInput.moveStrafe > 0)
							{
								float f1 = ep.rotationYaw * 0.01745329F;
								ep.motionY=ep.motionX=ep.motionZ =0;

								ep.motionX -= MathHelper.sin(f1-(float)(Math.PI/2)) * getmove();
								ep.motionZ += MathHelper.cos(f1-(float)(Math.PI/2)) * getmove();
								this.spawnCloud = true;
							}

							else if(epsp.movementInput.jump && canjump)
							{
								ep.motionY=ep.motionX=ep.motionZ =0;
								ep.motionY += getmove();
								this.spawnCloud = true;
							}
							else if(epsp.movementInput.sneak)
							{
								ep.motionY=ep.motionX=ep.motionZ =0;
								ep.motionY -= getmove();
								this.spawnCloud = true;
							}
						}
						else if(ep.inventory.armorInventory[2].itemID == CaveStory.CaveStoryID)
						{
							if(epsp.movementInput.jump && !ep.onGround && canjump)
							{
								ep.motionY=ep.motionX=ep.motionZ =0;
								ep.motionY += getmove();
								this.spawnCloud = true;
//								commonprocess(epMP,minecraft.theWorld);
							}
						}
						PacketDispatcher.sendPacketToServer(PacketHandler.getPacket(this));
					}
					if(this.spawnCloud)
						commonprocess(ep,world);
				}
				else
				{
					world.spawnParticle("smoke", ep.posX, ep.posY + 0.1D, ep.posZ, 0.0D, 0.0D, 0.0D);
				}
			}
			else if(boosterSwitch)
			{
				CanBoost = CaveStory.BoostPower;
			}
			if(ep.isSneaking())
			{
				ep.fallDistance = 0F;
			}
		}
	}
	void commonprocess(EntityPlayer ep,World world)
	{
		world.spawnParticle("cloud", ep.posX, ep.posY + 0.1D, ep.posZ, 0.0D, 0.0D, 0.0D);
		CanBoost--;
		ep.fallDistance = 0F;
	}
	double getmove()
	{
		return CaveStory.movement * 0.5d;
	}
 	public void readPacketData(ByteArrayDataInput data)
 	{
 		try
 		{
 			this.spawnCloud = data.readBoolean();
 			this.boosterSwitch = data.readBoolean();
 		}
 		catch (Exception e)
 		{
 			e.printStackTrace();
 		}
 	}
 	public void writePacketData(DataOutputStream dos)
 	{
 		try
 		{
 			dos.writeBoolean(this.spawnCloud);
 			dos.writeBoolean(boosterSwitch);
 		}
 		catch (Exception e)
 		{
 			e.printStackTrace();
 		}
 	}
}