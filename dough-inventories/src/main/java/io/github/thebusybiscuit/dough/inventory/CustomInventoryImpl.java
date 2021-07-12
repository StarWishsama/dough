package io.github.thebusybiscuit.dough.inventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

class CustomInventoryImpl implements CustomInventory {

    private final InventoryLayout layout;
    private final String title;
    private Inventory inventory;

    @ParametersAreNonnullByDefault
    CustomInventoryImpl(InventoryLayout layout) {
        Validate.notNull(layout, "The layout cannot be null.");

        this.layout = layout;
        this.title = layout.getTitle();
    }

    void setInventory(@Nonnull Inventory inventory) {
        Validate.notNull(inventory, "The Inventory must not be null.");
        Validate.isTrue(inventory.getSize() == layout.getSize(), "The inventory has a different size.");
        Validate.isTrue(inventory.getHolder() == this, "The Inventory does not seem to belong here. Holder: " + inventory.getHolder());

        this.inventory = inventory;
    }

    private void validate() {
        if (inventory == null) {
            throw new UnsupportedOperationException("No inventory has been set yet.");
        }
    }

    @Override
    public @Nonnull Inventory getInventory() {
        validate();

        return inventory;
    }

    @Override
    public @Nonnull InventoryLayout getLayout() {
        validate();

        return layout;
    }

    @Override
    public String getTitle() {
        validate();

        return title;
    }

    @Override
    public void setAll(SlotGroup group, ItemStack item) {
        validate();

        for (int slot : group) {
            setItem(slot, item);
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean addItem(SlotGroup group, ItemStack item) {
        Validate.notNull(group, "The Slot group cannot be null!");
        Validate.notNull(item, "The Item cannot be null!");
        validate();

        for (int slot : group) {
            ItemStack stack = getItem(slot);

            if (stack == null || stack.getType().isAir()) {
                setItem(slot, item);
                return true;
            } else {
                // TODO: Implement partial stack matching and stack overflowing
            }
        }

        return false;
    }

    @Override
    public void setItem(int slot, @Nullable ItemStack item) {
        validate();

        inventory.setItem(slot, item);
    }

    @Override
    public @Nullable ItemStack getItem(int slot) {
        validate();

        return inventory.getItem(slot);
    }

}