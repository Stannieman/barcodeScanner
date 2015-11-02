package stanWijckmans.barcodeScanner;

/**
 * Interface defining the methods to implement for listening to events from a BarcodeScannerJTextField.
 *
 * @author: Stan Wijckmans
 * Date: 17/07/2014
 * Time: 17:56
 * @version 1.0.0.0
 */
public interface BarcodeScannerInputListener {

    /**
     * Method called when a barcode scanner finished giving input into the BarcodeScannerJTextField.
     *
     * @param e BarcodeScannerJTextField that completed receiving input from a scanner
     */
    public void barcodeScannerInputCompleted(BarcodeScannerInputEvent e);
}
