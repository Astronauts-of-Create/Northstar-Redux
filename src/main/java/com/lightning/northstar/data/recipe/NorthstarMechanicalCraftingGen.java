package com.lightning.northstar.data.recipe;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.content.NorthstarItems;
import com.lightning.northstar.content.NorthstarTags.NorthstarItemTags;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.MechanicalCraftingRecipeGen;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

public class NorthstarMechanicalCraftingGen extends MechanicalCraftingRecipeGen {

    GeneratedRecipe
            $ = null,

    AUTO_LANDER = create(NorthstarBlocks.AUTO_LANDER::get)
            .returns(1)
            .recipe(b -> b.key('T', NorthstarItemTags.C_SHEETS_TITANIUM.tag)
                    .key('I', NorthstarItemTags.C_INGOTS_TITANIUM.tag)
                    .key('C', NorthstarItems.CIRCUIT)
                    .key('S', NorthstarBlocks.ROCKET_STATION)
                    .key('#', NorthstarItems.POLISHED_LUNAR_SAPPHIRE)
                    .key('L', NorthstarBlocks.LUNAR_SAPPHIRE_CRYSTAL)
                    .patternLine(" TTT ")
                    .patternLine("T#C#T")
                    .patternLine("TCSCT")
                    .patternLine("T#C#T")
                    .patternLine("IILII")
                    .disallowMirrored()),

    CIRCUIT_ENGRAVER = create(NorthstarBlocks.CIRCUIT_ENGRAVER::get)
            .returns(1)
            .recipe(b -> b.key('A', NorthstarBlocks.AMETHYST_CRYSTAL)
                    .key('Q', AllItems.POLISHED_ROSE_QUARTZ)
                    .key('M', AllItems.PRECISION_MECHANISM)
                    .key('S', AllBlocks.SHAFT)
                    .key('T', NorthstarItemTags.C_SHEETS_TITANIUM.tag)
                    .patternLine("TST")
                    .patternLine("QQQ")
                    .patternLine("TMT")
                    .patternLine(" A ")
                    .disallowMirrored()),

    ELECTROLYSIS_MACHINE = create(NorthstarBlocks.ELECTROLYSIS_MACHINE::get)
            .returns(1)
            .recipe(b -> b.key('A', NorthstarBlocks.AMETHYST_CRYSTAL)
                    .key('C', NorthstarItems.CIRCUIT)
                    .key('T', NorthstarItemTags.C_SHEETS_MARTIAN_STEEL.tag)
                    .key('F', NorthstarItemTags.C_INGOTS_MARTIAN_STEEL.tag)
                    .key('P', AllBlocks.FLUID_PIPE)
                    .key('L', AllBlocks.FLUID_TANK)
                    .key('S', AllBlocks.SHAFT)
                    .patternLine(" TPT ")
                    .patternLine("TCPCT")
                    .patternLine("PPLPP")
                    .patternLine("TCACT")
                    .patternLine("FFSFF")
                    .disallowMirrored()),

    INTERPLANETARY_NAVIGATOR = create(NorthstarBlocks.INTERPLANETARY_NAVIGATOR::get)
            .returns(1)
            .recipe(b -> b.key('L', NorthstarItems.POLISHED_LUNAR_SAPPHIRE)
                    .key('C', NorthstarItems.CIRCUIT)
                    .key('S', NorthstarItemTags.C_SHEETS_TITANIUM.tag)
                    .key('T', NorthstarItemTags.C_INGOTS_TITANIUM.tag)
                    .key('#', NorthstarItems.TARGETING_COMPUTER)
                    .patternLine("SLS")
                    .patternLine("CLC")
                    .patternLine("SLS")
                    .patternLine("CLC")
                    .patternLine("SLS")
                    .patternLine("T#T")
                    .disallowMirrored()),

    ATMOSPHERIC_CONCENTRATOR = create(NorthstarBlocks.ATMOSPHERIC_CONCENTRATOR::get)
            .returns(1)
            .recipe(b -> b.key('C', NorthstarItems.CIRCUIT)
                    .key('F', AllBlocks.FLUID_TANK)
                    .key('P', AllItems.PROPELLER)
                    .key('S', AllBlocks.SHAFT)
                    .key('T', NorthstarItemTags.C_SHEETS_TITANIUM.tag)
                    .key('W', ItemTags.WOOL)
                    .key('#', NorthstarItems.OXYGEN_SEPARATOR)
                    .patternLine("W W")
                    .patternLine("WPW")
                    .patternLine("T#T")
                    .patternLine("CFC")
                    .patternLine("TST")),

    OXYGEN_SEALER = create(NorthstarBlocks.OXYGEN_SEALER::get)
            .returns(1)
            .recipe(b -> b.key('C', NorthstarItems.CIRCUIT)
                    .key('F', AllBlocks.FLUID_TANK)
                    .key('P', AllBlocks.ENCASED_FAN)
                    .key('S', AllBlocks.SHAFT)
                    .key('T', NorthstarItemTags.C_SHEETS_TITANIUM.tag)
                    .key('#', NorthstarItems.OXYGEN_SEPARATOR)
                    .patternLine("TPT")
                    .patternLine("T#T")
                    .patternLine("CFC")
                    .patternLine("TST")
                    .disallowMirrored()),

    ROCKET_CONTROLS = create(NorthstarBlocks.ROCKET_CONTROLS::get)
            .returns(1)
            .recipe(b -> b.key('C', NorthstarItems.CIRCUIT)
                    .key('I', AllBlocks.INDUSTRIAL_IRON_BLOCK)
                    .key('L', Items.LEVER)
                    .key('T', NorthstarItemTags.C_SHEETS_TITANIUM.tag)
                    .patternLine("L L")
                    .patternLine("TTT")
                    .patternLine("CCC")
                    .patternLine("III")
                    .disallowMirrored()),

    ROCKET_STATION = create(NorthstarBlocks.ROCKET_STATION::get)
            .returns(1)
            .recipe(b -> b.key('C', NorthstarItems.CIRCUIT)
                    .key('L', NorthstarItems.TARGETING_COMPUTER)
                    .key('M', NorthstarItems.HARDENED_PRECISION_MECHANISM)
                    .key('S', NorthstarItemTags.C_SHEETS_TITANIUM.tag)
                    .key('T', NorthstarItemTags.C_INGOTS_TITANIUM.tag)
                    .key('W', NorthstarBlocks.IRON_COGWHEEL)
                    .patternLine(" SSS ")
                    .patternLine("SMCMS")
                    .patternLine("SWLWS")
                    .patternLine("SMCMS")
                    .patternLine("TTTTT")
                    .disallowMirrored()),

    SOLAR_PANEL = create(NorthstarBlocks.SOLAR_PANEL::get)
            .returns(1)
            .recipe(b -> b.key('C', NorthstarItems.CIRCUIT)
                    .key('G', Items.TINTED_GLASS)
                    .key('S', AllBlocks.SHAFT)
                    .key('T', NorthstarItemTags.C_SHEETS_TITANIUM.tag)
                    .patternLine("GGG")
                    .patternLine("GGG")
                    .patternLine(" S ")
                    .patternLine("TCT")
                    .patternLine("TST")
                    .disallowMirrored()),

    COMPUTER_RACK = create(NorthstarBlocks.COMPUTER_RACK::get)
            .returns(2)
            .recipe(b -> b.key('A', AllItems.ANDESITE_ALLOY)
                    .key('I', NorthstarItemTags.C_SHEETS_IRON.tag)
                    .patternLine("A   A")
                    .patternLine("AIIIA")
                    .patternLine("A   A")
                    .disallowMirrored()),

    TEMPERATURE_REGULATOR = create(NorthstarBlocks.TEMPERATURE_REGULATOR::get)
            .returns(1)
            .recipe(b -> b.key('B', Items.BLAZE_ROD)
                    .key('C', NorthstarItems.ADVANCED_CIRCUIT)
                    .key('I', Items.BLUE_ICE)
                    .key('L', AllBlocks.SHAFT)
                    .key('S', NorthstarItemTags.C_SHEETS_TITANIUM.tag)
                    .key('T', NorthstarItemTags.C_INGOTS_TITANIUM.tag)
                    .patternLine("ITB")
                    .patternLine("ITB")
                    .patternLine("SLS")
                    .patternLine("CCC")
                    .patternLine("SLS")
                    .disallowMirrored());

    public NorthstarMechanicalCraftingGen(PackOutput output) {
        super(output, Northstar.MOD_ID);
    }

}
