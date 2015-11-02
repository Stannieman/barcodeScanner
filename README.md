# barcodeScanner
Code to make a JTextField compatible with a barcode scanner.
It can be used as a regular JTextField, but when it detects input is coming from a barcode scanner it converts the input and fires an event.

Convertint the input means it converts the characters &é"'(§è!çà to 1234567890 respectively. This is useful when the scanner simulates a keyboard and the input method is set to AZERTY.
