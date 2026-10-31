package dev.winproject.rubik;

import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

record Move(int axis, int layer, int quarter) {

    Move inverse() {
        return new Move(axis, layer, -quarter);
    }

    Quaternionf rotation(float progress) {
        float angle = (float) (quarter * Math.PI / 2 * progress);
        return switch (axis) {
            case 0 -> new Quaternionf().rotationX(angle);
            case 1 -> new Quaternionf().rotationY(angle);
            default -> new Quaternionf().rotationZ(angle);
        };
    }

    static List<Move> scramble(Random random, int count) {
        List<Move> moves = new ArrayList<>(count);
        Move previous = null;
        while (moves.size() < count) {
            Move move = new Move(random.nextInt(3), random.nextInt(3) - 1, random.nextBoolean() ? 1 : -1);
            if (previous != null && previous.axis == move.axis && previous.layer == move.layer) continue;
            moves.add(move);
            previous = move;
        }
        return moves;
    }

    static List<Move> solution(List<Move> scramble) {
        List<Move> moves = new ArrayList<>(scramble.size());
        for (int i = scramble.size() - 1; i >= 0; i--) {
            moves.add(scramble.get(i).inverse());
        }
        return moves;
    }
}
