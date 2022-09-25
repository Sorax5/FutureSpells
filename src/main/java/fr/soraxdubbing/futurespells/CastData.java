package fr.soraxdubbing.futurespells;



public class CastData {
    private final float power;
    private final String[] args;

    public CastData(float power, String[] args) {
        this.power = power;
        this.args = args;
    }

    public float power() {
        return power;
    }

    public String[] args() {
        return args;
    }
}