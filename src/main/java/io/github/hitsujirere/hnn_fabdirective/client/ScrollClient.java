package io.github.hitsujirere.hnn_fabdirective.client;

import io.github.hitsujirere.hnn_fabdirective.util.IScrollableItem;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = "hnn_fabdirective", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ScrollClient {

    private static long ticks = 0;
    private static int scrollIdx = 0;
    private static ItemStack currentTooltipItem = ItemStack.EMPTY;
    private static long tooltipTick = 0;

    public static int getTooltipScrollIndex() {
        return scrollIdx;
    }

    public static int getTooltipScrollIndex(int size) {
        if (size <= 0) return 0;
        return Math.floorMod(scrollIdx, size);
    }

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent e) {
        if (e.phase == TickEvent.Phase.END) {
            ticks++;
        }
    }

    @SubscribeEvent
    public static void tooltip(ItemTooltipEvent e) {
        currentTooltipItem = e.getItemStack();
        tooltipTick = ticks;
    }

    @SubscribeEvent
    public static void onScreenScroll(ScreenEvent.MouseScrolled.Pre e) {
        handleScroll(e.getScrollDelta(), e);
    }

    @SubscribeEvent
    public static void onInputScroll(InputEvent.MouseScrollingEvent e) {
        handleScroll(e.getScrollDelta(), e);
    }

    private static void handleScroll(double delta, net.minecraftforge.eventbus.api.Event event) {
        if (currentTooltipItem.getItem() instanceof IScrollableItem && tooltipTick == ticks && Screen.hasShiftDown()) {
            scrollIdx += delta < 0 ? 1 : -1;
            if (event.isCancelable()) {
                event.setCanceled(true);
            }
        }
    }

}