package io.github.hitsujirere.hnn_fabdirective.item;

import dev.shadowsoffire.hostilenetworks.data.DataModel;
import dev.shadowsoffire.hostilenetworks.tile.LootFabTileEntity;
import dev.shadowsoffire.hostilenetworks.util.Color;
import dev.shadowsoffire.placebo.PlaceboClient;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import io.github.hitsujirere.hnn_fabdirective.accessor.ISelectionsAccessor;
import io.github.hitsujirere.hnn_fabdirective.util.SavedSelections;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * An item which holds a set of Loot Fabricator targets.
 * <p>
 * It can copy the targets from a fabricator and apply them to another. It can also be used to open a GUI to configure targets on-the-fly.
 */
public class FabDirectiveItem extends Item {

    public FabDirectiveItem(Properties properties) {
        super(properties);
    }

    @Override
    @NotNull
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        Player player = ctx.getPlayer();
        ItemStack stack = ctx.getItemInHand();
        BlockEntity be = level.getBlockEntity(ctx.getClickedPos());

        if (be instanceof LootFabTileEntity lootFab) {
            if (level.isClientSide) {
                return InteractionResult.SUCCESS;
            }

            if (player.isSecondaryUseActive()) {
                SavedSelections selections = SavedSelections.read(stack.getTagElement("fab_selections"));
                if (!selections.isEmpty()) {
                    ISelectionsAccessor selectionsAccessor = (ISelectionsAccessor) lootFab;
                    selectionsAccessor.setSelections(selections.getSelections());
                    player.sendSystemMessage(Component.translatable("text.hnn_fabdirective.selections_applied", selectionsAccessor.getSelections().size(), stack.getDisplayName()).withStyle(style -> style.withColor(Color.LIME)));
                    return InteractionResult.SUCCESS;
                }
            } else {
                ISelectionsAccessor selectionsAccessor = (ISelectionsAccessor) lootFab;
                Object2IntMap<DynamicHolder<DataModel>> fabSelections = selectionsAccessor.getSelections();
                SavedSelections selections = new SavedSelections(fabSelections);
                stack.getOrCreateTag().put("fab_selections", selections.write());
                player.sendSystemMessage(Component.translatable("text.hnn_fabdirective.selections_copied", selectionsAccessor.getSelections().size(), stack.getDisplayName()).withStyle(style -> style.withColor(Color.LIME)));
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.FAIL;
        }

        return super.useOn(ctx);
    }

    @Override
    @NotNull
    public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        // TODO: Implement FabDirectiveMenu to allow configuring on-the-fly.
        return super.use(level, player, usedHand);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Level level, List<Component> list, @NotNull TooltipFlag flag) {
        list.add(Component.translatable(this.getDescriptionId() + ".desc").withStyle(ChatFormatting.GRAY));
        list.add(Component.translatable(this.getDescriptionId() + ".desc2").withStyle(ChatFormatting.GRAY));
        if (FMLEnvironment.dist.isClient()) {
            ClientAccess.appendHoverText(stack, level, list, flag);
        }
    }

    public static class ClientAccess {

        public static void appendHoverText(ItemStack stack, Level level, List<Component> list, TooltipFlag flag) {
            SavedSelections selections = SavedSelections.read(stack.getTagElement("fab_selections"));
            if (selections.isEmpty()) {
                return;
            }

            list.add(CommonComponents.SPACE);

            Object2IntMap<DynamicHolder<DataModel>> fabSelections = selections.getSelections();
            int selIdx = selections.isEmpty() ? 0 : (int) ((PlaceboClient.ticks / 40) % selections.size());

            ObjectIterator<Object2IntMap.Entry<DynamicHolder<DataModel>>> it = fabSelections.object2IntEntrySet().iterator();
            it.skip(selIdx);

            Object2IntMap.Entry<DynamicHolder<DataModel>> entry = it.next();
            DynamicHolder<DataModel> holder = entry.getKey();
            int index = entry.getIntValue();
            DataModel model = holder.isBound() ? holder.get() : null;
            ItemStack drop = model != null && index < model.fabDrops().size() ? model.fabDrops().get(index) : ItemStack.EMPTY;

            MutableComponent comp = Component.translatable("text.hnn_fabdirective.stored_selection", selIdx + 1, selections.size()).withStyle(style -> style.withColor(Color.LIME));
            if (Screen.hasShiftDown()) {
                comp.append(CommonComponents.SPACE);
                comp.append(Component.translatable("text.hnn_fabdirective.scroll").withStyle(style -> style.withColor(ChatFormatting.DARK_GRAY).withUnderlined(false)));
            } else {
                comp.append(CommonComponents.SPACE);
                comp.append(Component.translatable("text.hnn_fabdirective.shift").withStyle(style -> style.withColor(ChatFormatting.DARK_GRAY).withUnderlined(false)));
            }

            list.add(comp);

            if (model != null) {
                if (!drop.isEmpty()) {
                    Component input = model.getPredictionDrop().getHoverName();
                    Component output = drop.getHoverName();
                    comp = Component.translatable("text.hnn_fabdirective.selection", input, drop.getCount(), output).withStyle(style -> style.withColor(Color.LIME));
                    list.add(comp);
                } else {
                    comp = Component.translatable("text.hnn_fabdirective.invalid_selection.index", index).withStyle(style -> style.withColor(ChatFormatting.RED));
                    list.add(comp);
                }
            } else {
                comp = Component.translatable("text.hnn_fabdirective.invalid_selection.model", holder.getId()).withStyle(style -> style.withColor(ChatFormatting.RED));
                list.add(comp);
            }
        }
    }

}