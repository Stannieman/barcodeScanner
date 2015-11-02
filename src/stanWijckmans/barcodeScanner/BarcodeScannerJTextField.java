package stanWijckmans.barcodeScanner;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * This class extends from JTextField. It adds functionality to convert characters automatically if the input is
 * detected as coming from a barcode scanner.
 * It works by checking if when a text is entered (char after char without backspacing, starting from empty),
 * the time it took to enter a given amount of characters lies within a given period if time.
 * This is kind of flaky though as the time it takes to get all characters inputted from the scanner depends on the
 * system and current situation. Therefore the value can be tweaked if needed.
 *
 * The default values are 8 for the length and 50ms for the time. This means that if 8 chars are entered and
 * the time passed from entering the first to the last char took less than 50ms, it's assumed to have come from a
 * scanner.
 *
 * When input from a scanner is completed, the BarcodeScannerInputCompleted method of all eventlisteners is called.
 *
 * @author: Stan Wijckmans
 * Date: 17/07/2014
 * Time: 14:55
 * @version 1.0.0.0
 */
public class BarcodeScannerJTextField extends JTextField implements DocumentListener {
    private int barcodeLength = 8, inputDelay = 50, previousLength = 0;
    private LocalDateTime referenceTime;
    private boolean stillValid = true;
    private List<BarcodeScannerInputListener> barcodeScannerInputListeners;

    /**
     * Default constructor.
     * Initializes the list of eventlisteners and adds a documentlistener.
     */
    public BarcodeScannerJTextField(){
        barcodeScannerInputListeners = new ArrayList<BarcodeScannerInputListener>();
        this.getDocument().addDocumentListener(this);
    }

    /**
     * Constructor taking the length of the barcode.
     * The length of the barcode determines after how many characters must be checked if the input came from a scanner, this must be set to the length of the barcodes you are scanning.
     *
     * @param barcodeLength how long the text must be when checking if it comes from a scanner
     */
    public BarcodeScannerJTextField(int barcodeLength) {
        this();
        setBarcodeLength(barcodeLength);
    }

    /**
     * Constructor taking the length of the barcode and the input delay.
     * The length of the barcode determines after how many characters must be checked if the input came from a scanner.
     * The input delay determines the maximum amount of milliseconds that may be between inputting the first and last character for the text to be treated as coming from a scanner.
     *
     * @param barcodeLength how long the text must be when checking if it comes from a scanner
     * @param inputDelay    the max time difference between the first and last character for the text to be treated as coming from a scanner
     */
    public BarcodeScannerJTextField(int barcodeLength, int inputDelay) {
        this();
        setBarcodeLength(barcodeLength);
        setInputDelay(inputDelay);
    }

    /**
     * Sets the length of the barcodes you are scanning.
     * When the text reaches this size there will be checked if the input came from a scanner.
     *
     * @param barcodeLength how long the text must be when checking if it comes from a scanner
     */
    public void setBarcodeLength(int barcodeLength) {
        this.barcodeLength = barcodeLength;
    }

    /**
     * Sets the input delay.
     * This specifies how many milliseconds there may at max be between the input of the first and the last character for the text to be treated as coming from a scanner.
     *
     * @param inputDelay max delay between the input of the first and last character coming from the scanner
     */
    public void setInputDelay(int inputDelay) {
        this.inputDelay = inputDelay;
    }

    /**
     * Adds an eventlistener.
     *
     * @param listener eventlistener to add
     */
    public void addBarcodeScannerInputListener(BarcodeScannerInputListener listener)
    {
        barcodeScannerInputListeners.add(listener);
    }

    /**
     * Removes an eventlistener.
     *
     * @param listener eventlistener to remove
     */
    public void removeBarcodeScannerInputListener(BarcodeScannerInputListener listener)
    {
        barcodeScannerInputListeners.remove(listener);
    }

    /**
     * Checks in what state the textfield is and what should be done with the text.
     */
    private void reviewText(){
        //Check if the text is still possibly entered by a scanner
        if (stillValid)
            //If it's the first character that got entered, set the timer to now
            if (this.getText().length() == 1) {
                referenceTime = LocalDateTime.now();
                previousLength = 1;
            }
            //If the length of the text didn't increase by 1 it can't come from a scanner (either it's copy pasted or set programmatically)
            else if (this.getText().length() != ++previousLength)
                stillValid = false;
            else {
                //Check if the text has reached the barcode length
                if (this.getText().length() == barcodeLength)
                    //Check if the text has been entered within the given time
                    if (Duration.between(referenceTime, LocalDateTime.now()).toMillis() <= inputDelay) {
                        //Detach eventhandner, convert the text and reattach the handler
                        this.getDocument().removeDocumentListener(this);
                        //Text is locked, with invokeLater it gets updated as soon as the lock is released.
                        //Also fire the event only after the text has actually been converted.
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                BarcodeScannerJTextField.this.setText(convertInput(BarcodeScannerJTextField.this.getText()));
                                fireBarcodeScannerInputCompletedEvent();
                            }
                        });
                        this.getDocument().addDocumentListener(this);
                    }
            }
    }

    /**
     * Returns a string with all scanned characters converted to digits.
     *
     * @param input string to convert
     * @return converted string
     */
    private static String convertInput(String input) {
        StringBuilder builder = new StringBuilder();

        for (char ch : input.toCharArray())
            switch (ch) {
                case '&':
                    builder.append('1');
                    break;
                case 'é':
                    builder.append('2');
                    break;
                case '"':
                    builder.append('3');
                    break;
                case '\'':
                    builder.append('4');
                    break;
                case '(':
                    builder.append('5');
                    break;
                case '§':
                    builder.append('6');
                    break;
                case 'è':
                    builder.append('7');
                    break;
                case '!':
                    builder.append('8');
                    break;
                case 'ç':
                    builder.append('9');
                    break;
                case 'à':
                    builder.append('0');
                    break;
                default:
                    builder.append(ch);
                    break;
            }

        return builder.toString();
    }

    private void fireBarcodeScannerInputCompletedEvent(){
        for (BarcodeScannerInputListener listener : barcodeScannerInputListeners)
            listener.barcodeScannerInputCompleted(new BarcodeScannerInputEvent(this));
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        reviewText();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        //If the length of the text is 0 then reset to valid
        if (this.getText().length() == 0)
            stillValid = true;
        else
            stillValid = false;
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
    }
}
