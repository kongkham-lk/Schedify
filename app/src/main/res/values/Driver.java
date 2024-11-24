
public class Driver
{
    public static void main(String[] args) {
        int start = 1;      // Start of range
        int end = 1000;     // End of range
        int[] tableSizes = {16, 17}; // Table sizes to test
    
        for (int size : tableSizes) {
            printDistribution(size, start, end);
            System.out.println("--");
        }
    }
    

}
