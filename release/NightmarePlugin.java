package net.runelite.client.plugins.nightmare;


import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.theatre.TheatreConfig;
import net.runelite.client.plugins.theatre.TheatreOverlay;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.*;

@PluginDescriptor(
        name = "[S] Nightmare",
        description = "Nightmare Plugin",
        tags = {"nightmare, ashihama, ross stinks"},
        enabledByDefault = false
)
public class NightmarePlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Getter(AccessLevel.PUBLIC)
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private NightmareOverlay overlay;
    @Inject
    private PrayerOverlay prayerOverlay;
    @Inject
    private SanfewOverlay sanfewOverlay;
    @Inject
    private NightmareConfig config;

    private static final List<Integer> INACTIVE_TOTEMS = Arrays.asList(9434, 9437, 9440, 9443);
    @Getter(AccessLevel.PACKAGE)
    private final Map<Integer, MemorizedTotem> totems = new HashMap<>();
    @Getter(AccessLevel.PACKAGE)
    private final Map<LocalPoint, GameObject> spores = new HashMap<>();
    @Getter(AccessLevel.PACKAGE)
    private boolean inFight;
    // Nightmare's attack animations
    private static final int NIGHTMARE_PRE_MUSHROOM = 37738;
    private static final int NIGHTMARE_MUSHROOM = 37739;
    String correctPray = "";
    Boolean cursePhase;
    Boolean impregnated;
    WorldPoint bossLoc;
    int parasiteSpawn; //counts ticks since parasite spawned
    boolean parasiteSpawned;
    @Provides
    NightmareConfig getConfig(ConfigManager configManager) { return configManager.getConfig(NightmareConfig.class); }

    @Override
    protected void startUp() {
        reset();
        correctPray = "";
        cursePhase = false;
        impregnated = false;
        this.overlayManager.add(this.overlay);
        this.overlayManager.add(this.sanfewOverlay);
        this.overlayManager.add(this.prayerOverlay);

    }

    private void reset(){
        spores.clear();
        totems.clear();
        inFight = false;
    }

    @Override
    protected void shutDown() {
        reset();
        correctPray = "";
        cursePhase = false;
        impregnated = false;
        this.overlayManager.remove(this.sanfewOverlay);
        this.overlayManager.remove(this.overlay);
        this.overlayManager.remove(this.prayerOverlay);

    }

    @Subscribe
    public void onNpcChanged(NpcChanged event)
    {
        final NPC npc = event.getNpc();

        if (npc == null)
        {
            return;
        }
        //this will trigger once when the fight begins
        if (npc.getId() == 9432)
        {
            reset();
            inFight = true;

        }
        //if npc is in the totems map, update its phase
        if (totems.containsKey(npc.getIndex()))
        {
            totems.get(npc.getIndex()).updateCurrentPhase(npc.getId());
        }
        else if (INACTIVE_TOTEMS.contains(npc.getId()))
        {
            //else if the totem is not in the totem array and it is an inactive totem, add it to the totem map.
            totems.putIfAbsent(npc.getIndex(), new MemorizedTotem(npc));
        }
    }

    @Subscribe
    private void onGameObjectSpawned(GameObjectSpawned event)
    {
       // if(!inFight){
            //return;
       // }

        GameObject gameObj = event.getGameObject();
        int id = gameObj.getId();
        if (id == NIGHTMARE_MUSHROOM || id == NIGHTMARE_PRE_MUSHROOM)
        {
            System.out.println("spawned");
            spores.put(gameObj.getLocalLocation(), gameObj);
        }
    }

    @Subscribe
    private void onGameObjectDespawned(GameObjectDespawned event)
    {

        GameObject gameObj = event.getGameObject();
        int id = gameObj.getId();
        if (id == NIGHTMARE_MUSHROOM || id == NIGHTMARE_PRE_MUSHROOM)
        {
            System.out.println("despawned");
            spores.remove(gameObj.getLocalLocation());
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event){
        if(event.getGroup().equalsIgnoreCase("Nightmare")){
            if(!config.hidePrayer()){
                clientThread.invoke(() -> {
                    Widget x = client.getWidget(WidgetInfo.PRAYER_BOX);
                    if(x != null && !x.isHidden() && !x.isSelfHidden()){
                       for(Widget y : x.getStaticChildren()){
                           y.setHidden(false);
                       }
                   }
                });
            }
        }
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event){
        if(event.getActor() == null || event.getActor().getName() == null){
            return;
        }
        if(event.getNpc().getName().toLowerCase().contains("nightmare")){
            System.out.println("setting parasitespawned to false because nightmare spawned");
            parasiteSpawned = false;
        }
        if(event.getNpc().getName().toLowerCase().contains("parasite")){
            System.out.println("setting parasitespawned to false because parasite spawned");
            parasiteSpawned = false;

        }
    }



    @Subscribe
    public void onAnimationChanged(AnimationChanged event){
        if(event.getActor() == null || event.getActor().getName() == null){
            return;
        }
        if(event.getActor().getName().toLowerCase().contains("nightmare")){
            if(event.getActor().getAnimation() == 8596) { //range attack
                correctPray = "missiles";
            } else if (event.getActor().getAnimation() == 8595){ //magic attack
                correctPray = "magic";
            } else if (event.getActor().getAnimation() == 8594){ //melee attack
                correctPray = "melee";
            } else if (event.getActor().getAnimation() == 8606){ //parasite shot out of boss
                if(!parasiteSpawned){
                    System.out.println("setting parasitespawned to true because boss spat one out");
                    parasiteSpawned = true;
                    parasiteSpawn = 27;
                }
            }

        }

    }




    @Subscribe
    public void onClientTick(ClientTick event){
        List<NPC> npcs = client.getNpcs();
        for(NPC n : npcs){
            if(n != null && n.getName() != null){
                if(n.getName().toLowerCase().contains("the nightmare")){
                    if(client.isInInstancedRegion()){
                        bossLoc = n.getWorldLocation();
                    }

                }
            }

        }
    }


    @Subscribe
    public void onChatMessage(ChatMessage event){
        if(event.getMessage().toLowerCase().contains("the nightmare has impregnated you with a deadly parasite")){
            impregnated = true;
        }
        if(event.getMessage().toLowerCase().contains("the parasite within you has been weakened") || event.getMessage().toLowerCase().contains("the parasite bursts out of you, fully grown")){
            impregnated = false;
        }
        if(event.getMessage().toLowerCase().contains("shuffling your prayers")){
            cursePhase = true;
        } else if (event.getMessage().toLowerCase().contains("feel the effects of the nightmare's curse wear off")){
            cursePhase = false;
        }
    }




    @Subscribe
    public void onGameTick(GameTick event){
        Widget x = client.getWidget(WidgetInfo.PRAYER_BOX);
        if(config.hidePrayer()){
            for(Widget y : x.getStaticChildren()){
                if(y.getName().toLowerCase().contains("piety") || y.getName().toLowerCase().contains("augury") || y.getName().toLowerCase().contains("preserve") ||
                        y.getName().toLowerCase().contains("melee") || y.getName().toLowerCase().contains("magic") || y.getName().toLowerCase().contains("missiles")
                        || y.getName().toLowerCase().contains("redemption") || y.getName().toLowerCase().contains("rapid heal")){
                    //System.out.println(y.getName() + " " + y.getCanvasLocation() + " ");
                } else {
                    y.setHidden(true);
                }
            }
        }
        if(parasiteSpawned){
            System.out.println("Parasitespawned must be true. Counting down ticks. " + parasiteSpawn);
            parasiteSpawn--; //ticks since parasite was shot out of boss
        }
        for(NPC npc: client.getNpcs()){
            if(npc != null && npc.getName() != null){
                if(npc.getName().contains("nightmare")){
                    if(npc.getId() == 9433){
                        reset();
                    }
                }
            }
        }
    }
    @Subscribe
    private void onGameStateChanged(GameStateChanged event)
    {
        GameState gamestate = event.getGameState();

        //if loading happens while inFight, the user has left the area (either via death or teleporting).
        if (gamestate == GameState.LOADING && inFight)
        {
            reset();
        }
    }
}
