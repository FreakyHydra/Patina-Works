package com.freakyhydra.patinaworks.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PatinaRecipeScreen extends Screen {
    private static final int PANEL_W = 360;
    private static final int PANEL_H = 230;
    private static final int LEFT_W = 120;
    private static final int RIGHT_X = 130;
    private static final int RIGHT_W = PANEL_W - RIGHT_X - 8;
    private static final int LIST_Y = 42;
    private static final int LIST_H = 160;
    private static final int ITEM_H = 20;
    private static final int VISIBLE_ITEMS = LIST_H / ITEM_H;

    private static final int BORDER = 0xFF8B7355;
    private static final int SECTION_BORDER = 0xFF9B8B6A;
    private static final int LIST_BG = 0x40FFFFFF;
    private static final int SELECTED_BG = 0x80FFFFAA;
    private static final int TEXT_COLOR = 0xFF3F3F3F;
    private static final int SLOT_BG = 0xFF8B8B8B;
    private static final int SLOT_OUTLINE = 0xFF6B6B6B;

    private int leftPos;
    private int topPos;
    private EditBox searchBox;

    private final List<RecipeEntry> allRecipes = new ArrayList<>();
    private final List<RecipeEntry> filteredRecipes = new ArrayList<>();
    private int selectedIndex = -1;
    private int recipeVariantIndex = 0;
    private int scrollOffset = 0;

    private int prevSelectedIndex = -1;

    public PatinaRecipeScreen() {
        super(Component.translatable("gui.patinaworks.recipe_index"));
    }

    @Override
    protected void init() {
        leftPos = (width - PANEL_W) / 2;
        topPos = (height - PANEL_H) / 2;

        clearWidgets();

        searchBox = new EditBox(font, leftPos + 10, topPos + 22, PANEL_W - 20, 14,
                Component.translatable("gui.patinaworks.search"));
        searchBox.setMaxLength(50);
        searchBox.setBordered(true);
        searchBox.setTextColor(0x3F3F3F);
        searchBox.setResponder(this::applyFilter);
        addRenderableWidget(searchBox);

        addRenderableWidget(Button.builder(Component.literal("X"), btn -> onClose())
                .bounds(leftPos + PANEL_W - 22, topPos + 4, 18, 14)
                .build());

        addRenderableWidget(Button.builder(
                        Component.translatable("gui.patinaworks.prev_recipe"), btn -> navigateRecipe(-1))
                .bounds(leftPos + RIGHT_X, topPos + PANEL_H - 24, 60, 16)
                .build());

        addRenderableWidget(Button.builder(
                        Component.translatable("gui.patinaworks.next_recipe"), btn -> navigateRecipe(1))
                .bounds(leftPos + RIGHT_X + 64, topPos + PANEL_H - 24, 60, 16)
                .build());

        loadRecipes();
    }

    private void loadRecipes() {
        allRecipes.clear();
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;

        List<RecipeHolder<CraftingRecipe>> craftingRecipes =
                level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING);

        for (RecipeHolder<CraftingRecipe> holder : craftingRecipes) {
            if (!holder.id().getNamespace().equals("patinaworks")) continue;
            CraftingRecipe recipe = holder.value();
            List<Ingredient> ingredients = getIngredients(recipe);
            ItemStack result = recipe.getResultItem(level.registryAccess());
            if (!result.isEmpty()) {
                allRecipes.add(new RecipeEntry(holder.id(), recipe, ingredients, result));
            }
        }
        allRecipes.sort(Comparator.comparing(e -> e.id.toString()));
        applyFilter(searchBox != null ? searchBox.getValue() : "");
    }

    private List<Ingredient> getIngredients(CraftingRecipe recipe) {
        if (recipe instanceof ShapedRecipe shaped) {
            NonNullList<Ingredient> ingredients = shaped.getIngredients();
            int width = shaped.getWidth();
            int height = shaped.getHeight();
            List<Ingredient> result = new ArrayList<>(9);
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    if (row < height && col < width) {
                        result.add(ingredients.get(row * width + col));
                    } else {
                        result.add(Ingredient.EMPTY);
                    }
                }
            }
            return result;
        } else if (recipe instanceof ShapelessRecipe shapeless) {
            NonNullList<Ingredient> ingredients = shapeless.getIngredients();
            List<Ingredient> result = new ArrayList<>(9);
            int idx = 0;
            for (int i = 0; i < 9; i++) {
                if (idx < ingredients.size()) {
                    result.add(ingredients.get(idx++));
                } else {
                    result.add(Ingredient.EMPTY);
                }
            }
            return result;
        }
        return NonNullList.withSize(9, Ingredient.EMPTY);
    }

    private void applyFilter(String query) {
        filteredRecipes.clear();
        String lower = query.toLowerCase();
        for (RecipeEntry entry : allRecipes) {
            if (query.isEmpty() || entry.result.getHoverName().getString().toLowerCase().contains(lower)
                    || entry.id.getPath().toLowerCase().contains(lower)) {
                filteredRecipes.add(entry);
            }
        }
        if (selectedIndex >= filteredRecipes.size()) {
            selectedIndex = filteredRecipes.isEmpty() ? -1 : filteredRecipes.size() - 1;
        } else if (selectedIndex < 0 && !filteredRecipes.isEmpty()) {
            selectedIndex = 0;
        }
        recipeVariantIndex = 0;
        scrollOffset = 0;
    }

    private void navigateRecipe(int direction) {
        if (selectedIndex < 0 || selectedIndex >= filteredRecipes.size()) return;
        List<RecipeEntry> variants = findVariants(filteredRecipes.get(selectedIndex).result);
        if (variants.size() > 1) {
            recipeVariantIndex = (recipeVariantIndex + direction + variants.size()) % variants.size();
        }
    }

    private List<RecipeEntry> findVariants(ItemStack result) {
        List<RecipeEntry> variants = new ArrayList<>();
        for (RecipeEntry entry : filteredRecipes) {
            if (ItemStack.isSameItemSameComponents(entry.result, result)) {
                variants.add(entry);
            }
        }
        return variants;
    }

    private int getListTop() {
        return topPos + LIST_Y;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(0, 0, width, height, 0xC0101010);
        guiGraphics.fill(leftPos, topPos, leftPos + PANEL_W, topPos + PANEL_H, 0xFFC6BDA5);

        guiGraphics.fill(leftPos, topPos, leftPos + PANEL_W, topPos + 1, BORDER);
        guiGraphics.fill(leftPos, topPos + PANEL_H - 1, leftPos + PANEL_W, topPos + PANEL_H, BORDER);
        guiGraphics.fill(leftPos, topPos, leftPos + 1, topPos + PANEL_H, BORDER);
        guiGraphics.fill(leftPos + PANEL_W - 1, topPos, leftPos + PANEL_W, topPos + PANEL_H, BORDER);
        guiGraphics.fill(leftPos + RIGHT_X - 2, topPos + LIST_Y - 2, leftPos + RIGHT_X - 1, topPos + LIST_Y + LIST_H + 2, SECTION_BORDER);

        guiGraphics.flush();

        // Render the EditBox and buttons first.
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.flush();

        // Render custom text and items after the widgets.
        drawTitle(guiGraphics);
        drawRecipeList(guiGraphics, mouseX, mouseY);
        drawRecipeDetail(guiGraphics, mouseX, mouseY);

        guiGraphics.flush();

        // Tooltips always render last.
        drawTooltips(guiGraphics, mouseX, mouseY);
    }

    private void drawTitle(GuiGraphics guiGraphics) {
        guiGraphics.drawString(font, title, leftPos + 8, topPos + 6, TEXT_COLOR, false);
    }

    private void drawRecipeList(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int lx = leftPos + 8;
        int ly = getListTop();

        guiGraphics.fill(lx, ly, lx + LEFT_W, ly + LIST_H, LIST_BG);

        for (int i = 0; i < VISIBLE_ITEMS; i++) {
            int idx = i + scrollOffset;
            if (idx >= filteredRecipes.size()) break;

            RecipeEntry entry = filteredRecipes.get(idx);
            int iy = ly + i * ITEM_H;

            if (idx == selectedIndex) {
                guiGraphics.fill(lx, iy, lx + LEFT_W, iy + ITEM_H, SELECTED_BG);
            }

            guiGraphics.renderItem(entry.result, lx + 2, iy + 2);

            String name = entry.result.getHoverName().getString();
            int maxNameW = LEFT_W - 22;
            if (font.width(name) > maxNameW) {
                name = font.plainSubstrByWidth(name, maxNameW - 2) + "\u2026";
            }
            guiGraphics.drawString(font, name, lx + 20, iy + 5, TEXT_COLOR, false);
        }

        if (filteredRecipes.isEmpty()) {
            Component msg = Component.translatable("gui.patinaworks.no_recipes");
            int tw = font.width(msg);
            guiGraphics.drawString(font, msg, lx + (LEFT_W - tw) / 2, ly + 20, 0xFF888888, false);
        }
    }

    private void drawRecipeDetail(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (selectedIndex < 0 || selectedIndex >= filteredRecipes.size()) return;

        List<RecipeEntry> variants = findVariants(filteredRecipes.get(selectedIndex).result);
        if (variants.isEmpty()) return;
        RecipeEntry selected = variants.get(Math.min(recipeVariantIndex, variants.size() - 1));

        int rx = leftPos + RIGHT_X;
        int ry = topPos + LIST_Y;

        guiGraphics.fill(rx, ry, rx + RIGHT_W, ry + LIST_H, LIST_BG);

        String outputName = selected.result.getHoverName().getString();
        int maxNameW = RIGHT_W - 12;
        if (font.width(outputName) > maxNameW) {
            outputName = font.plainSubstrByWidth(outputName, maxNameW - 2) + "\u2026";
        }
        guiGraphics.drawString(font, outputName, rx + 6, ry + 4, TEXT_COLOR, false);

        int gridTotal = 57;
        int gridLeft = rx + (RIGHT_W - gridTotal) / 2 + 12;
        int gridTop = ry + 24;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int idx = row * 3 + col;
                int sx = gridLeft + col * 19;
                int sy = gridTop + row * 19;
                guiGraphics.fill(sx, sy, sx + 18, sy + 18, SLOT_BG);
                guiGraphics.fill(sx, sy, sx + 1, sy + 18, SLOT_OUTLINE);
                guiGraphics.fill(sx, sy, sx + 18, sy + 1, SLOT_OUTLINE);

                Ingredient ingredient = selected.ingredients.get(idx);
                if (!ingredient.isEmpty()) {
                    ItemStack[] stacks = ingredient.getItems();
                    if (stacks.length > 0) {
                        int animIdx = (int) ((System.currentTimeMillis() / 1000) % stacks.length);
                        guiGraphics.renderItem(stacks[animIdx], sx + 1, sy + 1);
                    }
                }
            }
        }

        int arrowX = gridLeft + 57 + 4;
        int arrowY = gridTop + 20;
        guiGraphics.drawString(font, "\u2192", arrowX, arrowY, TEXT_COLOR, false);

        int outX = arrowX + 16;
        int outY = arrowY - 4;
        guiGraphics.renderItem(selected.result, outX, outY);
        guiGraphics.renderItemDecorations(font, selected.result, outX, outY);

        String countStr = "x" + selected.result.getCount();
        guiGraphics.drawString(font, countStr, outX + 18, outY + 4, TEXT_COLOR, false);

        String variantStr = (recipeVariantIndex + 1) + "/" + variants.size();
        guiGraphics.drawString(font, variantStr, rx + 6, ry + LIST_H - 12, TEXT_COLOR, false);
    }

    private void drawTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int lx = leftPos + 8;
        int ly = getListTop();

        for (int i = 0; i < VISIBLE_ITEMS; i++) {
            int idx = i + scrollOffset;
            if (idx >= filteredRecipes.size()) break;
            RecipeEntry entry = filteredRecipes.get(idx);
            int iy = ly + i * ITEM_H;

            if (mouseX >= lx + 2 && mouseX < lx + 18 && mouseY >= iy + 2 && mouseY < iy + 18) {
                guiGraphics.renderTooltip(font, entry.result, mouseX, mouseY);
                return;
            }
        }

        if (selectedIndex < 0 || selectedIndex >= filteredRecipes.size()) return;
        List<RecipeEntry> variants = findVariants(filteredRecipes.get(selectedIndex).result);
        if (variants.isEmpty()) return;
        RecipeEntry selected = variants.get(Math.min(recipeVariantIndex, variants.size() - 1));

        int rx = leftPos + RIGHT_X;
        int ry = topPos + LIST_Y;
        int gridTotal = 57;
        int gridLeft = rx + (RIGHT_W - gridTotal) / 2 + 12;
        int gridTop = ry + 24;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int idx = row * 3 + col;
                int sx = gridLeft + col * 19;
                int sy = gridTop + row * 19;
                Ingredient ingredient = selected.ingredients.get(idx);
                if (!ingredient.isEmpty()) {
                    ItemStack[] stacks = ingredient.getItems();
                    if (stacks.length > 0 && mouseX >= sx + 1 && mouseX < sx + 17 && mouseY >= sy + 1 && mouseY < sy + 17) {
                        int animIdx = (int) ((System.currentTimeMillis() / 1000) % stacks.length);
                        guiGraphics.renderTooltip(font, stacks[animIdx], mouseX, mouseY);
                        return;
                    }
                }
            }
        }

        int outX = gridLeft + 57 + 4 + 16;
        int outY = gridTop + 20 - 4;
        if (mouseX >= outX && mouseX < outX + 16 && mouseY >= outY && mouseY < outY + 16) {
            guiGraphics.renderTooltip(font, selected.result, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) return true;

        int lx = leftPos + 8;
        int ly = getListTop();

        for (int i = 0; i < VISIBLE_ITEMS; i++) {
            int idx = i + scrollOffset;
            if (idx >= filteredRecipes.size()) break;
            int iy = ly + i * ITEM_H;

            if (mouseX >= lx && mouseX < lx + LEFT_W && mouseY >= iy && mouseY < iy + ITEM_H) {
                if (idx == selectedIndex) {
                    List<RecipeEntry> variants = findVariants(filteredRecipes.get(idx).result);
                    if (variants.size() > 1) {
                        recipeVariantIndex = (recipeVariantIndex + 1) % variants.size();
                    }
                } else {
                    selectedIndex = idx;
                    recipeVariantIndex = 0;
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int lx = leftPos + 8;
        int ly = getListTop();

        if (mouseX >= lx && mouseX < lx + LEFT_W && mouseY >= ly && mouseY < ly + LIST_H) {
            int maxScroll = Math.max(0, filteredRecipes.size() - VISIBLE_ITEMS);
            int delta = (int) -Math.signum(scrollY);
            scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset + delta));
            return true;
        }
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private record RecipeEntry(ResourceLocation id, CraftingRecipe recipe, List<Ingredient> ingredients, ItemStack result) {}
}