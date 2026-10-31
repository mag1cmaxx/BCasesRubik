package dev.winproject.rubik;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

final class Cube<B> {
    private static final float CORE = 0.48f;
    private static final float STICKER = 0.41f;
    private static final float THICKNESS = 0.03f;

    private final float size;
    private final List<Piece<B>> pieces = new ArrayList<>();
    private final List<Part<B>> parts = new ArrayList<>();

    Cube(float size, Function<Integer, B> faces, Random random) {
        this.size = size;
        B core = faces.apply(6);
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    Vector3i grid = new Vector3i(x, y, z);
                    Piece<B> piece = new Piece<>(grid, randomAxis(random), (float) ((random.nextBoolean() ? 1 : -1) * Math.PI * (1 + random.nextFloat())));
                    Vector3f center = new Vector3f(x, y, z).mul(size);
                    piece.parts.add(new Part<>(piece, core, center, new Vector3f(CORE * size)));
                    int[] coords = {x, y, z};
                    for (int axis = 0; axis < 3; axis++) {
                        if (coords[axis] == 0) continue;
                        Vector3f normal = new Vector3f();
                        normal.setComponent(axis, coords[axis]);
                        Vector3f half = new Vector3f(STICKER * size);
                        half.setComponent(axis, THICKNESS * size);
                        Vector3f stickerCenter = new Vector3f(normal).mul((CORE + THICKNESS * 0.9f) * size).add(center);
                        B block = faces.apply(axis * 2 + (coords[axis] > 0 ? 0 : 1));
                        piece.parts.add(new Part<>(piece, block, stickerCenter, half));
                    }
                    pieces.add(piece);
                    parts.addAll(piece.parts);
                }
            }
        }
    }

    List<Part<B>> parts() {
        return parts;
    }

    void apply(Move move) {
        Quaternionf rotation = move.rotation(1);
        for (Piece<B> piece : pieces) {
            if (!piece.inLayer(move)) continue;
            Vector3f grid = new Vector3f(piece.grid).rotate(rotation);
            piece.grid.set(Math.round(grid.x), Math.round(grid.y), Math.round(grid.z));
            for (Part<B> part : piece.parts) {
                part.center.rotate(rotation);
                rotation.mul(part.orientation, part.orientation).normalize();
            }
        }
    }

    boolean isSolved() {
        for (Piece<B> piece : pieces) {
            if (!piece.grid.equals(piece.home)) return false;
            for (Part<B> part : piece.parts) {
                if (Math.abs(Math.abs(part.orientation.w) - 1) > 1.0E-3) return false;
            }
        }
        return true;
    }

    Pose pose(Part<B> part, Quaternionf global, Move active, float progress, float burst) {
        Piece<B> piece = part.piece;
        Vector3f center = new Vector3f(part.center);
        Quaternionf orientation = new Quaternionf(part.orientation);
        Vector3f half = new Vector3f(part.half);

        if (active != null && piece.inLayer(active)) {
            Quaternionf turn = active.rotation(progress);
            center.rotate(turn);
            turn.mul(orientation, orientation);
        }
        if (burst > 0) {
            Vector3f pieceCenter = new Vector3f(piece.grid).mul(size);
            Quaternionf spin = new Quaternionf().rotationAxis(piece.spinAngle * burst, piece.spinAxis);
            float keep = 1 - burst;
            center.sub(pieceCenter).mul(keep).rotate(spin).add(pieceCenter.mul(1 + 3 * burst));
            spin.mul(orientation, orientation);
            half.mul(Math.max(keep, 0.001f));
        }
        center.rotate(global);
        global.mul(orientation, orientation);

        Vector3f translation = new Vector3f(center).sub(new Vector3f(half).rotate(orientation));
        return new Pose(translation, orientation, half.mul(2));
    }

    private static Vector3f randomAxis(Random random) {
        Vector3f axis = new Vector3f(random.nextFloat() - 0.5f, random.nextFloat() - 0.5f, random.nextFloat() - 0.5f);
        return axis.lengthSquared() < 1.0E-4 ? new Vector3f(0, 1, 0) : axis.normalize();
    }

    record Pose(Vector3f translation, Quaternionf rotation, Vector3f scale) {
    }

    static final class Piece<B> {
        private final Vector3i grid;
        private final Vector3i home;
        private final Vector3f spinAxis;
        private final float spinAngle;
        private final List<Part<B>> parts = new ArrayList<>();

        private Piece(Vector3i grid, Vector3f spinAxis, float spinAngle) {
            this.grid = grid;
            this.home = new Vector3i(grid);
            this.spinAxis = spinAxis;
            this.spinAngle = spinAngle;
        }

        private boolean inLayer(Move move) {
            return grid.get(move.axis()) == move.layer();
        }
    }

    static final class Part<B> {
        private final Piece<B> piece;
        private final B block;
        private final Vector3f center;
        private final Vector3f half;
        private final Quaternionf orientation = new Quaternionf();

        private Part(Piece<B> piece, B block, Vector3f center, Vector3f half) {
            this.piece = piece;
            this.block = block;
            this.center = center;
            this.half = half;
        }

        B block() {
            return block;
        }
    }
}
