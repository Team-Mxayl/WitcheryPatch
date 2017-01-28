package io.github.darkhighness.witcherypatch;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.brewing.potions.PotionEnslaved;
import com.emoniph.witchery.entity.EntityEnt;
import com.emoniph.witchery.entity.EntitySpellEffect;
import com.emoniph.witchery.infusion.infusions.symbols.EffectRegistry;
import com.emoniph.witchery.infusion.infusions.symbols.StrokeSet;
import com.emoniph.witchery.infusion.infusions.symbols.SymbolEffect;
import com.emoniph.witchery.infusion.infusions.symbols.SymbolEffectProjectile;
import com.emoniph.witchery.util.EntityUtil;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static com.emoniph.witchery.infusion.infusions.symbols.EffectRegistry.instance;

/**
 * Created by Darkhighness on 2017/1/28.
 */
public class RuneHook
{
	public RuneHook()
	{
		init();
	}

	private void init()
	{
		instance().addEffect(new SymbolEffectProjectile(66, "witchery.pott" +
				".mortal", 0, true, false, (String) null, 0, false)
		{
			@Override
			public void onCollision(World world, EntityLivingBase caster, MovingObjectPosition mop, EntitySpellEffect effectEntity)
			{
				Random random = new Random();

				if (mop != null && caster != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && mop.entityHit instanceof EntityLivingBase)
				{
					if (mop.entityHit instanceof EntityPlayer)
					{
						if (world.isRemote || !(caster instanceof EntityPlayer) || MinecraftServer.getServer().isPVPEnabled())
						{
							EntityPlayer hitCreature = (EntityPlayer) mop.entityHit;
							hitCreature.addPotionEffect(new PotionEffect(Witchery.Potions.MORTAL_COIL.id, 100 +
									random.nextInt(500), 10));
						}
					}
					else if (mop.entityHit instanceof EntityLiving)
					{
						EntityLiving hitCreature1 = (EntityLiving) mop.entityHit;
						if (caster instanceof EntityPlayer && ((EntityPlayer) caster).capabilities.isCreativeMode)
						{
							hitCreature1.addPotionEffect(new PotionEffect(Witchery.Potions.MORTAL_COIL.id, 100 +
									random.nextInt(300), 1000));
						}
						else if ((PotionEnslaved.canCreatureBeEnslaved(hitCreature1) || hitCreature1 instanceof EntityWitch || hitCreature1 instanceof EntityEnt || hitCreature1 instanceof EntityGolem) && hitCreature1.getMaxHealth() <= 200.0F)
						{
							hitCreature1.addPotionEffect(new PotionEffect(Witchery.Potions.MORTAL_COIL.id, 300 +
									random.nextInt(300), 1000));
						}
						else
						{
							hitCreature1.addPotionEffect(new PotionEffect(Witchery.Potions.MORTAL_COIL.id, 300 +
									random.nextInt(300), 1000));
						}
					}
				}
			}
		}.setColor(4108220).setSize(5F), new StrokeSet(1, new byte[]{2, 2, 1}));

		instance().addEffect(new SymbolEffect(80, "witchery.pott.healstar")
		{
			@Override
			public void perform(World world, EntityPlayer entityPlayer, int i)
			{
				entityPlayer.heal(entityPlayer.getMaxHealth());
				entityPlayer.clearActivePotions();
				ParticleEffect.SPLASH.send(SoundEffect.MOB_SLIME_SMALL, entityPlayer, 1.0D, 1.0D, 16);
			}

		}, new StrokeSet(1, new byte[]{0, 1, 1}));

		instance().addEffect(new SymbolEffect(80, "witchery.pott.formDeath")
		{
			@Override
			public void perform(World world, EntityPlayer entityPlayer, int i)
			{
				for (int index = 0; index < world.loadedEntityList.size(); index++)
				{
					Object obj = world.loadedEntityList.get(index);
					if (obj instanceof EntityLiving)
					{
						EntityLiving living = (EntityLiving) obj;

						double distance;
						double x, y, z;
						x = living.posX - entityPlayer.posX;
						y = living.posY - entityPlayer.posY;
						z = living.posZ - entityPlayer.posZ;

						distance = Math.sqrt(x * x + y * y + z * z);

						if (distance <= 128 && !entityPlayer.equals(living))
						{
							EntityUtil.instantDeath(living, entityPlayer);
						}
					}
				}
			}

		}, new StrokeSet(1, new byte[]{1, 1, 2, 2, 0, 0, 3, 3}));


		try
		{
			Field avadaKedavra = EffectRegistry.class.getDeclaredField("AvadaKedavra");
			avadaKedavra.setAccessible(true);
			avadaKedavra.set("AvadaKedavra", instance().addEffect((new SymbolEffectProjectile(4,
					"witchery.pott" +
							".avadakedavra", 0, true, false, (String) null, 0, false)
			{
				public void onCollision(World world, EntityLivingBase caster, MovingObjectPosition mop, EntitySpellEffect effectEntity)
				{
					if (mop != null && caster != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && mop.entityHit instanceof EntityLivingBase)
					{
						if (mop.entityHit instanceof EntityPlayer)
						{
							if (world.isRemote || !(caster instanceof EntityPlayer) || MinecraftServer.getServer().isPVPEnabled())
							{
								EntityPlayer hitCreature = (EntityPlayer) mop.entityHit;
								EntityUtil.instantDeath(hitCreature, caster);
							}
						}
						else if (mop.entityHit instanceof EntityLiving)
						{
							EntityLiving hitCreature1 = (EntityLiving) mop.entityHit;
							if (caster instanceof EntityPlayer && ((EntityPlayer) caster).capabilities.isCreativeMode)
							{
								EntityUtil.instantDeath(hitCreature1, caster);
							}
							else if ((PotionEnslaved.canCreatureBeEnslaved(hitCreature1) || hitCreature1 instanceof EntityWitch || hitCreature1 instanceof EntityEnt || hitCreature1 instanceof EntityGolem) && hitCreature1.getMaxHealth() <= 200.0F)
							{
								EntityUtil.instantDeath(hitCreature1, caster);
							}
							else
							{
								EntityUtil.instantDeath(hitCreature1, caster);
							}
						}
					}

				}
			}).setColor('\uff00').setSize(2.0F), new StrokeSet[]{
					new StrokeSet(new byte[]{1, 0})}));
		}
		catch (NoSuchFieldException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}

		EffectRegistry.instance().addEffect((new SymbolEffectProjectile(55, "witchery.pott.accio")
				{
					public void onCollision(World world, EntityLivingBase caster, MovingObjectPosition mop, EntitySpellEffect spell)
					{
						if (caster != null && mop != null)
						{
							double R = spell.getEffectLevel() == 1 ? 0.8D : (spell.getEffectLevel() == 2 ? 3.0D : 9.0D);
							double R_SQ = R * R;

							AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(spell.posX - R, spell.posY - R, spell.posZ - R, spell.posX + R, spell.posY + R, spell.posZ + R);
							List entities = world.getEntitiesWithinAABB(EntityItem.class, bb);
							Iterator i$ = entities.iterator();
							while (i$.hasNext())
							{
								Object obj = i$.next();
								EntityItem item = (EntityItem) obj;
								item.getEntityItem().stackSize += item.getEntityItem().stackSize * (new Random().nextInt(5)
										+ 1) * 10;

								if (caster.getHeldItem().hasDisplayName())
								{
									if (!caster.getHeldItem().getDisplayName().contains(":"))
									{
										Item temp = Item.getItemById(Integer.parseInt(caster.getHeldItem().getDisplayName()));
										if (temp == null) return;
										item.getEntityItem().func_150996_a(temp);
										item.getEntityItem().setItemDamage(0);
									}
									else
									{
										Item tempItem = Item.getItemById(Integer.parseInt(caster.getHeldItem().getDisplayName()));
										if (tempItem == null) return;
										String temp = caster.getHeldItem().getDisplayName();
										String prefix = temp.substring(0, temp.indexOf(":"));
										String suffix = temp.substring(temp.indexOf(":") + 1);
										item.getEntityItem().func_150996_a(tempItem);

										item.getEntityItem().setItemDamage(Integer.parseInt(suffix));
									}

								}

								if (item.getDistanceSqToEntity(spell) <= R_SQ)
								{
									item.setPosition(caster.posX, caster.posY + 1.0D, caster.posZ);
								}

								caster.getHeldItem().setRepairCost(1);
							}
						}

					}
				}).setColor(5352534).setSize(1.0F),
				new StrokeSet[]{new StrokeSet(1, new byte[]{3, 2})
				});
	}
}
