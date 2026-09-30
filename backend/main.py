from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List, Optional
import math
from datetime import datetime

from backend.hazard_engine import HazardEvaluationEngine

app = FastAPI(
    title="Aakaash360 Environmental Intelligence API",
    description="Context-aware micro-climate telemetry, persona adaptation, and route hazard engine",
    version="1.0.0"
)

class RouteEvaluationRequest(BaseModel):
    persona_id: str
    route_id: str
    waypoints: Optional[List[dict]] = None

class AlarmShiftRequest(BaseModel):
    alarm_id: int
    shift_minutes: int

@app.get("/health")
def health_check():
    return {"status": "online", "system": "Aakaash360 Core Telemetry", "timestamp": datetime.utcnow()}

@app.get("/telemetry/current")
def get_current_telemetry(persona_id: str = "cyclist", hour_offset: int = 0):
    """
    Returns living bento telemetry calculated for the user persona and time-machine scrub hour.
    """
    wave = math.sin(hour_offset * 0.35)
    cos_wave = math.cos(hour_offset * 0.28)

    wind_speed = max(8, min(55, int(24 + (wave * 12))))
    gust_speed = max(15, min(75, int(wind_speed + 10 + (cos_wave * 8))))
    aqi = max(20, min(185, int(42 + (wave * 25))))
    traction = max(60, min(100, int(98 - (24 if 4 <= hour_offset <= 9 else 0))))
    uv = round(max(0.1, min(11.5, 3.2 + (math.sin((hour_offset + 4) * 0.25) * 4.5))), 1)
    pressure = max(992, min(1030, int(1014 + (cos_wave * 8))))

    return {
        "persona_id": persona_id,
        "hour_offset": hour_offset,
        "wind_telemetry": {
            "speed_kmh": wind_speed,
            "direction_deg": (315 + (hour_offset * 8)) % 360,
            "direction_cardinal": "NW",
            "max_gust_kmh": gust_speed,
            "aero_drag_pct": -22 if wind_speed > 30 else -12
        },
        "bento_matrix": {
            "air_matrix": {
                "aqi": aqi,
                "rating": "Good" if aqi < 50 else ("Moderate" if aqi < 100 else "Unhealthy"),
                "pm25_ug_m3": round(aqi * 0.28, 1),
                "lung_strain": "Minimal" if aqi < 60 else "Elevated"
            },
            "tire_traction": {
                "percentage": traction,
                "status": "Optimal Friction" if traction > 85 else "Caution: Damp Tarmac",
                "condition": "Dry asphalt baseline"
            },
            "solar_radiation": {
                "uv_index": uv,
                "status": "Low Sun" if uv < 3 else ("Moderate Sun" if uv < 6 else "Very High UV"),
                "gear_advice": "Tinted lenses"
            },
            "barometer": {
                "pressure_hpa": pressure,
                "tendency": "Steady isobar" if abs(wave) < 0.3 else "Falling squall line",
                "storm_cell_risk": "High Alert" if wind_speed > 35 and aqi > 80 else "Nil"
            }
        },
        "advisory": {
            "title": "Tactical Route Copilot",
            "message": f"Strong crosswinds ({gust_speed} km/h) forecasted on orbital Miles 3 to 6. Shifting departure unlocks a continuous +14% tailwind vector.",
            "suggested_departure_shift_min": -25
        }
    }

@app.post("/routes/evaluate-hazards")
def evaluate_route(req: RouteEvaluationRequest):
    default_waypoints = [
        {"mile": 0.8, "crosswind_kmh": 12, "aqi": 38, "micro_shear_prob": 0.05, "direction": "WNW"},
        {"mile": 3.2, "crosswind_kmh": 34, "aqi": 148, "micro_shear_prob": 0.72, "direction": "ESE"},
        {"mile": 5.4, "crosswind_kmh": 18, "aqi": 42, "micro_shear_prob": 0.12, "direction": "NW"}
    ]
    waypoints = req.waypoints or default_waypoints
    thresholds = {"max_wind_kmh": 28.0, "max_aqi": 75}
    return HazardEvaluationEngine.evaluate_route_hazards(14.2, waypoints, thresholds)

@app.post("/alarms/shift")
def shift_alarm(req: AlarmShiftRequest):
    return {
        "status": "success",
        "alarm_id": req.alarm_id,
        "shifted_by_minutes": req.shift_minutes,
        "new_alarm_time": "06:20 AM",
        "message": f"Departure time shifted by {abs(req.shift_minutes)} mins. Optimal weather window secured."
    }
