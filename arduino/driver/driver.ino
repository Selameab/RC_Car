// Pin definitions
const int ECHO_FRONT_PIN = 2;
const int TRIG_FRONT_PIN = 3;
const int TRIG_BACK_PIN = 4;
const int ECHO_BACK_PIN = 5;

const int LED_FRONT_PIN = A1;
const int LED_INDICATOR_RIGHT_PIN = 6;
const int LED_BRAKE_RIGHT_PIN = 7;
const int LED_INDICATOR_LEFT_PIN = 8;
const int LED_BRAKE_LEFT_PIN = 9;

const int LDR_PIN = A0;

const int DRIVE_PIN_1_PIN = 10;
const int DRIVE_PIN_2_PIN = 11;
const int STEER_PIN_1_PIN = 12;
const int STEER_PIN_2_PIN = 13;

// Bluetooth commands
enum BLUETOOTHCOMMANDS {
	BC_FRONT_DIST = 97,
	BC_BACK_DIST,
	BC_ILLUMINATION,
	BC_FORWARD,
	BC_BACKWARD,
	BC_STOP,
	BC_STEER_RIGHT,
	BC_STEER_LEFT,
	BC_STEER_NONE,
	BC_FRONT_ON,
	BC_FRONT_OFF,
	BC_BRAKE_ON,
	BC_BRAKE_OFF,
	BC_RIGHT_INDICATOR_ON,
	BC_RIGHT_INDICATOR_OFF,
	BC_LEFT_INDICATOR_ON,
	BC_LEFT_INDICATOR_OFF	
};

// Directions
enum DIRECTIONS {
	FORWARD,
	BACKWARD,
	RIGHT,
	LEFT,
	STOP
};

// LEDs
enum LEDS {
	LED_FRONT,
	LED_BRAKE,
	LED_INDICATOR_RIGHT,
	LED_INDICATOR_LEFT
};

// Limiting constants
const int LDR_MIN = 0;
const int LDR_MAX = 1000;

// Variables
int bluetoothCmd;


/** Functions **/
long getDistance(int direction)
{
	int trigPin, echoPin;
	if (direction == FORWARD){
		trigPin = TRIG_FRONT_PIN;
		echoPin = ECHO_FRONT_PIN;
	} else if (direction == BACKWARD){
		trigPin = TRIG_BACK_PIN;
		echoPin = ECHO_BACK_PIN;
	}
	// Send a 10us pulse and wait for echo
	digitalWrite(trigPin, LOW);   
	delayMicroseconds(2); 
	digitalWrite(trigPin, HIGH);
	delayMicroseconds(10); 
	digitalWrite(trigPin, LOW);
	return (pulseIn(echoPin, HIGH) / 58.2);
}

int getIllumination()
{
  return constrain(map(analogRead(LDR_PIN), 950, 1020, LDR_MAX, LDR_MIN), LDR_MIN, LDR_MAX);
}

void setLED(int led, int value){
	switch (led) {
		case LED_FRONT:
			analogWrite(LED_FRONT_PIN, value); // Front light can be set to an any intensity value
			break;
		case LED_BRAKE:
			digitalWrite(LED_BRAKE_RIGHT_PIN, value);
			digitalWrite(LED_BRAKE_LEFT_PIN, value);
			break;
		case LED_INDICATOR_RIGHT:
			digitalWrite(LED_INDICATOR_RIGHT_PIN, value);
			break;
		case LED_INDICATOR_LEFT:
			digitalWrite(LED_INDICATOR_LEFT_PIN, value);
			break;
	};	
}

void drive(int direction){
	if (direction == STOP){
		digitalWrite(DRIVE_PIN_1_PIN, LOW);
		digitalWrite(DRIVE_PIN_2_PIN, LOW);
	} else if (direction == FORWARD){
		digitalWrite(DRIVE_PIN_1_PIN, LOW);
		digitalWrite(DRIVE_PIN_2_PIN, HIGH);
	} else if (direction == BACKWARD){
		digitalWrite(DRIVE_PIN_1_PIN, HIGH);
		digitalWrite(DRIVE_PIN_2_PIN, LOW);
	}	
}

void steer(int direction){
	if (direction == STOP){
		digitalWrite(STEER_PIN_1_PIN, LOW);
		digitalWrite(STEER_PIN_2_PIN, LOW);
	} else if (direction == RIGHT){
		digitalWrite(STEER_PIN_1_PIN, LOW);
		digitalWrite(STEER_PIN_2_PIN, HIGH);
	} else if (direction == LEFT){
		digitalWrite(STEER_PIN_1_PIN, HIGH);
		digitalWrite(STEER_PIN_2_PIN, LOW);
	}
}


void setup() {
	// Serial communication init
	Serial.begin(9600);

	// Pin init
	pinMode(ECHO_FRONT_PIN, INPUT);
	pinMode(ECHO_BACK_PIN, INPUT);
	pinMode(LDR_PIN, INPUT);

	pinMode(TRIG_FRONT_PIN, OUTPUT);
	pinMode(TRIG_BACK_PIN, OUTPUT);	
	pinMode(LED_FRONT_PIN, OUTPUT);
	pinMode(LED_INDICATOR_RIGHT_PIN, OUTPUT);
	pinMode(LED_INDICATOR_LEFT_PIN, OUTPUT);
	pinMode(LED_BRAKE_RIGHT_PIN, OUTPUT);
	pinMode(LED_BRAKE_LEFT_PIN, OUTPUT);
	pinMode(DRIVE_PIN_1_PIN, OUTPUT);
	pinMode(DRIVE_PIN_2_PIN, OUTPUT);
	pinMode(STEER_PIN_1_PIN, OUTPUT);
	pinMode(STEER_PIN_2_PIN, OUTPUT);
}

void loop()
{
  if (Serial.available()){
  	bluetoothCmd = Serial.read();
        
    if (bluetoothCmd == BC_FORWARD)
  		drive(FORWARD);
  	else if (bluetoothCmd == BC_BACKWARD)
  		drive(BACKWARD);
  	else if (bluetoothCmd == BC_STOP)
  		drive(STOP);

  	else if (bluetoothCmd == BC_STEER_RIGHT)
  		steer(RIGHT);
  	else if (bluetoothCmd == BC_STEER_LEFT)
  		steer(LEFT);
  	else if (bluetoothCmd == BC_STEER_NONE)
  		steer(STOP);
        
	else if (bluetoothCmd == BC_FRONT_ON)
  		setLED(LED_FRONT, 1024);  		
  	else if (bluetoothCmd == BC_FRONT_OFF)
  		setLED(LED_FRONT, 0);

  	else if (bluetoothCmd == BC_BRAKE_ON)
  		setLED(LED_BRAKE, 1);  		
  	else if (bluetoothCmd == BC_BRAKE_OFF)
  		setLED(LED_BRAKE, 0);
  	else if (bluetoothCmd == BC_RIGHT_INDICATOR_ON)
  		setLED(LED_INDICATOR_RIGHT, 1);
  	else if (bluetoothCmd == BC_RIGHT_INDICATOR_OFF)
  		setLED(LED_INDICATOR_RIGHT, 0);
  	else if (bluetoothCmd == BC_LEFT_INDICATOR_ON)
  		setLED(LED_INDICATOR_LEFT, 1);
  	else if (bluetoothCmd == BC_LEFT_INDICATOR_OFF)
  		setLED(LED_INDICATOR_LEFT, 0);

  	else if (bluetoothCmd == BC_FRONT_DIST)
  		Serial.println(getDistance(FORWARD));
  	else if (bluetoothCmd == BC_BACK_DIST)
  		Serial.println(getDistance(BACKWARD));
  	else if (bluetoothCmd == BC_ILLUMINATION)
  		Serial.println(getIllumination());

  }
}
