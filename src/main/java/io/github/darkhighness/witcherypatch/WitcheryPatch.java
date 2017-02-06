package io.github.darkhighness.witcherypatch;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "witcherypatch",
		name = "WitcheryPatch",
		version = "0.0.1",
		dependencies = "required-after:witchery")
public class WitcheryPatch
{
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {}
}
