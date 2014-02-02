
#define led 13

//for Fwd and Backward
#define pin1 2
#define pin2 4

//for Right and left
#define LR1  7
#define LR2  8

int inByte = 0; 

void setup()
{
  Serial.begin(9600);
  Serial.println("Hello World");
  
  pinMode(led, OUTPUT);
  
  pinMode(pin1, OUTPUT);
  pinMode(pin2, OUTPUT);
  
  pinMode(LR1,  OUTPUT);
  pinMode(LR2, OUTPUT);
  
  digitalWrite(pin1, LOW);
  digitalWrite(pin2, LOW);
  digitalWrite(LR1, LOW);
  digitalWrite(LR2, LOW);
}

void loop()
{
  if (Serial.available() > 0)
  {
    inByte = Serial.read();
    int stp = 0; //Stop wheels
    int fwd = 1; //run in fwd direction
    int bw = 2;  //backward dir
    int right = 3; //right
    int left = 4;  //left
    int straight = 5;  //straight
    
    Serial.println(inByte, DEC);
    if (inByte == stp)
    {
       digitalWrite(pin1, LOW);
       digitalWrite(pin2, LOW);
       Serial.println(stp, DEC);
    }
    if (inByte == fwd)
    {
       digitalWrite(pin1, HIGH);
       digitalWrite(pin2, LOW);
       Serial.println(fwd ,DEC);
    }
    if (inByte == bw)
    {
      digitalWrite(pin1, LOW);
      digitalWrite(pin2, HIGH);
      Serial.println(bw, DEC);
    }
    if (inByte == right)
    {
      digitalWrite(LR1, LOW);
      digitalWrite(LR2, HIGH);
      Serial.println(right, DEC);
    }
    if (inByte == left)
    {
      digitalWrite(LR1, HIGH);
      digitalWrite(LR2, LOW);
      Serial.println(left, DEC);
    }
    if (inByte == straight)
    {
      digitalWrite(LR1, LOW);
      digitalWrite(LR2, LOW);
      Serial.println(straight, DEC);
    }
    Serial.println("done");
  }
}
