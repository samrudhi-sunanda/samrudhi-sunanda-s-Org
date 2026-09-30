from datetime import datetime
from sqlalchemy import (
    Column, Integer, String, Float, Boolean, DateTime, ForeignKey, Text, JSON
)
from sqlalchemy.orm import declarative_base, relationship

Base = declarative_base()

class User(Base):
    __tablename__ = "users"

    id = Column(String(64), primary_key=True, default="default_user")
    email = Column(String(120), unique=True, nullable=True)
    active_persona_id = Column(String(64), default="cyclist")
    active_domain = Column(String(64), default="Fitness & Outdoor Sports")
    sleep_time = Column(String(10), default="22:30")
    wake_time = Column(String(10), default="06:30")
    soundscape_enabled = Column(Boolean, default=True)
    is_onboarded = Column(Boolean, default=True)
    created_at = Column(DateTime, default=datetime.utcnow)

    thresholds = relationship("AlertThreshold", back_populates="user", cascade="all, delete-orphan")
    routes = relationship("Route", back_populates="user", cascade="all, delete-orphan")

class PersonaPreference(Base):
    __tablename__ = "persona_preferences"

    persona_id = Column(String(64), primary_key=True)
    domain = Column(String(64), nullable=False)
    title = Column(String(120), nullable=False)
    subtitle = Column(String(255))
    icon_emoji = Column(String(10))
    key_vectors = Column(JSON)  # e.g. ["WIND & GUST VECTORS", "RAIN ONSET", "TARMAC FRICTION"]
    primary_metric_name = Column(String(64))
    primary_unit = Column(String(20))
    telemetry_primed = Column(Boolean, default=True)

class AlertThreshold(Base):
    __tablename__ = "alert_thresholds"

    id = Column(Integer, primary_key=True, autoincrement=True)
    user_id = Column(String(64), ForeignKey("users.id"), default="default_user")
    persona_id = Column(String(64), nullable=False)
    max_wind_kmh = Column(Float, default=28.0)
    max_gust_kmh = Column(Float, default=38.0)
    max_aqi = Column(Integer, default=75)
    max_rain_prob_pct = Column(Integer, default=20)
    night_before_alerts_enabled = Column(Boolean, default=True)
    departure_shift_minutes = Column(Integer, default=25)

    user = relationship("User", back_populates="thresholds")

class CachedWeather(Base):
    __tablename__ = "cached_weather"

    location_key = Column(String(64), primary_key=True)
    location_name = Column(String(120), default="Sector 4 → Tech Corridor")
    wind_speed_kmh = Column(Integer, default=24)
    wind_direction_deg = Column(Integer, default=315)
    wind_cardinal = Column(String(10), default="NW")
    gust_speed_kmh = Column(Integer, default=38)
    aero_drag_pct = Column(Integer, default=-12)
    precip_window_pct = Column(Integer, default=0)
    precip_summary = Column(String(120), default="Bone dry for next 90m")
    sight_range_km = Column(Float, default=9.8)
    aqi = Column(Integer, default=42)
    aqi_rating = Column(String(30), default="Good")
    pm25 = Column(Float, default=11.0)
    tire_traction_pct = Column(Integer, default=98)
    solar_uv = Column(Float, default=3.2)
    solar_rating = Column(String(30), default="Moderate Sun")
    barometer_hpa = Column(Integer, default=1014)
    barometer_tendency = Column(String(40), default="Steady isobar")
    storm_cell_risk = Column(String(30), default="Nil")
    updated_at = Column(DateTime, default=datetime.utcnow)

class Route(Base):
    __tablename__ = "routes"

    route_id = Column(String(64), primary_key=True)
    user_id = Column(String(64), ForeignKey("users.id"), default="default_user")
    title = Column(String(120), nullable=False)
    subtitle = Column(String(255))
    distance_km = Column(Float, default=14.2)
    duration_min = Column(Integer, default=38)
    hazard_count = Column(Integer, default=1)
    hazard_summary = Column(Text)
    is_recommended = Column(Boolean, default=False)
    route_aqi = Column(Integer, default=38)
    peak_crosswind_kmh = Column(Integer, default=34)
    sensor_confidence_pct = Column(Float, default=96.4)

    user = relationship("User", back_populates="routes")

class BioSyncAlarm(Base):
    __tablename__ = "biosync_alarms"

    id = Column(Integer, primary_key=True, autoincrement=True)
    target_time = Column(String(20), nullable=False)
    suggested_shift_min = Column(Integer, default=-25)
    message = Column(Text, nullable=False)
    is_applied = Column(Boolean, default=False)
    created_at = Column(DateTime, default=datetime.utcnow)
