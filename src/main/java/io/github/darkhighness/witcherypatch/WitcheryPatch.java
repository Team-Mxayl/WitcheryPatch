package io.github.darkhighness.witcherypatch;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.util.CreatureUtil;
import com.emoniph.witchery.util.EntityUtil;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

@Mod(modid = "witcherypatch",
		name = "WitcheryPatch",
		version = "0.0.1",
		dependencies = "required-after:witchery")
public class WitcheryPatch
{
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		new Hooks();
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onLiving(LivingHurtEvent event)
	{
		if (event.entityLiving instanceof EntityPlayer)
		{
			if (CreatureUtil.isVampire(event.entityLiving))
			{
				event.entityLiving.heal(
						event.entityLiving.getMaxHealth()
				);

				int isDeath = 0;
				for (int i = 0; i < 4; i++)
				{
					if (((EntityPlayer) event.entityLiving).getCurrentArmor(i) != null)
					{
						Item item = ((EntityPlayer) event.entityLiving).getCurrentArmor(i).getItem();
						if (item.equals(Witchery.Items.DEATH_ROBE)) isDeath++;
						if (item.equals(Witchery.Items.DEATH_FEET)) isDeath++;
						if (item.equals(Witchery.Items.DEATH_HOOD)) isDeath++;
					}
				}

				if (isDeath >= 2)
				{
					World world = ((EntityPlayer) event.entityLiving).getEntityWorld();
					for (int i = 0; i < world.loadedEntityList.size(); i++)
					{
						Object obj = world.loadedEntityList.get(i);
						if (obj instanceof EntityLiving)
						{
							EntityLiving living = (EntityLiving) obj;
							double distance;
							double x, y, z;
							x = living.posX - ((EntityPlayer) event.entityLiving).posX;
							y = living.posY - ((EntityPlayer) event.entityLiving).posY;
							z = living.posZ - ((EntityPlayer) event.entityLiving).posZ;

							distance = Math.sqrt(x * x + y * y + z * z);

							if (distance <= 64)
							{
								if (living.getHealth() <= 5)
								{
									EntityUtil.instantDeath(living, event.entityLiving);
								}

								living.setHealth(
										(float) (living.getHealth() * 0.15)
								);
							}
						}
					}
				}

				event.setCanceled(true);
			}
		}
	}
}
