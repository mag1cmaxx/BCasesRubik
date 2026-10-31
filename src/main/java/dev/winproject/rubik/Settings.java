package dev.winproject.rubik;

import blib.com.mojang.serialization.Codec;
import blib.com.mojang.serialization.codecs.RecordCodecBuilder;

record Settings(
        double height,
        float size,
        float spin,
        float tilt,
        Scramble scramble,
        Solve solve,
        int showTicks,
        Colors colors
) {
    static final Settings DEFAULT = new Settings(2.3, 0.45f, 3, 30, Scramble.DEFAULT, Solve.DEFAULT, 80, Colors.DEFAULT);

    static final Codec<Settings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.doubleRange(0, 16).optionalFieldOf("height", DEFAULT.height).forGetter(Settings::height),
            Codec.floatRange(0.1f, 2).optionalFieldOf("size", DEFAULT.size).forGetter(Settings::size),
            Codec.floatRange(0, 45).optionalFieldOf("spin", DEFAULT.spin).forGetter(Settings::spin),
            Codec.floatRange(0, 90).optionalFieldOf("tilt", DEFAULT.tilt).forGetter(Settings::tilt),
            Scramble.CODEC.optionalFieldOf("scramble", DEFAULT.scramble).forGetter(Settings::scramble),
            Solve.CODEC.optionalFieldOf("solve", DEFAULT.solve).forGetter(Settings::solve),
            Codec.intRange(0, 1200).optionalFieldOf("show_ticks", DEFAULT.showTicks).forGetter(Settings::showTicks),
            Colors.CODEC.optionalFieldOf("colors", DEFAULT.colors).forGetter(Settings::colors)
    ).apply(instance, Settings::new));

    record Scramble(int moves, int ticks) {
        static final Scramble DEFAULT = new Scramble(20, 2);

        static final Codec<Scramble> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.intRange(1, 100).optionalFieldOf("moves", DEFAULT.moves).forGetter(Scramble::moves),
                Codec.intRange(1, 40).optionalFieldOf("ticks", DEFAULT.ticks).forGetter(Scramble::ticks)
        ).apply(instance, Scramble::new));
    }

    record Solve(int ticks, int finalTicks, int finalMoves) {
        static final Solve DEFAULT = new Solve(4, 10, 3);

        static final Codec<Solve> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.intRange(1, 40).optionalFieldOf("ticks", DEFAULT.ticks).forGetter(Solve::ticks),
                Codec.intRange(1, 80).optionalFieldOf("final_ticks", DEFAULT.finalTicks).forGetter(Solve::finalTicks),
                Codec.intRange(0, 100).optionalFieldOf("final_moves", DEFAULT.finalMoves).forGetter(Solve::finalMoves)
        ).apply(instance, Solve::new));
    }

    record Colors(String up, String down, String front, String back, String right, String left, String core) {
        static final Colors DEFAULT = new Colors(
                "white_concrete", "yellow_concrete", "lime_concrete", "blue_concrete",
                "red_concrete", "orange_concrete", "black_concrete"
        );

        static final Codec<Colors> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.optionalFieldOf("up", DEFAULT.up).forGetter(Colors::up),
                Codec.STRING.optionalFieldOf("down", DEFAULT.down).forGetter(Colors::down),
                Codec.STRING.optionalFieldOf("front", DEFAULT.front).forGetter(Colors::front),
                Codec.STRING.optionalFieldOf("back", DEFAULT.back).forGetter(Colors::back),
                Codec.STRING.optionalFieldOf("right", DEFAULT.right).forGetter(Colors::right),
                Codec.STRING.optionalFieldOf("left", DEFAULT.left).forGetter(Colors::left),
                Codec.STRING.optionalFieldOf("core", DEFAULT.core).forGetter(Colors::core)
        ).apply(instance, Colors::new));
    }
}
