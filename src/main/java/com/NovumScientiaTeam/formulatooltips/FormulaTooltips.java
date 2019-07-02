package com.EmosewaPixel.formulatooltips;

import com.EmosewaPixel.pixellib.materialSystem.MaterialRegistry;
import com.EmosewaPixel.pixellib.materialSystem.element.ElementUtils;
import com.EmosewaPixel.pixellib.materialSystem.lists.MaterialBlocks;
import com.EmosewaPixel.pixellib.materialSystem.lists.MaterialItems;
import com.EmosewaPixel.pixellib.materialSystem.materials.Material;
import com.EmosewaPixel.pixellib.materialSystem.materials.MaterialStack;
import com.EmosewaPixel.pixellib.materialSystem.materials.utility.GroupMaterial;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.stream.Collectors;

@Mod(FormulaTooltips.MOD_ID)
public class FormulaTooltips {
    public static final String MOD_ID = "formulatooltips";

    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class GameEvents {
        @SubscribeEvent
        public static void tooltipEvent(ItemTooltipEvent e) {
            String formula;
            Item item = e.getItemStack().getItem();
            if (item instanceof BlockItem) {
                Block block = Block.getBlockFromItem(item);
                if (MaterialBlocks.getAllBlocks().contains(block)) {
                    formula = getFormulaString(MaterialBlocks.getBlockMaterial(block));
                    if (!formula.equals("?"))
                        e.getToolTip().add(new StringTextComponent(TextFormatting.GRAY + formula));
                }
            } else if (MaterialItems.getAllItems().contains(item)) {
                formula = getFormulaString(MaterialItems.getItemMaterial(item));
                if (!formula.equals("?"))
                    e.getToolTip().add(new StringTextComponent(TextFormatting.GRAY + formula));
            }
        }
    }

    private static String getFormulaString(Material mat) {
        return mat.getComposition().size() == 0 ? mat.getElement().getSymbol()
                : mat.getComposition().stream().map(ms -> {
            if (ms.getMaterial() == MaterialRegistry.WATER)
                return "·" + ms.getCount() + "H2O";
            if (ms.getMaterial() instanceof GroupMaterial)
                if (ms.getCount() > 1)
                    return "(" + ElementUtils.getElementalComposition(ms.getMaterial()).stream().map(s -> s.getElement().getSymbol() + s.getCount()).collect(Collectors.toList()).toString() + ")" + ms.getCount();
                else
                    return ElementUtils.getElementalComposition(ms.getMaterial()).stream().map(s -> s.getElement().getSymbol() + s.getCount()).collect(Collectors.toList()).toString();
            List<MaterialStack> composition = ms.getMaterial().getComposition();
            if (composition.size() == 1)
                return getFormulaString(ms.getMaterial()) + ms.getCount();
            if (composition.size() > 1)
                if (ms.getCount() > 1)
                    return "(" + getFormulaString(ms.getMaterial()) + ")" + ms.getCount();
                else
                    return getFormulaString(ms.getMaterial());
            return ms.getMaterial().getElement().getSymbol() + (ms.getCount() > 1 ? ms.getCount() : "");
        })
                .collect(Collectors.toList()).toString();
    }
}