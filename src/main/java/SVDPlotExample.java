import org.ejml.simple.SimpleMatrix;
import org.ejml.simple.SimpleSVD;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.style.markers.SeriesMarkers;
import org.knowm.xchart.XYSeries;

public class SVDPlotExample {
    public static void main(String[] args) {
        // 1. Define a 3x2 matrix A using EJML (3 rows, 2 columns)
        SimpleMatrix A = new SimpleMatrix(new double[][]{
                {  1.06, -0.35,  0.25 },
                { -0.86,  1.53,  0.34}});

        // 2. Compute the Singular Value Decomposition (SVD) of A
        SimpleSVD<SimpleMatrix> svdA = A.svd();              // Decompose A :contentReference[oaicite:5]{index=5}
        SimpleMatrix U = svdA.getU();                        // Left singular vectors of A as columns
        SimpleMatrix firstLeftSingular_A = U.extractVector(false, 0);  // first left singular vector (column 0 of U)

        // 3. Compute a row-wise demeaned version of A
        SimpleMatrix A_demean = A.copy();  // copy A to avoid modifying the original
        for (int i = 0; i < A_demean.numRows(); i++) {
            // calculate mean of row i
            double mean = 0;
            for (int j = 0; j < A_demean.numCols(); j++) {
                mean += A_demean.get(i, j);
            }
            mean /= A_demean.numCols();
            // subtract mean from each element of row i
            for (int j = 0; j < A_demean.numCols(); j++) {
                double newVal = A_demean.get(i, j) - mean;
                A_demean.set(i, j, newVal);
            }
        }

        // 4. Perform SVD on the demeaned matrix
        SimpleSVD<SimpleMatrix> svdA_demean = A_demean.svd();   // Decompose A_demean
        SimpleMatrix U_demean = svdA_demean.getU();
        SimpleMatrix firstLeftSingular_demean = U_demean.extractVector(false, 0);

        // 5. Extract first left singular vectors (already done above as firstLeftSingular_A and firstLeftSingular_demean)
        // Also prepare the original column vectors of A for plotting
        SimpleMatrix col1 = A.extractVector(false, 0);  // first column of A
        SimpleMatrix col2 = A.extractVector(false, 1);  // second column of A

        // 6. Use XChart to plot the vectors from the origin
        // Prepare an XYChart
        XYChart chart = new XYChartBuilder().width(600).height(400)
                .title("Original Columns and First Left Singular Vectors")
                .xAxisTitle("X").yAxisTitle("Y").build();

        // Plot each vector as a series from (0,0) to (x,y). (Using first two components for 2D plotting.)
        // Original matrix column 1
        for (int i = 0; i < A.numCols(); i++) {
            double x = A.get(0, i);
            double y = A.get(1, i);

            chart.addSeries("Col " + i, new double[]{x}, new double[]{y})
                    .setMarker(SeriesMarkers.CIRCLE)
                    .setLineStyle(null);
        }

        chart.getStyler().setMarkerSize(5);  // make dots bigger



        // First left singular vector of original A (projected to 2D by using components 1 and 2)
        double scale = 10.0;  // try 10, 50, or even 100 depending on your chart

        double[] u1_x = new double[]{0.0, firstLeftSingular_A.get(0, 0) * scale};
        double[] u1_y = new double[]{0.0, firstLeftSingular_A.get(1, 0) * scale};

        double[] u1d_x = new double[]{0.0, firstLeftSingular_demean.get(0, 0) * scale};
        double[] u1d_y = new double[]{0.0, firstLeftSingular_demean.get(1, 0) * scale};


        chart.addSeries("First Left (orig A)", u1_x, u1_y);

            // First left singular vector of demeaned A

            chart.addSeries("First Left (demeaned A)", u1d_x, u1d_y);

            // (Optional) Customize markers for clarity – e.g., no marker on origin point for vectors
           /* for (XYSeries series : chart.getSeriesMap().values()) {
                series.setMarker(SeriesMarkers.NONE);
            }

            */

            // Display the chart in a window
            new SwingWrapper<>(chart).displayChart();  // opens a GUI window with the chart
        }
    }
