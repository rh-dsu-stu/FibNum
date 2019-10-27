import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.io.*;
import java.util.Arrays;
import java.util.Random;

public class FibonacciNumbers {
    static ThreadMXBean bean = ManagementFactory.getThreadMXBean();
    // defining variables
    static int numberOfTrials = 100000; // 50 trials for recur. 100000 for dp and matrix
    static int MAXINPUTSIZE = 47; // 41 for recur. dp and matrix = 92 = 7540113804746346429 93 = overflow
    static int MININPUTSIZE = 0; // first number in fibonacci sequence
    static int inputSize = 0;

    static String ResultsFolderPath = "/home/ryan/Results/"; // pathname to results folder
    static FileWriter resultsFile;
    static PrintWriter resultsWriter;

    public static void main(String[] args){
        // loop for testing each function
        System.out.println("Input   Output");
        for ( inputSize = MININPUTSIZE; inputSize <= MAXINPUTSIZE; inputSize++){
            System.out.println(inputSize  + "        " + FibRecur(inputSize));
            //System.out.println(inputSize  + "        " + FibMatrix(inputSize));
            //System.out.println(inputSize  + "        " + FibRecurDP(inputSize));
            //System.out.println(inputSize  + "        " + FibLoop(inputSize));
        }

        // print set up for each function test
        /* **********************************************UNCOMMENT ONE******************************************/
/*        System.out.println("Running first full experiment ...");
        runFullExperiment("FibRecur-Exp1-ThrowAway.txt");
        System.out.println("Running second full experiment ...");
        runFullExperiment("FibRecur-Exp2.txt");
        System.out.println("Running third full experiment ...");
        runFullExperiment("FibRecur-Exp3.txt");
*/
/*
        System.out.println("Running first full experiment ...");
        runFullExperiment("FibLoop-Exp1-ThrowAway.txt");
        System.out.println("Running second full experiment ...");
        runFullExperiment("FibLoop-Exp2.txt");
        System.out.println("Running third full experiment ...");
        runFullExperiment("FibLoop-Exp3.txt");
*/
/*
       System.out.println("Running first full experiment ...");
        runFullExperiment("FibRecurDP-Exp1-ThrowAway.txt");
        System.out.println("Running second full experiment ...");
        runFullExperiment("FibRecurDP-Exp2.txt");
        System.out.println("Running third full experiment ...");
        runFullExperiment("FibRecurDP-Exp3.txt");
*/

        System.out.println("Running first full experiment ...");
        runFullExperiment("FibMatrix-Exp1-ThrowAway.txt");
        System.out.println("Running second full experiment ...");
        runFullExperiment("FibMatrix-Exp2.txt");
        System.out.println("Running third full experiment ...");
        runFullExperiment("FibMatrix-Exp3.txt");

        /* **********************************************UNCOMMENT ONE******************************************/
    }

    // function to run each of the experiments on the functions
    static void runFullExperiment(String resultsFileName) {

        // making sure that we have results files available or can create new
        try {
            resultsFile = new FileWriter(ResultsFolderPath + resultsFileName);
            resultsWriter = new PrintWriter(resultsFile);
        } catch (Exception e) {
            System.out.println("*****!!!!!  Had a problem opening the results file " + ResultsFolderPath + resultsFileName);
            return;
        }

        ThreadCPUStopWatch BatchStopwatch = new ThreadCPUStopWatch(); // for timing an entire set of trials
        //ThreadCPUStopWatch TrialStopwatch = new ThreadCPUStopWatch(); // for timing an individual trial

        resultsWriter.println("#InputValue(x)    AverageTime     InputSize(n)"); // # marks a comment in gnuplot data
        resultsWriter.flush();

        // for each size of input we want to test: in this case incrementing by 1

        for (int inputSize = MININPUTSIZE; inputSize <= MAXINPUTSIZE; inputSize++) {
            /* repeat for desired number of trials (for a specific size of input)... */
            System.out.println("Running test for input size " + inputSize + " ... ");
            // will hold total amount of time
            // will reset after each batch of trials
            long batchElapsedTime = 0;

            /* force garbage collection before each batch of trials run so it is not included in the time */
            System.gc();
            System.out.print("    Running trial batch...");
            BatchStopwatch.start(); // comment this line if timing trials individually

            // run the trials
            for (int trial = 0; trial < numberOfTrials; trial++) {

                //actual beginning of trial
                /* **********************************************UNCOMMENT ONE******************************************/
                //FibRecur(inputSize);
                //FibLoop(inputSize);
                //FibRecurDP(inputSize);
                FibMatrix(inputSize);
                /* **********************************************UNCOMMENT ONE^^^******************************************/
                //System.out.println("....done.");// *** uncomment this line if timing trials individually
            }

            batchElapsedTime = BatchStopwatch.elapsedTime(); // *** comment this line if timing trials individually

            double averageTimePerTrialInBatch = (double) batchElapsedTime / (double) numberOfTrials; // calculate the average time per trial in this batch
            /* print data for this size of input */

            // this block of code determines the number of bits necessary for each input
            // log(x)/log(2) gives us log base 2
            // floor and add 1 gives us the min number of bits necessary to store the value
            double n = Math.floor(Math.log(inputSize)/Math.log(2))+1;

            resultsWriter.printf("%12d  %15.2f %12.2f\n", inputSize, (double) averageTimePerTrialInBatch, n); // might as well make the columns look nice
            resultsWriter.flush();
            System.out.println(" ....done.");
        }
    }

    // basic recursive function
    public static long FibRecur(int x)
    {
        // if x = 1 or 0 we will return 1 or 0 respectively
        if (x <= 1) return x;
        // else recurse through values x to calculate the desired fib number
        else return FibRecur(x-1) + FibRecur(x-2);
    }

    public static long FibLoop(int x)
    {
        // our base cases
        long fib0 = 0;
        long fib1 = 1;
        // loop to arrive at fib x
        for ( int i = 1; i <= x; i++)
        {
            // adding the previous two fibs to get the next
            long nextFib = fib0 + fib1;
            // swapping for the next iteration
            fib0 = fib1;
            fib1 = nextFib;
        }
        // fib0 will be our desired x
        return fib0;
    }

    // recursive function that will cache results in an array to avoid unnecessary recursion calls
    public static long FibRecurDP(int x)
    {
        long answer = 0; // where we will store our desired result
        long[] FibResultsCache = new long[MAXINPUTSIZE+1]; // this is +1 to account for <=
        Arrays.fill(FibResultsCache, -1); // filling the array with -1 flags
        answer = FibRecursiveWithCache(FibResultsCache, x); // calling the recursive function
        //Arrays.fill(FibResultsCache, -1);
        return answer; // returning the answer
    }
    // completes the actual recursion
    public static long FibRecursiveWithCache(long[] FibResultsCache, int x)
    {
        long result = 0; // value that will be returned
        if ( x == 0) return 0; // base cases so we do not go out of index for the first instance
        if ( x == 1 ) return 1;
        else if (FibResultsCache[x] != -1) // if the result already exists in the array, return it
        {
            result = FibResultsCache[x];
            return result;
        } else
        {  // if the result doesn't exist then call the function recursively until we have the desired result
            result = FibRecursiveWithCache(FibResultsCache,x-1) + FibRecursiveWithCache(FibResultsCache, x-2);
            FibResultsCache[x] = result; // then add the result to the cache.
            return result; // return the desired result for this instance
        }
    }

    // does not translate to binary but is still logx fast
    // source https://codereview.stackexchange.com/questions/51864/calculate-fibonacci-in-olog-n
    public static long FibMatrix(int x) {
        // base cases
        if (x <= 1) return x;

        // fib is x-1 iteration of fib
        // matForMultBy1 stores the result at [1][0] and [0][1]
        long[][] matForMultBy1 = {{1, 0}, {0, 1}};
        long[][] fib = {{1, 1}, {1, 0}};

        MatrixPower(fib, matForMultBy1, x);
        return matForMultBy1[1][0];
    }

    public static void MatrixPower(long[][] a, long[][] b, long x)
    {
        // continues til x is 0
        while ( x > 0) {
            if (x % 2 == 1) { // if x is an odd number
                MatrixMultiplication(b, a);
            }
            x = x / 2; // gets rid of the remainder
            MatrixMultiplication(a, a);
        }
    }

    public static void MatrixMultiplication(long[][] i, long[][] j)
    {
        // multiplying the matrices
        long a = i[0][0] * j[0][0] + i[0][1] * j[1][0];
        long b = i[0][0] * j[0][1] + i[0][1] * j[1][1];
        long c = i[1][0] * j[0][0] + i[1][1] * j[1][0];
        long d = i[1][0] * j[0][1] + i[1][1] * j[1][1];

        // putting the values into the i matrix
        i[0][0] = a;
        i[0][1] = b;
        i[1][0] = c;
        i[1][1] = d;

    }
}
