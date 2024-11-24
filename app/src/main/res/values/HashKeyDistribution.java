public class HashKeyDistribution {
    
    // Method to calculate hash distribution
    public static int[] hashDistribution(int tableSize, int start, int end) {
        int[] distribution = new int[tableSize];
        for (int num = start; num <= end; num++) {
            int hashKey = num % tableSize; // Calculate the hash key
            distribution[hashKey]++;      // Increment the count for this hash key
        }
        return distribution;
    }