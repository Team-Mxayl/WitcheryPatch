package io.github.darkhighness.witcherypatch;

import com.emoniph.witchery.util.CreatureUtil;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

@Mod(modid = "witcherypatch", name = "WitcheryPatch", version = "1.0.0", dependencies = "required-after:witchery")
public class WitcheryPatch {
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event){
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event){
		new Hooks();
	}

	@SubscribeEvent
			(
					priority = EventPriority.HIGHEST
			)
	public void onLiving(LivingDeathEvent event){
		if (event.entityLiving instanceof EntityPlayer){
			if (CreatureUtil.isVampire(event.entityLiving)){
				event.entityLiving.heal(
						event.entityLiving.getMaxHealth()
				);

				event.entityLiving.addPotionEffect(
						new PotionEffect(Potion.moveSpeed.id,100,5)
				);

				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
			(
					priority = EventPriority.HIGHEST
			)
	public void onLiving(LivingHurtEvent event){
		if (event.entityLiving instanceof EntityPlayer){
			if (CreatureUtil.isVampire(event.entityLiving)){
				event.entityLiving.heal(
						event.entityLiving.getMaxHealth()
				);
				event.setCanceled(true);
			}
		}
	}
}
