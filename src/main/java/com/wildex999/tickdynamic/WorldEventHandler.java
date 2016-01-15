package com.wildex999.tickdynamic;

import java.util.HashMap;
import java.util.List;

import com.wildex999.tickdynamic.listinject.CustomProfiler;
import com.wildex999.tickdynamic.listinject.EntityObject;
import com.wildex999.tickdynamic.listinject.EntityType;
import com.wildex999.tickdynamic.listinject.ListManager;
import com.wildex999.tickdynamic.listinject.ListManagerEntities;

import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class WorldEventHandler {
	public TickDynamicMod mod;
	
	private HashMap<World, ListManagerEntities> entityListManager;
	private HashMap<World, ListManager> tileListManager;
	
	public WorldEventHandler(TickDynamicMod mod) {
		this.mod = mod;
		entityListManager = new HashMap<World, ListManagerEntities>();
		tileListManager = new HashMap<World, ListManager>();
	}
	
    @SubscribeEvent
    public void worldTickEvent(WorldTickEvent event) {
		Profiler profiler = event.world.theProfiler;
		if(!(profiler instanceof CustomProfiler))
			return;
		CustomProfiler customProfiler = (CustomProfiler)profiler;
    	
    	if(event.phase == Phase.START) {
    		customProfiler.stage = CustomProfiler.Stage.BeforeLoop;
    	}
    	else {
    		customProfiler.stage = CustomProfiler.Stage.None;
    	}
    }
	
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onDimensionLoad(WorldEvent.Load event)
    {
    	if(event.world.isRemote)
    		return;
    	
    	//Inject Custom Profiler for watching Entity ticking
    	try {
    		setCustomProfiler(event.world, new CustomProfiler(event.world.theProfiler, event.world));
    	} catch(Exception e) {
    		System.err.println("Unable to set TickDynamic World profiler! World will not be using TickDynamic: " + event.world);
    		System.err.println(e);
    		return; //Do not add TickDynamic to world
    	}
    	
    	//Register our own Entity List manager, copying over any existing Entities
    	if(mod.debug)
    		System.out.println("World load: " + event.world.provider.getDimensionName());
    	ListManagerEntities entityManager = new ListManagerEntities(event.world, mod);
    	entityListManager.put(event.world, entityManager);
    	/*ListManager tileEntityManager = new ListManager(event.world, mod, EntityType.TileEntity);
    	tileListManager.put(event.world, tileEntityManager);*/
    	
    	//Overwrite existing lists, copying any loaded Entities
    	if(mod.debug)
    		System.out.println("Adding " + event.world.loadedEntityList.size() + " existing Entities.");
    	List<EntityObject> oldList = event.world.loadedEntityList;
    	event.world.loadedEntityList = entityManager;
    	for(EntityObject obj : oldList) {
    		entityManager.add(obj);
    	}
    	
    	//Tiles
    	/*if(mod.debug)
    		System.out.println("Adding " + event.world.loadedTileEntityList.size() + " existing TileEntities.");
    	oldList = event.world.loadedTileEntityList;
    	event.world.loadedTileEntityList = tileEntityManager;
    	for(EntityObject obj : oldList) {
    		tileEntityManager.add(obj);
    	}*/
    	
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onDimensionUnload(WorldEvent.Unload event)
    {
    	if(event.world.isRemote)
    		return;
    	
    	//Make sure we unload our own Entity List manager to avoid memory leak
    	//Unload local groups, clear entities etc.
    	//TODO: World Unload
    }
    
    private void setCustomProfiler(World world, CustomProfiler profiler) throws Exception {
    	ReflectionHelper.setPrivateValue(World.class, world, profiler, "theProfiler", "field_72984_F");
    }
}