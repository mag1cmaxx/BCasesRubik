package dev.winproject.rubik;

import dev.by1337.bc.CaseBlock;
import dev.by1337.bc.animation.AbstractAnimation;
import dev.by1337.bc.animation.AnimationContext;
import dev.by1337.bc.annotations.AsyncOnly;
import dev.by1337.bc.annotations.SyncOnly;
import dev.by1337.bc.prize.Prize;
import dev.by1337.bc.prize.PrizeSelector;
import dev.by1337.bc.task.AsyncTask;
import dev.by1337.bc.yaml.CashedYamlContext;
import dev.by1337.virtualentity.api.entity.BillboardConstraints;
import dev.by1337.virtualentity.api.entity.ItemDisplayType;
import dev.by1337.virtualentity.api.util.Transformation;
import dev.by1337.virtualentity.api.virtual.VirtualEntity;
import dev.by1337.virtualentity.api.virtual.display.VirtualBlockDisplay;
import dev.by1337.virtualentity.api.virtual.display.VirtualDisplay;
import dev.by1337.virtualentity.api.virtual.display.VirtualItemDisplay;
import dev.by1337.virtualentity.api.virtual.display.VirtualTextDisplay;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.SoundCategory;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.by1337.blib.geom.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public final class RubikAnimation extends AbstractAnimation {
    private static final int ASSEMBLE_TICKS = 24;
    private static final int BURST_TICKS = 16;
    private static final Color GLOW = Color.fromRGB(0xFFD54A);

    private final Settings settings;
    private final Prize winner;
    private final Cube<BlockData> cube;
    private final List<VirtualBlockDisplay> displays = new ArrayList<>();
    private final Vec3d cubeCenter;
    private float spin;
    private volatile boolean prizeGiven;

    public RubikAnimation(CaseBlock caseBlock, AnimationContext context, Runnable onEndCallback,
                          PrizeSelector prizeSelector, CashedYamlContext config, Player player) {
        super(caseBlock, context, onEndCallback, prizeSelector, config, player);
        settings = config.get("settings", v -> v.decode(Settings.CODEC).getOrThrow().getFirst(), Settings.DEFAULT);
        winner = prizeSelector.getRandomPrize();
        cubeCenter = center.add(0, settings.height(), 0);

        Settings.Colors colors = settings.colors();
        Settings.Colors fallback = Settings.Colors.DEFAULT;
        BlockData[] faces = {
                parseBlock(colors.right(), fallback.right()),
                parseBlock(colors.left(), fallback.left()),
                parseBlock(colors.up(), fallback.up()),
                parseBlock(colors.down(), fallback.down()),
                parseBlock(colors.front(), fallback.front()),
                parseBlock(colors.back(), fallback.back()),
                parseBlock(colors.core(), fallback.core())
        };
        cube = new Cube<>(settings.size(), i -> faces[i], RANDOM);
    }

    @Override
    @SyncOnly
    protected void onStart() {
        caseBlock.hideHologram();
    }

    @Override
    @AsyncOnly
    protected void animate() throws InterruptedException {
        for (Cube.Part<BlockData> part : cube.parts()) {
            VirtualBlockDisplay display = VirtualBlockDisplay.create();
            display.setPos(cubeCenter);
            display.setBlock(part.block());
            display.setBrightnessOverride(15, 15);
            display.setTransformation(transformation(cube.pose(part, global(), null, 0, 1)));
            displays.add(display);
            trackEntity(display);
        }
        sleepTicks(2);

        sound(cubeCenter, "block.beacon.power_select", 0.6f, 1.6f);
        for (int t = 1; t <= ASSEMBLE_TICKS; t++) {
            float burst = 1 - t / (float) ASSEMBLE_TICKS;
            frame(null, 0, burst * burst);
            if (t % 4 == 0) {
                sound(cubeCenter, "block.amethyst_block.chime", 0.8f, 0.6f + t / (float) ASSEMBLE_TICKS);
            }
        }
        sound(cubeCenter, "block.note_block.pling", 0.7f, 1.2f);
        idle(12);

        List<Move> scramble = Move.scramble(RANDOM, settings.scramble().moves());
        for (Move move : scramble) {
            turn(move, settings.scramble().ticks(), 1.5f);
            spawnParticle(Particle.ELECTRIC_SPARK, cubeCenter.x, cubeCenter.y, cubeCenter.z, 2, 0.5, 0.5, 0.5, 0.05);
        }
        idle(14);

        List<Move> solution = Move.solution(scramble);
        Settings.Solve solve = settings.solve();
        for (int i = 0; i < solution.size(); i++) {
            boolean last = i >= solution.size() - solve.finalMoves();
            turn(solution.get(i), last ? solve.finalTicks() : solve.ticks(), last ? 0.9f : 1.2f);
        }
        if (!cube.isSolved()) {
            logger.warn("The cube did not come back to the solved state");
        }

        sound(cubeCenter, "ui.toast.challenge_complete", 0.8f, 1);
        setGlowing(true);
        idle(16);
        setGlowing(false);

        sound(cubeCenter, "entity.firework_rocket.blast", 1, 1);
        sound(cubeCenter, "entity.firework_rocket.twinkle", 0.8f, 1);
        spawnParticle(Particle.FIREWORK, cubeCenter.x, cubeCenter.y, cubeCenter.z, 40, 0.2, 0.2, 0.2, 0.15);
        for (int t = 1; t <= BURST_TICKS; t++) {
            float burst = t / (float) BURST_TICKS;
            frame(null, 0, (float) Math.sqrt(burst));
        }
        displays.forEach(this::removeEntity);

        reveal();
        sleepTicks(settings.showTicks());
    }

    private void turn(Move move, int ticks, float pitch) throws InterruptedException {
        sound(cubeCenter, "ui.button.click", 0.35f, pitch + RANDOM.nextFloat() * 0.3f);
        for (int t = 1; t <= ticks; t++) {
            float progress = t / (float) ticks;
            frame(move, progress * progress * (3 - 2 * progress), 0);
        }
        cube.apply(move);
    }

    private void idle(int ticks) throws InterruptedException {
        for (int t = 0; t < ticks; t++) {
            frame(null, 0, 0);
        }
    }

    private void frame(Move active, float progress, float burst) throws InterruptedException {
        spin += (float) Math.toRadians(settings.spin());
        Quaternionf global = global();
        List<Cube.Part<BlockData>> parts = cube.parts();
        for (int i = 0; i < parts.size(); i++) {
            VirtualBlockDisplay display = displays.get(i);
            display.setTransformationInterpolationDuration(2);
            display.setTransformationInterpolationDelay(0);
            display.setTransformation(transformation(cube.pose(parts.get(i), global, active, progress, burst)));
        }
        sleepTicks(1);
    }

    private Quaternionf global() {
        return new Quaternionf()
                .rotationY(spin)
                .rotateX((float) Math.toRadians(settings.tilt()))
                .rotateY((float) (Math.PI / 4));
    }

    private void setGlowing(boolean glowing) {
        for (VirtualBlockDisplay display : displays) {
            display.setGlowing(glowing);
            if (glowing) display.setGlowColorOverride(GLOW, 255);
        }
    }

    private void reveal() throws InterruptedException {
        VirtualItemDisplay item = VirtualItemDisplay.create();
        item.setPos(cubeCenter);
        item.setItem(winner.itemStack());
        item.setDisplayId(ItemDisplayType.FIXED);
        item.setBrightnessOverride(15, 15);
        item.setGlowing(true);
        item.setGlowColorOverride(GLOW, 255);
        item.setTransformation(scaled(0.01f, 0));
        trackEntity(item);
        sound(cubeCenter, "entity.player.levelup", 0.7f, 1.2f);
        sync(this::givePrizeOnce).start();
        sleepTicks(1);

        new AsyncTask() {
            private int step;

            @Override
            public void run() {
                step++;
                animate(item, scaled(Math.min(1, step / 2f), (float) Math.toRadians(step * 90)), 10);
                spawnParticle(Particle.END_ROD, cubeCenter.x, cubeCenter.y, cubeCenter.z, 2, 0.4, 0.4, 0.4, 0.01);
            }
        }.timer().delay(10).start(this);
        sleepTicks(20);

        Component name = winner.displayNameComponent();
        VirtualTextDisplay label = VirtualTextDisplay.create();
        label.setPos(cubeCenter.add(0, 0.85, 0));
        label.setText(name == null ? message.componentBuilder(winner.displayName()) : name);
        label.setBillboardConstraints(BillboardConstraints.CENTER);
        label.setShadow(true);
        label.setDefaultBackground(false);
        label.setBackgroundColor(Color.BLACK, 0);
        label.setBrightnessOverride(15, 15);
        label.setTransformation(scaled(0.01f, 0));
        trackEntity(label);
        sleepTicks(1);
        animate(label, scaled(1, 0), 6);
    }

    @Override
    @SyncOnly
    protected void onEnd() {
        caseBlock.showHologram();
        givePrizeOnce();
    }

    @SyncOnly
    private void givePrizeOnce() {
        if (prizeGiven) return;
        prizeGiven = true;
        caseBlock.givePrize(winner, player);
    }

    @Override
    @SyncOnly
    protected void onClick(VirtualEntity entity, Player clicker) {
    }

    @Override
    @SyncOnly
    public void onInteract(PlayerInteractEvent event) {
    }

    private void sound(Vec3d pos, String key, float volume, float pitch) {
        Location at = pos.toLocation(world);
        forEachViewers(viewer -> viewer.playSound(at, key, SoundCategory.MASTER, volume, pitch));
    }

    private BlockData parseBlock(String value, String fallback) {
        try {
            return Bukkit.createBlockData(value);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid block '{}', {} is used instead", value, fallback);
            return Bukkit.createBlockData(fallback);
        }
    }

    private static Transformation transformation(Cube.Pose pose) {
        return new Transformation(pose.translation(), pose.rotation(), pose.scale(), new Quaternionf());
    }

    private static Transformation scaled(float scale, float yaw) {
        return new Transformation(new Vector3f(), new Quaternionf().rotationY(yaw), new Vector3f(scale), new Quaternionf());
    }

    private static void animate(VirtualDisplay display, Transformation target, int ticks) {
        display.setTransformationInterpolationDuration(ticks);
        display.setTransformationInterpolationDelay(0);
        display.setTransformation(target);
    }
}
