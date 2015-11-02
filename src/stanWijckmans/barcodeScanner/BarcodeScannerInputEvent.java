package stanWijckmans.barcodeScanner;

/**
 * Class representing an event that happened to a BarcodeScannerJTextField.
 *
 * @author: Stan Wijckmans
 * Date: 17/07/2014
 * Time: 17:58
 * @version 1.0.0.0
 */
public class BarcodeScannerInputEvent{
    private BarcodeScannerJTextField source;

    /**
     * Default constructor.
     */
    public BarcodeScannerInputEvent(){}

    /**
     * Constructor that takes the BarcodeScannerJTextField object that fired the event.
     *
     * @param source BarcodeScannerJTextField object that fired the event
     */
    public BarcodeScannerInputEvent(BarcodeScannerJTextField source){
        this.source = source;
    }

    /**
     * Returns the BarcodeScannerJTextField object that fired the event.
     *
     * @return BarcodeScannerJTextField object that fired the event
     */
    public BarcodeScannerJTextField getSource(){
        return source;
    }
}
