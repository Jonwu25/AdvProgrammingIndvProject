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
    int x, y, oldX, oldY;
    float velx, vely;
    float displayX, displayY;
    int width, height;
    int startX, startY;
    int[] numLayers;
    int numMoves;
    Environment env;

    public Creature(float speed, int[] numLayers, int[][] in, int[][] out, float energy, int x, int y, int width, int height) {
        this.speed = speed;
        this.energy = energy;
        this.x = x;
        this.y = y;
        this.startX = x;
        this.startY = y;
        this.width = width;
        this.height = height;
        this.numLayers = numLayers;
        numMoves = 0;
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

    public void mutate(float mutationAmount, float mutationRate) {
        for (int i = 0; i < parameters.length; i++) {
            for (int j = 0; j < parameters[i].length; j++) {
                for (int k = 0; k < parameters[i][j].length; k++) {
                    if (Math.random() < mutationRate) {
                        parameters[i][j][k] += (Math.random() - 0.5) * mutationAmount;
                    }
                }
            }
            for (int j = 0; j < weight[i].length; j++) {
                if (Math.random() < mutationRate) {
                    weight[i][j] += (Math.random() - 0.5) * mutationAmount;
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
        oldX = x;
        oldY = y;
        move(inputs[largest][0], inputs[largest][1], e);
        env = e;
    }

    public void move(int dx, int dy, Environment e) {
        x = (x+dx + e.tiles.length) % e.tiles.length;
        y = (y+dy + e.tiles[0].length) % e.tiles[0].length;
        velx = dx;
        vely = dy;
        numMoves++;
    }

    public void changeColor(Environment env) {
        int[] color = env.getColor(energy);
        Display.sketch.fill((2*color[0])/3, (2*color[1])/3, (2*color[2])/3);
    }

    public void display(float xOffset, float yOffset, float sidelength, int width, int height, Environment env) {
        changeColor(env);
        Display.sketch.strokeWeight(sidelength/50);
        Display.sketch.ellipse(displayX * sidelength + xOffset + sidelength/2, displayY * sidelength + yOffset + sidelength/2, sidelength*4/5, sidelength*4/5);
        if (displayX > width - 1) {
            Display.sketch.ellipse((displayX - width) * sidelength + xOffset + sidelength/2, displayY * sidelength + yOffset + sidelength/2, sidelength*4/5, sidelength*4/5);
            Display.sketch.noStroke();
            Display.sketch.fill(255);
            Display.sketch.rect((width) * sidelength + xOffset, (displayY) * sidelength + yOffset, sidelength, sidelength);
            Display.sketch.rect((-1) * sidelength + xOffset, (displayY) * sidelength + yOffset, sidelength, sidelength);
            Display.sketch.stroke(0);
            Display.sketch.line((width) * sidelength + xOffset, (displayY) * sidelength + yOffset, (width) * sidelength + xOffset, (displayY+1) * sidelength + yOffset);
            Display.sketch.line((0) * sidelength + xOffset, (displayY) * sidelength + yOffset, (0) * sidelength + xOffset, (displayY+1) * sidelength + yOffset);
            Display.sketch.fill(0);
        }
        if (displayX < 0) {
            Display.sketch.ellipse((displayX + width) * sidelength + xOffset + sidelength/2, displayY * sidelength + yOffset + sidelength/2, sidelength*4/5, sidelength*4/5);
            Display.sketch.noStroke();
            Display.sketch.fill(255);
            Display.sketch.rect((width) * sidelength + xOffset, (displayY) * sidelength + yOffset, sidelength, sidelength);
            Display.sketch.rect((-1) * sidelength + xOffset, (displayY) * sidelength + yOffset, sidelength, sidelength);
            Display.sketch.stroke(0);
            Display.sketch.line((width) * sidelength + xOffset, (displayY) * sidelength + yOffset, (width) * sidelength + xOffset, (displayY+1) * sidelength + yOffset);
            Display.sketch.line((0) * sidelength + xOffset, (displayY) * sidelength + yOffset, (0) * sidelength + xOffset, (displayY+1) * sidelength + yOffset);
            Display.sketch.fill(0);
        }
        if (displayY > height - 1) {
            Display.sketch.ellipse(displayX * sidelength + xOffset + sidelength/2, (displayY - height) * sidelength + yOffset + sidelength/2, sidelength*4/5, sidelength*4/5);
            Display.sketch.noStroke();
            Display.sketch.fill(255);
            Display.sketch.rect((displayX) * sidelength + xOffset, (height) * sidelength + yOffset, sidelength, sidelength);
            Display.sketch.rect((displayX) * sidelength + xOffset, (-1) * sidelength + yOffset, sidelength, sidelength);
            Display.sketch.stroke(0);
            Display.sketch.line((displayX) * sidelength + xOffset, (height) * sidelength + yOffset, (displayX+1) * sidelength + xOffset, (height) * sidelength + yOffset);
            Display.sketch.line((displayX) * sidelength + xOffset, (0) * sidelength + yOffset, (displayX+1) * sidelength + xOffset, (0) * sidelength + yOffset);
            Display.sketch.fill(0);
        }
        if (displayY < 0) {
            Display.sketch.ellipse(displayX * sidelength + xOffset + sidelength/2, (displayY + height) * sidelength + yOffset + sidelength/2, sidelength*4/5, sidelength*4/5);
            Display.sketch.noStroke();
            Display.sketch.fill(255);
            Display.sketch.rect((displayX) * sidelength + xOffset, (height) * sidelength + yOffset, sidelength, sidelength);
            Display.sketch.rect((displayX) * sidelength + xOffset, (-1) * sidelength + yOffset, sidelength, sidelength);
            Display.sketch.stroke(0);
            Display.sketch.line((displayX) * sidelength + xOffset, (height) * sidelength + yOffset, (displayX+1) * sidelength + xOffset, (height) * sidelength + yOffset);
            Display.sketch.line((displayX) * sidelength + xOffset, (0) * sidelength + yOffset, (displayX+1) * sidelength + xOffset, (0) * sidelength + yOffset);
            Display.sketch.fill(0);
        }
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

        // Second part is for balancing
        return Float.compare(this.energy - numMoves*env.getTile(this.startX, this.startY)/2, other.energy - other.numMoves*env.getTile(other.startX, other.startY)/2);
    }

    public void update(Environment e) {
        ai(e);
        // if (e.tiles[x][y] == 1f) {
        //     energy += 1;
        // } else if (e.tiles[x][y] == 2f) {
        //     energy -= 1;
        // } else if (e.tiles[x][y] == 0f) {
        //     energy -= 0.1;
        // }
        energy += e.getTile(x, y);
    }

    public void moveUpdate(int frame, int tickSpeed) {
        float ratio = (frame % tickSpeed) / (float) tickSpeed;
        displayX = ratio * velx + oldX;
        displayY = ratio * vely + oldY;
    }

    public void setStart(int startX, int startY) {
        this.startX = startX;
        this.startY = startY;
    }
}
