#include <stdio.h>
#include "gfx4.h"
#include <math.h>

// Global Struct
typedef struct
{
	double x;
	double y;
} point;

// Macros
#define WINSIZE 800
#define SPI_PARAM 0.15
#define MIN_THETA 10*M_PI

// Prototypes
void spiral_rec(point center1, double max_rad1, double angle_pos);

int main(void)
{
	// Declare Variable
	double r;
	point center;
	
	r = WINSIZE / 1.5;
	center.x = WINSIZE / 2.0;
	center.y = WINSIZE / 2.0;
	
	//// make window and drawing
	gfx_open(WINSIZE, WINSIZE, "Spiral");
	gfx_clear();
	gfx_color(255,255,255);
	spiral_rec(center, r, 0);
	gfx_flush();
	
	while(gfx_wait() != 'q');
}


void spiral_rec(point center1, double max_rad1, double angle_pos)
{
	// Base Case
	if (max_rad1 < 1 && max_rad1 > 0)
	{
		gfx_point(round(center1.x), round(center1.y));
		return;
	}
	else if(max_rad1 < 0)
	{
		return;
	}
	// Declare Variables
	double theta, r, a1, a2, dtheta, max_theta;
	point center2;
	
	// Calculate Values
	max_theta = MIN_THETA + angle_pos;
	
	a1 = (max_rad1)/pow(M_E, SPI_PARAM*max_theta);
	
	theta = max_theta;
	dtheta = M_PI/5.0;
	
	r = a1*pow(M_E,theta*SPI_PARAM);
	
	// Use Loop to make all spiral calls	
	while(theta>= 0)
	{
		// Calculate New Center
		center2.x = center1.x + r*cos(theta);
		center2.y = center1.y - r*sin(theta);
		
		// Recurssive Call
		spiral_rec(center2, 1.09*r*(M_PI/10.0), theta);
		
		// Update Values
		theta -= dtheta;
		r = a1*pow(M_E, theta*SPI_PARAM);
		
	}

}
