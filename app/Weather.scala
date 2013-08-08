object Weather {

	def weatherOutput(rain: Double, wind: Double, uv: Double, temp: Double): String = {

		println(s"rain = $rain, wind = $wind, uv = $uv, temp = $temp")

		var clothesOutput: String = ""

		// Rain

		if (rain <= 0.3) clothesOutput = ""
		else clothesOutput = "Rain Jacket and/or umbrella"

		//wind

		if (wind < 5.5) clothesOutput = clothesOutput
		else if (wind < 14.5) clothesOutput = clothesOutput + "jacket"
		else {
		    if (wind < 30.5 && rain > 0.3) clothesOutput = "Jacket, Jumper and scarf"
		    else if (rain >= 0.3) clothesOutput = "Rain Jacket, Jacket, Jumper and scarf"
		}

		//Uv

		if (uv <= 2) clothesOutput = clothesOutput
		else clothesOutput = clothesOutput + ", suncream"

		// temperature

		if (temp <= 0) clothesOutput = clothesOutput + ", a warm jaccket, jumper, scarf, hat and gloves"
		else if (temp > 0 && temp < 10) clothesOutput = clothesOutput + ", a warm jacket or jumper"
		else if (temp > 10 && temp < 20) clothesOutput = clothesOutput + ", jacket"
		else if (temp > 20 ) clothesOutput = clothesOutput + ", a sun hat and stay hydrated"
		else {if (temp > 20 && uv >= 2) clothesOutput = clothesOutput + ", suncream, sun hat and stay hydrated"
		} 

		// Other if then else statements

		clothesOutput
	}

}