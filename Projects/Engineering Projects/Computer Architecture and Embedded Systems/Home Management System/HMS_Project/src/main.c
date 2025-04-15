
/********************************************
*			STM32F439 Main (C Startup File)  			*
*			Developed for the STM32								*
*			Author: Dr. Glenn Matthews						*
*			Source File														*
********************************************/

//	Home Management System Project  			
//	Developed for the STM32								
//	Authors: Dende Te(s3839316), Rish Rao(s3843246)	
//	Last Updated: 27/05/2022

#include <stdint.h>
#include "boardSupport.h"
#include "main.h"
#include "stm32f439xx.h"
#include <stdio.h>

// Functions
void configRCC(void);
void configGPIOs(void);
void configUSART(void);
void configADC(void);
void configTimer(void);
void LEDs_off(void);
uint16_t readADCTemp(void);
float calculatedTemp(uint16_t);
int8_t receive_UART(void);
void transmit_UART(char character[], int size);
void startTIMER(uint16_t countVal);
int checkSwitchPress(int check);
void actOnButtonpress(int check);

volatile int switchA3 = 0;
volatile int switchA9 = 0;
//volatile int check = 0;
volatile int toggleLight = 0;
volatile int toggleFan = 1;
volatile int heatingMode = 0;
volatile int coolingMode = 0;

// Timer variables
const int count15seconds = 0xEA60;
const int count1second = 0x0FA0;
const int counthalfsecond = 0x07D0;

//******************************************************************************//
// Function: main()
// Input : None
// Return : None
// Description : Entry point into the application.
// *****************************************************************************//
int main(void)
{

	uint16_t adcValue = 0;											// Variable that receives raw ADC value.
	float receivedTemp = 0;											// Variable that holds the temperature value.
	char temp_array[4] = {0,0,0,0};							// Array that stores temperature. 
	char space_text[1] = {0x20};									// character for (End of text).
	//char eot_text[1] = {0x04};									// character for (End of transmission).
	char header_text[1] = {0x21};								// Header for UART message ('!').
	char carriage_ret[1] = {0x0D};
	char output_status[4] = {'1','1','1','1'};	// Output statuses in order - Light, Fan, Heating, Cooling.
	char UART_buffer[6] = {0,0,0,0,0,0};		// Array the stores message from UART.
	int i = 0;																	// Counter for UART buffer.
	
	int check = 0;
		
		
	// Bring up the GPIO for the power regulators.
	boardSupport_init();
	
	// Configure RCC
	configRCC();
	
	// Configure GPIOs
	configGPIOs();
	
	// Configure USART
	configUSART();
	
	// Configure ADC
	configADC();
	
	// RISH EDIT *********************************************************
	// Configure/Deal with INTERRUPTS
	
	__disable_irq();
	
	NVIC_SetPriority(TIM6_DAC_IRQn, 2);
	
	NVIC_EnableIRQ(TIM6_DAC_IRQn);
	
	// Configure Timers
	configTimer();
	// RISH EDIT **********************************************************
	
	// Turn all LEDs off
	LEDs_off();
	
		// Part(a) - Send system parameters via UART for monitoring:
		// Current temperature value 1.dec (##.#) (4-ASCII values).
		// Output status (Light, Fan, Heater, Cooling).
		// Part(b) - Description of switches:
		// Fan SW(UP - PA3) and light SW(DOWN - PA8) operate in 'toggle' mode.
		// Part(c) - Temperature functions:
		// Target temp = 23 (degreeC)
		// if temp <= 22 ------> Heater and Fan LEDS ON.
		// if temp >= 24 ------> Cooling and Fan LEDS ON.
		// Only ONE of Heating OR Cooling can be ON.
		// if Fan SW is pressed -----> Turn Fan LED OFF for 15 seconds.
		// Part(d) - 
		// SW2 (light intensity switch).
		// If light output is OFF before HOLDING SW2
		// ---> light turns on during HOLD, then turns light OFF.
		// If light output is ON before HOLDING SW2
		// ---> light turns on during HOLD, then light stays ON.
	
  while (1)
  {
		
		
		UART_buffer[i] = receive_UART();							// Receive value from UART.
		adcValue = readADCTemp();								 			// Perform actual ADC conversion.
		receivedTemp = calculatedTemp(adcValue); 			// Converting ADC to temperature. receivedTemp is a float. 
		snprintf(temp_array, 5, "%f", receivedTemp); 	// Converts temperature value to character array.
		
		// Checking temperature
		if (receivedTemp <= 22) {								// Temperature below 22 degrees.
			// Turn ON Heater and Fan LEDS.
			//GPIOA->BSRR |= GPIO_BSRR_BR10;
			GPIOF->BSRR |= GPIO_BSRR_BR8;
			GPIOB->ODR |= (0x01 << GPIO_ODR_OD8_Pos);
			// Set output status
			//output_status[1] = '0';								// Fan LED ON
			output_status[2] = '0';								// Heating LED ON
			output_status[3] = '1';								// Cooling LED OFF
			heatingMode = 1;
			coolingMode = 0;
			if (toggleFan == 1)
			{
				output_status[1] = '0';								// Fan LED ON
				GPIOA->BSRR |= GPIO_BSRR_BR10;
			}
			else if(toggleFan == 0){
				output_status[1] = '1';								// Fan LED ON
				GPIOA->ODR |= (0x01 << GPIO_ODR_OD10_Pos);
			}
			
			if (toggleLight == 1)
			{
				output_status[0] = '0';								// Fan LED ON
				GPIOB->BSRR |= GPIO_BSRR_BR0;
			}
			else if(toggleLight == 0){
				output_status[0] = '1';								// Fan LED ON
				GPIOB->ODR |= (0x01 << GPIO_ODR_OD0_Pos);
			}
			
			
		}
		else if (receivedTemp >= 24) {						// Temperature above 24 degrees.
			// Turn ON Cooling and Fan LEDS.
			GPIOB->BSRR |= GPIO_BSRR_BR8;
			GPIOF->ODR |= (0x01 << GPIO_ODR_OD8_Pos);
			// Set output status
			//output_status[1] = '0';								// Fan LED ON
			output_status[2] = '1';								// Heating LED OFF
			output_status[3] = '0';								// Cooling LED ON
			coolingMode = 1;
			heatingMode = 0;
			if (toggleFan == 1)
			{
				output_status[1] = '0';								// Fan LED ON
				GPIOA->BSRR |= GPIO_BSRR_BR10;
			}
			else if(toggleFan == 0){
				output_status[1] = '1';								// Fan LED ON
				GPIOA->ODR |= (0x01 << GPIO_ODR_OD10_Pos);
			}
			if (toggleLight == 1)
			{
				output_status[0] = '0';								// Fan LED ON
				GPIOB->BSRR |= GPIO_BSRR_BR0;
			}
			else if(toggleLight == 0){
				output_status[0] = '1';								// Fan LED ON
				GPIOB->ODR |= (0x01 << GPIO_ODR_OD0_Pos);
			}
			
		}
		else{
			GPIOB->ODR |= (0x01 << GPIO_ODR_OD8_Pos);
			GPIOF->ODR |= (0x01 << GPIO_ODR_OD8_Pos);
			//output_status[1] = '1';								// Fan LED OFF
			output_status[2] = '1';								// Heating LED OFF
			output_status[3] = '1';								// Cooling LED OFF
			coolingMode = 0;
			heatingMode = 0;
			//toggleFan = 0;
			if (toggleFan == 1)
			{
				output_status[1] = '0';								// Fan LED ON
				GPIOA->BSRR |= GPIO_BSRR_BR10;
			}
			else if(toggleFan == 0){
				output_status[1] = '1';								// Fan LED ON
				GPIOA->ODR |= (0x01 << GPIO_ODR_OD10_Pos);
			}
			if (toggleLight == 1)
			{
				output_status[0] = '0';								// Fan LED ON
				GPIOB->BSRR |= GPIO_BSRR_BR0;
			}
			else if(toggleLight == 0){
				output_status[0] = '1';								// Fan LED ON
				GPIOB->ODR |= (0x01 << GPIO_ODR_OD0_Pos);
			}
		}
		
		// Check switches pressed and execute if pressed.
		check = checkSwitchPress(check);
		actOnButtonpress(check);
		
		// UART PC to HMS
		if(UART_buffer[i] != 0)									// UART value is not NULL (checking)
		{
			if(UART_buffer[i] == -1)							// UART value is invalid (checking)
			{
				for(i = 0; i < 3; i++) 
				{ UART_buffer[i] = 0;}							// Clears buffer.
				i = 0;															// Reset buffer counter
			}
			
			else if(UART_buffer[0] == '!')							// Check if buffer has received header byte 
			{
				if (UART_buffer[i] == 0x0A || UART_buffer[i] == 0x0D)	// Buffer detects a control character
				{
					// check each status
					// Light status
					if (UART_buffer[1] == '0')
					{
						toggleLight = 1;
					}
					else if (UART_buffer[1] == '1')
					{
						toggleLight = 0;
					}
					// Fan, Heater and Cooling changes only between 18 to 26 degrees
					if (receivedTemp > 18 && receivedTemp < 26){
						// Fan status
						if (UART_buffer[2] == '0')
						{
							toggleFan = 1;
						}
						else if (UART_buffer[2] == '1')
						{
							toggleFan = 0;
						}
						// Heat status
						if (UART_buffer[3] == '0')
						{
							// output_status[2] == '0';
						}
						else if (UART_buffer[3] == '1')
						{
							// output_status[2] == '1';
						}
						// Cooling status
						if (UART_buffer[4] == '0')
						{
							// output_status[2] == '0';
						}
						else if (UART_buffer[4] == '1')
						{
							// output_status[2] == '1';
						}
					}
					
					
					
					for(i = 0; i < 4; i++) 
					{ 
						UART_buffer[i] = 0x00; 			// Clears the buffer
					}
					i = 0;												// Reset buffer
				}
				
			}
			// Checks if buffer detects valid character
			else if (UART_buffer[i] != -1)
			{ 
				i++; 
			}
			// Checks if its beyond 6
			else if(i > 5) 
			{ 
				for(i = 0; i < 6; i++) 
				{ 
					UART_buffer[i] = 0x00; 			// Clears the buffer
				}
				i = 0;												// Reset buffer
			}
		}
		
		/*
		if (toggleFan == 1) {
			output_status[1] = '0';
		}
		else if (toggleFan == 0) {
			output_status[1] = '1';
		}
		
		
		if (toggleLight == 1) {
			output_status[0] = '0';
			GPIOB->BSRR |= GPIO_BSRR_BR0;
		}
		else if (toggleLight == 0) {
			output_status[0] = '1';
			GPIOB->ODR |= (0x01 << GPIO_ODR_OD0);
		}
		*/
		
		// Send monitoring parameters if UART control has not started.
		if(UART_buffer[0] != '!')	
		{
			// *********************** START HMS to PC ******************************
			// Send monitoring parameters via UART to PC
			transmit_UART(header_text, 1);								// Send '!' header.
			transmit_UART(temp_array, 4);									// Send Temperature Value.
			transmit_UART(space_text, 1);										// Send 0b0011 == 0x20 for space.
			transmit_UART(output_status, 4);							// Send Output statuses
			transmit_UART(carriage_ret,1);
			// Set 1 second Timer
			startTIMER(count1second);
			while((TIM6->SR & TIM_SR_UIF) == 0);
			
		
		// *********************** END HMS to PC ******************************
		}
  }
} 

//******************************************************************************//
// Function: configRCC()
// Input : None
// Return : None
// Description : Configurates the RCC to enable GPIOs, U(S)ART3, Timer & ADC
// *****************************************************************************//
void configRCC()
{
	// Enable GPIOs A,B,F, TIMER6, USART3, ADC1 -----------------------------
	RCC->AHB1ENR |= (RCC_AHB1ENR_GPIOAEN | RCC_AHB1ENR_GPIOBEN | RCC_AHB1ENR_GPIOFEN);				// Enable GPIOs A, B and F
	RCC->APB1ENR |= RCC_APB1ENR_TIM6EN | RCC_APB1ENR_USART3EN;				// Enable the RCC for Timer 6 & USART3.
	RCC->APB2ENR |= RCC_APB2ENR_ADC3EN;
	
	// Reset the interfaces
	RCC->AHB1RSTR |= (RCC_AHB1RSTR_GPIOARST | RCC_AHB1RSTR_GPIOBRST | RCC_AHB1RSTR_GPIOFRST);	// Set the reset GPIOs.
	RCC->APB1RSTR |= RCC_APB1RSTR_TIM6RST | RCC_APB1RSTR_USART3RST;		// Reset the peripheral interfaces.
	RCC->APB2RSTR |= RCC_APB2RSTR_ADCRST;
	
	// Insert a two cycle delay.
	__asm("NOP");	__asm("NOP");
	
	// Clear the peripheral resets.
	RCC->AHB1RSTR &= ~(RCC_AHB1RSTR_GPIOARST | RCC_AHB1RSTR_GPIOBRST | RCC_AHB1RSTR_GPIOFRST);	// clear GPIOs
	RCC->APB1RSTR &= ~(RCC_APB1RSTR_TIM6RST | RCC_APB1RSTR_USART3RST);	// clear the reset bits
	RCC->APB2RSTR &= ~(RCC_APB2RSTR_ADCRST);
	
	// Insert a two cycle delay.
	__asm("NOP");	__asm("NOP");
}


//******************************************************************************//
// Function: configGPIOs()
// Input : None
// Return : None
// Description : Configurates the inputs & outputs of GPIOs A,B,F.
// *****************************************************************************//
void configGPIOs()
{
	// inputs (pins): PA3,8,9, PF10.
	// outputs(pins): PA10, PB0,8, PF8.
	
	/* -------------------GPIOA Configuration - PA3,8,9 and 10.-------------------------*/
	// Clear the MODER Bits for GPIOAs 3,8,9 and 10.
	GPIOA->MODER &= ~(GPIO_MODER_MODE3_Msk | GPIO_MODER_MODE8_Msk | 
											GPIO_MODER_MODE9_Msk | GPIO_MODER_MODE10_Msk);
	// Enable PA3,8,9 as inputs (0x00) and PA10 as output(0x01).
	GPIOA->MODER |= (0x00 << GPIO_MODER_MODE3_Pos) | (0x00 << GPIO_MODER_MODE8_Pos) | 
											(0x00 << GPIO_MODER_MODE9_Pos) | (0x01 << GPIO_MODER_MODE10_Pos);
	// Enable push-pull for PA3,8,9 and 10.
	GPIOA->OTYPER &= ~(GPIO_OTYPER_OT3_Msk | GPIO_OTYPER_OT8_Msk | 
												GPIO_OTYPER_OT9_Msk | GPIO_OTYPER_OT10_Msk);
	// Clear the OSPEEDR bits for GPIOAs 3,8,9 and 10.
	GPIOA->OSPEEDR &= ~(GPIO_OSPEEDR_OSPEED3_Msk | GPIO_OSPEEDR_OSPEED8_Msk | 
												GPIO_OSPEEDR_OSPEED9_Msk | GPIO_OSPEEDR_OSPEED10_Msk);
	// Set the speed to medium.
	GPIOA->OSPEEDR |= (0x01 << GPIO_OSPEEDR_OSPEED3_Pos) | (0x01 << GPIO_OSPEEDR_OSPEED8_Pos) | 
											(0x01 << GPIO_OSPEEDR_OSPEED9_Pos) | (0x01 << GPIO_OSPEEDR_OSPEED10_Pos);
	// Clear the PUPDR register.
	GPIOA->PUPDR &= ~(GPIO_PUPDR_PUPD3_Msk | GPIO_PUPDR_PUPD8_Msk | 
											GPIO_PUPDR_PUPD9_Msk | GPIO_PUPDR_PUPD10_Msk);
	// Set the IDR for PA3,8,9.
	GPIOA->IDR |= (0x01 << GPIO_IDR_ID3_Pos) | (0x01 << GPIO_IDR_ID8_Pos) | 
									(0x01 << GPIO_IDR_ID9_Pos);
	// Set the ODR for PA10.
	GPIOA->ODR |= (0x01 << GPIO_ODR_OD10_Pos);
	
	/* ---------------------GPIOB Configuration - PB0,8---------------------------*/
	// Clear the MODER Bits for GPIOBs PB0,8.
	GPIOB->MODER &= ~(GPIO_MODER_MODE0_Msk | GPIO_MODER_MODE8_Msk);
	// Enable PB0,8 as output(0x01).
	GPIOB->MODER |= (0x01 << GPIO_MODER_MODE0_Pos) | (0x01 << GPIO_MODER_MODE8_Pos);
	// Enable push-pull for PB0,8
	GPIOB->OTYPER &= ~(GPIO_OTYPER_OT0_Msk | GPIO_OTYPER_OT8_Msk); 
	// Clear the OSPEEDR bits for GPIOB PB0,8
	GPIOB->OSPEEDR &= ~(GPIO_OSPEEDR_OSPEED0_Msk | GPIO_OSPEEDR_OSPEED8_Msk); 
	// Set the speed to medium.
	GPIOB->OSPEEDR |= (0x01 << GPIO_OSPEEDR_OSPEED0_Pos) | (0x01 << GPIO_OSPEEDR_OSPEED8_Pos); 
	// Clear the PUPDR register.
	GPIOB->PUPDR &= ~(GPIO_PUPDR_PUPD0_Msk | GPIO_PUPDR_PUPD8_Msk);						
	// Set the ODR for PA10.
	GPIOB->ODR |= (0x01 << GPIO_ODR_OD10_Pos);
	
	/* ---------------------GPIOF Configuration - PF8-----------------------------*/
	// Clear the MODER Bits for GPIOFs PF8.
	GPIOF->MODER &= ~(GPIO_MODER_MODE8_Msk);
	// Enable PB8 as output(0x01).
	GPIOF->MODER |= (0x01 << GPIO_MODER_MODE8_Pos);
	// Enable push-pull for PF8.
	GPIOF->OTYPER &= ~(GPIO_OTYPER_OT8_Msk); 
	// Clear the OSPEEDR bits for GPIOF PF8
	GPIOF->OSPEEDR &= ~(GPIO_OSPEEDR_OSPEED8_Msk | GPIO_OSPEEDR_OSPEED10_Msk); 
	// Set the speed to medium.
	GPIOF->OSPEEDR |= (0x01 << GPIO_OSPEEDR_OSPEED8_Pos); 
	// Clear the PUPDR register.
	GPIOF->PUPDR &= ~(GPIO_PUPDR_PUPD8_Msk);							
	// Set the ODR for PF8.
	GPIOF->ODR |= (0x01 << GPIO_ODR_OD8_Pos);
}

//******************************************************************************//
// Function: configUSART()
// Input : None
// Return : None
// Description : Configurates the USART3.
// *****************************************************************************//
void configUSART()
{
	// Following code provided from Lecture 7 example with modifcation to baud rate.
	// Configure GPIOB MODER register
	// Clears MODER bits 10 and 11
	GPIOB->MODER &= ~(GPIO_MODER_MODE10_Msk | GPIO_MODER_MODE11_Msk);
	// Enable PB10,11 (0x02).
	GPIOB->MODER |= (0x02 << GPIO_MODER_MODE10_Pos) | (0x02 << GPIO_MODER_MODE11_Pos);
	
	// Setup the Alternate Function - AF7 on ports 10 & 11
	GPIOB->AFR[1] &= ~(GPIO_AFRH_AFSEL10_Msk | GPIO_AFRH_AFSEL11_Msk);
	GPIOB->AFR[1] |= (0x07 << GPIO_AFRH_AFSEL10_Pos) | (0x07 << GPIO_AFRH_AFSEL11_Pos);
	
	// Enable OVER16
	USART3->CR1 &= ~(USART_CR1_OVER8);
	
	// Configure USART baud rate to 57,600 bps
	USART3->BRR &= 0xFFFF0000;		// Clear first
	USART3->BRR |= (0x2D << USART_BRR_DIV_Mantissa_Pos) | (0x09 << USART_BRR_DIV_Fraction_Pos);	
	
	// Set the number of bits per transfer (8-bits)
	USART3->CR1 &= ~(USART_CR1_M);
	
	// Set number of stop bits
	USART3->CR2 &= ~(USART_CR2_STOP_Msk);
	USART3->CR2 |= (0x00 << USART_CR2_STOP_Pos);
	
	// Disable system polarity
	USART3->CR1 &= ~(USART_CR1_PCE);
	
	// Select mode of operation (async - no clock)
	USART3->CR2 &= ~(USART_CR2_CLKEN | USART_CR2_CPOL | USART_CR2_CPHA);
	
	// Disable hardware flow control
	USART1->CR3 &= ~(USART_CR3_CTSE | USART_CR3_RTSE);
	
	// Enable the USART
	USART3->CR1 |= (USART_CR1_TE | USART_CR1_RE | USART_CR1_UE);
	
}
//******************************************************************************//
// Function: configADC()
// Input : None
// Return : None
// Description : Configurates the ADC3.
// *****************************************************************************//
void configADC()
{
	// Code below guided from Lectorial 4
	
	// Setup PF10 to be an analogue input
	GPIOF->MODER |= (0x03 << GPIO_MODER_MODE10_Pos);
	
	// Set the prescaler to divide by 8.
	ADC123_COMMON->CCR |= (0x03 << ADC_CCR_ADCPRE_Pos);
	
	// Disable scan mode and set the resolution to 12 bits. 
	ADC3->CR1 &= ~((ADC_CR1_SCAN) | (0x03 << ADC_CR1_RES_Pos));
	
	// Alignment set to right and set single mode conversion
	ADC3->CR2 &= ~(ADC_CR2_CONT | ADC_CR2_ALIGN | ADC_CR2_SWSTART);
	
	// Set to a single channel - Channel 8 (temperature sensor)
	ADC3->SQR3 &= ~(ADC_SQR3_SQ1_Msk);
	ADC3->SQR3 |= 0x08;
	ADC3->SQR1 &= ~(ADC_SQR1_L_Msk);
	
	// Set the sample time register - 56 cycles
	ADC3->SMPR2 &= ~(ADC_SMPR2_SMP8_Msk);			// Clears first
	ADC3->SMPR2 |= (0x03 << ADC_SMPR2_SMP8_Pos);
	
	//Enable the ADC
	ADC3->CR2 |= ADC_CR2_ADON;
}

//******************************************************************************//
// Function: configTimer()
// Input : None
// Return : None
// Description : Configurates the Timer 6.
// *****************************************************************************//
void configTimer()
{
	// Code below follows Lectorial 4 code provided
	
	TIM6->CR1 &= ~(TIM_CR1_CEN);				// Ensure that the timer is off.
	TIM6->PSC &= ~(TIM_PSC_PSC_Msk);		// Clear the prescaler register.
	TIM6->PSC |= 0x5208;									// Divide the APB1 Clock by 1600.
																			// One tick = 84 x 10^6 / 1600
																			// Timer count = 1/52.5kHz * 3281 
																			//						 = 62.5ms
	TIM6->ARR &= ~(TIM_ARR_ARR_Msk);		// Clear the auto-reload register.
	TIM6->CR1 |= TIM_CR1_OPM;						// Set the timer to run in 'single shot' mode.
	
	/*
	TIM6->CR1 &= ~(TIM_CR1_ARPE | TIM_CR1_OPM | TIM_CR1_CEN);
	TIM6->CR1 |= TIM_CR1_ARPE | TIM_CR1_OPM;
	//TIM6->DIER &= ~(TIM_DIER_UIE);												// Simulator
	//TIM6->DIER |= (TIM_DIER_UIE);
	TIM6->CNT &= ~(TIM_CNT_CNT_Msk);
	TIM6->PSC &= ~(TIM_PSC_PSC_Msk);
	TIM6->ARR &= ~(TIM_ARR_ARR_Msk);
	TIM6->PSC |= (0x5208 << TIM_PSC_PSC_Pos);
	*/
	
}

//******************************************************************************//
// Function: LEDs_off()
// Input : None
// Return : None
// Description :  Turns off all LEDs.
// *****************************************************************************//
void LEDs_off()
{
	// Output LEDs are: PA10, PB0,8 & PF8 
	GPIOA->ODR |= (0x01 << GPIO_ODR_OD10_Pos);
	GPIOB->ODR |= (0x01 << GPIO_ODR_OD0_Pos | 0x01 << GPIO_ODR_OD8_Pos);
	GPIOF->ODR |= (0x01 << GPIO_ODR_OD8_Pos);
}

//******************************************************************************//
// Function: readADCTemp()
// Input : None
// Return : Return ADC value
// Description :  ADC value is read from ADC3
// *****************************************************************************//
uint16_t readADCTemp()
{
	uint16_t currentTemp = 0;
	
	// Trigger and ADC conversion.
	ADC3->CR2 |= ADC_CR2_SWSTART;
	while((ADC3->SR & ADC_SR_EOC) == 0x00);
	currentTemp = (ADC3->DR & 0x0000FFFF);
	
	return currentTemp;
}
//******************************************************************************//
// Function: calculatedTemp()
// Input : None
// Return : Return ADC value
// Description :  ADC value is read from ADC3
// *****************************************************************************//
float calculatedTemp(uint16_t rawADC)
{
	float calcTemp = 0.0;
	float adcCast = (float)rawADC;
	calcTemp = ((37*adcCast)/4095) + 8;
	
	return calcTemp;
}
//******************************************************************************//
// Function: receive_UART()
// Input : None
// Return : UART_value
// Description : Receive data from UART DR register and determine its validity
// *****************************************************************************//
int8_t receive_UART()
{
	int8_t valid_input = -1;
	int8_t UART_value = 0;
	
	if(USART3->SR & USART_SR_RXNE)
	{
		// Read the DR register
		UART_value = USART3->DR;
		
		// Check for valid inputs
		if(UART_value == 0) 
		{ 
			valid_input = 1; 
		}
		else if(UART_value == '!' || UART_value == '0' || UART_value == '1') 
		{ 
			valid_input = 1; 
		}
		
		if(valid_input == -1) { UART_value = valid_input; }
		
	}
	
	return UART_value;
	
}


//******************************************************************************//
// Function: transmit_UART()
// Input : character[], size
// Return : None
// Description : Transmit 'temperature value', 'light' and 'fan' statuses back to PC
// *****************************************************************************//
void transmit_UART(char character[], int size)
{
	int i = 0;
	
	for(i = 0; i < size; i++)
	{
		while((USART3->SR & USART_SR_TXE) == 0);
		USART3->DR = character[i];
		while((USART3->SR & USART_SR_TC) == 0);
	}
}

//******************************************************************************//
// Function: startTimer()
// Input : uint16_t
// Return : None
// Description : Starts timer with a given count(ARR) value
// *****************************************************************************//

void startTIMER(uint16_t countVal)
{	
	// Disable the timer
	TIM6->CR1 &= ~(TIM_CR1_CEN_Msk);
	
	// Clear auto reload register
	TIM6->ARR &= ~(TIM_ARR_ARR_Msk);
	
	// Set the count to inputted value
	TIM6->ARR |= countVal;
	
	// Clear interrupt flag
	TIM6->SR &= ~(TIM_SR_UIF_Msk);
				
	// Enable the count
	TIM6->CR1 |= TIM_CR1_CEN;
}

int checkSwitchPress(int check)
{	
	// Switch Masks (PA3,8,9):
	uint16_t maskSWs = 0xFCF7; 			// Clears all except bits of interests. 1100 1111 0111
	uint16_t fanSW = 0xFFF7;				// Only PA3 pressed											1111 1111 0111
	uint16_t lightSW = 0xDFFF;			// Only PA9 pressed											1101 1111 1111
	uint16_t lightSensor = 0xEFFF;	// Only PA8 pressed											1110 1111 1111
	uint16_t readSWs = GPIOA->IDR;	// Storing switch input								 (1011 0010 0111 - FAN EXAMPLE)
	readSWs |= maskSWs;							// Mask switch inputs									 (1111 1111 0111 - FAN EXAMPLE)
	
	if (toggleLight == 0 && toggleFan == 0) {
		if (readSWs == fanSW) {
			//startTIMER(counthalfsecond);
			check = 3;
			toggleFan = 1;
				
			
		}
		else if (readSWs == lightSW) {
			check = 9;
			toggleLight = 1;
		} 
		else {
			check = 0;
			toggleLight = 0;
			toggleFan = 0;
		}
		//TIM6->SR &= ~(TIM_SR_UIF);
	} else if (toggleLight == 0 && toggleFan == 1) {
		if (readSWs == fanSW) {
				check = 3;
				toggleFan = 0;
			}
			else if (readSWs == lightSW) {
				check = 9;
				toggleLight = 1;
			} 
			else {
				check = 0;
				toggleLight = 0;
				toggleFan = 1;
			}
	} else if (toggleLight == 1 && toggleFan == 0) {
		if (readSWs == fanSW) {
				check = 3;
				toggleFan = 1;
			}
			else if (readSWs == lightSW) {
				check = 9;
				toggleLight = 0;
			} 
			else {
				check = 0;
				toggleLight = 1;
				toggleFan = 0;
		}
		
	} else if (toggleLight == 1 && toggleFan == 1) {
		if (readSWs == fanSW) {
				check = 3;
				toggleFan = 0;
			}
			else if (readSWs == lightSW) {
				check = 9;
				toggleLight = 0;
			} 
			else {
				check = 0;
				toggleLight = 1;
				toggleFan = 1;
			}
	}
	
	if (toggleLight == 0 && (readSWs == lightSensor)) {
		check = 8;
	} else if (toggleLight == 1 && (readSWs == lightSensor)) {
		check = 17; 
	} else if (toggleLight == 0 && (readSWs != lightSensor)){
		check = 0;
	} else if (toggleLight == 1 && (readSWs != lightSensor)) {
		check = 9;
	}
	
	return check;
}
	

void actOnButtonpress(int check) {
	// Switch Masks:
	uint16_t maskSWs = 0xFCF7; 			// Clears all except bits of interests. 1100 1111 0111
	uint16_t lightSensor = 0xEFFF;	// Only PA8 pressed											1110 1111 1111
	uint16_t readSWs = GPIOA->IDR;	// Storing switch input	
	readSWs |= maskSWs;							// Mask switch inputs	
	
	if (toggleLight == 0) {	
		if (check == 8) {
			while(readSWs == lightSensor){
				GPIOB->BSRR |= GPIO_BSRR_BR0;
			}
		} 
		else if (check == 9) {
			GPIOB->ODR |= (0x01 << GPIO_ODR_OD0_Pos);
		}
		else if (check == 0) {
			GPIOB->ODR |= (0x01 << GPIO_ODR_OD0_Pos);
		}
	}
	else if (toggleLight == 1) {
	  if (check == 9) {
			GPIOB->BSRR |= GPIO_BSRR_BR0;
		} 
		else if (check == 17) {
			while(readSWs == lightSensor){
				GPIOB->BSRR |= GPIO_BSRR_BR0;
			}
		} 
		else if (check == 0) {
			GPIOB->BSRR |= GPIO_BSRR_BR0;
		}
	}
	
	if (toggleFan == 0) {
		if ((heatingMode == 1 || coolingMode == 1) && check == 3) {
			GPIOA->ODR |= (0x01 << GPIO_ODR_OD10_Pos);
			startTIMER(count15seconds);
			while((TIM6->SR & TIM_SR_UIF) == 0);
			GPIOA->BSRR |= GPIO_BSRR_BR10;
			toggleFan = 1;
		}
		else if (check == 3) {
			GPIOA->ODR |= (0x01 << GPIO_ODR_OD10_Pos);
		} 
		else if (check == 0) {
			GPIOA->ODR |= (0x01 << GPIO_ODR_OD10_Pos);
		}
	} 
	else if (toggleFan == 1) {
		
		if (check == 3) {
		 GPIOA->BSRR |= GPIO_BSRR_BR10;
		} 
		else if (check == 0) {
		GPIOA->BSRR |= GPIO_BSRR_BR10;
		}
	}
	
}
	
	
