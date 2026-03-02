import java.util.*;
import java.lang.reflect.Method;

public class Creature implements Comparable<Creature> {
    float speed;
    float[][][] parameters;
    float[][] weight;
    int[][] inputs; // {(x, y), ...}
    int[][] outputs; // {(x, y), ...}
    Method[] methods;
    float energy;
    int x, y;
    int width, height;
    int startX, startY;
    int[] numLayers;

    public Creature(float speed, int[] numLayers, int[][] in, int[][] out, float energy, int x, int y, int width, int height) {
        this.speed = speed;
        // Randomly initialize parameters and weights
        // First array in parameters has dimensions numLayers[0] x (input dimension)
        // Second has dimensions numLayers[1] x numLayers[0], etc.
        // Last has (output dimension) x numLayers[numLayers.length-1]
        // First array in weight has dimension numlayers[0]
        // Second has numLayers[1], etc.
        // Last has output dimension
        Random rand = new Random();
        inputs = in;
        outputs = out;
        parameters = new float[numLayers.length+1][][];
        weight = new float[numLayers.length+1][];
        for (int i = 0; i < numLayers.length; i++) {
            if (i == 0) {
                parameters[i] = new float[numLayers[i]][inputs.length];
            } else {
                parameters[i] = new float[numLayers[i]][numLayers[i-1]];
            }
            weight[i] = new float[numLayers[i]];
        }
        parameters[numLayers.length] = new float[outputs.length][numLayers[numLayers.length-1]];
        weight[numLayers.length] = new float[outputs.length];
        for (int i = 0; i < parameters.length; i++) {
            for (int j = 0; j < parameters[i].length; j++) {
                for (int k = 0; k < parameters[i][j].length; k++) {
                    parameters[i][j][k] = rand.nextFloat() * 4 - 2;
                }
            }
            for (int j = 0; j < weight[i].length; j++) {
                weight[i][j] = rand.nextFloat() * 4 - 2;
            }
        }
        this.energy = energy;
        this.startX = x;
        this.startY = y;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.numLayers = numLayers;
    }

    public void setMethods() {
        try {
            methods = new Method[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                methods[i] = this.getClass().getMethod("line", float.class); // Placeholder
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } // Placeholder for setting methods, should be able to set different methods for each layer

    }

    public static float line(float x) {
        return x;
    }

    public static float sigmoid(float x) {
        return (float)(1/(1+Math.exp(-x)));
    }

    public static float relu(float x) {
        return Math.max(0, x);
    }
    
    public float[] applyOneStep(float[] input, int step) {
        float[] output = new float[parameters[step].length];
        for (int i = 0; i < parameters[step].length; i++) {
            for (int j = 0; j < parameters[step][0].length; j++) {
                output[i] += input[j] * parameters[step][i][j];
            }
            output[i] += weight[step][i];
            try {
                output[i] = (Float) methods[step].invoke(null, output[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return output;
        // Should be matrix and vector multiplication using parameters
        // Then add weight to result
        // Also should apply methods at end
    }

    public float[] apply(float[] input) {
        for (int i = 0; i < parameters.length; i++) {
            input = applyOneStep(input, i);
        }
        return input;
    }

    public void mutate() {
        for (int i = 0; i < parameters.length; i++) {
            for (int j = 0; j < parameters[i].length; j++) {
                for (int k = 0; k < parameters[i][j].length; k++) {
                    if (Math.random() < 0.5) {
                        parameters[i][j][k] += (Math.random() - 0.5) * 0.2;
                    }
                }
            }
            for (int j = 0; j < weight[i].length; j++) {
                if (Math.random() < 0.5) {
                    weight[i][j] += (Math.random() - 0.5) * 0.2;
                }
            }
        }
    }

    public void ai(Environment e) {
        float[] in = new float[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            in[i] = e.getTile((int) x+inputs[i][0], (int)y+inputs[i][1]);
        }
        float[] out = apply(in);
        int largest = 0;
        for (int i = 1; i < out.length; i++) {
            if (out[i] > out[largest]) {
                largest = i;
            }
        }
        move(inputs[largest][0], inputs[largest][1], e);
    }

    public void move(int dx, int dy, Environment e) {
        x = (x+dx + e.tiles.length) % e.tiles.length;
        y = (y+dy + e.tiles[0].length) % e.tiles[0].length;
    }

    public void display() {
        Display.sketch.fill(0, 0, 0);
        Display.sketch.ellipse(x * Display.sketch.width/204 + Display.sketch.width/204/2, y * Display.sketch.height/115 + Display.sketch.height/115/2, Display.sketch.width/204*4/5, Display.sketch.height/115*4/5);
    }
    
    public static Creature deepCopy(Creature original) {
        Creature result = new Creature(original.speed, original.numLayers, original.inputs, original.outputs, 0, original.x, original.y, original.width, original.height);
        float[][][] newParams = new float[original.parameters.length][][];
        float[][] newWeights = new float[original.weight.length][];
        for (int i = 0; i < original.parameters.length; i++) {
            newParams[i] = new float[original.parameters[i].length][original.parameters[i][0].length];
            System.arraycopy(original.parameters[i], 0, newParams[i], 0, original.parameters[i].length);
        }
        System.arraycopy(original.weight, 0, newWeights, 0, original.weight.length);
        result.parameters = newParams;
        result.weight = newWeights;
        result.methods = original.methods; // Methods are immutable, so we can just copy the reference
        result.setMethods();
        return result;
    }

    public void resetEnergy() {
        energy = 0;
    }

    public int compareTo(Creature other) {
        // +1f*(float)(Math.abs(this.x - this.startX) + Math.abs(this.y - this.startY))
        // +1f*(float)(Math.abs(other.x - other.startX) + Math.abs(other.y - other.startY))
        // Add above to encourage exploration, but for now just use energy
        return Float.compare(this.energy, other.energy);
    }

    public void update(Environment e) {
        ai(e);
        if (e.tiles[x][y] == 1) {
            energy += 1;
        } else if (e.tiles[x][y] == 2) {
            energy -= 1;
        } else if (e.tiles[x][y] == 0) {
            energy -= 0.1;
        }
    }
}
