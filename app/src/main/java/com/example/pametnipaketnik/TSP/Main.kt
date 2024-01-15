//import com.sun.net.httpserver.Request
//import org.apache.pdfbox.pdmodel.PDDocument
//import org.apache.pdfbox.text.PDFTextStripper
import org.json.JSONObject
import tsp.GA
import tsp.TSP
import tsp.Tour

/*fun extractTextFromPdf(pdfPath: String): String {
    PDDocument.load(File(pdfPath)).use { document ->
        val stripper = PDFTextStripper()
        return stripper.getText(document)
    }
}*/

fun extractData(text: String): MutableList<String> {
    return text.lineSequence() // Convert the text into a sequence of lines
        .drop(1) // Skip the header row
        .mapNotNull { line ->
            var trimmedLine = line.trim() // Trim leading and trailing whitespaces
            val firstSpaceIndex = trimmedLine.indexOf(" ") // Find the index of the first space
            val lastSpaceIndex = trimmedLine.lastIndexOf(" ") // Find the index of the last space
            // Check if the line is valid and contains spaces
            if (firstSpaceIndex != -1 && lastSpaceIndex != -1 && firstSpaceIndex != lastSpaceIndex) {
                trimmedLine.substring(firstSpaceIndex + 1, lastSpaceIndex).trim() // Extract the middle part
            } else {
                null // Invalid line format or unable to parse
            }
        }
        .toMutableList()
}

fun parseJSON(jsonString: String): String {
    // Create a JSONObject and pass the json string
    val jsonObject = JSONObject(jsonString)

    val res = jsonObject.getJSONArray("results")
    if (res.length() == 0) {
        println(jsonString)
        println("Naslov ni bil najden")
        return ""
    }
    val lat = res.getJSONObject(0)["lat"].toString()
    val lon = res.getJSONObject(0)["lon"].toString()
    val address =
        res.getJSONObject(0)["address_line1"].toString() + ", " + res.getJSONObject(0)["address_line2"].toString()

    return lat + "\n" + lon + "\n" + address + "\n"
}

fun getLocation(jsonString: String): Pair<Double, Double> {
    val jsonObject = JSONObject(jsonString)

    val res = jsonObject.getJSONArray("features")
    val distance = res.getJSONObject(0).getJSONObject("properties")["distance"].toString().toDouble()
    val time = res.getJSONObject(0).getJSONObject("properties")["time"].toString().toDouble()

    return Pair(distance, time)
}

fun main() {
    /*val pdfPath = "src/main/resources/neke.pdf"
    val text = extractTextFromPdf(pdfPath)

    val data = extractData(text)
    for (i in 0..<data.size) {
        if (data[i].contains("Lj.")) {
            data[i] = data[i].replace("Lj.", "Ljubljana")
        }
    }

    val client: OkHttpClient = OkHttpClient()*/


    /*var final = mutableListOf<String>()

    data.forEach { line ->
        val request: okhttp3.Request = okhttp3.Request.Builder()
            .url("https://api.geoapify.com/v1/geocode/search?text=$line&format=json&apiKey=635b84cbf55241c6b792a66cd02745a9")
            .build()

        val response: okhttp3.Response = client.newCall(request).execute()
        response.body?.let { final.add(it.string()) }
    }

    val file = File("data.dat")

    final.forEach { line ->
        val tmp=parseJSON(line)
        file.appendText(tmp, Charsets.UTF_8)
        println()
    }*/


    /*val parser = Parser("data.dat")
    val lines = parser.parse()

    var cities = mutableListOf<City>()
    for (i in 0..<lines.size step 3) {
        cities.add(City(i / 3, lines[i].toDouble(), lines[i + 1].toDouble()))
    }

    var distanceMatrix = MutableList<MutableList<Double>>(cities.size) { MutableList<Double>(cities.size) { 0.0 } }
    var timeMatrix = MutableList<MutableList<Double>>(cities.size) { MutableList<Double>(cities.size) { 0.0 } }

    for (i in 0..<cities.size) {
        for (j in 0..<cities.size) {
            if (i != j) {
                val request: okhttp3.Request = okhttp3.Request.Builder()
                    .url("https://api.geoapify.com/v1/routing?waypoints=${cities[i].x},${cities[i].y}|${cities[j].x},${cities[j].y}&mode=medium_truck&apiKey=635b84cbf55241c6b792a66cd02745a9")
                    .build()

                val response: okhttp3.Response = client.newCall(request).execute()
                print("$i $j ")
                response.body?.let {
                    val tmp = getLocation(it.string())
                    distanceMatrix[i][j] = tmp.first
                    timeMatrix[i][j] = tmp.second
                }
            }
        }
    }

    val distanceFile = File("distance_preloaded.dat")
    val timeFile = File("time_preloaded.dat")

    distanceMatrix.forEach { line ->
        line.forEach {
            print("$it ")
            distanceFile.appendText("$it ", Charsets.UTF_8)
        }
        distanceFile.appendText("\n", Charsets.UTF_8)
        println()
    }
    println()
    println()

    timeMatrix.forEach { line ->
        line.forEach {
            print("$it ")
            timeFile.appendText("$it ", Charsets.UTF_8)
        }
        timeFile.appendText("\n", Charsets.UTF_8)
        println()
    }*/

        val eilTsp = TSP("raw/distance_preloaded.dat", 1000000)
        //val eilTsp = TSP("src/main/resources/bays29.tsp", 10000)
        val ga = GA(200, 0.8, 0.15)
        val bestPath: Tour = ga.execute(eilTsp)
        println(bestPath.distance/1000)

}
