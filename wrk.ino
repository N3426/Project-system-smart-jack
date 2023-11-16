#include <SoftwareSerial.h>

// Define the pins for the LED and the HC-05 module
const int ledPin1 = 13;  // LED connected to digital pin 13
const int ledPin2 = 12;
const int rxPin = 1;   // RXD pin of HC-05 connected to digital pin 10
const int txPin = 0;   // TXD pin of HC-05 connected to digital pin 11
const int infraRed = 11; // Infra red sensor
const int ldrPin = A0;     // Analog pin for LDR
// Define the pins for the BTS7960
const int ENL = 5;  // Left Enable (Speed Control)
const int ENR = 6;  // Right Enable (Speed Control)
const int LPWM = 3; // Left PWM (Direction Control)
const int RPWM = 4; // Right PWM (Direction Control)

int ldrValue = 0;          // Variable to store LDR readings
int threshold = 1000;       // Adjust this threshold value as needed
// Set up the software serial port
SoftwareSerial bluetooth(rxPin, txPin);

char Incoming_value = 0;
char infraRed_value = 0;
bool motorRunning = false; // Flag to keep track of the motor's state

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  pinMode(ledPin1, OUTPUT);
  pinMode(ledPin2, OUTPUT);
  pinMode(LPWM, OUTPUT);
  pinMode(RPWM, OUTPUT);
  pinMode(infraRed, INPUT);
  pinMode(ldrPin, INPUT);
  // motor
  pinMode(ENL, OUTPUT);
  pinMode(ENR, OUTPUT);

}

void  loop() {
  // put your main code here, to run repeatedly:
  ldrValue = analogRead(ldrPin);

  // Check if it's dark (below the threshold) and turn on the LED
  if (ldrValue > threshold) {
    digitalWrite(ledPin1, LOW); // Turn on the LED
    digitalWrite(ledPin2, HIGH);
  } else {
    digitalWrite(ledPin1, HIGH);  // Turn off the LED
    digitalWrite(ledPin2, LOW);
  }


  if (Serial.available() > 0) {
        Incoming_value = Serial.read();
        Serial.print(Incoming_value);
        Serial.print("\n");
        Serial.println("EN High");
        digitalWrite(ENR, HIGH);
        digitalWrite(ENL, HIGH);
        delay(1000);
    }

    // Control logic for '1'
    if (Incoming_value == '1') {
        // Check if the IR sensor detects an object
        if (digitalRead(infraRed) == LOW) {
            Serial.println("Object detected, motor moving clockwise");
            analogWrite(LPWM, 255); // Motor move clockwise
            analogWrite(RPWM, 0);
        } else {
            Serial.println("No object detected, stopping motor");
            analogWrite(LPWM, 0);
            analogWrite(RPWM, 0);
        }
    
    } else if (Incoming_value == '0') {
            if (digitalRead(infraRed) == HIGH) {
                Serial.println("Object detected, motor moving anticlockwise");
                analogWrite(RPWM, 255); // Motor move anticlockwise
                analogWrite(LPWM, 0);
            } else {
                Serial.println("No object detected, stopping motor");
                analogWrite(RPWM, 0);
                analogWrite(LPWM, 0);
            }
        }
    }


