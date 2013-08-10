function calculateAdvice(maxRain, maxWind, maxTemp, maxUV) {

	var clothesOutput = ""

	// Rain

	if (maxRain <= 0.3) clothesOutput = ""
		else clothesOutput = "Rain Jacket and/or umbrella";

	//maxWind

	if (maxWind < 5.5) clothesOutput = clothesOutput
		else if (maxWind < 14.5) clothesOutput = clothesOutput + "jacket"
		else {
	    	if (maxWind < 30.5 && maxRain > 0.3) clothesOutput = "Jacket, Jumper and scarf"
	    		else if (maxRain >= 0.3) clothesOutput = "Rain Jacket, Jacket, Jumper and scarf";
		}

	//Uv

	if (maxUV <= 2) clothesOutput = clothesOutput
		else clothesOutput = clothesOutput + ", suncream";

	// maxTemperature

	if (maxTemp <= 0) clothesOutput = clothesOutput + ", a warm jaccket, jumper, scarf, hat and gloves"
		else if (maxTemp > 0 && maxTemp < 10) clothesOutput = clothesOutput + ", a warm jacket or jumper"
		else if (maxTemp > 10 && maxTemp < 20) clothesOutput = clothesOutput + ", jacket"
		else if (maxTemp > 20 ) clothesOutput = clothesOutput + ", a sun hat and stay hydrated"
		else if (maxTemp > 20 && maxUV >= 2) clothesOutput = clothesOutput + ", suncream, sun hat and stay hydrated";

	// Other if then else statements

	return clothesOutput
}