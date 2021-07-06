package github.pitbox46.spectatorshuffle;

import net.minecraft.util.math.vector.Vector3d;

import java.util.ArrayList;
import java.util.List;

public class EntropyScore extends ArrayList<Double> {
    private double total;
    //Convenience. Used for checking if the player has moved
    public Vector3d position;

    public EntropyScore(int capacity) {
        super(capacity);
        this.total = 0;
    }

    @Override
    public void add(int index, Double score) {
        total += score;
        super.add(index, score);
    }

    @Override
    public Double remove(int index) {
        total -= this.get(index);
        return super.remove(index);
    }

    @Override
    public Double set(int index, Double score) {
        this.total -= this.get(index);
        this.total += score;
        return super.set(index, score);
    }

    public double getTotal() {
        return total;
    }
}
