package dev.winproject.rubik;

import dev.by1337.bc.addon.AbstractAddon;
import dev.by1337.bc.animation.AnimationRegistry;
import org.by1337.blib.util.SpacedNameKey;

import java.io.File;

public final class RubikAddon extends AbstractAddon {
    static final String PROVIDER = "mag1cmax:rubik";

    @Override
    protected void onEnable() {
        saveResourceToFile("rubik.yml", new File(getPlugin().getDataFolder(), "animations/rubik.yml"));
        AnimationRegistry.INSTANCE.register(PROVIDER, RubikAnimation::new);
    }

    @Override
    protected void onDisable() {
        AnimationRegistry.INSTANCE.unregister(new SpacedNameKey(PROVIDER));
    }
}
