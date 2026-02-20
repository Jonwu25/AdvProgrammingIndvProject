import java.util.*;
import java.lang.reflect.Method;

public class GenericAi {
    float[][][] parameters;
    float[][] weight;
    Method[] methods;

    public GenericAi(int[] numLayers) {
        // Randomly initialize parameters and weights
        // First array in parameters has dimensions (input dimension) x numLayers[0]
        // First array in weight has dimension numlayers[0]
    }
    
    public float[] applyOneStep(float[] input, int step) {
        return input; // Placeholder
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
        // Should randomly change some parameters and weights
    }
}
