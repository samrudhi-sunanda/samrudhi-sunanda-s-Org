package com.example.data.model

data class PersonaCategory(
    val id: String,
    val title: String,
    val badge: String,
    val description: String,
    val iconName: String,
    val personaCount: Int,
    val personas: List<PersonaProfile>
)

data class PersonaProfile(
    val id: String,
    val domainId: String,
    val title: String,
    val subtitle: String,
    val emoji: String,
    val keyVectors: List<String>,
    val heroTelemetryType: HeroTelemetryType = HeroTelemetryType.WIND_DIAL,
    val primaryUnit: String = "km/h",
    val primaryMetricName: String = "Velocity"
)

enum class HeroTelemetryType {
    WIND_DIAL,      // Cyclists, Aviation, UAV
    AQI_SERENE_RING,// Asthmatic, Runners, Parents
    SOIL_MOISTURE,  // Farmers, Agronomists
    BAROMETER_SOLAR,// Hikers, Migraine, Astrophotography
    CROSSWIND_SHEAR // Truckers, Marine, Paragliders
}

object PersonaCatalog {
    val categories: List<PersonaCategory> = listOf(
        PersonaCategory(
            id = "fitness",
            title = "Fitness & Sports",
            badge = "4 Sub-Personas",
            description = "Runners, Cyclists, Hikers, Field Athletes",
            iconName = "directions_run",
            personaCount = 4,
            personas = listOf(
                PersonaProfile(
                    id = "cyclist",
                    domainId = "fitness",
                    title = "Cyclists & Road Bikers",
                    subtitle = "Micro-climate crosswind vectors",
                    emoji = "🚴",
                    keyVectors = listOf("WIND & GUST VECTORS", "RAIN ONSET (MIN)", "TARMAC FRICTION"),
                    heroTelemetryType = HeroTelemetryType.WIND_DIAL,
                    primaryUnit = "km/h",
                    primaryMetricName = "Wind Velocity"
                ),
                PersonaProfile(
                    id = "marathon",
                    domainId = "fitness",
                    title = "Athletes & Marathon Runners",
                    subtitle = "Metabolic strain & lung exposure",
                    emoji = "🏃",
                    keyVectors = listOf("WET-BULB TEMP", "HEAT EXERTION INDEX", "AQI & OZONE LEVEL"),
                    heroTelemetryType = HeroTelemetryType.AQI_SERENE_RING,
                    primaryUnit = "°C",
                    primaryMetricName = "Wet-Bulb Temp"
                ),
                PersonaProfile(
                    id = "hiker",
                    domainId = "fitness",
                    title = "Hikers & Alpine Trekkers",
                    subtitle = "Elevation changes & frontal storms",
                    emoji = "🥾",
                    keyVectors = listOf("BAROMETRIC RAPID DROP", "LIGHTNING 15KM RADIUS", "WIND CHILL FACTOR"),
                    heroTelemetryType = HeroTelemetryType.BAROMETER_SOLAR,
                    primaryUnit = "hPa",
                    primaryMetricName = "Barometric Gradient"
                ),
                PersonaProfile(
                    id = "field_athlete",
                    domainId = "fitness",
                    title = "Field Athletes & Team Sports",
                    subtitle = "Pitch condition & heat endurance",
                    emoji = "⚽",
                    keyVectors = listOf("GROUND MOISTURE", "UV SOLAR FLUX", "HEAT STROKE RISK"),
                    heroTelemetryType = HeroTelemetryType.SOIL_MOISTURE,
                    primaryUnit = "UV",
                    primaryMetricName = "UV Solar Index"
                )
            )
        ),
        PersonaCategory(
            id = "agri",
            title = "Work & Agri",
            badge = "Soil & Heat",
            description = "Agronomists, Site Crews, Field Engineering",
            iconName = "agriculture",
            personaCount = 6,
            personas = listOf(
                PersonaProfile(
                    id = "agronomist",
                    domainId = "agri",
                    title = "Agronomists & Crop Managers",
                    subtitle = "Evapotranspiration & topsoil dew",
                    emoji = "🌾",
                    keyVectors = listOf("SOIL MOISTURE 10CM", "LEAF WETNESS (HOURS)", "GDD ACCUMULATION"),
                    heroTelemetryType = HeroTelemetryType.SOIL_MOISTURE,
                    primaryUnit = "%",
                    primaryMetricName = "Soil Hydration"
                ),
                PersonaProfile(
                    id = "crane_operator",
                    domainId = "agri",
                    title = "Crane & Rigging Operators",
                    subtitle = "Altitude boom wind gust shears",
                    emoji = "🏗️",
                    keyVectors = listOf("GUST VELOCITY AT 60M", "LIGHTNING 20KM CONE", "THERMAL PLUME DRAFT"),
                    heroTelemetryType = HeroTelemetryType.CROSSWIND_SHEAR,
                    primaryUnit = "km/h",
                    primaryMetricName = "Boom Gusts"
                ),
                PersonaProfile(
                    id = "construction_crew",
                    domainId = "agri",
                    title = "Construction & Site Crews",
                    subtitle = "Wet bulb globe temperature & concrete cure",
                    emoji = "👷",
                    keyVectors = listOf("CONCRETE DRY RATE", "HEAT INDEX REST RATIO", "RAIN ONSET WINDOW"),
                    heroTelemetryType = HeroTelemetryType.AQI_SERENE_RING,
                    primaryUnit = "°C",
                    primaryMetricName = "WBGT Heat Strain"
                ),
                PersonaProfile(
                    id = "highrise_window",
                    domainId = "agri",
                    title = "High-Rise Façade & Window Crews",
                    subtitle = "Vertical wind eddies & sudden squalls",
                    emoji = "🪟",
                    keyVectors = listOf("VORTEX SHEDDING INDEX", "INSTANTANEOUS GUST DROP", "SURFACE ICE GLAZE"),
                    heroTelemetryType = HeroTelemetryType.WIND_DIAL,
                    primaryUnit = "km/h",
                    primaryMetricName = "Eddy Velocity"
                ),
                PersonaProfile(
                    id = "field_lineman",
                    domainId = "agri",
                    title = "Grid & Utility Linemen",
                    subtitle = "Conductor galloping & lightning track",
                    emoji = "⚡",
                    keyVectors = listOf("ICE ACCRETION RATE", "CORONA DISCHARGE HUMIDITY", "SEVERE DOWNBURST PROB"),
                    heroTelemetryType = HeroTelemetryType.BAROMETER_SOLAR,
                    primaryUnit = "mm/h",
                    primaryMetricName = "Ice Accretion"
                ),
                PersonaProfile(
                    id = "orchardist",
                    domainId = "agri",
                    title = "Precision Orchardists",
                    subtitle = "Inversion frost & blossom freeze risk",
                    emoji = "🍎",
                    keyVectors = listOf("RADIATION FROST HOUR", "CHILLING UNITS", "WIND DRIFT SPRAY SAFELY"),
                    heroTelemetryType = HeroTelemetryType.SOIL_MOISTURE,
                    primaryUnit = "°C",
                    primaryMetricName = "Inversion Delta"
                )
            )
        ),
        PersonaCategory(
            id = "travel",
            title = "Travel & Transit",
            badge = "Crosswind",
            description = "Aviation, Road Commuters, Marine Transit",
            iconName = "flight_takeoff",
            personaCount = 5,
            personas = listOf(
                PersonaProfile(
                    id = "aviation_pilot",
                    domainId = "travel",
                    title = "Aviation & Private Pilots",
                    subtitle = "Density altitude & runway crosswinds",
                    emoji = "✈️",
                    keyVectors = listOf("METAR DENSITY ALTITUDE", "RUNWAY CROSSWIND COMPONENT", "CEILING & VISIBILITY"),
                    heroTelemetryType = HeroTelemetryType.WIND_DIAL,
                    primaryUnit = "kts",
                    primaryMetricName = "Crosswind Component"
                ),
                PersonaProfile(
                    id = "road_commuter",
                    domainId = "travel",
                    title = "Two-Wheeler & Road Commuters",
                    subtitle = "Flash water pooling & bottleneck visibility",
                    emoji = "🛵",
                    keyVectors = listOf("HYDROPLANING PROB", "OPTICAL SIGHT DISTANCE", "SMOG BOTTLENECK"),
                    heroTelemetryType = HeroTelemetryType.CROSSWIND_SHEAR,
                    primaryUnit = "km",
                    primaryMetricName = "Sight Range"
                ),
                PersonaProfile(
                    id = "trucker",
                    domainId = "travel",
                    title = "Long-Haul Freight Truckers",
                    subtitle = "High-profile vehicle roll alerts",
                    emoji = "🚛",
                    keyVectors = listOf("BLOWOVER RISK RATING", "MOUNTAIN PASS BLACK ICE", "BRAKE OVERHEAT AMBIENT"),
                    heroTelemetryType = HeroTelemetryType.CROSSWIND_SHEAR,
                    primaryUnit = "km/h",
                    primaryMetricName = "Lateral Shear"
                ),
                PersonaProfile(
                    id = "marine_skipper",
                    domainId = "travel",
                    title = "Marine & Coastal Skippers",
                    subtitle = "Swell period & wave break height",
                    emoji = "⛵",
                    keyVectors = listOf("SIGNIFICANT WAVE HEIGHT", "TIDAL CURRENT VECTORS", "SEA SMOKE FOG DENSITY"),
                    heroTelemetryType = HeroTelemetryType.WIND_DIAL,
                    primaryUnit = "m",
                    primaryMetricName = "Swell Amplitude"
                ),
                PersonaProfile(
                    id = "ev_driver",
                    domainId = "travel",
                    title = "Electric Vehicle Route Planners",
                    subtitle = "Aerodynamic drag & battery cooling penalty",
                    emoji = "🔋",
                    keyVectors = listOf("WIND RESISTANCE BATTERY DRAIN", "HVAC THERMAL OVERHEAD", "ELEVATION ENERGY REGEN"),
                    heroTelemetryType = HeroTelemetryType.WIND_DIAL,
                    primaryUnit = "%",
                    primaryMetricName = "Aero Range Impact"
                )
            )
        ),
        PersonaCategory(
            id = "family",
            title = "Family & Life",
            badge = "AQI & Pollen",
            description = "Parents, Sensitive Airways, Pet Walkers",
            iconName = "nest_multi_room",
            personaCount = 6,
            personas = listOf(
                PersonaProfile(
                    id = "asthmatic",
                    domainId = "family",
                    title = "Sensitive Airways & Asthmatics",
                    subtitle = "Fine particulate spikes & sudden cold dry fronts",
                    emoji = "🫁",
                    keyVectors = listOf("PM2.5 MICRO-HOTSPOTS", "COLD AIR BRONCHOSPASM INDEX", "GRASS & TREE POLLEN PPM"),
                    heroTelemetryType = HeroTelemetryType.AQI_SERENE_RING,
                    primaryUnit = "AQI",
                    primaryMetricName = "Air Purity Index"
                ),
                PersonaProfile(
                    id = "parents",
                    domainId = "family",
                    title = "Parents & Stroller Walkers",
                    subtitle = "Sidewalk ground heat & UV sun shields",
                    emoji = "👶",
                    keyVectors = listOf("STROLLER HEIGHT THERMAL REFLECT", "UV SHIELD NECESSITY", "RAIN-FREE PARK WINDOW"),
                    heroTelemetryType = HeroTelemetryType.AQI_SERENE_RING,
                    primaryUnit = "UV",
                    primaryMetricName = "Sidewalk UV Flux"
                ),
                PersonaProfile(
                    id = "pet_walker",
                    domainId = "family",
                    title = "Pet Owners & Dog Walkers",
                    subtitle = "Asphalt paw burn temperature & thunder fear",
                    emoji = "🐕",
                    keyVectors = listOf("ASPHALT SURFACE TEMP (52°C+)", "DISTANT THUNDER RUMBLE", "TICK ACTIVITY HUMIDITY"),
                    heroTelemetryType = HeroTelemetryType.SOIL_MOISTURE,
                    primaryUnit = "°C",
                    primaryMetricName = "Paw Thermal Index"
                ),
                PersonaProfile(
                    id = "migraine",
                    domainId = "family",
                    title = "Barometric Sensitive & Migraine",
                    subtitle = "Rapid atmospheric pressure drops",
                    emoji = "🧠",
                    keyVectors = listOf("3-HOUR DELTA PRESSURE DROP", "CHINOOK/FOEHN SQUALL DETECT", "HIGH-HUMIDITY FLUX"),
                    heroTelemetryType = HeroTelemetryType.BAROMETER_SOLAR,
                    primaryUnit = "hPa/3h",
                    primaryMetricName = "Pressure Velocity"
                ),
                PersonaProfile(
                    id = "solar_prosumer",
                    domainId = "family",
                    title = "Solar Rooftop Prosumers",
                    subtitle = "Irradiance forecasting & dust film",
                    emoji = "☀️",
                    keyVectors = listOf("GLOBAL HORIZONTAL IRRADIANCE", "PANEL SOILING FACTOR", "CLOUD EDGE ENHANCEMENT"),
                    heroTelemetryType = HeroTelemetryType.BAROMETER_SOLAR,
                    primaryUnit = "W/m²",
                    primaryMetricName = "Solar Influx"
                ),
                PersonaProfile(
                    id = "event_host",
                    domainId = "family",
                    title = "Outdoor Event & Wedding Hosts",
                    subtitle = "Micro-gust canopy safety & evening dew point",
                    emoji = "🎪",
                    keyVectors = listOf("TENT GUY-WIRE GUST LIMIT", "CHAIR CONDENSATION ONSET", "HUMIDITY COMFORT INDEX"),
                    heroTelemetryType = HeroTelemetryType.CROSSWIND_SHEAR,
                    primaryUnit = "km/h",
                    primaryMetricName = "Canopy Risk Factor"
                )
            )
        ),
        PersonaCategory(
            id = "specialized",
            title = "Specialized Missions",
            badge = "KP & Deck",
            description = "UAV/Drone Piloting, Astrophotography, Emergency Storm Tracking",
            iconName = "satellite_alt",
            personaCount = 7,
            personas = listOf(
                PersonaProfile(
                    id = "drone_pilot",
                    domainId = "specialized",
                    title = "UAV & Commercial Drone Pilots",
                    subtitle = "Geomagnetic KP storm & 400ft AGL winds",
                    emoji = "🛸",
                    keyVectors = listOf("KP GEOMAGNETIC INDEX", "400FT AGL WIND SHEAR", "GPS SATELLITE LOCK DILUTION"),
                    heroTelemetryType = HeroTelemetryType.WIND_DIAL,
                    primaryUnit = "Kp",
                    primaryMetricName = "Geomagnetic KP"
                ),
                PersonaProfile(
                    id = "astrophotographer",
                    domainId = "specialized",
                    title = "Astrophotographers & Stargazers",
                    subtitle = "Astronomical seeing & dew point margin",
                    emoji = "🔭",
                    keyVectors = listOf("ASTRONOMICAL SEEING (ARCSEC)", "CIRRUS DECK TRANSPARENCY", "LENS DEW MARGIN (°C)"),
                    heroTelemetryType = HeroTelemetryType.BAROMETER_SOLAR,
                    primaryUnit = "arcsec",
                    primaryMetricName = "Seeing Clarity"
                ),
                PersonaProfile(
                    id = "storm_spotter",
                    domainId = "specialized",
                    title = "Emergency Storm Chasers",
                    subtitle = "CAPE energy & supercell helicity",
                    emoji = "⚡",
                    keyVectors = listOf("SURFACE CAPE (J/KG)", "0-1KM STORM RELATIVE HELICITY", "RADAR REFLECTIVITY DBZ"),
                    heroTelemetryType = HeroTelemetryType.BAROMETER_SOLAR,
                    primaryUnit = "J/kg",
                    primaryMetricName = "CAPE Instability"
                ),
                PersonaProfile(
                    id = "wildfire_watcher",
                    domainId = "specialized",
                    title = "Wildfire & Prescribed Burn Crews",
                    subtitle = "Haines index & relative humidity plunge",
                    emoji = "🔥",
                    keyVectors = listOf("HAINES DRYNESS INDEX", "10-HOUR DEAD FUEL MOISTURE", "PYROCUMULONIMBUS RISK"),
                    heroTelemetryType = HeroTelemetryType.SOIL_MOISTURE,
                    primaryUnit = "Haines",
                    primaryMetricName = "Haines Index"
                ),
                PersonaProfile(
                    id = "paraglider",
                    domainId = "specialized",
                    title = "Paragliders & Hang Gliders",
                    subtitle = "Ridge lift strength & thermal updraft core",
                    emoji = "🪂",
                    keyVectors = listOf("THERMAL CLIMB VELOCITY (M/S)", "ROTOR ZONE TURBULENCE", "CLOUD BASE ALTITUDE AGL"),
                    heroTelemetryType = HeroTelemetryType.WIND_DIAL,
                    primaryUnit = "m/s",
                    primaryMetricName = "Thermal Updraft"
                ),
                PersonaProfile(
                    id = "search_rescue",
                    domainId = "specialized",
                    title = "Mountain Search & Rescue",
                    subtitle = "Hypothermia index & helicopter flight floor",
                    emoji = "🚁",
                    keyVectors = listOf("MOUNTAIN ROTOR TURBULENCE", "EXPOSURE SURVIVAL WINDOW", "RIDGE HOVER VISIBILITY"),
                    heroTelemetryType = HeroTelemetryType.CROSSWIND_SHEAR,
                    primaryUnit = "min",
                    primaryMetricName = "Survival Window"
                ),
                PersonaProfile(
                    id = "wind_tech",
                    domainId = "specialized",
                    title = "Wind Turbine Blade Techs",
                    subtitle = "Nacelle cut-out speeds & lightning static",
                    emoji = "🌀",
                    keyVectors = listOf("HUB HEIGHT 120M SHEAR", "STATIC CHARGE ACCUMULATION", "BLADE TIP ICING RISK"),
                    heroTelemetryType = HeroTelemetryType.WIND_DIAL,
                    primaryUnit = "km/h",
                    primaryMetricName = "120m Hub Wind"
                )
            )
        )
    )

    fun findPersona(id: String): PersonaProfile {
        return categories.flatMap { it.personas }.firstOrNull { it.id == id }
            ?: categories.first().personas.first()
    }
}
