import java.util.*;
import java.lang.reflect.Method;

public class GenericAi {
    float[][][] parameters;
    float[][] weight;
    float[][][] inputs; // {(x, y), ...}
    float[][][] outputs; // {(x, y), ...}
    Method[] methods;

    public GenericAi(int[] numLayers, float[][][] in, float[][][] out) {
        // Randomly initialize parameters and weights
        // First array in parameters has dimensions (input dimension) x numLayers[0]
        // First array in weight has dimension numlayers[0]
        Random rand = new Random();
        inputs = in;
        outputs = out;
        parameters = new float[numLayers.length][][];
        weight = new float[numLayers.length][];
        for (int i = 0; i < numLayers.length; i++) {
            if (i == 0) {
                parameters[i] = new float[inputs.length][numLayers[i]];
            } else {
                parameters[i] = new float[numLayers[i-1]][numLayers[i]];
            }
            weight[i] = new float[numLayers[i]];
        }
        for (int i = 0; i < numLayers.length; i++) {
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
    
    public float[] applyOneStep(float[] input, int step) {
        float[] output = new float[parameters[step].length];
        for (int i = 0; i < parameters[step][0].length; i++) {
            for (int j = 0; j < parameters[step].length; j++) {
                output[i] += input[j] * parameters[step][j][i];
            }
            output[i] += weight[step][i];
            try {
                output[i] = (Float) methods[step].invoke(null, output[i]); // Placeholder for method application
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
}
